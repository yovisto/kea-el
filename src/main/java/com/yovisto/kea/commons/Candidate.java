package com.yovisto.kea.commons;

import java.io.Serializable;
import java.util.List;

/**
 * The Interface URI.
 */
public interface Candidate  extends Serializable, Comparable<Candidate>{

	/**
	 * Sets the iri.
	 *
	 * @param iri the new iri
	 */
	public abstract void setIri(String iri);

	/**
	 * Gets the iri.
	 *
	 * @return the iri
	 */
	public abstract String getIri();

	/**
	 * Sets the labels.
	 *
	 * @param labels the new labels
	 */
	public abstract void setLabels(List<Label> labels);

	/**
	 * Gets the labels.
	 *
	 * @return the labels
	 */
	public abstract List<Label> getLabels();

	/**
	 * Gets the words
	 * 
	 * @return the words
	 */
	
	public abstract List<Word> getWords();
	
	/**
	 * Sets the words
	 * 
	 * @param words the new words
	 */
	
	public abstract void setWords(List<Word> words);

	/**
	 * Sets the distance of term to uri's label
	 * 
	 * @param distance
	 */
	public abstract void setDistance(Double distance);

	/**
	 * Returns the distance of the term to the uri's label
	 * 
	 * @return the distance as double
	 */
	public abstract Double getDistance();

	/**
	 * Sets the categories of the URI
	 * 
	 * @param categories
	 */
	public abstract void setCategories(List<String> categories);
	
	/**
	 * Returns the list of categories for the URI
	 * 
	 * @return list of categories
	 */
	public abstract List<String> getCategories();

	public abstract void setDomains(List<String> domains);

	public abstract List<String> getDomains();

	public abstract void addDomain(String domain);
	
	public abstract void setMainLabel(String mainLabel);
	
	public abstract String getMainLabel();
	
	public abstract String getProvenance();
	public abstract void setProvenance(String provenance);

}