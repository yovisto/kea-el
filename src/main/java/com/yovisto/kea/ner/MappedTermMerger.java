package com.yovisto.kea.ner;

import java.util.List;

import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;

public interface MappedTermMerger  extends KeaComponent{

	public abstract List<MappedTerm> merge( List<MappedTerm> terms, Parameters params);
}
