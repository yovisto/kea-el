package com.yovisto.kea.ned;

import java.util.List;

import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.ScoredTerm;

public interface Normalizer extends KeaComponent {

	public abstract List<ScoredTerm> normalize(List<ScoredTerm> scoredTerms, Parameters params);

}
