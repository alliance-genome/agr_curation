package org.alliancegenome.curation_api.util;

import java.util.concurrent.TimeUnit;

public interface ProcessDisplayHandler {

	public static String getBigNumber(long number) {
		return String.format("%,d", number);
	}

	public static String getHumanReadableTimeDisplay(long duration) {
		long hours = TimeUnit.MILLISECONDS.toHours(duration) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}

	public void startProcess(String message, long startTime, long totalSize);
	public void progressProcess(String message, String data, long startTime, long nowTime, long lastTime, long currentCount, long lastCount, long totalSize);
	public void finishProcess(String message, String data, long currentCount, long totalSize, long duration);
}
