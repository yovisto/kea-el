package com.yovisto.kea.ned;

import java.util.List;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;

public interface GraphGenerator {

	public abstract Context createGraphContext(List<MappedTerm> terms, Parameters params);

}