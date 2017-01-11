package com.poof.crawler.test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.poof.crawler.reviews.Item;

public class TestTimer {
	static int count = 0;

	public static void showTimer() {
		final ExecutorService pool = Executors.newFixedThreadPool(2);
		final ExecutorCompletionService<String> completionService = new ExecutorCompletionService<String>(pool);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				for (int i = 0; i < 10; i++) {
					completionService.submit(new Callable() {
						@Override
						public Object call() throws Exception {
							System.err.println("ok...");
							TimeUnit.SECONDS.sleep(1);
							return null;
						}
					});
				}
			}
		};

		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(year, month, day, 10, 00, 00);
		Date date = calendar.getTime();
		Timer timer = new Timer();
		timer.schedule(task, date, 20 * 1000);
	}

	public static void main(String[] args) {
		showTimer();
	}
}