package com.barchart.proto.fixbuf.gen;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class Util {

	public static String cleanupText(final String str) {

		final StringBuilder b = new StringBuilder(128);

		for (int i = 0; i < str.length(); ++i) {

			final char t = str.charAt(i);

			if (t == ' ')
				continue;
			if (t == '.')
				continue;

			b.append(t);

		}

		return b.toString();

	}

	static List<String> getNames(final Element element, final String path,
			final List<String> names) {
		final int separatorOffset = path.indexOf("/");
		if (separatorOffset == -1) {
			final NodeList fieldNodeList = element.getElementsByTagName(path);
			for (int i = 0; i < fieldNodeList.getLength(); i++) {
				names.add(((Element) fieldNodeList.item(i))
						.getAttribute("name"));
			}
		} else {
			final String tag = path.substring(0, separatorOffset);
			final NodeList subnodes = element.getElementsByTagName(tag);
			for (int i = 0; i < subnodes.getLength(); i++) {
				getNames((Element) subnodes.item(i),
						path.substring(separatorOffset + 1), names);
			}
		}
		return names;
	}

	static List<String> getNames(final Element element, final String path) {
		return getNames(element, path, new ArrayList<String>());
	}

}
