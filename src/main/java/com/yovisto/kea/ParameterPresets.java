package com.yovisto.kea;

import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.commons.Parameters;

public class ParameterPresets {

	public static Parameters getDefaultParameters() {
		Parameters params = new Parameters();
		params.setProperty(Parameters.NUM_SHINGLES, 5);
		params.setProperty(Parameters.NUM_SHINGLES_FILTER, 3);
		params.setProperty(Parameters.LANGUAGE, Lang.DE);
		params.setProperty(Parameters.NO_MERGING, false);
		params.setProperty(Parameters.NO_TOKENIZING, false);	
		params.setProperty(Parameters.DATA_PATH, "/var/indizes/ner-data/lucene2020");
		
		String [] scorers = {"CategoryScorer^0.5", "DirectLinkScorerLucene^0.5", "GraphScorer^1.0", "StringDistanceUriSuffixScorer^1.0", "PrepositionScorer^0.5", "BlacklistScorer^1.0"};
		
		
		params.setProperty(Parameters.SCORERS, scorers);		
		return params;
	}
	
	
	static Parameters getDefaultParametersWithExplain() {
		Parameters params = getDefaultParameters();
		params.setProperty(Parameters.EXPLAIN_MERGING, true);
		params.setProperty(Parameters.EXPLAIN_ANALYSIS, true);
		return params;
	}
	
}
