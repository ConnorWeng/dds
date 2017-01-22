package com.icbc.dds.message.client.consumer;

import java.util.List;

import com.icbc.dds.message.common.Message;

//@FunctionalInterface
public interface IProcessor {
	public void process(List<Message> messages);
	public void init();
	public void close();
}
