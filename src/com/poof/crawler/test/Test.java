package com.poof.crawler.test;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.poof.crawler.reviews.ReviewFetcher;
import com.poof.crawler.utils.TimeUtil;

public class Test {
	private static Logger log = Logger.getLogger(Test.class);
	public static void main(String[] args) throws IOException, ParseException {
		System.setProperty("http.maxRedirects", "50");
		System.getProperties().setProperty("proxySet", "true");
		// 如果不设置，只要代理IP和代理端口正确,此项不设置也可以
		String ip = "35.164.11.171";
		ip = "52.221.9.144";
//		ip = "52.79.97.152";
		System.getProperties().setProperty("http.proxyHost", ip);
		System.getProperties().setProperty("http.proxyPort", "8099");

		// 确定代理是否设置成功
		System.err.println(getHtml("http://www.ip138.com/ip2city.asp"));
		

		if (1 == 1)
			return;
		
		String str1 = TimeUtil.parseEnTimeZone(TimeZone.getTimeZone("PST"), 2);
		Date d1 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(str1);		//out conition
		
		String str2 = TimeUtil.parseEnTimeZone(TimeZone.getTimeZone("PST"), 511);				//页面值
		Date d2 = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse(str2);
		System.err.println(str1);
		System.err.println(str2);
		System.err.println(d1.compareTo(d2));

//		System.err.println(outcondition.substring(0,outcondition.indexOf(",")).replaceAll("[^\\d.]", ""));
		if (1 == 1)
			return;
		
		log.error(log.getName() + "program error: ");
		
		String endatetime = TimeUtil.parseEnTimeZone(TimeZone.getTimeZone("PST"), 1);
		System.err.println(endatetime);
		
		if (1 == 1)
			return;
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("PST"));
		Date d = new Date();
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, c.get(Calendar.DATE) - 1);
		System.err.println(dateFormat.format(c.getTime()));

		Date reviewDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("October 16, 2016");

		System.err.println(reviewDate.toString().substring(3, reviewDate.toString().length()));
		System.err.println(reviewDate);
		System.err.println(new SimpleDateFormat("yyyy-MM-dd").format(reviewDate));

		if (1 == 1)
			return;


	}

	private static String getHtml(String address) {
		StringBuffer html = new StringBuffer();
		String result = null;
		try {
			URL url = new URL(address);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; NT 5.1; GTB5; .NET CLR 2.0.50727; CIBA)");
			BufferedInputStream in = new BufferedInputStream(conn.getInputStream());

			try {
				String inputLine;
				byte[] buf = new byte[4096];
				int bytesRead = 0;
				while (bytesRead >= 0) {
					inputLine = new String(buf, 0, bytesRead, "ISO-8859-1");
					html.append(inputLine);
					bytesRead = in.read(buf);
					inputLine = null;
				}
				buf = null;
			} finally {
				in.close();
				conn = null;
				url = null;
			}
			result = new String(html.toString().trim().getBytes("ISO-8859-1"), "gb2312").toLowerCase();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			html = null;
		}
		return result;
	}
}
