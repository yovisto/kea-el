package com.yovisto.kea.commons;

import java.io.Serializable;

/**
 * The Interface Label.
 */
public interface Label  extends Serializable{

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public abstract void setValue(String value);

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public abstract String getValue();

	/**
	 * Sets the language.
	 *
	 * @param language the new language
	 */
	public abstract void setLanguage(String language);

	/**
	 * Gets the language.
	 *
	 * @return the language
	 */
	public abstract String getLanguage();

	public abstract String getSource();
	public abstract void setSource(String source);

}