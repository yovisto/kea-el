package com.yovisto.kea.commons;

import java.util.List;

/**
 * The Interface MappedTerm.
 * A mapped term has a candidate list. 
 */
public interface MappedTerm extends Term, Comparable<Term> {


	public abstract void setCandidates(List<Candidate> uris);

	public abstract List<Candidate> getCandidates();

	
	public List<MappedTerm> getMergeItems();

	public void setMergeItems(List<MappedTerm> mergeItems);
	
	public boolean isSkip();
	
	public void setSkip(boolean skip);

	public abstract void setIsMergeItem(boolean b);
	
	public boolean isMergeItem();
	
	public String toString();
		
}