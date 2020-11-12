package com.yovisto.kea.ner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;
import org.junit.Before;
import org.junit.Test;

import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.ner.lucene.NETAttribute;
import com.yovisto.kea.ner.lucene.NetClassifierProvider;
import com.yovisto.kea.ner.lucene.POSAttribute;
import com.yovisto.kea.ner.lucene.SentenceDetectorProvider;
import com.yovisto.kea.ner.lucene.SimpleAnalyzer;
import com.yovisto.kea.util.KeaResourceException;

public class SimpleAnalyzerTest {
	private Analyzer analyzer;

	@Before
	public void setup() throws KeaResourceException {		
		PosTaggerProvider posTaggerProvider = new StandardPOSTaggerProvider();
		StopListProvider stopListProvider = new StandardStopListProvider();
		SentenceDetectorProvider sdProvider = new SentenceDetectorProvider();		
		NetClassifierProvider netProvider = new NetClassifierProvider();
		analyzer = new SimpleAnalyzer(3, Lang.EN, posTaggerProvider, stopListProvider, sdProvider, netProvider, Version.LUCENE_45);
	}
	

	@Test
	public void test() throws Exception {
		String text = "On June 8, 1729, English civil engineer John Smeaton was born. Smeaton actually is referred to having coined the term civil engineering to distinguish from military engineers. He was esponsible for the design of bridges, canals, harbours and lighthouses.";
				
		TokenStream stream = analyzer.tokenStream("foo", text);
		stream.reset();
		int position = 0;
		while (stream.incrementToken()) {
			AttributeSource src = (AttributeSource) stream;
			PositionIncrementAttribute posIncr = (PositionIncrementAttribute) src.addAttribute(PositionIncrementAttribute.class);
			int from =stream.getAttribute(OffsetAttribute.class).startOffset();
			int to = stream.getAttribute(OffsetAttribute.class).endOffset();
			System.out.println(posIncr.getPositionIncrement() + " " + stream.getAttribute(OffsetAttribute.class).startOffset() + "-" + stream.getAttribute(OffsetAttribute.class).endOffset() + " " + stream.getAttribute(TypeAttribute.class).type() + "[" + stream.getAttribute(CharTermAttribute.class) + "][" + text.substring(from, to) + "] -> " + stream.getAttribute(POSAttribute.class).getPartOfSpeech() + " -> " + stream.getAttribute(NETAttribute.class).getNET());
			position = position + posIncr.getPositionIncrement();
		}
		stream.close();
		
	}
}
