package com.yovisto.kea.commons;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * The Interface Term.
 */
public interface Term extends Serializable, Comparable<Term> {
	public void setWords(List<Word> words);

	public List<Word> getWords();

	public String getStemmedTermString();

	public void setStemmedTermString(String string);

	public void setPosition(int position);

	public int getPosition();

	public void setPositionLength(int positionLength);

	public int getPositionLength();

	public void setPositionIncrement(int positionIncrement);

	public int getPositionIncrement();

	public void setStartOffset(int startOffset);

	public int getStartOffset();

	public void setEndOffset(int endOffset);

	public int getEndOffset();

	/**
	 * The part of speech (space separated) Example: "NNP VBD IN"
	 * 
	 * @param partOfSpeech
	 */
	public void setPartOfSpeech(String partOfSpeech);

	/**
	 * The part of speech (space separated) Example: "NNP VBD IN"
	 * 
	 * @returns partOfSpeech
	 */
	public String getPartOfSpeech();

	public String getNamedEntityType();

	public void setNamedEntityType(String namedEntityType);

	public String getAnalysisType();

	public void setAnalysisType(String analysisType);

	public String getSurfaceForm();

	public void setSurfaceForm(String surfaceForm);

	public Map<String, String> getProperties();

	public List<PropertyImpl> getPropertyCandidates();

	public void setPropertyCandidates(List<PropertyImpl> propertyCandidates);

}