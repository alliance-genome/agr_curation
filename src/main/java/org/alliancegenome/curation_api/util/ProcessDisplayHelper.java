package org.alliancegenome.curation_api.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

public class ProcessDisplayHelper {

	private long startTime = 0;
	private long lastTime = 0;
	private String message;
	private long lastSizeCounter = 0;
	private long totalSize;
	
	private final Semaphore sem = new Semaphore(1);
	
	private AtomicLong sizeCounter = new AtomicLong(0);
	
	private long displayTimeout = 30000; // How often to display to the console

	private List<ProcessDisplayHandler> handlers;
	
	public ProcessDisplayHelper() {
		this(30000, new DefaultProcessDisplayHandler());
	}
	
	public ProcessDisplayHelper(Integer displayTimeout) {
		this(displayTimeout, new DefaultProcessDisplayHandler());
	}
	
	public ProcessDisplayHelper(Integer displayTimeout, ProcessDisplayHandler handler) {
		this(displayTimeout, new ArrayList<>() {{ add(handler); }});
	}
	
	public ProcessDisplayHelper(Integer displayTimeout, List<ProcessDisplayHandler> handlers) {
		this.displayTimeout = displayTimeout;
		this.handlers = handlers;
	}

	public void startProcess(String message) {
		startProcess(message, 0);
	}
	
	public void startProcess(String message, long totalSize) {
		this.message = message + ": ";
		this.totalSize = totalSize;
		lastSizeCounter = 0;
		startTime = new Date().getTime();
		sizeCounter = new AtomicLong(0);
		if(handlers != null && handlers.size() > 0) {
			for(ProcessDisplayHandler handler: handlers) {
				handler.startProcess(this.message, startTime, totalSize);
			}
		}
		lastTime = new Date().getTime();
	}

	public void progressProcess() {
		progressProcess(null);
	}
	
	public void progressProcess(String data) {
		sizeCounter.getAndIncrement();

		boolean permit = sem.tryAcquire();

		if(permit) {
			Date nowTime = new Date();
			long nowLong = nowTime.getTime();
			
			if ((nowLong - lastTime) < displayTimeout) {
				sem.release();
				return;
			}

			if(handlers != null && handlers.size() > 0) {
				for(ProcessDisplayHandler handler: handlers) {
					handler.progressProcess(this.message, data, 
						startTime, nowLong, lastTime,
						sizeCounter.get(), lastSizeCounter, totalSize);
				}
			}

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
		
		if(handlers != null && handlers.size() > 0) {
			for(ProcessDisplayHandler handler: handlers) {
				handler.finishProcess(this.message, data, sizeCounter.get(), sizeCounter.get(), duration);
			}
		}
	}

	public void addDisplayHandler(ProcessDisplayHandler handler) {
		handlers.add(handler);
	}

	
}
