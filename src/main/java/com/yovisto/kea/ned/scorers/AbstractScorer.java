package com.yovisto.kea.ned.scorers;

import org.apache.commons.collections4.Trie;

import com.yovisto.kea.commons.Score;
import com.yovisto.kea.ned.Scorer;

public abstract class AbstractScorer implements Scorer{

	private static final long serialVersionUID = -973531811862669166L;
	private double boost;
		
	protected Trie<String, Score> scoreCache ;
	
//	private String serName;
	
	public AbstractScorer() {
//		serName = "scorerCache-" + this.getClass().getSimpleName()+ ".ser";
//		// try to deserialize cache if existing
//		L.info("Deserializing score cache for " + this.getClass().getSimpleName());
//		if (new File(serName).exists()){			
//			scoreCache = (Trie<String, Score>)SerializationUtil.doLoad(serName);			
//		}
//		if (scoreCache==null){
//			scoreCache = new PatriciaTrie<String, Score>(StringKeyAnalyzer.CHAR);
//		}
//		L.info("  cache size: " + scoreCache.size());
	}
	
	@Override
	public void setBoost(double boost) {
		this.boost = boost;
	}

	@Override
	public double getBoost() {
		return boost;
	}
	
	@Override
	public void shutdown(){		
		//L.info("Serializing score cache for " + this.getClass().getSimpleName());		
		//SerializationUtil.doSave(scoreCache, serName);
	}
	

}
