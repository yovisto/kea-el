package com.yovisto.kea.commons;

import java.util.Map;

/**
 * The Interface ScoredURI.
 */
public interface ScoredCandidate extends Candidate {

	/**
	 * Gets the score.
	 * 
	 * @return the score
	 */
	public abstract double getTotalScore();

	/**
	 * Sets the score.
	 * 
	 * @param score
	 *            the new score
	 */
	public abstract void setTotalScore(double score);

	public Map<String, Score> getScores();

	public void setScores(Map<String, Score> scores);

//	public Map<String, Score> getNormalizedScores();
//
//	public void setNormalizedScores(Map<String, Score> scores);
	
	public boolean isClassifierAccepted();
	
	public void setClassifierAccepted(boolean accepted);
}
