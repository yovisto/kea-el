package com.yovisto.kea.commons;

import java.util.ArrayList;
import java.util.List;

/**
 * The MappedTerm implementation.
 */
public class MappedTermImpl extends TermImpl implements MappedTerm {

	private static final long serialVersionUID = 1L;


	/** The candidate uris. */
	private List<Candidate> candidateUris;
	private List<MappedTerm> mergeItems = new ArrayList<MappedTerm>();
	private boolean skip;
	private boolean isMergeItem;

	
	
	public MappedTermImpl() {

	}

	public MappedTermImpl(Term term, List<Candidate> candidateUris) {
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
	
		setPropertyCandidates(term.getPropertyCandidates());
		this.candidateUris = candidateUris;
		for (Candidate uri : candidateUris){
			uri.setWords(term.getWords());
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yovisto.services.semantic.commons.MappedTerm#setUris(java.util.List)
	 */
	@Override
	public void setCandidates(List<Candidate> uris) {
		this.candidateUris = uris;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.services.semantic.commons.MappedTerm#getUris()
	 */
	@Override
	public List<Candidate> getCandidates() {
		return candidateUris;
	}



	public List<MappedTerm> getMergeItems() {
		return mergeItems;
	}

	public void setMergeItems(List<MappedTerm> mergeItems) {
		this.mergeItems = mergeItems;
	}

	public boolean isSkip() {
		return skip;
	}

	public void setSkip(boolean skip) {
		this.skip = skip;
	}

	public boolean isMergeItem() {
		return isMergeItem;
	}

	public void setIsMergeItem(boolean isMergeItem) {
		this.isMergeItem = isMergeItem;
	}

	public String toString() {
		return getPosition() + " " +getPositionIncrement() + " " + getPositionLength() + " " + getAnalysisType() + " [" + getSurfaceForm() + "] " + getPartOfSpeech() + " t:" + getNamedEntityType() + " s:" + skip + " mi:" + isMergeItem + " c:" + (getCandidates() == null ? "-" : getCandidates().size() + " m:" + (mergeItems == null ? "-" : mergeItems.size()));
	}

	
}
