package com.yovisto.kea.ned.scorers;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;

public class ProvenanceScorer extends AbstractScorer{

	private static final long serialVersionUID = 1442357541610118239L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		Score score = new ScoreImpl(this.getClass().getSimpleName());

		double result = 0.0;
		if (cand.getProvenance().contains("disambiguate")){
			result = result + 0.5;
			score.getVector().add(1.0);
		}else{
			score.getVector().add(0.0);
		}
		if (cand.getProvenance().contains("redirect")){
			result = result + 0.5;
			score.getVector().add(1.0);
		}else{
			score.getVector().add(0.0);
		}
			
		score.setValue(result);
		return score;
	}

}
