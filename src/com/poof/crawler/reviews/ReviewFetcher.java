package com.poof.crawler.reviews;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.util.Cookie;
import com.poof.crawler.db.DBUtil;

/**
 * @author wilkey 
 * @mail admin@wilkey.vip
 * @Date 2017年1月10日 下午4:28:07
 */
public class ReviewFetcher {

	private static Logger log = Logger.getLogger(ReviewFetcher.class);

	/**
	 * @param args
	 * @throws IOException
	 * @throws ParseException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException {
		System.err.println("starting......");
		ReviewFetcher.timer();
		System.in.read();
	}

	@SuppressWarnings("unchecked")
	public static void timer() {
		final Hashtable<String, Integer> ht = new Hashtable<String, Integer>();
		// add proxy list to hash table
		final FinalCounter count = new FinalCounter(0);
		final List<String> excelData = loadAsin();
		final ExecutorService pool = Executors.newFixedThreadPool(2);
		final ExecutorCompletionService<String> completionService = new ExecutorCompletionService<String>(pool);

		TimerTask task = new TimerTask() {
			@Override
			public synchronized void run() {
				long startTime = System.currentTimeMillis();
				Map<String, String> cookies = null;
				for (final String itemid : excelData) {
					completionService.submit(new Callable() {
						@Override
						public Object call() throws Exception {
							count.increment();
							System.err.println(itemid);
							Item anitem = new Item(itemid);
							anitem.fetchReview(ht, cookies);
							log.info(log.getName() + " : currently at " + anitem.itemID + " fetching size : " + anitem.reviews.size());
							if (anitem.reviews.size() == 0 || anitem.reviews.isEmpty()) {
								return null;
							}
							Collections.sort(anitem.reviews);
							BatchInsert(anitem);
							TimeUnit.SECONDS.sleep(new Random().nextInt(30) % (30 - 10 + 1) + 10);
							return null;
						}
					});
				}

				for (int i = 0; i < excelData.size(); i++) {
					try {
						completionService.take();
//						log.info(log.getName() + " : Generated Report(null/full)for " + " " + excelData.get(i) + " " + "Has arrived" + " " + " " + "and Right now we are at" + " " + i + "In the SKU LIST");
					} catch (Exception e) {

					}
				}
				long endTime = System.currentTimeMillis();
				System.err.println("done.");
				log.info(log.getName() + " : " + String.format("队列任务完成，耗时%s秒", (endTime - startTime) / 1000));
			}
		};
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(year, month, day, 9, 30, 00);
		Date date = calendar.getTime();
		Timer timer = new Timer();
		timer.schedule(task, date, 60 * 1000 * 60 * 24);
	}

	// save to file
	protected static void saveToFile(Item anitem) throws Exception {
		ListIterator<Review> iteratori = anitem.reviews.listIterator();

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("new sheet");

		int rowIndex = 1;
		while (iteratori.hasNext()) {
			Review r = iteratori.next();
			Row row = sheet.createRow(rowIndex++);

			sheet.createRow(0).createCell(0).setCellValue("ASIN");
			sheet.getRow(0).createCell(1).setCellValue("ReviewID");
			sheet.getRow(0).createCell(2).setCellValue("CustomerName");
			sheet.getRow(0).createCell(3).setCellValue("Rating Received");
			sheet.getRow(0).createCell(4).setCellValue("Maxium Rating");
			sheet.getRow(0).createCell(5).setCellValue("Is verified Purchase?");
			sheet.getRow(0).createCell(6).setCellValue("RealName");
			sheet.getRow(0).createCell(7).setCellValue("ReviewDate");
			sheet.getRow(0).createCell(8).setCellValue("Review Header");
			sheet.getRow(0).createCell(9).setCellValue("Review");
			sheet.getRow(0).createCell(10).setCellValue("CustomerID");

			row.createCell(0).setCellValue(r.getItemID());
			row.createCell(1).setCellValue(r.getReviewID());
			row.createCell(2).setCellValue(r.getCustomerName());
			row.createCell(3).setCellValue(r.getRating());
			row.createCell(4).setCellValue(r.getFullRating());
			row.createCell(5).setCellValue(r.isVerifiedPurchase());
			row.createCell(6).setCellValue(r.getRealName());
			row.createCell(7).setCellValue(r.getReviewDate().toString().substring(3, r.getReviewDate().toString().length()));
			row.createCell(8).setCellValue(r.getTitle());
			row.createCell(9).setCellValue(r.getContent());
			row.createCell(10).setCellValue(r.getCustomerID());

		}
		FileOutputStream fileOut = new FileOutputStream("d:\\" + anitem.itemID + ".xls");
		wb.write(fileOut);
		fileOut.close();
	}

	// save to db
	private static void BatchInsert(Item anitem) throws Exception {
		String sql = "insert into bz_reviews (asin, review_id, buyer_name, buyer_id, rating, rating_date, verified, title, content)values(?,?,?,?,?,?,?,?,?)";
		Connection conn = null;
		try {
			conn = DBUtil.openConnection();
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			PreparedStatement pstmt = conn.prepareStatement(sql);
			int size = anitem.reviews.size() / 20;
			size = anitem.reviews.size() % 20 >= 0 ? size + 1 : size; // 5521,5
			for (int i = 0; i < size; i++) { // 6
				for (int j = 0; j < (i == size - 1 ? anitem.reviews.size() % 20 : 20); j++) {
					Review bean = anitem.reviews.get(i * 20 + j);
					pstmt.setString(1, bean.getItemID());
					pstmt.setString(2, bean.getReviewID());
					pstmt.setString(3, bean.getCustomerName());
					pstmt.setString(4, bean.getCustomerID());
					pstmt.setDouble(5, bean.getRating());
					pstmt.setString(6, new SimpleDateFormat("yyyy-MM-dd").format(bean.getReviewDate()));
					pstmt.setBoolean(7, bean.isVerifiedPurchase());
					pstmt.setString(8, bean.getTitle());
					pstmt.setString(9, bean.getContent());
					pstmt.addBatch();
				}
				pstmt.executeBatch();
				pstmt.clearBatch();
			}
			if (size > 0) {
				// DBUtil.execute(conn, "delete from bz_reviews where asin = '"
				// + anitem.itemID + "'");
			}
			conn.commit();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			log.error(log.getName() + " : program error: " + e);
			throw e;
		} finally {
			DBUtil.closeConnection();
		}
	}

	public static List<String> loadAsin() {
		try {
			List<String> results = DBUtil.queryObjectList(DBUtil.openConnection(), "select parent_asin from bz_parent_child_asin group by parent_asin", String.class);
			return results;
		} catch (Exception e) {
			log.error(log.getName() + " : program error: " + e);
			e.printStackTrace();
		} finally {
		}
		return new ArrayList<String>();
	}

	private static Map<String, String> getCookie() {
		WebClient webClient = new WebClient();
		webClient.getCookieManager().setCookiesEnabled(true);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setTimeout(10000);
		webClient.getCookieManager().clearCookies();
		webClient.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");
		webClient.addRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		webClient.addRequestHeader("Accept-Encoding", "gzip, deflate");
		webClient.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8");
		try {
			List<String> checkList = new ArrayList<String>();
			Map<String, String> cookies = new HashMap<String, String>();
			do {
				checkList.clear();
				cookies.clear();
				webClient.getPage("https://www.amazon.com/");
				webClient.waitForBackgroundJavaScript(30000);
				Set<Cookie> set = webClient.getCookieManager().getCookies();
				for (Iterator<Cookie> iterator = set.iterator(); iterator.hasNext();) {
					Cookie cookie = iterator.next();
					cookies.put(cookie.getName(), cookie.getValue());
					checkList.add(cookie.getName());
				}
			} while (!checkList.contains("session-token") && !checkList.contains("session-id"));
			return cookies;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			webClient.close();
			System.out.println("got amazon cookie.....");
		}
		return null;
	}
}