package com.yovisto.kea.commons;

import java.io.Serializable;

import org.apache.lucene.util.Version;

public interface KeaComponent extends Serializable {
	public static final Version LUCENE_VERSION = Version.LUCENE_45;
}
