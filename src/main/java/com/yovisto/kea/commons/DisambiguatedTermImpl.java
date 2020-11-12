package com.yovisto.kea.commons;

import java.util.Set;

/**
 * The DisambiguatedTerm implementation.
 */
public class DisambiguatedTermImpl extends ScoredTermImpl implements
		DisambiguatedTerm {

	private static final long serialVersionUID = 1L;

	/** The Disambiguation Score. */
	private double score;
	private boolean accepted;

	
	private Candidate cand;
	
	public DisambiguatedTermImpl(){
		
	}
	
	public DisambiguatedTermImpl(ScoredTerm term, ScoredCandidate cand, boolean accepted) {
		setEndOffset(term.getEndOffset());
		setStartOffset(term.getStartOffset());
		setPosition(term.getPosition());
		setPositionLength(term.getPositionLength());
		setPositionIncrement(term.getPositionIncrement());
		setStemmedTermString(term.getStemmedTermString());
		setWords(term.getWords());
		setAnalysisType(term.getAnalysisType());
		setPartOfSpeech(term.getPartOfSpeech());
		setNamedEntityType(term.getNamedEntityType());
		setSurfaceForm(term.getSurfaceForm());
	 	setCandidates(term.getCandidates());
	 	setScoredUris(term.getScoredCandidates());
	 	this.cand = cand;
	 	this.accepted = accepted;
	 	this.score = cand.getTotalScore();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yovisto.services.semantic.commons.DisambiguatedTerm#setUri(com.yovisto
	 * .services.semantic.commons.api.URI)
	 */
	@Override
	public void setCandidate(Candidate uri) {
		this.cand = uri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.services.semantic.commons.DisambiguatedTerm#getUri()
	 */
	@Override
	public Candidate getCandidate() {
		return cand;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yovisto.services.semantic.commons.api.DisambiguatedTerm#setScore(
	 * double)
	 */
	@Override
	public void setScore(double score) {
		this.score = score;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yovisto.services.semantic.commons.api.DisambiguatedTerm#getScore()
	 */
	@Override
	public double getScore() {
		return score;
	}

	@Override
	public int compareTo(Term disambiguatedTerm) {
		if (this.getStartOffset() > disambiguatedTerm.getStartOffset()) {
			return -1;
		}
		if (this.getStartOffset() < disambiguatedTerm.getStartOffset()) {
			return 1;
		}
		return 0;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	private Set<String> importantCategories;
	

	public Set<String> getEssentialCategories() {
		return importantCategories;
	}

	@Override
	public void setEssentialCategories(Set<String> importantCats) {
		this.importantCategories=importantCats;
		
	}
}
