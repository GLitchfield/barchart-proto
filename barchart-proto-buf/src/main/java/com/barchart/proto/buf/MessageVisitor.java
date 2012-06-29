package com.barchart.proto.buf;

/** TODO more types */
public interface MessageVisitor {

	void visit(MarketData message);

	void visit(MarketNews message);

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
