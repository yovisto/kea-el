package com.yovisto.kea.commons;

import java.util.List;

public interface ScoredTerm extends MappedTerm {

	public abstract void setScoredUris(List<ScoredCandidate> uris);

	public abstract List<ScoredCandidate> getScoredCandidates();
	
	public String toString();
}