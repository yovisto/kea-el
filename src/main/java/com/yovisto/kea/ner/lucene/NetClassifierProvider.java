package com.yovisto.kea.ner.lucene;

import java.util.HashMap;
import java.util.Map;

import com.yovisto.kea.commons.Lang;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class NetClassifierProvider {

	private Map<Lang, CRFClassifier<CoreLabel>> classifiers = new HashMap<Lang, CRFClassifier<CoreLabel>>();
		
	@SuppressWarnings("unchecked")
	public CRFClassifier<CoreLabel> getDetector(Lang language){
		
		if (classifiers.containsKey(language)){
			return classifiers.get(language);
		}
		CRFClassifier<CoreLabel> c =null ;
		try {
			if (language==Lang.EN){
				c = CRFClassifier.getClassifier("english.conll.4class.distsim.crf.ser.gz");
			}
			if (language==Lang.DE){
				c = CRFClassifier.getClassifier("dewac_175m_600.crf.ser.gz");
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
						
		classifiers.put(language, c);
		return c;
	}
}
