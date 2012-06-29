package com.barchart.proto.buf;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.Message;

public class MessageCodec {

	private static final Logger log = LoggerFactory
			.getLogger(MessageCodec.class);

	/** from specific message type into base extension descriptor */
	private static final Map<Class<Message>, GeneratedExtension<Base, Message>> //
	messageExtensionMap = new ConcurrentHashMap<Class<Message>, GeneratedExtension<Base, Message>>();

	private static final ExtensionRegistry registry;

	static {

		registry = ExtensionRegistry.newInstance();

		prepareExtensions(registry);

	}

	private static void prepareExtensions(final ExtensionRegistry registry) {

		MessageSpec.registerAllExtensions(registry);

		/** extension name convention */
		final String prefix = Base.getDescriptor().getOptions()
				.getExtension(MessageSpec.optionExtensionPrefix);

		for (final Field field : MessageSpec.class.getDeclaredFields()) {

			final String name = field.getName();
			final Class<?> type = field.getType();

			final boolean isNameMatch = name.startsWith(prefix);
			final boolean isTypeMatch = GeneratedExtension.class
					.isAssignableFrom(type);

			final boolean isMessageExtension = isNameMatch && isTypeMatch;

			if (isMessageExtension) {

				try {

					@SuppressWarnings("unchecked")
					final GeneratedExtension<Base, Message> extension = //
					(GeneratedExtension<Base, Message>) field.get(null);

					@SuppressWarnings("unchecked")
					final Class<Message> klaz = (Class<Message>) extension
							.getMessageDefaultInstance().getClass();

					messageExtensionMap.put(klaz, extension);

				} catch (final Exception e) {
					log.error("can not prepare message extenstion", e);
				}

			}

		}

	}

	@SuppressWarnings("unchecked")
	private static <T extends Message> T castType(final Base message,
			final Class<T> klaz) throws Exception {

		final GeneratedExtension<Base, Message> extension = messageExtensionMap
				.get(klaz);

		if (extension == null) {
			throw new IllegalStateException("missing message extenstion : {}"
					+ klaz);
		}

		return (T) message.getExtension(extension);
	}

	public static Base decode(final byte[] array) throws Exception {

		return Base.newBuilder().mergeFrom(array, registry).build();

	}

	/** TODO more types */
	public static void decode(final MessageVisitor visitor, final byte[] array)
			throws Exception {

		final Base base = decode(array);

		if (!base.hasType()) {
			return;
		}

		final MessageType type = base.getType();

		switch (type) {
		case Type_MarketData:
			visitor.visit(castType(base, MarketData.class));
			break;
		case Type_MarketNews:
			visitor.visit(castType(base, MarketNews.class));
			break;

		default:
			log.error("missing message type : {}", type);
			break;
		}

		return;

	}

	/** TODO make type safe */
	public static <T extends Message> Base wrap(final MessageType messageType,
			final T message) {

		final Class<? extends Message> klaz = message.getClass();

		final GeneratedExtension<Base, Message> extension = messageExtensionMap
				.get(klaz);

		return Base.newBuilder().setType(messageType)
				.setExtension(extension, message).build();

	}

}
