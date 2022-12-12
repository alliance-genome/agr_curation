package org.alliancegenome.curation_api.util;

import java.text.DecimalFormat;
import java.util.Date;

import lombok.extern.jbosslog.JBossLog;

@JBossLog
public class DefaultProcessDisplayHandler implements ProcessDisplayHandler {

	private Runtime runtime = Runtime.getRuntime();
	private DecimalFormat df = new DecimalFormat("#");

	@Override
	public void startProcess(String message, long startTime, long totalSize) {
		if (totalSize > 0)
			logInfoMessage(message + "Starting Process [total = " + ProcessDisplayHandler.getBigNumber(totalSize) + "] " + new Date(startTime));
		else
			logInfoMessage(message + "Starting Process... (" + new Date(startTime) + ")");
	}

	@Override
	public void progressProcess(String message, String data, long startTime, long nowTime, long lastTime, long currentCount, long lastCount, long totalSize) {

		double percent = 0;
		if (totalSize > 0) {
			percent = ((double) (currentCount) / totalSize);
		}
		long processedAmount = (currentCount - lastCount);

		StringBuffer sb = new StringBuffer(message == null ? "" : message);
		sb.append(ProcessDisplayHandler.getBigNumber(currentCount));
		if (totalSize > 0) {
			sb.append(" of [" + ProcessDisplayHandler.getBigNumber(totalSize) + "] " + (int) (percent * 100L) + "%");
		}
		long time = (nowTime - lastTime);
		long diff = (nowTime - startTime);
		sb.append(", " + (time / 1000) + "s to process " + ProcessDisplayHandler.getBigNumber(processedAmount) + " records at " + ProcessDisplayHandler.getBigNumber((processedAmount * 1000L) / time)
			+ "r/s");
		if (data != null) {
			sb.append(" " + data);
		}

		checkMemory(message, data);

		if (percent > 0) {
			int perms = (int) (diff / percent);
			Date end = new Date(startTime + perms);
			String expectedDuration = ProcessDisplayHandler.getHumanReadableTimeDisplay(end.getTime() - nowTime);
			sb.append(", Mem: " + df.format(memoryPercent() * 100) + "%, ETA: " + expectedDuration + " [" + end + "]");
		}
		logInfoMessage(sb.toString());

	}

	@Override
	public void finishProcess(String message, String data, long currentCount, long totalSize, long duration) {
		String result = ProcessDisplayHandler.getHumanReadableTimeDisplay(duration);
		String localMessage = message + "Finished: took: " + result + " to process " + ProcessDisplayHandler.getBigNumber(currentCount);
		if (duration != 0) {
			localMessage += " records at a rate of: " + ProcessDisplayHandler.getBigNumber((currentCount * 1000) / duration) + "r/s "
				+ ProcessDisplayHandler.getBigNumber((currentCount * 60000) / duration) + "r/m";
		} else {
			localMessage += " records";
		}

		if (data != null) {
			localMessage += " " + data;
		}
		logInfoMessage(localMessage);

	}

	private void checkMemory(String message, String data) {
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
