package com.yovisto.kea.ned.scorers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.ScoredCandidate;
import com.yovisto.kea.commons.ScoredTerm;
import com.yovisto.kea.ned.MaxOfMeanDisambiguator;
import com.yovisto.kea.ned.ScoreImpl;
import com.yovisto.kea.util.IndexAccess;

public class CategoryScorer extends AbstractScorer {

	private static final long serialVersionUID = 3796723448703135788L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		Score score = new ScoreImpl(this.getClass().getSimpleName());
		score.getVector().add(0.0);
		score.setValue(0.0);
		return score;
	}

	public static void addScore(List<ScoredTerm> terms, Parameters params, IndexAccess access) {
		
		String sId=null;
		for (String s : params.getStringArray(Parameters.SCORERS)){
			if (s.startsWith("CategoryScorer")){
				sId = s;
			}
		}
		
		// for each term, get the top 3 best candidates
		// for each candidate retrieve its categories
		// compare the 3 candidates categories pairwise with the other top 
		// candidates' categories
		// the more categories a candidate has in common with other candidates,
		// the higher is its score

		Map<ScoredTerm, ScoredCandidate> top1map = new HashMap<ScoredTerm, ScoredCandidate>();
		Map<ScoredTerm, List<ScoredCandidate>> top3map = new HashMap<ScoredTerm, List<ScoredCandidate>>();
		
		
		for (ScoredTerm t : terms) {

			List<ScoredCandidate> top3 = getCategories(getTop3(t.getScoredCandidates(), params), access);
			top3map.put(t, top3);
//			for (ScoredURI u : top3) {
//				L.info(t.getWords() + " " + u.getIri());
//			}

			if (top3.size() > 0) {
				top1map.put(t, top3.get(0));
			}
		}
		
		// for each candidate get the categories:
		
		for (ScoredTerm term3 : top3map.keySet()){			
			for (ScoredCandidate top3 : top3map.get(term3)) {
				//for (List<ScoredURI> l2 : top3map.values()) {
				for (ScoredTerm term1 : top1map.keySet()){		
					ScoredCandidate top1 = top1map.get(term1) ;
						if (top1 != top3 && term3!=term1) {
//							L.info(top1.getIri() + " <> " + top3.getIri());
							Set<String> intersect = new HashSet<String>(top1.getCategories());
							intersect.retainAll(top3.getCategories());
//							L.info("=" + intersect.size());
							if (intersect.size() > 0) {
								double current = top1.getScores().get(sId).getValue();
								top1.getScores().get(sId).setValue(current + 1);
								top1.getScores().get(sId).getVector().set(0, current + 1);
								
							}

						}
					}
			//	}
			}

		}

		// L.info("break here");

	}

	private static List<ScoredCandidate> getCategories(List<ScoredCandidate> top3, IndexAccess access) {

		for (ScoredCandidate uri : top3) {
			uri.setCategories(new ArrayList<String>());
			List<String> links = access.getLinks(uri.getIri());
			for (String l : links) {
				if (l.startsWith("Kategorie")) {
					uri.getCategories().add(l);

					List<String> links2 = access.getLinks(l);
					for (String l2 : links2) {
						if (l2.startsWith("Kategorie")) {
							uri.getCategories().add(l2);

							List<String> links3 = access.getLinks(l2);
							for (String l3 : links3) {
								if (l3.startsWith("Kategorie")) {
									uri.getCategories().add(l3);

								}
							}

						}
					}

				}
			}
		}
		return top3;
	}

	private static List<ScoredCandidate> getTop3(List<ScoredCandidate> scoredUris, final Parameters params) {
//		for (ScoredURI u : scoredUris) {
//			L.info("Vorher: " + u.getIri());
//		}
		Collections.sort(scoredUris, new Comparator<ScoredCandidate>() {

			@Override
			public int compare(ScoredCandidate o1, ScoredCandidate o2) {

				double sum1 = 0.0;
				for (String scorer1 : o1.getScores().keySet()) {
					sum1 = sum1 + MaxOfMeanDisambiguator.getBoost(scorer1, params)* o1.getScores().get(scorer1).getValue();
				}

				double sum2 = 0.0;
				for (String scorer2 : o2.getScores().keySet()) {
					sum2 = sum2 + MaxOfMeanDisambiguator.getBoost(scorer2, params)* o2.getScores().get(scorer2).getValue();
				}
				if (sum1 > sum2)
					return -1;
				if (sum1 < sum2)
					return 1;
				return 0;
			}
		});

//		for (ScoredURI u : scoredUris) {
//			double sum1 = 0.0;
//			for (String scorer1 : u.getScores().keySet()) {
//				sum1 = sum1 + MaxOfMeanDisambiguator.getBoost(scorer1, params) * u.getScores().get(scorer1).getValue();
//			}
//			L.info("Danach: " + u.getIri() + " " + sum1);
//		}

		List<ScoredCandidate> ret = new ArrayList<ScoredCandidate>();

		if (scoredUris.size() > 0) {
			ret.add(scoredUris.get(0));
		}
		if (scoredUris.size() > 1) {
			ret.add(scoredUris.get(1));
		}
		if (scoredUris.size() > 2) {
			ret.add(scoredUris.get(2));
		}

		return ret;

	}

}
