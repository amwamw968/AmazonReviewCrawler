package com.poof.crawler.reviews;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Response;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.poof.crawler.utils.TimeUtil;

/**
 * @author wilkey 
 * @mail admin@wilkey.vip
 * @Date 2017年1月10日 下午4:28:21
 */
public class Item {
	private static Logger log = Logger.getLogger(Item.class);
	// item id refer to the ASIN in this particular program, can be changed.
	public String itemID;
	public ArrayList<Review> reviews;
	// list of user agent for user agent spoofing
	public ArrayList<String> ua = new ArrayList<String>(Arrays.asList(

			"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:40.0) Gecko/20100101 Firefox/40.1 ",
			"Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25 ",
			"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2226.0 Safari/537.36 ",
			"Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.3a) Gecko/20021207 Phoenix/0.5 ", "Mozilla/5.0 (Windows NT 5.2; RW; rv:7.0a1) Gecko/20091211 SeaMonkey/9.23a1pre",
			"Surf/0.4.1 (X11; U; Unix; en-US) AppleWebKit/531.2+ Compatible (Safari; MSIE 9.0)", "Mozilla/5.0 (compatible; U; ABrowse 0.6; Syllable) AppleWebKit/420+ (KHTML, like Gecko)",
			"Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0; Acoo Browser 1.98.744; .NET CLR 3.5.30729) ",
			"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; Acoo Browser; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0; .NET CLR 3.0.04506)",
			"Mozilla/4.0 (compatible; MSIE 7.0; America Online Browser 1.1; rev1.5; Windows NT 5.1; .NET CLR 1.1.4322; .NET CLR 2.0.50727) ",
			"Mozilla/4.0 (compatible; MSIE 6.0; America Online Browser 1.1; Windows NT 5.1; SV1; HbTools 4.7.0)",
			"Mozilla/4.0 (compatible; MSIE 8.0; AOL 9.7; AOLBuild 4343.27; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)",
			"Mozilla/4.0 (compatible; MSIE 8.0; AOL 9.7; AOLBuild 4343.19; Windows NT 5.1; Trident/4.0; GTB7.2; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729) ",
			"Mozilla/5.0 (Windows NT 5.1) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30 ChromePlus/1.6.3.1",
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.10 (KHTML, like Gecko) Chrome/8.0.552.224 Safari/534.10 ChromePlus/1.5.2.0",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.6b) Gecko/20031212 Firebird/0.7+ ", "Mozilla/5.0 (Windows; U; Windows NT 6.1; x64; fr; rv:1.9.2.13) Gecko/20101203 Firebird/3.6.13",
			"Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko", "Mozilla/1.22 (compatible; MSIE 10.0; Windows 3.1) ",
			"Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; Media Center PC 6.0; InfoPath.3; MS-RTC LM 8; Zune 4.7) ", "Lynx/2.8.8dev.3 libwww-FM/2.14 SSL-MM/1.4.1 ",
			"Lynx/2.8.7dev.4 libwww-FM/2.14 SSL-MM/1.4.1 OpenSSL/0.9.8d ", "Opera/9.80 (X11; Linux i686; Ubuntu/14.10) Presto/2.12.388 Version/12.16",
			"Opera/12.80 (Windows NT 5.1; U; en) Presto/2.10.289 Version/12.02)", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0; MyIE2; SLCC1; .NET CLR 2.0.50727; Media Center PC 5.0) ",
			"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_3) AppleWebKit/534.55.3 (KHTML, like Gecko) Version/5.1.3 Safari/534.53.10",
			"Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US) AppleWebKit/533.1 (KHTML, like Gecko) Maxthon/3.0.8.2 Safari/533.1",
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/532.4 (KHTML, like Gecko) Maxthon/3.0.6.27 Safari/532.4",
			"Mozilla/5.0 (X11; U; Linux i686; pt-BR) AppleWebKit/533.3 (KHTML, like Gecko) Navscape/Pre-0.2 Safari/533.3",
			"Mozilla/5.0 (Windows; U; Windows NT 5.1; pt-BR) AppleWebKit/534.12 (KHTML, like Gecko) Navscape/Pre-0.1 Safari/534.12"

	)

	);

    public static String getRandomIp(){
        //ip范围
        int[][] range = {{607649792,608174079},//36.56.0.0-36.63.255.255
                         {1038614528,1039007743},//61.232.0.0-61.237.255.255
                         {1783627776,1784676351},//106.80.0.0-106.95.255.255
                         {2035023872,2035154943},//121.76.0.0-121.77.255.255
                         {2078801920,2079064063},//123.232.0.0-123.235.255.255
                         {-1950089216,-1948778497},//139.196.0.0-139.215.255.255
                         {-1425539072,-1425014785},//171.8.0.0-171.15.255.255
                         {-1236271104,-1235419137},//182.80.0.0-182.92.255.255
                         {-770113536,-768606209},//210.25.0.0-210.47.255.255
                         {-569376768,-564133889}, //222.16.0.0-222.95.255.255
        };
        Random rdint = new Random();
        int index = rdint.nextInt(10);
        String ip = num2ip(range[index][0]+new Random().nextInt(range[index][1]-range[index][0]));
        return ip;
    }
    
    public static String num2ip(int ip) {
        int [] b=new int[4] ;
        String x = "";
        b[0] = (int)((ip >> 24) & 0xff);
        b[1] = (int)((ip >> 16) & 0xff);
        b[2] = (int)((ip >> 8) & 0xff);
        b[3] = (int)(ip & 0xff);
        x=Integer.toString(b[0])+"."+Integer.toString(b[1])+"."+Integer.toString(b[2])+"."+Integer.toString(b[3]); 
        return x; 
     }

	public Item(String theitemid) {
		itemID = theitemid;
		reviews = new ArrayList<Review>();
	}

	public void addReview(Review thereview) {
		reviews.add(thereview);
	}

	/**
	 * Fetch all reviews for the item from Amazon.com
	 * @param cookies 
	 * 
	 * @throws IOException
	 * @throws ParseException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws InterruptedException
	 */
	public void fetchReview(Hashtable<String, Integer> ht, Map<String, String> cookies) throws IOException, ParseException, InvalidKeyException, NoSuchAlgorithmException, InterruptedException {

		String url = "http://www.amazon.com/product-reviews/" + itemID + "/ref=cm_cr_arp_d_show_all?reviewerType=all_reviews&showViewpoints=0&sortBy=recent&pageNumber=";

		// key for hash table, in this case it is the proxy IP
		String keyht;
		Integer valueht;
		String str;

		Iterator<Entry<String, Integer>> entries = ht.entrySet().iterator();
		Iterator<Entry<String, Integer>> entries1 = ht.entrySet().iterator();

		Connection con = null;

		int count = 0;
		int maxtries = 10;
		int maxpage = 1;

		for (int i = 1; i <= maxpage; i++) {
			org.jsoup.nodes.Document reviewpage = null;
			con = Jsoup.connect(url + i)
//					.cookies(cookies)
					.header("User-Agent", ua.get(new Random().nextInt(ua.size())))
					.header("Accept", "application/json, text/javascript, */*; q=0.01")
					.header("Accept-Encoding", "gzip, deflate")
					.header("Accept-Language", "zh-CN,zh;q=0.8")
					.header("X-Forwarded-For", getRandomIp() + ", " + getRandomIp() + ", " + getRandomIp())
					.timeout(1000 * 30);

			while (true) {
				try {
					Response resp = con.execute();
					int statuscode = resp.statusCode();

					if (statuscode == 200) {
						log.info(log.getName() + " : ASIN[" + itemID + "] connect http 200 , ok");

						reviewpage = resp.parse();
						int rcount = 0;
						while (reviewpage.text().contains("Robot")) {
							if (entries.hasNext()) {
								Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) entries.next();
								keyht = (String) entry.getKey();
								valueht = (Integer) entry.getValue();

							} else {
								Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) entries1.next();
								keyht = (String) entry.getKey();
								valueht = (Integer) entry.getValue();
							}
							Thread.sleep(15 * 1000);

							log.info(log.getName() + " : Setting up Proxy, mimic user, closing connection, trying to reconnect....");

							// using http for proxy connection

							final URL website = new URL(url);
							Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(keyht, valueht)); // set
																												// proxy
																												// server
																												// and
																												// port
							HttpURLConnection httpUrlConnetion = (HttpURLConnection) website.openConnection(proxy);
							httpUrlConnetion.setRequestProperty("User-Agent", ua.get(rcount));
							httpUrlConnetion.setRequestProperty("referer", "http://www.google.com");
							httpUrlConnetion.connect();

							BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConnetion.getInputStream()));
							StringBuilder buffer = new StringBuilder();

							while ((str = br.readLine()) != null) {
								buffer.append(str);
							}
							reviewpage = Jsoup.parse(buffer.toString());
							rcount++;

							// if robot check has occured 5 times
							if (rcount == 29) {
								log.error(log.getName() + " : Robot Check !!!  Repeat, Robot Check !!!");
								con.timeout(50000);
								Thread.sleep(100000);
								rcount = 0;
							}
						}

						if (reviewpage.select("div.a-section.review").isEmpty()) {
							break;
						} else {
							try {
								log.info(log.getName() + " : page " + i + ", " + itemID + " Parsing Dom, fetching reviews data....");
								// get max page
								if (maxpage == 1) {
									Elements pagelinks = reviewpage.select("ul.a-pagination").select("li.page-button");
									if (pagelinks.size() != 0) {
										maxpage = Integer.valueOf(pagelinks.last().text().trim());
									}
								}

								Elements reviewsHTMLs = reviewpage.select("div.a-section.review");

								for (Element reviewBlock : reviewsHTMLs) {

									Elements date = reviewBlock.select("span.review-date");
									String datetext = date.first().text().substring(3); 		// 去掉[on ]dec 2016
									String endatetime = TimeUtil.parseEnTimeZone(TimeZone.getTimeZone("PST"), 7);		//取什么时候的
									String outcondition = TimeUtil.parseEnTimeZone(TimeZone.getTimeZone("PST"), 2);		//大于 xx 时 break
									Date stopdate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(endatetime);
									Date outdate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(outcondition);
									Date curdate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(datetext);

									if (curdate.compareTo(stopdate) >= 1) {

										Review theReview = cleanReviewBlock(reviewBlock);
										this.addReview(theReview);
									} else if (outdate.compareTo(curdate) == 1) {
										log.info(log.getName() + " : pagevalue ["+curdate+"] passing and break");
										return;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								log.error(log.getName() + " : program error: " + e);
							}
						}
						break;
					} else {
						log.info(log.getName() + " : Not sucessfully connected,  it is " + " " + resp.statusCode() + " " + "Move on to next sku in list, admin please log this sku");
						return;
					}
				} catch (HttpStatusException e) {
					if (e.getStatusCode() == 503 || e.getStatusCode() == 500) {
						// retry after catch
						log.error(log.getName() + " : Status 503, Amazon has throttled the response due to too many requests being send, need to slow down.");
						Thread.sleep(5000);

						con = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2").timeout(30000).followRedirects(true);
						if (++count == maxtries) {
							log.error(log.getName() + " : program error: " + e);
							throw e;
						}
					} else {
						log.error(log.getName() + " : Unable to establish connection, sku might no longer exists, exception code" + e.toString() + " " + "will move on to next sku in list" + " "
								+ "Admin please log this sku");
						return;
					}
				} catch (SocketTimeoutException e) {
					log.error(log.getName() + " : socket excpetion" + " " + e);
					if (++count == maxtries)
						throw e;
				}
			}
		}
	}

	/**
	 * cleans the html block that contains a review
	 * 
	 * @param reviewBlock
	 *            a html review block (Jsoup Element)
	 * @return
	 * @throws ParseException
	 */
	public Review cleanReviewBlock(Element reviewBlock) throws ParseException {
		try {

			String theitemID = this.itemID;
			String reviewID = "";
			String customerName = "";
			String customerID = "";
			String title = "";
			int rating = 0;
			int fullRating = 5;
			int helpfulVotes = 0;
			int totalVotes = 0;
			boolean verifiedPurchase = false;
			String realName = "N/A";
			String comments;

			String content = "";

			reviewID = reviewBlock.id();
			// customer name and id
			Elements customerIDs = reviewBlock.getElementsByAttributeValueContaining("href", "/gp/pdp/profile/");
			if (customerIDs.size() > 0) {
				Element customer = customerIDs.first();
				String customerhref = customer.attr("href");
				String patternString = "(/gp/pdp/profile/)(.+)/(.+)";
				Pattern pattern = Pattern.compile(patternString);
				Matcher matcher = pattern.matcher(customerhref);
				matcher.find();
				customerID = matcher.group(2);
				customerName = customer.text();
			}

			// rating
			Element star = reviewBlock.select("i.a-icon-star").first();
			String starinfo = star.text();
			rating = Integer.parseInt(starinfo.substring(0, 1));

			// verified purchase
			Elements verified = reviewBlock.select("span.a-size-mini:contains(Verified Purchase)");

			if (verified.size() > 0) {
				verifiedPurchase = true;
			}

			// review date
			Elements date = reviewBlock.select("span.review-date");
			String datetext = date.first().text();
			datetext = datetext.substring(3); // remove "On "
			Date reviewDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(datetext);

			// review content
			Element contentDoc = reviewBlock.select("span.review-text").first();
			content = contentDoc.text();

			// implementing commnets section
			String comments1 = "";

			Review thereview = new Review(theitemID, reviewID, customerName, customerID, title, rating, fullRating, helpfulVotes, totalVotes, verifiedPurchase, realName, reviewDate, content,
					comments1);

			return thereview;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(log.getName() + " : program error: " + e);
		}
		return null;
	}

}
