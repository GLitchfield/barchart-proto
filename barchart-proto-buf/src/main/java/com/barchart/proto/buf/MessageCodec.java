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

	/** from specific message type into type/extension */
	private static final Map<Class<Message>, MessageMeta> //
	messageMetaMap = new ConcurrentHashMap<Class<Message>, MessageMeta>();

	private static final ExtensionRegistry registry;

	static {

		registry = ExtensionRegistry.newInstance();

		prepareExtensions(registry);

	}

	/**
	 * note: 1) extension name contract; 2) enum name contract; 3) enum +
	 * extension number contract
	 */
	private static void prepareExtensions(final ExtensionRegistry registry) {

		MessageSpec.registerAllExtensions(registry);

		final String prefix = Base.getDescriptor().getOptions()
				.getExtension(MessageSpec.optionExtensionPrefix);

		for (final Field field : MessageSpec.class.getDeclaredFields()) {

			final String fieldName = field.getName();
			final Class<?> fieldType = field.getType();

			final boolean isNameMatch = fieldName.startsWith(prefix);
			final boolean isTypeMatch = GeneratedExtension.class
					.isAssignableFrom(fieldType);

			final boolean isMessageExtension = isNameMatch && isTypeMatch;

			if (isMessageExtension) {

				try {

					@SuppressWarnings("unchecked")
					final GeneratedExtension<Base, Message> extension = //
					(GeneratedExtension<Base, Message>) field.get(null);

					@SuppressWarnings("unchecked")
					final Class<Message> klaz = (Class<Message>) extension
							.getMessageDefaultInstance().getClass();

					final int number = extension.getDescriptor().getNumber();

					final MessageType type = MessageType.valueOf(number);

					final MessageMeta meta = new MessageMeta(type, extension);

					messageMetaMap.put(klaz, meta);

				} catch (final Exception e) {
					log.error("can not prepare message extenstion", e);
				}

			}

		}

	}

	@SuppressWarnings("unchecked")
	private static <T extends Message> T castType(final Base message,
			final Class<T> klaz) throws Exception {

		final GeneratedExtension<Base, Message> extension = messageMetaMap
				.get(klaz).extension;

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
		case MarketDataType:
			visitor.visit(castType(base, MarketData.class));
			break;
		case MarketNewsType:
			visitor.visit(castType(base, MarketNews.class));
			break;

		default:
			log.error("missing message type : {}", type);
			break;
		}

		return;

	}

	public static <T extends Message> Base wrap(final T message) {

		final Class<? extends Message> klaz = message.getClass();

		final MessageMeta meta = messageMetaMap.get(klaz);

		return Base.newBuilder().setType(meta.type)
				.setExtension(meta.extension, message).build();

	}

}
