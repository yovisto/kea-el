	package com.yovisto.kea.ner.lucene;

import java.io.Reader;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.pattern.PatternTokenizer;
import org.apache.lucene.util.Version;

public class SpecialTokenizerAnalyzer extends Analyzer {

	public SpecialTokenizerAnalyzer(Version version) {
		super(Analyzer.GLOBAL_REUSE_STRATEGY);
	}

	@Override
	protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
		Tokenizer source = new KeywordTokenizer(reader);

		// for neel
		TokenStream filter = new PatternTokenizer(reader, Pattern.compile("\\[[^\\]]*\\]|<[^>]*>|\\s+|[^\\p{L}a-zA-Z0-9ß\\-öäüÖÄÜ@]"), -1);
		
		//TokenStream filter = new PatternTokenizer(reader, Pattern.compile("\\[[^\\]]*\\]|<[^>]*>|\\s+|[^\\p{L}a-zA-Z0-9ß\\-öäüÖÄÜ]"), -1);
		
		return new TokenStreamComponents(source, filter);
	}

}
