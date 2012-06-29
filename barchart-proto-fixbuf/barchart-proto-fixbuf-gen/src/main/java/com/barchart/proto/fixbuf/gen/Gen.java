package com.barchart.proto.fixbuf.gen;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class Gen {

	protected final static Logger log = LoggerFactory.getLogger(Gen.class);

	protected final Map<String, Document> specMap = new HashMap<String, Document>();

	private Transformer createTransform(final Task task,
			final String transformFile) throws Exception {

		final StreamSource styleSource = new StreamSource(new File(
				task.getFixTransformDirectory() + "/" + transformFile));

		final TransformerFactory transformerFactory = TransformerFactory
				.newInstance();

		final Transformer transformer = transformerFactory
				.newTransformer(styleSource);

		return transformer;

	}

	private Document getSpecification(final Task task) throws Exception {

		Document document = specMap.get(task.getName());

		if (document == null) {

			final DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();

			final DocumentBuilder builder = factory.newDocumentBuilder();

			document = builder.parse(new File(task.getFixSpecification()));

			specMap.put(task.getName(), document);

		}

		return document;

	}

	protected void makeProtoFile(final Task task, final String className,
			final Map<String, String> params) throws Exception {

		log.debug("generating " + className + " for " + task.getName());

		final Document document = getSpecification(task);

		final String source = className + ".xsl";

		final String target = task.getOutputDirectory() + "/" + //
				className + "_" + task.getFixVersion() + ".proto";

		saveProtoFile(task, document, params, target,
				createTransform(task, source));

	}

	private void saveProtoFile(final Task task, final Document document,
			final Map<String, String> parameters, final String outputFileName,
			final Transformer transformer) throws Exception {

		if (parameters != null) {

			final Iterator<Map.Entry<String, String>> paramItr = parameters
					.entrySet().iterator();

			while (paramItr.hasNext()) {

				final Map.Entry<String, String> entry = paramItr.next();

				transformer.setParameter(entry.getKey(), entry.getValue());

			}

		}

		final File out = new File(outputFileName);

		if (!out.getParentFile().exists()) {
			out.getParentFile().mkdirs();
		}

		final File outputFile = new File(outputFileName);

		final DOMSource source = new DOMSource(document);

		final StreamResult result = new StreamResult(new FileOutputStream(
				outputFile));

		transformer.transform(source, result);

	}

}
