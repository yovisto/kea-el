package com.yovisto.kea.ner;

import org.apache.lucene.analysis.util.CharArraySet;

import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.util.KeaResourceException;

public interface StopListProvider extends KeaComponent{

	abstract public CharArraySet getStopList(Lang language) throws KeaResourceException;

}
