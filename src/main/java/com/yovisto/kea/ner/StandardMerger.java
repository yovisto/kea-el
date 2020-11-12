package com.yovisto.kea.ner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Candidate;

public class StandardMerger implements MappedTermMerger {

	protected final Logger L = Logger.getLogger(getClass());
	
	private int numShingles;
	private List<String> nouns = Arrays.asList("NOUN", "CD", "NN", "NNPS", "NNS", "NNP", "NE", "JJ", "VBZ", "VBD", "VBP", "PROPN", "FM", "CARD");
	private List<String> mergeNouns = Arrays.asList("NOUN", "NN", "NNPS", "NNS", "NNP", "NE");
	private List<String> mergeIndicator = Arrays.asList("DT" ,"IN");

	private List<String> doNotMergeFirstPOSof = Arrays.asList("APPR" ,"ART");
	
	private boolean explain = false;
	private boolean noMerging = false;
	private static final long serialVersionUID = -2014503762543850286L;

	private void setParams(Parameters params) {
		numShingles = params.getInt(Parameters.NUM_SHINGLES);
		if (params.containsKey(Parameters.EXPLAIN_MERGING))
			explain = params.getBoolean(Parameters.EXPLAIN_MERGING);
		if (params.containsKey(Parameters.NO_MERGING))
			noMerging = params.getBoolean(Parameters.NO_MERGING);

	}

	@Override
	public List<MappedTerm> merge(List<MappedTerm> terms, Parameters params) {
		setParams(params);
		if (explain) {
			for (MappedTerm item : terms) {
				L.info(item.toString());
			}
		}
		Collections.reverse(terms);

		for (int shingle = numShingles; shingle > 0; shingle--) {
			if (explain)
				L.info("# Mapping shingle size " + shingle);

			for (MappedTerm term : terms) {				
				String lastPos = term.getPartOfSpeech().split(" ")[term.getPositionLength() - 1];
				String firstPos = term.getPartOfSpeech().split(" ")[0];

				if ( (nouns.contains(lastPos) || term.getSurfaceForm().startsWith("@")|| term.getSurfaceForm().startsWith("#")) && term.getPositionLength() == shingle && term.isSkip() == false &&!doNotMergeFirstPOSof.contains(firstPos)) {
					if (explain)
						L.info(term.getPosition() + " " + term.toString() + " " + ((term.getCandidates() == null) ? "--" : term.getCandidates().size()));

					if (term.getCandidates().size() > 0) {

						// find and set smaller items to skip
						// skip all with the same position
						for (MappedTerm itemToSkip : terms) {
							if (term.getPosition() == itemToSkip.getPosition() && term != itemToSkip) {
								// String[] partOfSpeech =
								// term.getPartOfSpeech().split(" ");
								// if
								// (!mergeIndicator.contains(partOfSpeech[0]))
								// {
								itemToSkip.setSkip(true);
								if (explain)
									L.info(" skipsame: " + itemToSkip.toString());
								// }
							}
						}

						// skip all next items with the position + (
						// 1
						// to
						// shingle)
						// but only if length = <=
						// for (int i = 1; i < shingle; i++) {
						for (MappedTerm itemToSkip : terms) {
							if (itemToSkip.getPosition() > term.getPosition() && itemToSkip.getPosition() < term.getPosition() + shingle) {

								String[] partOfSpeech = term.getPartOfSpeech().split(" ");
								// wenn der das erste POS ein the,
								// in,
								// oder to ist, wird gemerged

								itemToSkip.setSkip(true);
								if (explain)
									L.info(" skipnext: " + itemToSkip.toString());

								if (mergeIndicator.contains(partOfSpeech[0]) && mergeNouns.contains(lastPos) && itemToSkip.getPositionLength() == term.getPositionLength() - 1) {
									String lastPosItemToSkip = itemToSkip.getPartOfSpeech().split(" ")[itemToSkip.getPositionLength() - 1];
									if ((nouns.contains(lastPosItemToSkip)|| term.getSurfaceForm().startsWith("@")|| term.getSurfaceForm().startsWith("#")) && !noMerging) {
										term.getMergeItems().add(itemToSkip);
										itemToSkip.setSkip(false);
										itemToSkip.setIsMergeItem(true);
										if (explain)
											L.info(" merge from next: " + itemToSkip.toString());
									}
								}
							}
						}

						// and the same for the other direction
						for (MappedTerm itemToSkip : terms) {
							if (itemToSkip.getPosition() < term.getPosition() && itemToSkip.getPosition() > term.getPosition() - numShingles - 1 && itemToSkip.getPositionLength() > (term.getPosition() - itemToSkip.getPosition())) {

								String[] partOfSpeech = term.getPartOfSpeech().split(" ");

								itemToSkip.setSkip(true);
								if (explain)
									L.info(" skipprev: " + itemToSkip.toString());

								if (mergeIndicator.contains(partOfSpeech[0]) && mergeNouns.contains(lastPos) && itemToSkip.getPositionLength() == term.getPositionLength() - 1) {
									String lastPosItemToSkip = itemToSkip.getPartOfSpeech().split(" ")[itemToSkip.getPositionLength() - 1];
									if ((nouns.contains(lastPosItemToSkip) || term.getSurfaceForm().startsWith("@")|| term.getSurfaceForm().startsWith("#")) && !noMerging) {
										term.getMergeItems().add(itemToSkip);
										itemToSkip.setSkip(false);
										itemToSkip.setIsMergeItem(true);
										if (explain)
											L.info(" merge from prev: " + itemToSkip.toString());
									}
								}
							}
						}
					}
				}
			}
		}

		List<MappedTerm> mergedItems = mergeTerms(terms);

		return mergedItems;
	}

	private List<MappedTerm> mergeTerms(List<MappedTerm> terms) {
		// handle items to merge
		if (explain)
			L.info("\nMERGING:");
		List<MappedTerm> mergedItems = new ArrayList<MappedTerm>();
		for (MappedTerm item : terms) {

			if (explain)
				L.info("ITEM: " + item.toString());

			String lastPos = item.getPartOfSpeech().split(" ")[item.getPositionLength() - 1];
			// L.info(lastPos);
			if ((nouns.contains(lastPos)|| item.getSurfaceForm().startsWith("@")|| item.getSurfaceForm().startsWith("#"))) {
				if (item.getMergeItems() != null)
					for (MappedTerm mergeItem : item.getMergeItems()) {
						// if this item is not for merging, just copy it
						if (mergeItem.getCandidates() != null) {
							if (explain)
								L.info("merge: " + mergeItem.toString());
							if (explain)
								L.info("  into: " + item.toString());
							if (item.getCandidates() == null) {
								item.setCandidates(new ArrayList<Candidate>());
							}
							item.getCandidates().addAll(mergeItem.getCandidates());
							item.setSkip(false);
							// mergeItem.candidates = new HashSet<URI>();
						}
					}
				if (!item.isSkip() && !item.isMergeItem() && item.getCandidates() != null && item.getCandidates().size() > 0) {
					mergedItems.add(item);
				}
			}
		}
		return mergedItems;
	}

}
