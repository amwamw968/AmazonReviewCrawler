package com.poof.crawler.test;

import java.io.IOException;
import java.sql.SQLException;

import com.poof.crawler.db.DBUtil;

public class SqlTest {
	public static void main(String[] args) {
		try {
			DBUtil.execute(DBUtil.openConnection(), "insert into bz_reviews (content) value('Very nice carabiner and light will order again soon😄😆😊☺')");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
