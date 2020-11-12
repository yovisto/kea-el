package com.yovisto.kea.commons;

import java.io.Serializable;

/**
 * A single string token (without whitespace). Usually belonging to a term (sequence of words).
 *
 */
public interface Word  extends Serializable{

	public abstract void setValue(String value);

	public abstract String getValue();

	public abstract String toString();
	
	public abstract Term getTerm();
	
	public abstract void setTerm(Term term);

}