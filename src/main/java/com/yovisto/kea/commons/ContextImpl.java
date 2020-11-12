package com.yovisto.kea.commons;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ahocorasick.trie.Trie;

import com.yovisto.kea.ned.GraphElements;

/**
 * The Context implementation.
 */
public class ContextImpl implements Context {

	private static final long serialVersionUID = 1L;

	/** The terms. */
	private List<? extends Term> terms;

	private String contextId;

	private GraphElements graphElements;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yovisto.services.semantic.commons.Context#setTerms(java.util.List)
	 */
	@Override
	public void setTerms(List<? extends Term> terms) {
		this.terms = terms;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yovisto.services.semantic.commons.Context#getTerms()
	 */
	@Override
	public List<? extends Term> getTerms() {
		return this.terms;
	}

	public GraphElements getGraphElements() {
		return graphElements;
	}

	public void setGraphElements(GraphElements graph) {
		this.graphElements = graph;
	}

	public String getContextId() {
		return contextId;
	}

	public void setContextId(String contextId) {
		this.contextId = contextId;
	}

	private Set<String> candidatesUniqSurfaceForms;

	@Override
	public synchronized Set<String> getContextCandidatesUniqSurfaceForms() {
		if (candidatesUniqSurfaceForms == null) {
			candidatesUniqSurfaceForms = new HashSet<String>();
			for (Term t : terms){
				MappedTerm m = (MappedTerm)t;
				for (Candidate u : m.getCandidates()){
					for (Label l : u.getLabels()){
						if (l!=null && l.getValue()!=null){
							candidatesUniqSurfaceForms.add(l.getValue());
						}
					}
				}
			}
		}
		return candidatesUniqSurfaceForms;
	}
	
	private Trie trie;
	
	@Override
	public synchronized Trie getContextCandidateSurfaceFormsAsTrie(){
		if (trie == null){
			trie = new Trie();
			for (String candidate : getContextCandidatesUniqSurfaceForms()){
				trie.addKeyword(candidate);
			}
		}		
		return trie;		
	}

}
