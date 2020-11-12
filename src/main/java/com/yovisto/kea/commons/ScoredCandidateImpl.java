package com.yovisto.kea.commons;

import java.util.Map;
import java.util.TreeMap;

/**
 * The Class ScoredURIImpl.
 */
public class ScoredCandidateImpl extends CandidateImpl implements ScoredCandidate {

	private static final long serialVersionUID = 1L;

	/** The score. */
	private double totalScore;
	
	private boolean classifierAccepted;

	public boolean isClassifierAccepted() {
		return classifierAccepted;
	}

	public void setClassifierAccepted(boolean classifierAccepted) {
		this.classifierAccepted = classifierAccepted;
	}

	private Map<String, Score> scores;
	private Map<String, Score> normalizedScores;

	public ScoredCandidateImpl() {
	};

	public ScoredCandidateImpl(Candidate uri) {
		setCategories(uri.getCategories());
		setDistance(uri.getDistance());
		setDomains(uri.getDomains());
		setIri(uri.getIri());
		setLabels(uri.getLabels());
		setMainLabel(uri.getMainLabel());
		setWords(uri.getWords());
		scores = new TreeMap<String, Score>();
	}

	public Map<String, Score> getNormalizedScores() {
		return normalizedScores;
	}

	public void setNormalizedScores(Map<String, Score> normalizedScores) {
		this.normalizedScores = normalizedScores;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.services.semantic.commons.api.ScoredURI#setScore(float)
	 */
	@Override
	public void setTotalScore(double score) {
		this.totalScore = score;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.services.semantic.commons.api.ScoredURI#getScore()
	 */
	@Override
	public double getTotalScore() {
		return totalScore;
	}

	

	@Override
	public Map<String, Score> getScores() {
		return scores;
	}

	@Override
	public void setScores(Map<String, Score> scores) {
		this.scores = scores;
	}

	@Override
	public int compareTo(Candidate arg0) {
		ScoredCandidate u = (ScoredCandidate)arg0;
		if (this.getTotalScore() - u.getTotalScore() < 0.0){
			return -1 ;
		}
		if (this.getTotalScore() - u.getTotalScore() > 0.0){
			return 1 ;
		}		
		return 0;
	}

}
