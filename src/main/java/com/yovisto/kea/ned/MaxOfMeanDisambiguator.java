package com.yovisto.kea.ned;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.DisambiguatedTermImpl;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.ScoredTerm;
import com.yovisto.kea.commons.ScoredCandidate;

import junit.framework.Assert;

/**
 * Maximum of sum of vector scores disambiguator.
 * 
 * 
 */
public class MaxOfMeanDisambiguator implements Disambiguator {

	private static final long serialVersionUID = -5600242223517382533L;
	
	protected final Logger L = Logger.getLogger(getClass());

	@Override
	public List<DisambiguatedTerm> disambiguate(List<ScoredTerm> scoredterms, Parameters params) {
		List<DisambiguatedTerm> disambiguatedTerms = new ArrayList<DisambiguatedTerm>();

		for (ScoredTerm term : scoredterms) {
			double sumAll = 0.0;
			double numAll = 0.0;
			ScoredCandidate winner = null;
			double max = 0.0;
			for (ScoredCandidate uri : term.getScoredCandidates()) {
				double sum = 0.0;
				double num = 0.0;
				for (String scorer : uri.getScores().keySet()) {
					Score s = uri.getScores().get(scorer);
					s.setBoost(getBoost(scorer, params));

					sum = sum + getBoost(scorer, params) * s.getNormalizedValue();
					num++;
				}
				uri.setTotalScore(sum / num);
				sumAll = sumAll + sum;
				numAll = numAll + num;
				if (uri.getTotalScore() >= max) {
					max = uri.getTotalScore();
					winner = uri; 
				}
			}
			// decide threshold = mean
			double mean = sumAll / numAll;
			boolean accepted = winner.getTotalScore() > 0.7 * mean;
			DisambiguatedTerm disambiguatedTerm = new DisambiguatedTermImpl(term, winner, accepted);
			disambiguatedTerms.add(disambiguatedTerm);
		}

		// filter disambiguated terms according to shingles
		//return handleShingles(disambiguatedTerms);
		return disambiguatedTerms;
	}

	
	@SuppressWarnings("unused")
	private List<DisambiguatedTerm> handleShingles(List<DisambiguatedTerm> disambiguatedTerms) {
		int maxShingleSize = 3;
		List<DisambiguatedTerm> result= new ArrayList<DisambiguatedTerm>();
		for (int shingleSize = maxShingleSize; shingleSize > 0; shingleSize--) {
			for (DisambiguatedTerm term : disambiguatedTerms) {
				if (term.getPositionLength() == shingleSize) {
					L.info(term.getSurfaceForm() + "(" + term.getScore() + ") " + term.getPosition() + " " + term.getPositionLength());
					// get the maximum of all child scores
					List<DisambiguatedTerm> children = getChildren(term, disambiguatedTerms);
					double maxscore = -1.0;
					for (DisambiguatedTerm child : children) {
						if (maxscore < child.getScore()){
							maxscore = child.getScore();
						}
					}					
					// if the score  equals maxscore 
					// add this term
					if (term.getScore() >= maxscore){
						result.add(term);
					}
				}
			}
		}
		//System.exit(0);
		return result;
	}

	private List<DisambiguatedTerm> getChildren(DisambiguatedTerm sterm, List<DisambiguatedTerm> disambiguatedTerms) {
		List<DisambiguatedTerm> result= new ArrayList<DisambiguatedTerm>();
		
		for (DisambiguatedTerm cterm : disambiguatedTerms) {
			if (sterm.getPosition() <= cterm.getPosition() && cterm.getPosition() < sterm.getPosition() + sterm.getPositionLength() ){
				if (!cterm.equals(sterm)){
					result.add(cterm);		
				}
			}
		}
		
		return result;
	}

	public static double getBoost(String method, Parameters params) {
		for (String scorer : params.getStringArray(Parameters.SCORERS)) {
			String className = scorer.split("\\^")[0];
			if (className.equals(method.split("\\^")[0])) {
				return Double.parseDouble(scorer.split("\\^")[1]);
			}
		}
		Assert.assertTrue(false);
		return 0.0;
	}

}
