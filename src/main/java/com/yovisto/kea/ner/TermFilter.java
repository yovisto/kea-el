package com.yovisto.kea.ner;

import java.util.List;

import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Term;

public interface TermFilter extends KeaComponent {

	public List<Term> filter(List<Term> terms, Parameters params);
	
}
