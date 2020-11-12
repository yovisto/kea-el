package com.yovisto.kea.ned;

import java.util.List;
import java.util.Map;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.ScoredTerm;
import com.yovisto.kea.util.KeaResourceException;

public interface ScoreGenerator extends KeaComponent{

	public abstract ScoredTerm generateScores(MappedTerm term, Context contect, Parameters params) throws KeaResourceException;
	
	public abstract List<ScoredTerm> generateScores(List<MappedTerm> terms, Context contect, Parameters params) throws KeaResourceException;

	void shutdown();
	
	public abstract Map<String, Long> getTimings();
}
