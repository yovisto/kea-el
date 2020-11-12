package com.yovisto.kea.commons;

import java.io.Serializable;
import java.util.List;
import java.util.Set;



import org.ahocorasick.trie.Trie;

import com.yovisto.kea.ned.GraphElements;

/**
 * The Interface Context.
 * A context is used to disambiguate a given term.
 * The context includes multiple terms (provided as list). 
 */
public interface Context  extends Serializable{

	/**
	 * Sets the terms.
	 *
	 * @param terms the new terms
	 */
	public abstract void setTerms(List<? extends Term> terms);

	/**
	 * Gets the terms.
	 *
	 * @return the terms
	 */
	public abstract List<? extends Term> getTerms();
	

	public abstract GraphElements getGraphElements();
	
	public abstract void setGraphElements(GraphElements graphElements);
	
	public abstract void setContextId(String contextId);
	public abstract String getContextId();
	
	public abstract Set<String> getContextCandidatesUniqSurfaceForms();
	
	public abstract Trie getContextCandidateSurfaceFormsAsTrie();
}