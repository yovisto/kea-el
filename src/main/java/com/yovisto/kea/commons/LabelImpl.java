package com.yovisto.kea.commons;

/**
 * The Label implementation.
 */
public class LabelImpl implements Label {

	private static final long serialVersionUID = 1L;

	/** The value. */
	private String value;

	/** The language. */
	private String language;

	private String source;

	public LabelImpl() {
	}


	@Override
	public String getSource() {
		return source;
	}

	@Override
	public void setSource(String source) {
		this.source = source;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yovisto.services.semantic.commons.Label#setValue(java.lang.String)
	 */
	@Override
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.services.semantic.commons.Label#getValue()
	 */
	@Override
	public String getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yovisto.services.semantic.commons.Label#setLanguage(com.yovisto.services
	 * .semantic.commons.Language)
	 */
	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.services.semantic.commons.Label#getLanguage()
	 */
	@Override
	public String getLanguage() {
		return language;
	}

}
