package com.barchart.proto.buf;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMessageCodec {

	private static final Logger log = LoggerFactory
			.getLogger(TestMessageCodec.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {

		final MarketData loginRequest = MarketData.newBuilder()
				.setMarketId(123).build();

		final Base base = MessageCodec.wrap(MessageType.Type_LoginRequest,
				loginRequest);

		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		base.writeTo(output);

		final byte[] array = output.toByteArray();

		final MessageVisitor visitor = new MessageVisitor() {

			@Override
			public void visit(final MarketData message) {

				log.debug("got MarketData {}", message);

				assertEquals(message.getMarketId(), 123);

			}

			@Override
			public void visit(final MarketNews message) {

			}

		};

		MessageCodec.decode(visitor, array);

	}
}
