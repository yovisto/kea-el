package com.yovisto.kea.ned;

import grph.Grph;

import java.util.Map;

public class GraphElementsImpl implements GraphElements {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2493432030404338556L;

	private Grph graph;
	
	private Map<String, Map<String,Double>> termStatistics;

	/* (non-Javadoc)
	 * @see com.yovisto.kea.ned.GraphElements#getGraph()
	 */
	@Override
	public Grph getGraph() {
		return graph;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.ned.GraphElements#setGraph(grph.Grph)
	 */
	@Override
	public void setGraph(Grph graph) {
		this.graph = graph;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.ned.GraphElements#getTermStatistics()
	 */
	@Override
	public Map<String, Map<String,Double>> getStatistics() {
		return termStatistics;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.ned.GraphElements#setTermStatistics(java.util.Map)
	 */
	@Override
	public void setStatistics(Map<String, Map<String,Double>> termStatistics) {
		this.termStatistics = termStatistics;
	}
}
