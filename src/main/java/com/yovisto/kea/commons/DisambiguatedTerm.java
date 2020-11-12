package com.yovisto.kea.commons;

import java.util.Set;

/**
 * The Interface DisambiguatedTerm. A disambiguated term is a term with exactly
 * one URI representing the semantic entity.
 */
public interface DisambiguatedTerm extends ScoredTerm, Comparable<Term> {

	/**
	 * Sets the uri.
	 * 
	 * @param uri
	 *            the new uri
	 */
	public abstract void setCandidate(Candidate uri);

	/**
	 * Gets the uri.
	 * 
	 * @return the uri
	 */
	public abstract Candidate getCandidate();

	/**
	 * Gets the Score.
	 * 
	 * @return the score
	 */
	public abstract double getScore();

	/**
	 * Sets the score.
	 * 
	 * @param the
	 *            disambiguation score
	 */
	public abstract void setScore(double score);


	public boolean isAccepted();

	public void setAccepted(boolean accepted);

	public abstract void setEssentialCategories(Set<String> importantCats);
	
	public abstract Set<String> getEssentialCategories();
}