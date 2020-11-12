package com.yovisto.kea.ned;

import java.util.List;

import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.MappedTerm;

public interface CoreferenceResolver {

	public List<MappedTerm> getCorefCandidateTerms(List<MappedTerm> terms);
	
	public List<DisambiguatedTerm> resolve(List<DisambiguatedTerm> terms, List<MappedTerm> corefCandidates);

}