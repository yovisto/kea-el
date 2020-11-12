package com.yovisto.kea.ner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

import com.google.inject.Singleton;
import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.commons.TermImpl;
import com.yovisto.kea.ner.lucene.NETAttribute;
import com.yovisto.kea.ner.lucene.NetClassifierProvider;
import com.yovisto.kea.ner.lucene.POSAttribute;
import com.yovisto.kea.ner.lucene.SentenceDetectorProvider;
import com.yovisto.kea.ner.lucene.SimpleAnalyzer;
import com.yovisto.kea.util.KeaResourceException;

/**
 * The Class SimpleGramGenerator.
 * 
 * Holds a list of Lucene analyzers for different languages.
 * 
 */
@Singleton
public class StandardNGramGenerator implements NGramGenerator {
	
	protected final Logger L = Logger.getLogger(getClass());

	/** The Constant DEFAULT_SHINGLE_SIZE. */
	private final static int MAX_SHINGLE_SIZE = 10;

	/** The analyzers. */
	private Map<String, Analyzer> analyzers = new HashMap<String, Analyzer>();

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1774750758524165118L;

	private boolean explain = false;

	private void setup(Parameters params) {
		if (params.containsKey(Parameters.EXPLAIN_ANALYSIS)) {
			explain = params.getBoolean(Parameters.EXPLAIN_ANALYSIS);
		}
	}

	/**
	 * Instantiates a new simple gram generator.
	 * 
	 * @throws KeaResourceException
	 *             the kea resource exception
	 */
	public StandardNGramGenerator() throws KeaResourceException {
		PosTaggerProvider posTaggerProvider = new StandardPOSTaggerProvider();
		StopListProvider stopListProvider = new StandardStopListProvider();
		SentenceDetectorProvider sdProvider = new SentenceDetectorProvider();
		NetClassifierProvider netProvider = new NetClassifierProvider();
		for (Lang l : Lang.values()) {
			for (int s = 1; s <= MAX_SHINGLE_SIZE; s++) {
				Analyzer analyzer = new SimpleAnalyzer(s, l, posTaggerProvider, stopListProvider, sdProvider, netProvider, Version.LUCENE_45);
				String analyzerKey = l.toString() + "_" + s;
				analyzers.put(analyzerKey, analyzer);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yovisto.kea.ner.api.GramGenerator#generateGrams(java.lang.String,
	 * com.yovisto.kea.Parameters)
	 */
	@Override
	public List<Term> generateGrams(String text, Parameters params) throws KeaResourceException {
		setup(params);

		
		List<Term> terms = new ArrayList<Term>();

		if (params.getBoolean(Parameters.NO_TOKENIZING)) {
			Term term = new TermImpl(0, 0, "word", text, "NN", 0, text.length(), 1);

			try {
				// add NER
				Lang l = (Lang) params.getProperty(Parameters.LANGUAGE);
				int s = params.getInt(Parameters.NUM_SHINGLES);
				String analyzerKey = l.toString() + "_" + s;
				TokenStream stream = analyzers.get(analyzerKey).tokenStream("foo", text);
				stream.reset();
				term.setNamedEntityType(stream.getAttribute(NETAttribute.class).getNET()); // assigns empty label 
				stream.close();
			}
			catch (IOException e) {
				throw new KeaResourceException(e.getMessage());
			}
			
			terms.add(term);
		} else {

			try {
				Lang l = (Lang) params.getProperty(Parameters.LANGUAGE);
				int s = params.getInt(Parameters.NUM_SHINGLES);
				String analyzerKey = l.toString() + "_" + s;
				TokenStream stream = analyzers.get(analyzerKey).tokenStream("foo", text);
				stream.reset();

				int position = 0;
				while (stream.incrementToken()) {
					AttributeSource src = (AttributeSource) stream;
					PositionIncrementAttribute posIncr = (PositionIncrementAttribute) src.addAttribute(PositionIncrementAttribute.class);
					if (explain)
						L.info(posIncr.getPositionIncrement() + " " + stream.getAttribute(OffsetAttribute.class).startOffset() + "-" + stream.getAttribute(OffsetAttribute.class).endOffset() + " " + stream.getAttribute(TypeAttribute.class).type() + "[" + stream.getAttribute(CharTermAttribute.class) + "] -> " + stream.getAttribute(POSAttribute.class).getPartOfSpeech());
					position = position + posIncr.getPositionIncrement();
					Term term = new TermImpl(position, posIncr.getPositionIncrement(), stream.getAttribute(TypeAttribute.class).type(), stream.getAttribute(CharTermAttribute.class).toString(), stream.getAttribute(POSAttribute.class).getPartOfSpeech(), stream.getAttribute(OffsetAttribute.class).startOffset(), stream.getAttribute(OffsetAttribute.class).endOffset(), stream.getAttribute(PositionLengthAttribute.class).getPositionLength());
					term.setNamedEntityType(stream.getAttribute(NETAttribute.class).getNET());
					terms.add(term);
				}
				stream.close();
			} catch (IOException e) {
				throw new KeaResourceException(e.getMessage());
			}
		}

		return terms;
	}

}
