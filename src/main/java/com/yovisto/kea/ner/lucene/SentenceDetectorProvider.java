package com.yovisto.kea.ner.lucene;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.yovisto.kea.commons.Lang;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceDetectorProvider {

	private Map<Lang, SentenceDetectorME> detectors = new HashMap<Lang, SentenceDetectorME>();
		
	public SentenceDetectorME getDetector(Lang language){
		
		if (detectors.containsKey(language)){
			return detectors.get(language);
		}
				
		SentenceDetectorME sd = null;
		String fname = "en-sent.bin";
		if (language == Lang.DE){
			fname= "de-sent.bin";
		}			
		InputStream modelIn = this.getClass().getClassLoader().getResourceAsStream(fname);
		try {
			SentenceModel model = new SentenceModel(modelIn);
			sd = new SentenceDetectorME(model);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}
		detectors.put(language, sd);
		return sd;
	}
}
