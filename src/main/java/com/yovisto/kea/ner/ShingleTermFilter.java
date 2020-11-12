package com.yovisto.kea.ner;

import java.util.ArrayList;
import java.util.List;

import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Term;

public class ShingleTermFilter implements TermFilter {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9177146345241736094L;

	@Override
	public List<Term> filter(List<Term> terms, Parameters params) {
		// for every shingle:
		// check if contains person or shinglesize < 3
		List<Term> result = new ArrayList<Term>();
		for (Term t : terms) {
			if (t.getNamedEntityType()!=null && (t.getNamedEntityType().contains("PERSON") ||  t.getNamedEntityType().contains("ORGANIZATION") || t.getNamedEntityType().contains("LOCATION") || t.getPositionLength() <= params.getInt(Parameters.NUM_SHINGLES_FILTER))) {
				result.add(t);
			}
		}
		return result;
	}

}
