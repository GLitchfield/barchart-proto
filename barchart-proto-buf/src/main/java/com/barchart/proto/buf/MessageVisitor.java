package com.barchart.proto.buf;

public interface MessageVisitor {

	void visit(MarketData message);

	void visit(MarketNews message);

	// TODO more types

	//

	class Adaptor implements MessageVisitor {

		@Override
		public void visit(final MarketData message) {
		}

		@Override
		public void visit(final MarketNews message) {
		}

	}

}
