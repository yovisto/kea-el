package com.yovisto.kea.ned;

import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.Candidate;

public interface Scorer extends KeaComponent{

	abstract public Score getScore(Candidate uri, MappedTerm mappedTerm, Context context, Parameters params);

	public abstract void setBoost(double boost);
	
	public abstract double getBoost();

	public abstract void shutdown();

}
