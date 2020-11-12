package com.yovisto.kea.ner;

import java.util.List;

import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Term;

public interface Mapper  extends KeaComponent{
	
	public abstract List<MappedTerm> mapAllCandidates(List<Term> terms, Parameters params);

}
