package com.yovisto.kea.ned;

import java.util.List;

import com.yovisto.kea.commons.DisambiguatedTerm;

public interface CategoryFilter {

	public List<DisambiguatedTerm> filter(List<DisambiguatedTerm> terms);

}