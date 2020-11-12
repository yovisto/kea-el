package com.yovisto.kea;

import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.ScoredTerm;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.ned.CategoryFilter;
import com.yovisto.kea.ned.Disambiguator;
import com.yovisto.kea.ned.GraphGenerator;
import com.yovisto.kea.ned.Normalizer;
import com.yovisto.kea.ned.ScoreGenerator;
import com.yovisto.kea.ned.scorers.CategoryScorer;
import com.yovisto.kea.ner.MappedTermFilter;
import com.yovisto.kea.ner.MappedTermMerger;
import com.yovisto.kea.ner.Mapper;
import com.yovisto.kea.ner.NGramGenerator;
import com.yovisto.kea.ner.TermFilter;
import com.yovisto.kea.util.IndexAccess;

public class StandardEntityResolver implements EntityResolver {

	private static final long serialVersionUID = -1686420941145664889L;
	
	protected final Logger L = Logger.getLogger(getClass());

	@Inject
	private NGramGenerator grams;

	@Inject
	private Mapper mapper;

	@Inject
	private TermFilter termFilter;

	@Inject
	private MappedTermMerger merger;

	@Inject
	private MappedTermFilter filter;

	@Inject
	public ScoreGenerator scoreGenerator;

	@Inject
	public CategoryFilter catFilter;

	public ScoreGenerator getScoreGenerator() {
		return scoreGenerator;
	}

	@Inject
	private Normalizer normalizer;

	@Inject
	private Disambiguator disambiguator;

	@Inject
	private GraphGenerator graphGenerator;

	@Inject
	private IndexAccess access;

	public List<DisambiguatedTerm> resolve(String text, Parameters params) throws Exception {

		// This is the magic pipeline ...
		
		// generate n-grams of tokens
		L.info("NGRAM GENERATION");
		List<Term> terms = grams.generateGrams(text, params);
		L.info("Size: " + terms.size());

		L.info("SHINGLE FILTER");
		List<Term> shingleFilteredTerms = termFilter.filter(terms, params);

		L.info("Size: " + shingleFilteredTerms.size());
		
		// map the ngrams to uri candidates
		L.info("MAPPING");
		List<MappedTerm> mappedTerms = mapper.mapAllCandidates(shingleFilteredTerms, params);
		

		// merge overlapping n-grams
		L.info("Size: " + mappedTerms.size());
		L.info("MERGING");
		List<MappedTerm> mergedTerms = merger.merge(mappedTerms, params);

		// filter terms
		L.info("Size: " + mergedTerms.size());
		L.info("FILTERING");
		List<MappedTerm> filteredTerms = filter.filter(mergedTerms, params);

		Context context = graphGenerator.createGraphContext(filteredTerms, params);

		// score the candidates
		L.info("Size: " + filteredTerms.size());
		L.info("SCORING");
		List<ScoredTerm> scoredTerms = scoreGenerator.generateScores(filteredTerms, context, params);

		CategoryScorer.addScore(scoredTerms, params, access);

		// normalize the scores
		L.info("Size: " + scoredTerms.size());
		L.info("NORMALIZATION");
		normalizer.normalize(scoredTerms, params);

		// disambiguate aka chose the winner from the scores
		L.info("Size: " + scoredTerms.size());
		L.info("DISAMBIGUATION");
		List<DisambiguatedTerm> dTerms = disambiguator.disambiguate(scoredTerms, params);

		// final filtering according to category overlap
		List<DisambiguatedTerm> filtered = catFilter.filter(dTerms);

		return filtered;		
	}

}
