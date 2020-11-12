package com.yovisto.kea.ner;

import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.util.KeaResourceException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public interface PosTaggerProvider extends KeaComponent{

	abstract public MaxentTagger getTagger(Lang language) throws KeaResourceException ;

}
