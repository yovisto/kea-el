package com.yovisto.kea.ner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.inject.Inject;
import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.MappedTermImpl;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.commons.CandidateImpl;
import com.yovisto.kea.util.IndexAccess;
import com.yovisto.kea.util.Plurals;
import com.yovisto.kea.util.StringSupport;

public class StandardMapper implements Mapper {

	private static final long serialVersionUID = 2333534908258828441L;

	
	private Plurals plurals = new Plurals();
	
	@Inject
	private IndexAccess access;

	@Override
	public List<MappedTerm> mapAllCandidates(List<Term> terms, Parameters params) {
				
		access.setup(params);

		List<MappedTerm> mappedTerms = new ArrayList<MappedTerm>();

		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		int count = 0;
		Set<Future<MappedTerm>> set = new HashSet<Future<MappedTerm>>();
		for (Term term : terms) {
			Callable<MappedTerm> worker = new Worker(term, params, access, count++);
			Future<MappedTerm> future = executor.submit(worker);
			set.add(future);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		for (Future<MappedTerm> future : set) {
			try {
				mappedTerms.add(future.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		// we have to ensure the same ordering for mapped terms, like for terms  
		
		List<MappedTerm> mappedTermsSameOrder = new ArrayList<MappedTerm>();
		
		for (Term t : terms)
		for (MappedTerm m : mappedTerms){
			if (t.getStartOffset() == m.getStartOffset() && t.getPositionIncrement() == m.getPositionIncrement() && t.getPosition() == m.getPosition() && t.getEndOffset() == m.getEndOffset()){
				mappedTermsSameOrder.add(m);
			}
			
		}
				
		return mappedTermsSameOrder;
	}

	private List<Candidate> createCandidateList(Set<String> rawList, Lang language) {
		List<Candidate> iris = new ArrayList<Candidate>();
		if (rawList != null)
			for (String iriString : rawList) {
				//TODO: fix this
				if (!iriString.startsWith("Kategorie:")){
					iris.add(new CandidateImpl(iriString));
				}
			}
		
		return iris;

	}

	public class Worker implements Callable<MappedTerm> {

		private Term term;
		private Parameters params;
		private IndexAccess access;

		public Worker(Term term, Parameters params, IndexAccess access, int count) {
			this.term = term;
			this.params = params;
			this.access = access;
			// L.info("Thread: " + count);
		}

		@Override
		public MappedTerm call() throws Exception {
			
			boolean skip = true;
			for (String noun : StandardFilter.nouns){
				if (term.getPartOfSpeech().contains(noun)){
					skip = false;
				}
			}
			if (skip){
				return new MappedTermImpl(term, new ArrayList<Candidate>());
			}
			
			
			String needle = StringSupport.wordsToString(term.getWords());
			
			needle = needle.replaceAll("\"", "");			
			needle = needle.replaceAll(" ", "_");
			Set<String> rawList = new HashSet<String>();					
			rawList.addAll(access.getIrisForLabel(needle, params.getAdditionalSurfaces()));
			needle = removeApostrophS(needle);
			rawList.addAll(access.getIrisForLabel(needle, params.getAdditionalSurfaces()));
			
			// if needle end with s, remove it and append the candidates for the
			// plural form
			
			String singular = plurals.getSingular(needle);
			if (!needle.equals(singular)) {							
				List<String> l = access.getIrisForLabel(singular,params.getAdditionalSurfaces());
				rawList.addAll(l);
			}
		
			List<Candidate> candidates = createCandidateList(rawList, (Lang) (params.getProperty(Parameters.LANGUAGE)));
			
			// for neel
			if (candidates.size()==0 && term.getAnalysisType()!=null && term.getAnalysisType().equals("word") && term.getSurfaceForm()!=null && (term.getSurfaceForm().startsWith("@") || term.getSurfaceForm().startsWith("#"))){
				candidates.add(new CandidateImpl("NIL"));
			}

			MappedTerm mappedTerm = new MappedTermImpl(term, candidates);

			return mappedTerm;
		}

		private String removeApostrophS(String needle) {

			
			String [] tokens = needle.split(" ");
			String result = "";
			for (String token : tokens){
				token = token.replaceAll("'s$|´s$|`s$", "");
				result = result + " " + token;
			}
			return result.trim();
		}
	}
}
