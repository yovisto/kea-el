package com.yovisto.kea.ner;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;

import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.util.KeaResourceException;

public class StandardStopListProvider implements StopListProvider {

	private Map<Lang, CharArraySet> stopLists = new HashMap<Lang, CharArraySet>();

	private static final long serialVersionUID = -586810354587752249L;

	public StandardStopListProvider() {
		
		/** english stoplist **/
		CharArraySet englishStopList = new CharArraySet(LUCENE_VERSION, StandardAnalyzer.STOP_WORDS_SET, true);		
		String additionalStoplistEn = ResourceBundle.getBundle("stop").getString("lang.en"); 
		if (additionalStoplistEn.contains(",")){
			for (String stop : additionalStoplistEn.split(",")){
				englishStopList.add(stop.trim());
			}			
		}		
		stopLists.put(Lang.EN, englishStopList);

		/** german stoplist **/
		CharArraySet germanStopList = GermanAnalyzer.getDefaultStopSet();
		stopLists.put(Lang.DE, germanStopList);
		String additionalStoplistDe = ResourceBundle.getBundle("stop").getString("lang.de"); 
		if (additionalStoplistDe.contains(",")){
			for (String stop : additionalStoplistDe.split(",")){
				germanStopList.add(stop.trim());
			}			
		}
		
	}

	@Override
	public CharArraySet getStopList(Lang language) throws KeaResourceException {
		if (!stopLists.keySet().contains(language)) {
			throw new KeaResourceException("Language '" + language + "' is not initialized in StopListProvider.");
		}
		return stopLists.get(language);
	}

}
