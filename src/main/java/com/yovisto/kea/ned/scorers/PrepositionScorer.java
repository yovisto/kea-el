package com.yovisto.kea.ned.scorers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.ned.ScoreImpl;

public class PrepositionScorer extends AbstractScorer {

	private final List<String> prepositions = new ArrayList<String>(Arrays.asList("list,aboard,about,above,across,after,against,along,amid,among,anti,around,as,at,before,behind,below,beneath,beside,besides,between,beyond,but,by,concerning,considering,despite,down,during,except,excepting,excluding,following,for,from,in,inside,into,like,minus,near,of,off,on,onto,opposite,outside,over,past,per,plus,regarding,round,save,since,the,than,through,to,toward,towards,under,underneath,unlike,until,up,upon,versus,via,with,within,without".split(",")));

	private static final long serialVersionUID = 1442357541610118239L;

	@Override
	public Score getScore(Candidate cand, MappedTerm mappedTerm, Context context, Parameters params) {
		Score score = new ScoreImpl(this.getClass().getSimpleName());

		double result = 1.0;

		// penalize, if uri suffix is a preposition
		String suffix = cand.getIri().toLowerCase();
		if (prepositions.contains(suffix.trim())) {
			result = Math.min(result, 0.0);
		}

		// penalize, if the first word of the uri suffix is a preposition
		if (suffix.contains(" ")) {
			if (prepositions.contains(suffix.split(" ")[0])) {
				result = Math.min(result, 0.0);
			}
		}

		score.getVector().add(result);

		return score;
	}

}
