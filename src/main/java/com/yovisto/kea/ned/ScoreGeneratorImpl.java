package com.yovisto.kea.ned;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.ContextImpl;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Score;
import com.yovisto.kea.commons.ScoredTerm;
import com.yovisto.kea.commons.ScoredTermImpl;
import com.yovisto.kea.commons.ScoredCandidate;
import com.yovisto.kea.commons.ScoredCandidateImpl;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.util.KeaResourceException;

import junit.framework.Assert;

public class ScoreGeneratorImpl implements ScoreGenerator {

	private Map<String, Scorer> scorers = new HashMap<String, Scorer>();

	@Inject
	private Injector injector;

	private static final long serialVersionUID = 2621666873409989088L;

	@Override
	public List<ScoredTerm> generateScores(List<MappedTerm> terms, Context context, Parameters params) throws KeaResourceException {
		List<ScoredTerm> scoredTerms = new ArrayList<ScoredTerm>();

		// ExecutorService executor =
		// Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		ExecutorService executor = Executors.newFixedThreadPool(1);
		int count = 0;
		Set<Future<ScoredTerm>> set = new HashSet<Future<ScoredTerm>>();
		for (MappedTerm term : terms) {

			Callable<ScoredTerm> worker = new Worker(term, context, params, count++);
			Future<ScoredTerm> future = executor.submit(worker);
			set.add(future);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		for (Future<ScoredTerm> future : set) {
			try {
				scoredTerms.add(future.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		return scoredTerms;
	}

	@Override
	public void shutdown() {
		for (Scorer s : scorers.values()) {
			s.shutdown();
		}
	}

	@Override
	public ScoredTerm generateScores(MappedTerm mappedTerm, Context context, Parameters params) throws KeaResourceException {

		List<ScoredCandidate> scoredUris = new ArrayList<ScoredCandidate>();
		String[] scorersToUse = (String[]) params.getStringArray(Parameters.SCORERS);

		for (Candidate uri : mappedTerm.getCandidates()) {
			ScoredCandidate scoredUri = new ScoredCandidateImpl(uri);

			for (String method : scorersToUse) {
				Scorer scorer = getScorer(method);
				Score score = scorer.getScore(uri, mappedTerm, context, params);
				score.setBoost(scorer.getBoost());
				score.setMethod(method);
				score.setNormalizedValue(score.getNormalizedValue() * score.getBoost());
				Assert.assertTrue("The score vector should have values.", score.getVector().size() > 0);
				scoredUri.getScores().put(method, score);
			}
			scoredUris.add(scoredUri);
		}

		return new ScoredTermImpl(mappedTerm, scoredUris);
	}

	private Scorer getScorer(String method) throws KeaResourceException {
		if (!scorers.containsKey(method)) {
			// initialize the scorer
			Scorer scorer = ScorerFactory.createScorer(method);
			injector.injectMembers(scorer);
			scorers.put(method, scorer);
		}
		return scorers.get(method);
	}

	private final Map<String, Long> timings = new HashMap<String, Long>();

	public class Worker implements Callable<ScoredTerm> {

		private MappedTerm mappedTerm;
		private Parameters params;
		private Context context;

		public Worker(MappedTerm mappedTerm, Context context, Parameters params, int count) {
			this.mappedTerm = mappedTerm;
			this.params = params;
			// remove this term from the context
			// context.getTerms().remove(term);
			this.context = new ContextImpl();
			List<Term> newTerms = new ArrayList<Term>();
			for (Term t : context.getTerms()) {
				if (t != mappedTerm) {
					newTerms.add(t);
				}
			}
			this.context.setTerms(newTerms);
			// L.info("CTX SIZE " + this.context.getTerms().size() +
			// " " + context.getTerms().size());
			this.context.setGraphElements(context.getGraphElements());
			// this.context.setContextId(params.getProperty("contextId").toString()
			// + mappedTerm.getSurfaceForm());
		}

		@Override
		public ScoredTerm call() {
			List<ScoredCandidate> scoredUris = new ArrayList<ScoredCandidate>();
			String[] scorersToUse = (String[]) params.getStringArray(Parameters.SCORERS);

			for (Candidate uri : mappedTerm.getCandidates()) {
				ScoredCandidate scoredUri = new ScoredCandidateImpl(uri);

				for (String method : scorersToUse) {
					Scorer scorer = null;
					try {
						scorer = getScorer(method);
					} catch (KeaResourceException e) {
						e.printStackTrace();
						System.exit(1);
					}
					long startTime = System.currentTimeMillis();
					Score score = scorer.getScore(uri, mappedTerm, context, params);

					long time = System.currentTimeMillis() - startTime;
					synchronized (ScoreGeneratorImpl.class) {
						if (timings.containsKey(method)) {
							timings.put(method, timings.get(method) + time);
						} else {
							timings.put(method, time);
						}
					}
					score.setBoost(scorer.getBoost());
					// score.setNormalizedValue(score.getNormalizedValue() *
					// score.getBoost());
					Assert.assertTrue("The score vector should have values.", score.getVector().size() > 0);
					scoredUri.getScores().put(method, score);
				}
				scoredUris.add(scoredUri);
			}

			return new ScoredTermImpl(mappedTerm, scoredUris);
		}
	}

	@Override
	public Map<String, Long> getTimings() {		
		return timings;
	}

}
