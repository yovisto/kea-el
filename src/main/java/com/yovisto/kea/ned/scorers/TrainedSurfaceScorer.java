package com.yovisto.kea.ned.scorers;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;

public class TrainedSurfaceScorer extends AbstractScorer {

	private static final long serialVersionUID = 1L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		String needle = mappedTerm.getSurfaceForm().toLowerCase() + "\t" + cand.getIri();

		Score score = new ScoreImpl(this.getClass().getName());
		if (params.getAdditionalSurfaces()!=null && params.getAdditionalSurfaces().contains(needle)) {
			score.setValue(1.0);
			score.getVector().add(1.0);
		} else {
			score.setValue(0.0);
			score.getVector().add(0.0);
		}

		return score;
	}

}
