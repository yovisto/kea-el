package com.yovisto.kea.ned;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.Trie;

//import org.ardverk.collection.PatriciaTrie;
//import org.ardverk.collection.StringKeyAnalyzer;
//import org.ardverk.collection.Trie;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.apache.log4j.Logger;

import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.DisambiguatedTermImpl;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.ScoredTerm;
import com.yovisto.kea.commons.ScoredTermImpl;
import com.yovisto.kea.commons.ScoredCandidate;
import com.yovisto.kea.commons.ScoredCandidateImpl;

public class CoreferenceResolverImpl implements CoreferenceResolver {

	protected final Logger L = Logger.getLogger(getClass());
	
	private static Trie<String, Integer> persons = new PatriciaTrie<Integer>();
	
	private List<String> refs = Arrays.asList("he", "his", "she", "her"); 

	private void init() {
		L.info("Loading tries for coreference resolution");
		try {
			// load persons
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream is = classloader.getResourceAsStream("persons2.nt");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line;
				while ((line = br.readLine()) != null) {
					persons.put(line, 0);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			is.close();
			L.info(" ... persons trie: " + persons.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.yovisto.kea.ned.CoreferenceResolver#resolve(java.util.List)
	 */
	@Override
	public List<MappedTerm> getCorefCandidateTerms(List<MappedTerm> terms) {
		
		List<MappedTerm> list = new ArrayList<MappedTerm>();
		
		for (MappedTerm t : terms){
			if (refs.contains(t.getSurfaceForm().toLowerCase())){
				list.add(t);
			}
		}

		return list;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.ned.CoreferenceResolver#resolve(java.util.List)
	 */
	@Override
	public List<DisambiguatedTerm> resolve(List<DisambiguatedTerm> terms, List<MappedTerm> corefCandidates) {
		List<DisambiguatedTerm> list = new ArrayList<DisambiguatedTerm>();
		for (DisambiguatedTerm t : terms){
			list.add(t);
		}
		
		if (persons.size() == 0){			
			init();			
		}
		// most simple assumption: take the first person of the context
		// 	first check the type,  then check NER type
		for (DisambiguatedTerm t : terms){
			if (persons.containsKey(t.getCandidate().getIri())){				
				for (MappedTerm c : corefCandidates){
					
					ScoredTerm s = new ScoredTermImpl(c, new ArrayList<ScoredCandidate>());
					ScoredCandidate u = new ScoredCandidateImpl(t.getCandidate());
					DisambiguatedTerm d = new DisambiguatedTermImpl(s, u, true);
					list.add(d);
				}
			}
		}

		return list;
	}

}
