package com.yovisto.kea.ned.scorers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.TreeSet;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;

public class DisambScorer extends AbstractScorer {

	private static final long serialVersionUID = 1L;

	private Set<String> disambiguateUris = new TreeSet<String>();

	public DisambScorer() {
		// load wordlist
		InputStream in = this.getClass().getResourceAsStream("/disambiguate_uris_en.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		try {
			while ((line = br.readLine()) != null) {
				disambiguateUris.add(line);
			}
			br.close();
			in.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
	
		Score score = new ScoreImpl(this.getClass().getSimpleName());
		if (disambiguateUris.contains(cand.getIri())) {
			score.setValue(0.0);
			score.getVector().add(0.0);
		} else {
			score.setValue(1.0);
			score.getVector().add(1.0);
		}
		return score;
	}

}
