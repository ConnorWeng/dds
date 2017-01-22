package com.icbc.dds.message.example;

import java.util.List;

import com.icbc.dds.message.client.consumer.IProcessor;
import com.icbc.dds.message.common.Message;

public class NullProcessor implements IProcessor {
	@Override
	public void process(List<Message> msgs) {
	}

	@Override
	public void init() {
	}

	@Override
	public void close() {
	}
}
