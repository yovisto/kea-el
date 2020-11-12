package com.yovisto.kea.ner.lucene;

import org.apache.lucene.util.AttributeImpl;

public class NETAttributeImpl extends AttributeImpl implements NETAttribute, Cloneable {
	private String namedEntityType = "";

	public NETAttributeImpl() {
	}

	public NETAttributeImpl(String namedEntityType) {
		this.namedEntityType = namedEntityType;
	}

	@Override
	public String getNET() {
		return namedEntityType;
	}

	@Override
	public void setNET(String namedEntityType) {
		this.namedEntityType = namedEntityType;
	}

	@Override
	public void clear() {
		namedEntityType = "";
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}

		if (other instanceof NETAttributeImpl) {
			final NETAttributeImpl o = (NETAttributeImpl) other;
			return (this.namedEntityType == null ? o.namedEntityType == null
					: this.namedEntityType.equals(o.namedEntityType));
		}

		return false;
	}

	@Override
	public int hashCode() {
		return (namedEntityType == null) ? 0 : namedEntityType.hashCode();
	}

	@Override
	public void copyTo(AttributeImpl target) {
		NETAttribute t = (NETAttribute) target;
		t.setNET(namedEntityType);
	}
}
