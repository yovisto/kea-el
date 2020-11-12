package com.yovisto.kea.ned.scorers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;
import com.yovisto.kea.util.ListSupport;

public class SimpleUriSuffixScorer extends AbstractScorer{

	private static final long serialVersionUID = 1L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		Score score = new ScoreImpl(this.getClass().getName());
		if (cand.getMainLabel() != null) {
			String term = ListSupport.joinWordList(cand.getWords(), " ");
			String mainLabel = cand.getMainLabel();
			if (term.toLowerCase().equals(mainLabel.toLowerCase())) {
				score.setValue(1.0);
				score.getVector().add(1.0);
			} else {
				score.setValue(0.0);
				score.getVector().add(0.0);
			}

		} else {
			String uriString = cand.getIri();
			try {
				uriString = URLDecoder.decode(uriString, "UTF-8");
			} catch (IllegalArgumentException e) {
				//L.info(uriString);
				//e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				//L.info(uriString);
				//e.printStackTrace();
			}
				// replace parentheses
			uriString = uriString.replaceAll("\\(.*\\)", "").trim();
			
			if (uriString.toLowerCase().equals(ListSupport.joinWordList(cand.getWords(), " ").toLowerCase())) {
				score.setValue(1.0);
				score.getVector().add(1.0);
			} else {
				score.setValue(0.0);
				score.getVector().add(0.0);
			}
		}
		return score;
	}

}
