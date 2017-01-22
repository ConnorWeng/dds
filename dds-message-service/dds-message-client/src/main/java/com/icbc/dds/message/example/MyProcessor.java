package com.icbc.dds.message.example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.icbc.dds.message.client.consumer.IProcessor;
import com.icbc.dds.message.common.Message;

public class MyProcessor implements IProcessor {

	private final static Logger logger = Logger.getLogger(MyProcessor.class);
	
	private FileOutputStream out;
	private String path;
	
	public MyProcessor(String path) {
		this.path = path;
	}
	
	@Override
	public void process(List<Message> msgs) {
		try {
			for (Message msg : msgs) {
				out.write((new Date() + " " +msg.getMessage() + "\n").getBytes());
				logger.info("写入一条数据--" + msg.getMessage());
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		logger.info("睡10秒");
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {
		try {
			out = new FileOutputStream (path, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void close() {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			out = null;
		}
	}
}
