package com.yovisto.kea.ned;

import java.util.ArrayList;
import java.util.List;

import com.yovisto.kea.commons.Score;

public class ScoreImpl implements Score {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8649950337758348210L;
	public double getNormalizedValue() {
		return normalizedValue;
	}

	public void setNormalizedValue(double vectorNormalizedValue) {
		this.normalizedValue = vectorNormalizedValue;
	}
	
	private String method;
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	private double boost;
	private double value;
	private double normalizedValue;

	public double getBoost() {
		return boost;
	}

	public void setBoost(double boost) {
		this.boost = boost;
	}

	private String creator;

	private List<Double> vector = new ArrayList<Double>();
	private List<Double> normalizedVector = new ArrayList<Double>();

	public ScoreImpl(String creator) {
		this.creator = creator;
	}

	public List<Double> getNormalizedVector() {
		return normalizedVector;
	}

	public void setNormalizedVector(List<Double> vectorNormalizedVector) {
		this.normalizedVector = vectorNormalizedVector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.kea.ned.Score#getValue()
	 */
	@Override
	public double getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.kea.ned.Score#setValue(double)
	 */
	@Override
	public void setValue(double value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.kea.ned.Score#getCreator()
	 */
	@Override
	public String getCreator() {
		return creator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.kea.ned.Score#setCreator(java.lang.String)
	 */
	@Override
	public void setCreator(String creator) {
		this.creator = creator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.kea.ned.Score#getVector()
	 */
	@Override
	public List<Double> getVector() {
		return vector;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.kea.ned.Score#setVector(java.util.List)
	 */
	@Override
	public void setVector(List<Double> vector) {
		this.vector = vector;
	}
}
