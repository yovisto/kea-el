package com.yovisto.kea.commons;

public class WordImpl implements Word {

	private static final long serialVersionUID = 1L;

	private String value;

	@Override
	public void setValue(String value) {
		if (value.contains(" "))
			throw new IllegalStateException("A 'Word' cannot contain whitespace.");
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public Term getTerm() {
		return term;
	}

	public void setTerm(Term term) {
		this.term = term;
	}

	private Term term;

}
