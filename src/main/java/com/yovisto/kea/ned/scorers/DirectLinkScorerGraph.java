package com.yovisto.kea.ned.scorers;

import java.util.Map;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;

public class DirectLinkScorerGraph extends AbstractScorer{

	private static final long serialVersionUID = 2215007014658519580L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		
		Score score = new ScoreImpl(this.getClass().getSimpleName());		
		
		Map<String, Double> uriStats = context.getGraphElements().getStatistics().get(cand.getIri());
		
		score.getVector().add(uriStats.get("numberOfLinkedTerms"));
		score.getVector().add(uriStats.get("totalNumberOfTerms"));
		score.getVector().add(uriStats.get("basicDegree"));
		
		score.setValue(uriStats.get("numberOfLinkedTerms")/uriStats.get("totalNumberOfTerms") * uriStats.get("basicDegree"));
		
		return score;
	}
}
