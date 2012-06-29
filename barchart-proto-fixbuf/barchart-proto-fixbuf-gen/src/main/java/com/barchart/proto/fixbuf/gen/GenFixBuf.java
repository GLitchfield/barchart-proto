package com.barchart.proto.fixbuf.gen;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class GenFixBuf extends Gen {

	static final String NAME_PACKAGE = "com.barchart.proto.fixbuf";
	static final String NAME_FACTORY = "Factory";

	private void makeFactoryProto(final Task task) throws Exception {

		final Map<String, String> params = new HashMap<String, String>();

		final String version = task.getFixVersion();

		params.put("java_package", NAME_PACKAGE + "." + version);

		params.put("java_outer_classname", NAME_FACTORY + "_" + version);

		makeProtoFile(task, NAME_FACTORY, params);

	}

	public void run(final Task task) throws Exception {

		makeFactoryProto(task);

	}

	public static void main(final String[] args) throws Exception {

		if (args.length != 3) {

			final String getClassName = GenFixBuf.class.getName();

			log.error("usage: " + getClassName
					+ " fixSpecificationDir fixTransformDir outputDir");

			return;

		}

		final GenFixBuf generator = new GenFixBuf();

		final String fixSpecificationDir = args[0];
		final String fixTransformDir = args[1];
		final String outputDir = args[2];

		final String[] versionArray = new String[] { "FIXT 1.1", "FIX 5.0",
				"FIX 4.4", "FIX 4.3", "FIX 4.2", "FIX 4.1", "FIX 4.0" };

		//

		for (int index = 0; index < versionArray.length; ++index) {

			final Task task = new Task();

			final String version = Util.cleanupText(versionArray[index]);

			task.setTaskName(version);
			task.setFixVersion(version);

			task.setFixSpecification(fixSpecificationDir + "/" + version
					+ ".xml");

			task.setFixTransformDirectory(fixTransformDir);

			task.setProtocolPackage(NAME_PACKAGE + "." + version.toLowerCase());

			task.setOutputDirectory(outputDir);

			generator.run(task);

		}

	}

}
