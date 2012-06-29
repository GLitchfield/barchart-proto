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

	private long id1;
	private long id2;

	private final MessageVisitor visitor = new MessageVisitor.Adaptor() {

		@Override
		public void visit(final MarketData message) {
			log.debug("got MarketData {}", message);
			id1 = message.getMarketId();
		}

		@Override
		public void visit(final MarketNews message) {
			log.debug("got MarketNews {}", message);
			id2 = message.getMarketId();
		}

	};

	@Test
	public void test1() throws Exception {

		assertEquals(id1, 0);

		final MarketData message = MarketData.newBuilder().setMarketId(123)
				.build();

		final Base base = MessageCodec.wrap(message);

		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		base.writeTo(output);

		final byte[] array = output.toByteArray();

		MessageCodec.decode(visitor, array);

		assertEquals(id1, 123);

	}

	@Test
	public void test2() throws Exception {

		assertEquals(id2, 0);

		final MarketNews message = MarketNews.newBuilder().setMarketId(456)
				.build();

		final Base base = MessageCodec.wrap(message);

		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		base.writeTo(output);

		final byte[] array = output.toByteArray();

		MessageCodec.decode(visitor, array);

		assertEquals(id2, 456);

	}

	public static void main(final String... args) throws Exception {

		log.debug("init");

		MessageCodec.decode(null);

		// new TestMessageCodec().test();

		log.debug("done");

	}

}
