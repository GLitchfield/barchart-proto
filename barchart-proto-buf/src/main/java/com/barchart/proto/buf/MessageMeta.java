package com.barchart.proto.buf;

import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.Message;

class MessageMeta {

	final MessageType type;

	final GeneratedExtension<Base, Message> extension;

	MessageMeta(final MessageType type,
			final GeneratedExtension<Base, Message> extension) {
		this.type = type;
		this.extension = extension;
	}

}
