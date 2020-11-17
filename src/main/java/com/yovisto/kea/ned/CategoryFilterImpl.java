package com.yovisto.kea.ned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.util.IndexAccess;

public class CategoryFilterImpl implements CategoryFilter {

	protected final Logger L = Logger.getLogger(getClass());
	
	@Inject
	private IndexAccess access;

	@Override
	public List<DisambiguatedTerm> filter(List<DisambiguatedTerm> terms) {
		
		Set<String> importantCats = new HashSet<String>();

		List<DisambiguatedTerm> filtered = new ArrayList<DisambiguatedTerm>();
		
		// collect categories for each term:
		Map<DisambiguatedTerm, Set<String>> cats = new HashMap<DisambiguatedTerm, Set<String>>();

		for (DisambiguatedTerm t : terms) {
			Set<String> tcats = new HashSet<String>();
			if (t.getCandidate().getCategories()==null) {

				List<String> links = access.getLinks(t.getCandidate().getIri());

				// TODO: this is too German 
				for (String l : links) {
					if (l.startsWith("Kategorie")) {

						tcats.add(l);

						List<String> links2 = access.getLinks(l);
						for (String l2 : links2) {
							if (l2.startsWith("Kategorie")) {

								tcats.add(l2);

								//List<String> links3 = access.getLinks(l2);
								//for (String l3 : links3) {
								//	if (l3.startsWith("Kategorie")) {

								//		tcats.add(l3);
								//	}
								//}
							}
						}
					}
				}

			} else {
				tcats.addAll(t.getCandidate().getCategories());
			}
			cats.put(t, tcats);
			//L.info(t.getCandidate().getIri() + " " + tcats.size());
			//for (String cat : tcats) {
			//	L.info("  " + cat);
			//}
		}

		//L.info(cats);

		// pairwise Compare
		Map<DisambiguatedTerm, Integer> scores = new HashMap<DisambiguatedTerm, Integer>();

		for (DisambiguatedTerm key1 : cats.keySet()) {
			scores.put(key1, 0);
			for (DisambiguatedTerm key2 : cats.keySet()) {
				if (!key1.getCandidate().getIri().equals(key2.getCandidate().getIri())) {

					Set<String> intersect = new HashSet<String>(cats.get(key1));
					intersect.retainAll(cats.get(key2));
					// L.info(intersect.size());
					if (intersect.size() > 0) {
						scores.put(key1, scores.get(key1) + 1);
						importantCats.addAll(intersect);
						
					}

				}
			}
		}
		double sum = 0.0;
		double num = 0.0;
		for (DisambiguatedTerm t : scores.keySet()){
			sum = sum + scores.get(t);
			num = num + 1;
		}
		double mean = 0.0 ;
		if (num > 1) mean = sum/num;    // num=1 should always survive 
		for (DisambiguatedTerm t : scores.keySet()){
			//L.info(t.getCandidate().getIri() + "=" + scores.get(t) + " " + mean);
			if (scores.get(t) >= mean * 0.7){     //   >=    0.0==0.0  should also survive  (e.g. only one item without category) 
				filtered.add(t);				
				t.setEssentialCategories(importantCats);
			}
		}
		//L.info(importantCats);
		return filtered;
	}

}
