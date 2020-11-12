package com.yovisto.kea.ned.scorers;

import java.util.Map;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;

public class GraphScorer extends AbstractScorer{

	private static final long serialVersionUID = 2215007014658519580L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		
		Score score = new ScoreImpl(this.getClass().getSimpleName());
		
		String uriString = cand.getIri();
		
		Map<String, Double> uriStats = context.getGraphElements().getStatistics().get(uriString);
						
		score.getVector().add(uriStats.get("componentSize"));
		score.getVector().add(uriStats.get("vertexDegree"));
		score.getVector().add(uriStats.get("stringDistance"));
		score.getVector().add(uriStats.get("purity"));
		score.getVector().add(uriStats.get("max1"));
		
		if (uriStats.get("numInterpretationScore")!=null)
			score.getVector().add(uriStats.get("numInterpretationScore"));
		
		if (uriStats.get("interpretationFreq")!=null)
			score.getVector().add(uriStats.get("interpretationFreq"));
		
		score.setValue(uriStats.get("aggregated"));
		
		return score;
	}
}
