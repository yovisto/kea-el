package com.yovisto.kea.ned.scorers;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;

public class BlacklistScorer extends AbstractScorer {

	private static final long serialVersionUID = 1442357541610118239L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		Score score = new ScoreImpl(this.getClass().getSimpleName());
	
		double result = 1.0;
		if (cand.getIri().contains("Glossary_of")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().contains("List_of") || cand.getIri().contains("Lists_of")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		
		if (cand.getIri().matches("(.*)\\((.*)album(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)band(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)song(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)film(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)name(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)surname(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)surename(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)crater(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)TV(.*)series(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)anime(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}
		if (cand.getIri().matches("(.*)\\((.*)disambiguation(.*)\\)(.*)")) {
			result = Math.min(result, 0.0);
			score.getVector().add(0.0);
		} else {
			score.getVector().add(1.0);
		}		
		score.setValue(result);
		return score;
	}

}
