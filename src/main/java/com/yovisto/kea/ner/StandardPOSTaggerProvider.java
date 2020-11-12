package com.yovisto.kea.ner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.util.KeaResourceException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class StandardPOSTaggerProvider implements PosTaggerProvider {

	private static final long serialVersionUID = 1375028832267171825L;

	private Map<Lang, MaxentTagger> taggers = new HashMap<Lang, MaxentTagger>();

	private static final String MODEL_EN = "left3words-wsj-0-18.tagger";
	private static final String MODEL_DE = "german-accurate.tagger";


	private void initTagger(Lang language) throws ClassNotFoundException, IOException {
		if (language == Lang.EN && !taggers.containsKey(language)) {
			MaxentTagger tagger = new MaxentTagger(MODEL_EN);
			taggers.put(Lang.EN, tagger);
		}
		
		if (language == Lang.DE && !taggers.containsKey(language)) {
			MaxentTagger tagger = new MaxentTagger(MODEL_DE);
			taggers.put(Lang.DE, tagger);
		}
		
	}

	@Override
	public MaxentTagger getTagger(Lang language) throws KeaResourceException {
		try {
			initTagger(language);
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
			throw new KeaResourceException("Class not found");
		} catch (IOException e) {			
			e.printStackTrace();
			throw new KeaResourceException("IO exception.");
		}
		if (!taggers.containsKey(language)) {
			throw new KeaResourceException("Tagger with language '" + language + "' cannot be initialized.");
		}
		return taggers.get(language);
	}
}
