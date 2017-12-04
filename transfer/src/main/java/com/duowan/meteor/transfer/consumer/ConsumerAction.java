package com.duowan.meteor.transfer.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.duowan.meteor.transfer.tool.ServiceConfigTool;

public abstract class ConsumerAction {

	private int batchMaxSize;
	private long batchIntervalMilli;
	private LinkedBlockingQueue<byte[]> queue = new LinkedBlockingQueue<byte[]>();

	public ConsumerAction(int batchMaxSize, long batchIntervalMilli) {
		super();
		this.batchMaxSize = batchMaxSize;
		this.batchIntervalMilli = batchIntervalMilli;
		startup();
	}

	public void startup() {
		ServiceConfigTool.threadExecutor.submit(new Runnable() {
			@Override
			public void run() {
				List<byte[]> msgList = new ArrayList<byte[]>();
				long startTime = System.currentTimeMillis();
				byte[] msg = null;
				while (true) {
					try {
						msg = queue.poll(1000, TimeUnit.MILLISECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (msg != null) {
						msgList.add(msg);
					}
					long timeInteval = System.currentTimeMillis() - startTime;
					if (msgList.size() >= batchMaxSize || timeInteval > batchIntervalMilli) {
						final List<byte[]> msgs = msgList;
						ServiceConfigTool.threadExecutor.submit(new Runnable() {
							@Override
							public void run() {
								if(msgs!=null && msgs.size()>0) {
									exec(msgs);
								}
							}
						});
						startTime = System.currentTimeMillis();
						msgList = new ArrayList<byte[]>();
					}
				}
			}
		});

	}

	public abstract void exec(List<byte[]> msgList);

	public LinkedBlockingQueue<byte[]> getQueue() {
		return queue;
	}
}
