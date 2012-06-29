package com.barchart.proto.fixbuf.gen;

public class Task {

	private String taskName;

	private String fixSpecification;
	private String fixTransformDirectory;
	private String outputDirectory;

	private String fixVersion;

	public String getFixVersion() {
		return fixVersion;
	}

	public void setFixVersion(final String fixVersion) {
		this.fixVersion = fixVersion;
	}

	public String getTaskName() {
		return taskName;
	}

	private String protocolPackage;

	public String getName() {
		return taskName;
	}

	public String getFixTransformDirectory() {
		return fixTransformDirectory;
	}

	public void setTaskName(final String name) {
		this.taskName = name;
	}

	public String getProtocolDirectory() {
		return protocolPackage.replace('.', '/');
	}

	public String getProtocolPackage() {
		return protocolPackage;
	}

	public void setProtocolPackage(final String messagePackage) {
		this.protocolPackage = messagePackage;
	}

	public String getOutputDirectory() {
		return outputDirectory;
	}

	public void setOutputDirectory(final String outputBaseDirectory) {
		this.outputDirectory = outputBaseDirectory;
	}

	public String getFixSpecification() {
		return fixSpecification;
	}

	public void setFixSpecification(final String specification) {
		this.fixSpecification = specification;
	}

	public void setFixTransformDirectory(final String transformDirectory) {
		this.fixTransformDirectory = transformDirectory;
	}

}
