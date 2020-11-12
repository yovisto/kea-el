package com.yovisto.kea.ner.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.junit.Assert;

import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.ner.PosTaggerProvider;
import com.yovisto.kea.ner.StopListProvider;
import com.yovisto.kea.util.KeaResourceException;

public class SimpleAnalyzer extends Analyzer {

	private int shingleSize;
	private PosTaggerProvider posTaggerProvider;	
	private SentenceDetectorProvider sdProvider;
	private CharArraySet stopList = StandardAnalyzer.STOP_WORDS_SET;
	private Lang language;
	private Version version;
	private NetClassifierProvider netProvider;

	public SimpleAnalyzer(int shingleSize, Lang language, PosTaggerProvider posTaggerProvider, StopListProvider stopListProvider, SentenceDetectorProvider sdProvider, NetClassifierProvider netProvider, Version version) {
		super(Analyzer.GLOBAL_REUSE_STRATEGY);		
		this.shingleSize = shingleSize;
		this.posTaggerProvider = posTaggerProvider;
		this.sdProvider = sdProvider;
		this.netProvider = netProvider;
		this.version = version;
		this.language = language;
		try {
			this.stopList = stopListProvider.getStopList(language);
		} catch (KeaResourceException e) {			
			e.printStackTrace();
		}
		Assert.assertTrue(shingleSize > 0);
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		CharArraySet stopWords = new CharArraySet(Version.LUCENE_45, stopList, true);		
		Tokenizer source = new KeywordTokenizer(reader);
		TokenStream filter = new POSTaggerFilter(source, posTaggerProvider, sdProvider, netProvider, language);
		filter = new RemoveTextTypeTokenFilter(version, filter);
		//filter = new ASCIIFoldingFilter(filter);
		filter = new TrimFilter(version, filter);
		filter = new ShingleFilter(filter);
		((ShingleFilter) filter).setMaxShingleSize(shingleSize);
		filter = new StopFilter(version, filter, stopWords);
		return new TokenStreamComponents(source, filter);
	}

}
