package com.yovisto.kea.ner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Candidate;

public class StandardFilter implements MappedTermFilter {

	private static final long serialVersionUID = 6810042566461347709L;

	public static final List<String> nouns = Arrays.asList("NOUN", "NN", "FM", "NNPS", "NNS", "NNP", "NE", "JJ", "CD", "PROPN"); 	
	
	/**
	 * If a candidate is in this list, the entire term is removed (resp. its cndidate list is emptied)
	 *  
	 */
	private Set<String> termBlackList = new TreeSet<String>();
	
	/**
	 * If a candidate is in this list, the candidate is only removed. 
	 *  
	 */
	private Set<String> candidateBlackList = new TreeSet<String>();

	public StandardFilter() {	
		InputStream in = this.getClass().getResourceAsStream("/term_blacklist.properties");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));

		String line;
		try {
			while ((line = br.readLine()) != null) {					
				termBlackList.add(line.trim());				
			}
			br.close();
			in.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		InputStream in2 = this.getClass().getResourceAsStream("/candidate_blacklist.properties");
		BufferedReader br2 = new BufferedReader(new InputStreamReader(in2));

		String line2;
		try {
			while ((line2 = br2.readLine()) != null) {					
				candidateBlackList.add(line2.trim());				
			}
			br2.close();
			in2.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public List<MappedTerm> filter(List<MappedTerm> terms, Parameters params) {

		List<MappedTerm> filteredList = new ArrayList<MappedTerm>();

		for (MappedTerm term : terms) {
			
			if ((term.getCandidates() != null && term.getCandidates().size() > 0) || term.getSurfaceForm().startsWith("@")|| term.getSurfaceForm().startsWith("#")) {
			
			//if ((term.getCandidateUris() != null && term.getCandidateUris().size() > 0) ) {
				boolean termIsAllowed = false;				
				
				//if (hasNoun(term.getPartOfSpeech()))
				// länger als 3 oder mind. 2 Großbuchstaben
				if ((term.getSurfaceForm().trim().length() >= 3 || term.getSurfaceForm().matches("[A-Z0-9]+[A-Z0-9]+[A-Z0-9]+"))) {

					// wenn JJ (verb), dann nur wenn der erste
					// Großbuchstabe ist : Bsp: German YES,
					// advertise NO
					if (!term.getPartOfSpeech().equals("JJ") || (term.getPartOfSpeech().equals("JJ") && term.getSurfaceForm().matches("[A-Z]+.*"))) {
						// aber nicht "DT JJ": Bsp. "the Same"
						if (!term.getPartOfSpeech().equals("DT JJ")) {

							// cadinalities only if longer that 3
							// chars
							if (!term.getPartOfSpeech().equals("CD") || (term.getPartOfSpeech().equals("CD") && term.getSurfaceForm().length() > 3)) {
								termIsAllowed = true;																
							}
						}
					}
				}
				//"NNS", "NNP", "NN", "NNPS",  "NE"
				//if (! (term.getPartOfSpeech().contains("NNS")|| term.getPartOfSpeech().contains("NNP"))){
				if (! (term.getPartOfSpeech().contains("NN")| term.getPartOfSpeech().contains("NE")| term.getPartOfSpeech().contains("FM")| term.getPartOfSpeech().contains("CARD"))){
					termIsAllowed = false;
				}
				
				
				// remove blacklisted candidates
				List<Candidate> filteredCandidates = new ArrayList<Candidate>();
				for (Candidate uri : term.getCandidates() ) {
					if (! candidateBlackList.contains(uri.getIri())){
						filteredCandidates.add(uri);
					}
				}
				if (filteredCandidates.size()==0){
					termIsAllowed = false;
				}
				term.setCandidates(filteredCandidates);
				
				// if found candidates in termBlacklist, remove all candidates				
				for (Candidate uri : term.getCandidates() ) {
					if (termBlackList.contains(uri.getIri())){
						termIsAllowed = false;
					}
				}				
				
				// remove http terms
				if (term.getSurfaceForm().toLowerCase().startsWith("http")){
					termIsAllowed = false;
				}

				// for neel
				// remove @ terms
				if (term.getSurfaceForm().toLowerCase().startsWith("@")||term.getSurfaceForm().toLowerCase().startsWith("#")){
					termIsAllowed = true;
				}
				
				
				if (termIsAllowed){
					filteredList.add(term);
				}
			}			
		}
		return filteredList;
	}


}
