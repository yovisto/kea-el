package com.yovisto.kea.ned.scorers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.commons.Word;
import com.yovisto.kea.ned.ScoreImpl;
import com.yovisto.kea.util.JaroWinklerDistance;
import com.yovisto.kea.util.ListSupport;

public class StringDistanceUriSuffixScorer extends AbstractScorer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1950148792507264439L;

	/**
	 * Gets the uri suffix matching.
	 */
	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		String uriString = cand.getIri();
		try {
			uriString = URLDecoder.decode(uriString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		} catch (IllegalArgumentException e){
			//e.printStackTrace();
		}

		
		uriString = uriString.replaceAll("\\(.*\\)", " ").trim();
		
		double distance = JaroWinklerDistance.similarity(uriString.toLowerCase(), ListSupport.joinWordList(mappedTerm.getWords(), " ").toLowerCase());
		
		//L.info(uriSuffix.toLowerCase() + " <> " + ListSupport.joinWordList(mappedTerm.getWords(), " ").toLowerCase() +  " = " + distance);
				
		// get averaged token distances
		//int count = 0;
		double sum = 0.0;
		for (String uriToken: uriString.split(" ")){
			for (Word word : mappedTerm.getWords()){
				sum = sum + JaroWinklerDistance.similarity(uriToken, word.getValue());
			//	count++;
			}			
		}
		
		Score score = new ScoreImpl(this.getClass().getSimpleName());
		score.setValue(distance);
//		score.setValue(distance + (sum/count));
		score.getVector().add(distance);
//		score.getVector().add(sum / count);
		return score;

	}


}
