package com.yovisto.kea.ner.lucene;

import org.apache.lucene.util.AttributeImpl;

public class POSAttributeImpl extends AttributeImpl implements POSAttribute, Cloneable {
	private String partOfSpeech = "";

	public POSAttributeImpl() {
	}

	public POSAttributeImpl(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	@Override
	public String getPartOfSpeech() {
		return partOfSpeech;
	}

	@Override
	public void setPartOfSpeech(String partOfSpeech) {
		this.partOfSpeech = partOfSpeech;
	}

	@Override
	public void clear() {
		partOfSpeech = "";
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other instanceof POSAttributeImpl) {
			final POSAttributeImpl o = (POSAttributeImpl) other;
			return (this.partOfSpeech == null ? o.partOfSpeech == null : this.partOfSpeech.equals(o.partOfSpeech));
		}

		return false;
	}

	@Override
	public int hashCode() {
		return (partOfSpeech == null) ? 0 : partOfSpeech.hashCode();
	}

	@Override
	public void copyTo(AttributeImpl target) {
		POSAttribute t = (POSAttribute) target;
		t.setPartOfSpeech(partOfSpeech);
	}
}
