package com.yovisto.kea;

import java.util.List;

import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.ned.ScoreGenerator;

public interface EntityResolver extends KeaComponent{

	public abstract List<DisambiguatedTerm> resolve(String text, Parameters params) throws Exception;

	public abstract ScoreGenerator getScoreGenerator();
}
