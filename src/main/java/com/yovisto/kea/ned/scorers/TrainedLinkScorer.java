package com.yovisto.kea.ned.scorers;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;

public class TrainedLinkScorer extends AbstractScorer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6549560684347860658L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		
		Score score = new ScoreImpl(this.getClass().getSimpleName());
		double scoreValue = 0;

		// wie viele uris haben überhaupt einen treffer?
		int countTag = 0;

		// wieviele treffer gibt es insgesamt
		int countUriAll = 0;

		for (final Term term : context.getTerms()) {
			int countUri = 0;

			final List<Candidate> uriList = Lists.newArrayList();

			if (term instanceof MappedTerm) {
				final MappedTerm mTerm = (MappedTerm) term;
				uriList.addAll(mTerm.getCandidates());
			} else {
				if (term instanceof DisambiguatedTerm) {
					final DisambiguatedTerm disambiguatedTerm = (DisambiguatedTerm) term;
					uriList.add(disambiguatedTerm.getCandidate());
				}
			}
			// her we have the uri list
			List<String> uris = new ArrayList<String>();
			for (Candidate u : uriList) {
				uris.add(u.getIri());
			}
			countUri = getLinkCount(cand.getIri(), uris, params);

			if (countUri != 0) {
				countTag++;
			}
			countUriAll = countUriAll + countUri;

		}
		if (countUriAll != 0) {
			scoreValue = ((double) countTag) / ((double) context.getTerms().size() * (double) countUriAll);
			score.setValue(scoreValue);
			score.getVector().add((double) countTag);
			score.getVector().add((double) context.getTerms().size());
			score.getVector().add((double) countUriAll);
		} else {
			score.setValue(0.0);
			score.getVector().add(0.0);
			score.getVector().add(0.0);
			score.getVector().add(0.0);
		}
		return score;
	}

	private int getLinkCount(String uri, List<String> uris, Parameters params) {
		int count = 0;
		for (String uri2 : uris){
			if (params.getAdditionalLinks()!=null && (params.getAdditionalLinks().contains(uri + "\t" + uri2) || params.getAdditionalLinks().contains(uri2 + "\t" + uri))){
				count++;
			}
		}
		return count;
	}

}
