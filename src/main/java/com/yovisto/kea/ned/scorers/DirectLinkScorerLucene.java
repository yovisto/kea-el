package com.yovisto.kea.ned.scorers;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;
import com.yovisto.kea.util.IndexAccess;

public class DirectLinkScorerLucene extends AbstractScorer {

	private static final long serialVersionUID = -6549560684347860658L;

	protected final Logger L = Logger.getLogger(getClass());
	
	@Inject
	private IndexAccess sary;

	@Override
	public Score getScore(Candidate uri, MappedTerm mappedTerm, Context context, Parameters params) {
		if (scoreCache != null) {
			Score s = scoreCache.get(uri.getIri() + context.getContextId());
			if (s != null)
				return s;
		}

		//String path = params.getString(Parameters.SARY_LIB_PATH);
		// if (sary==null) sary = new JniSary();
		// sary.setup(params.getString(Parameters.SARY_DATA_PATH), path);

		Score score = new ScoreImpl(this.getClass().getSimpleName());

		double scoreValue = 0;

		// wie viele uris haben überhaupt einen treffer?
		int countTerms = 0;

		// wieviele treffer gibt es insgesamt
		int countUriAll = 0;

		// TODO: is this necessary?
		List<String> links = sary.getLinks( uri.getIri(), params.getAdditionalLinks());
		
		for (final Term term : context.getTerms()) {
			int countUri = 0;

			final List<Candidate> uriListOfAllContextCandidates = Lists.newArrayList();

			if (term instanceof MappedTerm) {
				final MappedTerm mTerm = (MappedTerm) term;
				uriListOfAllContextCandidates.addAll(mTerm.getCandidates());
			} else {
				if (term instanceof DisambiguatedTerm) {
					final DisambiguatedTerm disambiguatedTerm = (DisambiguatedTerm) term;
					uriListOfAllContextCandidates.add(disambiguatedTerm.getCandidate());
				}
			}			
			
			for (Candidate u : uriListOfAllContextCandidates) {
				if (links.contains(u.getIri())) {
					countUri++;
				}
			}

			if (countUri != 0) {
				countTerms++;
			}
			countUriAll = countUriAll + countUri;

		}
		if (countUriAll != 0) {
			scoreValue = ((double) countTerms) / ((double) context.getTerms().size() * (double) countUriAll);
			score.setValue(scoreValue);
			score.getVector().add((double) countTerms);
			score.getVector().add((double) context.getTerms().size());
			score.getVector().add((double) countUriAll);
		} else {
			score.setValue(0.0);
			score.getVector().add(0.0);
			score.getVector().add(0.0);
			score.getVector().add(0.0);
		}
		if (scoreCache != null) {
			scoreCache.put(uri.getIri() + context.getContextId(), score);
		}
		return score;
	}
	
	@Override
	public void shutdown() {	
		super.shutdown();
		L.info("shutting down direct link scorer");
	}

}
