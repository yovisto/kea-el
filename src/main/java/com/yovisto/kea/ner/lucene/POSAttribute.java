package com.yovisto.kea.ner.lucene;

import org.apache.lucene.util.Attribute;

public interface POSAttribute extends Attribute {

	public String getPartOfSpeech();

	public void setPartOfSpeech(String partOfSpeech);

}
