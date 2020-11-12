package com.yovisto.kea.ned.scorers;

import java.util.List;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;


/**
 * Boost URIs according to their frequency in the context.
 * 
 */

public class SynonymScorer extends AbstractScorer{

	private static final long serialVersionUID = 1L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		Score score = new ScoreImpl(this.getClass().getSimpleName());

		// in how many other terms does this candidate uri occur 
		int numFound = 0;
		for (Term term : context.getTerms()) {
			if (term instanceof MappedTerm) {
				if (((MappedTerm) term).getCandidates() != null) {
					List<Candidate> uri2list = ((MappedTerm) term).getCandidates();
					for (Candidate uri2 : uri2list) {
						if (cand.getIri().equals(uri2.getIri())) {
							numFound++;
						}
					}
				}
			}
		}
		if (numFound>0){
			score.setValue(numFound * 1.0);
			score.getVector().add(numFound * 1.0);
		}else{
			score.setValue(0.0);
			score.getVector().add(0.0);
		}
		return score;
	}
}
