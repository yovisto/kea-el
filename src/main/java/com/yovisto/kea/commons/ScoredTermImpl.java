package com.yovisto.kea.commons;

import java.util.List;

public class ScoredTermImpl extends MappedTermImpl implements ScoredTerm{

	private static final long serialVersionUID = 8501438460072851549L;
	private List<ScoredCandidate> scoredUris;
	
	public ScoredTermImpl(){
		
	}
	
	public ScoredTermImpl(MappedTerm term, List<ScoredCandidate> scoredUris){
		setEndOffset(term.getEndOffset());
		setStartOffset(term.getStartOffset());
		setPosition(term.getPosition());
		setPositionLength(term.getPositionLength());
		setPositionIncrement(term.getPositionIncrement());
		setStemmedTermString(term.getStemmedTermString());
		setWords(term.getWords());
		setAnalysisType(term.getAnalysisType());
		setPartOfSpeech(term.getPartOfSpeech());
		setSurfaceForm(term.getSurfaceForm());
	 	setCandidates(term.getCandidates());
	 	this.scoredUris = scoredUris;
	}
	
	@Override
	public void setScoredUris(List<ScoredCandidate> uris) {
		this.scoredUris = uris;	
	}

	@Override
	public List<ScoredCandidate> getScoredCandidates() {
		return scoredUris;
	}

}
