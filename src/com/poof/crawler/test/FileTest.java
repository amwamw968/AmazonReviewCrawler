package com.poof.crawler.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileTest {
	public static void main(String[] args) {
		BufferedReader reader = null;
		try {
			InputStreamReader isr = new InputStreamReader(FileTest.class.getResourceAsStream("/asin.txt"));

			reader = new BufferedReader(isr);
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				String[] asins = tempString.split("\t");
				System.out.print(asins[0] + "\t" + asins[1] + "\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
	}
}
