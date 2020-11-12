package com.yovisto.kea.ner.lucene;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.Version;

import com.google.common.collect.Lists;
import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.ner.PosTaggerProvider;
import com.yovisto.kea.util.KeaResourceException;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.util.Span;

/**
 * TokenFilter to add part of speech tags.
 */
public final class POSTaggerFilter extends TokenFilter {

	protected final Logger L = Logger.getLogger(getClass());
	
	private CharTermAttribute termAttr;
	private POSAttribute partOfSpeechAttr;
	private NETAttribute namedEntityAttr;
	private PositionIncrementAttribute posIncAttr;
	private OffsetAttribute offsetAttr;

	private AttributeSource.State current;
	private TokenStream input;
	private Stack<char[]> words;
	private Stack<char[]> tags;
	private Stack<char[]> nets;
	private Stack<Integer> startPositions;
	private Stack<Integer> endPositions;
	private TypeAttribute typeAttr;
	private PosTaggerProvider posTaggerProvider;
	private MaxentTagger tagger;
	private Lang language;

	private SentenceDetectorME sentenceDetector;
	private CRFClassifier<CoreLabel> c;

	public POSTaggerFilter(TokenStream input, PosTaggerProvider posTaggerProvider, SentenceDetectorProvider sdProvider, NetClassifierProvider netProvider, Lang language) {
		super(input);
		this.termAttr = addAttribute(CharTermAttribute.class);
		this.posIncAttr = addAttribute(PositionIncrementAttribute.class);
		this.partOfSpeechAttr = addAttribute(POSAttribute.class);
		this.namedEntityAttr = addAttribute(NETAttribute.class);
		this.typeAttr = addAttribute(TypeAttribute.class);
		this.offsetAttr = addAttribute(OffsetAttribute.class);

		this.input = input;
		this.words = new Stack<char[]>();
		this.nets = new Stack<char[]>();
		this.tags = new Stack<char[]>();
		this.startPositions = new Stack<Integer>();
		this.endPositions = new Stack<Integer>();

		this.posTaggerProvider = posTaggerProvider;
		this.language = language;

		sentenceDetector = sdProvider.getDetector(language);
		c = netProvider.getDetector(language);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if (words.size() > 0) {
			// output the last tagged word
			char[] word = words.pop();
			char[] tag = tags.pop();
			char[] net = nets.pop();
			int start = startPositions.pop();
			int end = endPositions.pop();
			restoreState(current);
			termAttr.copyBuffer(word, 0, word.length);
			partOfSpeechAttr.setPartOfSpeech(new String(tag));
			namedEntityAttr.setNET(new String(net));
			posIncAttr.setPositionIncrement(0);
			offsetAttr.setOffset(start, end);
			return true;
		}
		if (!input.incrementToken()) {
			return false;
		}

		if (doPosTagging()) {
			current = captureState();
		}
		typeAttr.setType("text");
		return true;
	}

	private boolean doPosTagging() {
		if (tagger == null) {
			try {
				tagger = posTaggerProvider.getTagger(language);
			} catch (KeaResourceException e) {
				e.printStackTrace();
			}
		}
		CharSequence textBuffer = termAttr.subSequence(0, termAttr.length());
		String text = new String(textBuffer.toString());

		Span[] spans = sentenceDetector.sentPosDetect(text);
		
		// be aware of spaces and special chars
		for (Span s : spans){
			//System.out.println(s.getStart() + "-" + s.getEnd() + " '" + text.substring(s.getStart(), s.getEnd()) + "'");
			String sentence = text.substring(s.getStart(), s.getEnd());
		
			List<TaggedWord> wordList = toTaggedWordList(sentence);
			List<CoreLabel> labels = new ArrayList<CoreLabel>();
			if (wordList.size() > 0) {
				List<TaggedWord> taggedWordList = tagger.tagSentence(wordList);
				for (TaggedWord w : taggedWordList) {
					words.add(0, (w.value().trim()).toCharArray());
					tags.add(0, (w.tag()).toCharArray());
					startPositions.add(0, s.getStart() + w.beginPosition());
					endPositions.add(0, s.getStart() + w.endPosition());

					CoreLabel l = new CoreLabel();
					l.setWord(w.value());
					labels.add(l);
				}				
			}

			List<CoreLabel> map = c.classify(labels);

			L.info("TAGGER: ");
			String info = "";
			for (CoreLabel word : map) {
				info = info + word.word() + '/' + word.get(AnswerAnnotation.class) + ' ';
				nets.add(0, word.get(AnswerAnnotation.class).toCharArray());
			}
			L.info(info);

		}

		return true;
	}

	private Analyzer analyzer = new SpecialTokenizerAnalyzer(Version.LUCENE_45);

	private List<TaggedWord> toTaggedWordList(String input) {

		List<TaggedWord> output = Lists.newArrayList();
		try {
			TokenStream stream = analyzer.tokenStream("foo", input);
			stream.reset();

			while (stream.incrementToken()) {
				TaggedWord tw = new TaggedWord();
				tw.setBeginPosition(stream.getAttribute(OffsetAttribute.class).startOffset());
				tw.setEndPosition(stream.getAttribute(OffsetAttribute.class).endOffset());
				tw.setWord(stream.getAttribute(CharTermAttribute.class).toString());
				output.add(tw);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		clearAttributes();
		this.words = new Stack<char[]>();
		this.tags = new Stack<char[]>();
		this.input.clearAttributes();
		this.input.reset();
		this.current = null;
		this.termAttr.setEmpty();
		this.termAttr.resizeBuffer(0);
		this.termAttr.setLength(0);
		

	}
}