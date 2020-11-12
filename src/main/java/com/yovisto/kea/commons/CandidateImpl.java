package com.yovisto.kea.commons;

import java.util.ArrayList;
import java.util.List;

/**
 * The URI implementation.
 */
public class CandidateImpl implements Candidate {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The iri. */
	private String iri;

	/** The labels. */
	private List<Label> labels;

	private List<Word> words;

	private List<String> categories;

	private Double distance;

	private List<String> domains;

	private String mainLabel;
	
	private String provenance;

	public String getProvenance() {
		return provenance;
	}

	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}

	public CandidateImpl(){

	}

	public CandidateImpl(String iri){
		this.iri = iri;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.services.semantic.commons.api.URI#setCategories(java.util.List)
	 */
	@Override
	public void setCategories(List<String> categories){
		this.categories = categories;
	}

	@Override public List<String> getCategories() {
		return categories;
	};

	@Override
	public void setDomains(List<String> domains){
		this.domains = domains;
	}

	@Override
	public List<String> getDomains(){
		return domains;
	}

	@Override
	public void addDomain(String domain){
		if(domains==null){
			domains = new ArrayList<String>();
			domains.add(domain);
		}else{
			if(!domains.contains(domain)){
				domains.add(domain);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.yovisto.services.semantic.commons.URI#setIri(java.lang.String)
	 */
	@Override
	public void setIri(String iri) {
		this.iri = iri;
	}
	/* (non-Javadoc)
	 * @see com.yovisto.services.semantic.commons.URI#getIri()
	 */
	@Override
	public String getIri() {
		return iri;
	}
	/* (non-Javadoc)
	 * @see com.yovisto.services.semantic.commons.URI#setLabels(java.util.List)
	 */
	@Override
	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}
	/* (non-Javadoc)
	 * @see com.yovisto.services.semantic.commons.URI#getLabels()
	 */
	@Override
	public List<Label> getLabels() {
		return labels;
	}
	/* (non-Javadoc)
	 * @see com.yovisto.services.semantic.commons.URI#setWords(java.util.List)
	 */

	@Override
	public void setWords(List<Word> words) {
		this.words = words;
	}
	/* (non-Javadoc)
	 * @see com.yovisto.services.semantic.commons.URI#getWords()
	 */
	@Override
	public List<Word> getWords() {
		return words;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.services.semantic.commons.api.URI#setDistance(java.lang.Double)
	 */
	@Override
	public void setDistance(Double distance){
		this.distance = distance;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.services.semantic.commons.api.URI#getDistance(java.lang.Double)
	 */
	@Override
	public Double getDistance(){
		return distance;
	}

	public String getMainLabel(){
		return mainLabel;
	}


	public void setMainLabel(String label){
		this.mainLabel = label;
	}

	@Override
	public int compareTo(Candidate o) {				
		return (this.getIri().compareTo(o.getIri()));		
	}

}
