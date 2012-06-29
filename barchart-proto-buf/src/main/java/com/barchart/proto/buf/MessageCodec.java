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

	private static final ExtensionRegistry registry;

	static {

		registry = ExtensionRegistry.newInstance();

		prepareExtensions(registry);

	}

	private static final Map<Class<Message>, GeneratedExtension<Base, Message>> //
	typeMap = new ConcurrentHashMap<Class<Message>, GeneratedExtension<Base, Message>>();

	private static void prepareExtensions(final ExtensionRegistry registry) {

		MessageSpec.registerAllExtensions(registry);

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
					final Class<Message> klaz = (Class<Message>) type;

					typeMap.put(klaz, extension);

				} catch (final Exception e) {
					log.error("can not prepare message extenstion", e);
				}

			}

		}

	}

	@SuppressWarnings("unchecked")
	private static <T extends Message> T castType(final Base message,
			final Class<T> klaz) throws Exception {

		final GeneratedExtension<Base, Message> extension = typeMap.get(klaz);

		if (extension == null) {
			log.error("missing message extenstion : {}", klaz);
		}

		return (T) message.getExtension(extension);
	}

	public static Base decode(final byte[] array) throws Exception {

		/** use the registry so extensions will be decoded */
		return Base.newBuilder().mergeFrom(array, registry).build();

	}

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

		// TODO more types

		default:
			log.error("missing message type : {}", type);
			break;
		}

		return;

	}

	public static <T extends Message> Base wrap(final MessageType messageType,
			final T message) {

		final Class<? extends Message> klaz = message.getClass();

		final GeneratedExtension<Base, Message> extension = typeMap.get(klaz);

		return Base.newBuilder().setType(messageType)
				.setExtension(extension, message).build();

	}

}
