package com.yovisto.kea.ned;

import java.util.List;

import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.ScoredTerm;

public interface Disambiguator extends KeaComponent {

	public abstract List<DisambiguatedTerm> disambiguate(List<ScoredTerm> scoredterms, Parameters params) throws Exception;

}
