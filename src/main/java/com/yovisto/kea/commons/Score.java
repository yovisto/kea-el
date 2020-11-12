package com.yovisto.kea.commons;

import java.io.Serializable;
import java.util.List;

public interface Score extends Serializable{

	public abstract double getValue();

	public abstract void setValue(double value);

	public abstract double getBoost();

	public abstract void setBoost(double boost);
	
	public abstract String getCreator();

	public abstract void setCreator(String creator);

	public abstract List<Double> getVector();

	public abstract void setVector(List<Double> vector);

	public abstract double getNormalizedValue();

	public abstract void setNormalizedValue(double value);
	
	public abstract List<Double> getNormalizedVector();

	public abstract void setNormalizedVector(List<Double> vector);
	
	public abstract void setMethod(String method);

	public abstract String getMethod();
}