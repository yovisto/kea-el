package com.yovisto.kea.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yovisto.kea.commons.Parameters;

public interface IndexAccess {

	public abstract List<String> getIrisForLabel(String surfaceForm);

	public abstract void setup(Parameters params);

	public abstract List<String> getLinks(String uri);

	public abstract List<String> getLinks(String uri, HashSet<String> additionalLinks);

	public abstract Set<String> getLabelsForIRI(String iri);

	public abstract List<String> getIrisForLabel(String surfaceForm, HashSet<String> additionalSurfaces);

	public abstract void resetTraining();
	
}