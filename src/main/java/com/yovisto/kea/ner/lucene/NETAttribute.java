package com.yovisto.kea.ner.lucene;

import org.apache.lucene.util.Attribute;

public interface NETAttribute extends Attribute {

	public String getNET();

	public void setNET(String namedEntityType);
}
