package org.alliancegenome.curation_api.util;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class ProcessDisplayHelper {

	private Runtime runtime = Runtime.getRuntime();
	private DecimalFormat df = new DecimalFormat("#");

	private long startTime = 0;
	private long lastTime = 0;
	private String message;
	private long lastSizeCounter = 0;
	private long totalSize;
	
	private final Semaphore sem = new Semaphore(1);
	
	private AtomicLong sizeCounter = new AtomicLong(0);
	
	private long displayTimeout = 30000; // How often to display to the console
	
	public ProcessDisplayHelper() { }
	
	public ProcessDisplayHelper(Integer displayTimeout) {
		this.displayTimeout = displayTimeout;
	}

	public void startProcess(String message) {
		startProcess(message, 0);
	}
	
	public void startProcess(String message, int totalSize) {
		this.message = message + ": ";
		this.totalSize = totalSize;
		lastSizeCounter = 0;
		startTime = new Date().getTime();
		sizeCounter = new AtomicLong(0);
		if (totalSize > 0)
			logInfoMessage(this.message + "Starting Process [total =	" + getBigNumber(totalSize) + "] " + new Date());
		else
			logInfoMessage(this.message + "Starting Process... (" + new Date() + ")");

		lastTime = new Date().getTime();
	}

	public void progressProcess() {
		progressProcess(null);
	}
	
	public void progressProcess(String data) {
		sizeCounter.getAndIncrement();

		boolean permit = sem.tryAcquire();

		if(permit) {
			Date now = new Date();
			long nowLong = now.getTime();
			
			long time = nowLong - lastTime;
	
			if (time < displayTimeout) {
				sem.release();
				return;
			}
			
			long diff = nowLong - startTime;
			checkMemory();
			
			double percent = 0;
			if (totalSize > 0) {
				percent = ((double) (sizeCounter.get()) / totalSize);
			}
			long processedAmount = (sizeCounter.get() - lastSizeCounter);
			StringBuffer sb = new StringBuffer(this.message);
			sb.append(getBigNumber(sizeCounter.get()));
			if(totalSize > 0) {
				sb.append(" of [" + getBigNumber(totalSize) + "] " + (int) (percent * 100L) + "%");
			}
			sb.append(", " + (time / 1000) + "s to process " + getBigNumber(processedAmount) + " records at " + getBigNumber((processedAmount * 1000L) / time) + "r/s");
			if(data != null) {
				sb.append(" " + data);
			}

			if (percent > 0) {
				int perms = (int) (diff / percent);
				Date end = new Date(startTime + perms);
				String expectedDuration = getHumanReadableTimeDisplay(end.getTime() - nowLong);
				sb.append(", Mem: " + df.format(memoryPercent() * 100) + "%, ETA: " + expectedDuration + " [" + end + "]");
			}
			logInfoMessage(sb.toString());
			lastSizeCounter = sizeCounter.get();
			lastTime = nowLong;
			sem.release();
		}

	}

	public void finishProcess() {
		finishProcess(null);
	}
	
	public void finishProcess(String data) {
		Date now = new Date();
		long duration = now.getTime() - startTime;
		String result = getHumanReadableTimeDisplay(duration);
		String localMessage = message + "Finished: took: " + result + " to process " + getBigNumber(sizeCounter.get());
		if (duration != 0) {
			localMessage += " records at a rate of: " + getBigNumber((sizeCounter.get() * 1000) / duration) + "r/s " + getBigNumber((sizeCounter.get() * 60000) / duration) + "r/m";
		} else {
			localMessage += " records";
		}
		
		if(data != null) {
			localMessage += " " + data;
		}
		logInfoMessage(localMessage);
	}

	private static String getBigNumber(long number) {
		return String.format("%,d", number);
	}

	public static String getHumanReadableTimeDisplay(long duration) {
		long hours = TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}


	private void checkMemory() {
		if (memoryPercent() > 0.95) {
			logWarnMessage(message + "Memory Warning: " + df.format(memoryPercent() * 100) + "%");
			logWarnMessage(message + "Used Mem: " + (runtime.totalMemory() - runtime.freeMemory()));
			logWarnMessage(message + "Free Mem: " + runtime.freeMemory());
			logWarnMessage(message + "Total Mem: " + runtime.totalMemory());
			logWarnMessage(message + "Max Memory: " + runtime.maxMemory());
		}
	}

	private double memoryPercent() {
		return ((double) runtime.totalMemory() - (double) runtime.freeMemory()) / (double) runtime.maxMemory();
	}
	
	private void logWarnMessage(String message) {
		log.warn(message);
	}
	
	private void logInfoMessage(String message) {
		log.info(message);
	}
}
