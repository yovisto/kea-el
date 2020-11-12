package com.yovisto.kea.commons;

import java.util.HashSet;

import org.apache.commons.configuration.XMLConfiguration;

public class Parameters extends XMLConfiguration{

	private static final long serialVersionUID = -7050905072685420142L;
	public static final String NUM_SHINGLES = "numberOfShingles";
	public static final String LANGUAGE = "language";
	public static final String EXPLAIN_MERGING = "explainMerging";
	public static final String EXPLAIN_ANALYSIS = "explainAnalysis";
	public static final String NO_MERGING = "noMerging";
	public static final String NO_TOKENIZING = "noTokenizing";
	public static final String SCORERS = "scorers";
	public static final String NUM_SHINGLES_FILTER = "numberOfShinglesFilter";
	public static String DATA_PATH;

	
	private HashSet<String> additionalLinks ;
	private HashSet<String> additionalSurfaces;
	
	public synchronized HashSet<String> getAdditionalLinks() {
		return additionalLinks;
	}
	public synchronized void setAdditionalLinks(HashSet<String> additionalLinks) {
		this.additionalLinks = additionalLinks;
	}
	public synchronized HashSet<String> getAdditionalSurfaces() {
		return additionalSurfaces;
	}
	public synchronized void setAdditionalSurfaces(HashSet<String> additionalSurfaces) {
		this.additionalSurfaces = additionalSurfaces;
	}
}
