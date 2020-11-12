package com.yovisto.kea.ned.scorers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;
import com.yovisto.kea.util.ListSupport;

public class TopWordScorer extends AbstractScorer{

	private static final long serialVersionUID = 1L;

	private Map<String, Integer> topWords = new HashMap<String, Integer>();

	private int maxFreq = 0;

	public TopWordScorer() {
		// load wordlist
		InputStream in = this.getClass().getResourceAsStream("/topwords_en.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		try {
			while ((line = br.readLine()) != null) {
				String term = line.split(" ")[0];
				int value = Integer.parseInt(line.split(" ")[1]);
				topWords.put(term, value);
				maxFreq = Math.max(maxFreq, value);
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
		String term = ListSupport.joinWordList(cand.getWords(), " ").toLowerCase();
		
		Score score = new ScoreImpl(this.getClass().getSimpleName());
		if (topWords.containsKey(term)) {
			// always ensure, the larger the value, the better!!
			score.setValue((maxFreq - topWords.get(term)) * 1.0);
			score.getVector().add((maxFreq - topWords.get(term)) * 1.0);
		} else {
			score.setValue(maxFreq * 1.0);
			score.getVector().add(maxFreq * 1.0);
		}
		return score;
	}

}
