package com.yovisto.kea.commons;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yovisto.kea.util.ListSupport;
import com.yovisto.kea.util.StringSupport;

/**
 * The Term implementation.
 */
public class TermImpl implements Term {

	private static final long serialVersionUID = 1L;

	/** The words aka tokens, generated from whitespace tokenizing from the . */
	private List<Word> words;

	private String surfaceForm;

	private String stemmedTermString;

	private int startOffset;

	private int endOffset;

	private String analysisType;

	private String namedEntityType;
	
	private String partOfSpeech;

	private int positionLength;
	
	private Map<String,String> properties = new HashMap<String,String>();

	public TermImpl() {

	}

	public TermImpl(int position, int positionIncrement, String analysisType, String surfaceForm, String partOfSpeech, int startOffset, int endOffset, int positionLength) {
		this.position = position;
		this.positionIncrement = positionIncrement;
		this.setAnalysisType(analysisType);
		this.words = StringSupport.toWordList(surfaceForm);
		this.surfaceForm = surfaceForm;
		this.setPartOfSpeech(partOfSpeech);
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.positionLength = positionLength;		
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public int getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(int offset) {
		this.startOffset = offset;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPositionIncrement() {
		return positionIncrement;
	}

	public void setPositionIncrement(int positionIncrement) {
		this.positionIncrement = positionIncrement;
	}

	private int position;
	private int positionIncrement;

	public String getStemmedTermString() {
		return stemmedTermString;
	}

	public void setStemmedTermString(String stemmedTermString) {
		this.stemmedTermString = stemmedTermString;
	}

	/**
	 * Sets the words.
	 * 
	 * @param words
	 *            the new words
	 */
	public void setWords(List<Word> words) {
		this.words = words;
	}

	/**
	 * Gets the words.
	 * 
	 * @return the words
	 */
	public List<Word> getWords() {
		return words;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();
		for (Word w : words) {
			buf.append(w.toString());
			buf.append(" ");
		}
		return buf.toString().trim();
	}

	public boolean equals(Term other) {
		if (ListSupport.joinWordList(this.getWords(), " ").equals(ListSupport.joinWordList(other.getWords(), " "))) {
			return true;
		}

		return false;
	}

	public String getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(String analysisType) {
		this.analysisType = analysisType;
	}

	public String getSurfaceForm() {
		return surfaceForm;
	}

	public void setSurfaceForm(String surfaceForm) {
		this.surfaceForm = surfaceForm;
	}

	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	public int getPositionLength() {
		return positionLength;
	}

	public void setPositionLength(int positionLength) {
		this.positionLength = positionLength;
	}

	public Map<String,String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String,String> properties) {
		this.properties = properties;
	}

	@Override
	public int compareTo(Term o) {		
		return o.getSurfaceForm().compareTo(surfaceForm);
	}

	public String getNamedEntityType() {
		return namedEntityType;
	}

	public void setNamedEntityType(String namedEntityType) {
		this.namedEntityType = namedEntityType;
	}

	private Surface propertySurface;
	
	
	private List<PropertyImpl> propertyCandidates;
	public List<PropertyImpl> getPropertyCandidates() {
		return propertyCandidates;
	}

	public void setPropertyCandidates(List<PropertyImpl> propertyCandidates) {
		this.propertyCandidates = propertyCandidates;
	}

	public Surface getPropertySurface() {
		return propertySurface;
	}

	public void setPropertySurface(Surface propertySurface) {
		this.propertySurface = propertySurface;
	}

}
