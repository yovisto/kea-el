package com.yovisto.kea.eval;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.yovisto.kea.EntityResolver;
import com.yovisto.kea.ParameterPresets;
import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.ScoredCandidate;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.guice.KeaModule;

public class EvaluationTool {

	public class EvalResult {
		double prec;
		double rec;
		double f;
		String[] scorers;
	}

	private DecimalFormat f = new DecimalFormat("#0.0000");

	private Injector injector = Guice.createInjector(new KeaModule());

	private EntityResolver resolver = injector.getInstance(EntityResolver.class);

	public EvaluationTool() {
		DecimalFormatSymbols decimalSymbol = new DecimalFormatSymbols(Locale.getDefault());
		decimalSymbol.setDecimalSeparator('.');
		f.setDecimalFormatSymbols(decimalSymbol);
		// System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
	}

	
	public static List<String> testData = Arrays.asList(
			"<a href='Angelina_Jolie'>Angelina</a> ist mal wieder als <a href='Tomb_Raider'>Tomb Raider</a> unterwegs.",
			"Das <a href='Angelina_(Café)'>Angelina</a> hat in ganz <a href='Frankreich'>Frankreich</a> die besten <a href='Gebäck'>Teilchen<(a>.", 
			"<a href='Neil_Armstrong'>Armstrong</a> landet auf dem <a href='Mond'>Mond</a>.",
			"<a href='Louis_Armstrong'>Armstrong</a> spielt auf der <a href='Jazztrompete'>Trompete</a>.",
			"<a href='Rom'>Rom</a> ist die <a href='Hauptstadt'>Hauptstadt</a> von <a href='Italien'>Italien</a>");
	
	
	
	ArrayList<String> annotatedData = new ArrayList<String>();

	public EvalResult evaluate(Parameters params) throws Exception {
		String datasetName = "simple";
		annotatedData.addAll(testData);

		int numFound = 0;
		int numNotFound = 0;
		int numWrong = 0;
		int TODO = annotatedData.size();
		StringBuffer all = new StringBuffer();
		Set<String> totalGoldSet = new TreeSet<String>();
		Set<String> totalCandidateSet = new TreeSet<String>();
		for (int textNo = 0; textNo < annotatedData.size(); textNo++) {
			if (true) {
				// if (annotatedData.get(textNo).contains("Bosch")) {
				System.out.println("#######################################################################################################################################");
				Set<String> resultset = new TreeSet<String>();
				String text = annotatedData.get(textNo);
				System.out.println("--> TEXT-BEGIN");
				System.out.println(text);
				System.out.println("--> TEXT-END");

				// for NED only comment out this 2 lines
			//	text = stripHTML(annotatedData.get(textNo));
			//	System.out.println(stripHTML(annotatedData.get(textNo)));

				Set<String> goldset = genGold(annotatedData.get(textNo));
				// Parameters params =
				// ParameterPresets.getDefaultParametersWithExplain();

				params.addProperty("contextId", datasetName + textNo);
				List<DisambiguatedTerm> result = resolver.resolve(text, params);
				// SerializationUtil.doSave(result, "currentContext.dat");

				double maxScore = 0.0;
				double sum = 0.0;

				for (DisambiguatedTerm disambiguatedTerm : result) {
					maxScore = Math.max(disambiguatedTerm.getScore(), maxScore);
					sum = sum + disambiguatedTerm.getScore();
				}
				// check the all candidate set
				Set<String> allCandidates = new HashSet<String>();
				for (DisambiguatedTerm disambiguatedTerm : result) {
					for (Candidate c : disambiguatedTerm.getCandidates()) {						
						allCandidates.add(c.getIri());
					}
				}

				double meanScore = sum / result.size();
				double threshold = 0.9 * meanScore;
				double goodThres = 0.0;
				int goodThresNum = 0;
				double badThres = 0.0;
				int badThresNum = 0;

				Collections.sort(result);

				for (DisambiguatedTerm disambiguatedTerm : result) {
					String uri = disambiguatedTerm.getCandidate().getIri();
					System.out.println("#######################################################################################################################################".replaceAll("#", "+"));
					
					System.out.println("Term: " + disambiguatedTerm.getWords().toString() + " --> " + URLDecoder.decode(uri, "UTF-8") + " " + disambiguatedTerm.getScore() + " " + ((ScoredCandidate) disambiguatedTerm.getCandidate()).getTotalScore());
					boolean OK = false;
					if (disambiguatedTerm.isAccepted()) {

						System.out.println((goldset.contains(URLDecoder.decode(uri, "UTF-8")) ? "\t OK " : "\t XX ( OK, but not in gold set --> False Positive) "));
						resultset.add(URLDecoder.decode(uri, "UTF-8"));

						if (goldset.contains(URLDecoder.decode(uri, "UTF-8"))) {
							OK = true;
							goodThres = goodThres + disambiguatedTerm.getScore();
							goodThresNum++;
						}
					} else {
						System.out.println((goldset.contains(URLDecoder.decode(uri, "UTF-8")) ? "\t ++ (correct, but classifier reject or score too small --> Wrong Negative) " : "\t -- (classifier reject or score too small, and not in gold set --> True Negative)"));
						if (goldset.contains(URLDecoder.decode(uri, "UTF-8"))) {
							goodThres = goodThres + disambiguatedTerm.getScore();
							goodThresNum++;
						} else {
							badThres = badThres + disambiguatedTerm.getScore();
							badThresNum++;
						}
					}

					int count = 0;
					if (!OK)
						if (disambiguatedTerm.getScoredCandidates() != null) {
							List<ScoredCandidate> suris = disambiguatedTerm.getScoredCandidates();
							Collections.sort(suris);
							Collections.reverse(suris);
							boolean goldFound = false;
							for (ScoredCandidate suri : suris) {
								if (count++ < 5) {
									goldFound = inGold(suri, goldset);
									dumpURI2(suri, goldset);
								}
								if (!goldFound) {
									if (inGold(suri, goldset)) {
										dumpURI2(suri, goldset);
									}
								}
							}
						}
				}

				System.out.println("--------");
				System.out.println("Threshold       : " + threshold);
				System.out.println("Threshold (good): " + goodThres / goodThresNum);
				System.out.println("Threshold (bad) : " + badThres / badThresNum);

				System.out.println("--------");

				Set<String> found = new HashSet<String>();
				found.addAll(goldset);
				found.retainAll(resultset);
				System.out.println("Found (TP): " + found.size() + " (" + (100.0 * found.size() / goldset.size()) + "%)");
				numFound = numFound + found.size();

				Set<String> notFound = new HashSet<String>();
				notFound.addAll(goldset);
				notFound.removeAll(resultset);
				System.out.println("Not found (FN): " + notFound.size() + " (" + (100.0 * notFound.size() / goldset.size()) + "%)");
				numNotFound = numNotFound + notFound.size();

				Set<String> wrong = new HashSet<String>();
				wrong.addAll(resultset);
				wrong.removeAll(goldset);
				System.out.println("Wrong (FP): " + wrong.size());
				System.out.println("--------");
				numWrong = numWrong + wrong.size();

				Set<String> cfound = new HashSet<String>();
				cfound.addAll(goldset);
				cfound.retainAll(allCandidates);
				System.out.println("Candidates Found (TP): " + cfound.size() + " (" + (100.0 * cfound.size() / goldset.size()) + "%)");
				for (String ccc : cfound) {
					System.out.println("Candidate found: " + ccc);
				}

				Set<String> cnotFound = new HashSet<String>();
				cnotFound.addAll(goldset);
				cnotFound.removeAll(allCandidates);
				System.out.println("Candidates Not found (FN): " + cnotFound.size() + " (" + (100.0 * cnotFound.size() / goldset.size()) + "%)");
				for (String ccc : cnotFound) {
					System.out.println("Candidate not found: " + ccc);
				}

				System.out.println("Candidates Total: " + allCandidates.size());
				double cr = 1.0 * cfound.size() / (cfound.size() + cnotFound.size());
				System.out.println("Max Possible Rec : " + cr);
				totalGoldSet.addAll(goldset);
				totalCandidateSet.addAll(allCandidates);
				System.out.println("--------");

				double p = 1.0 * found.size() / (found.size() + wrong.size());
				double r = 1.0 * found.size() / (found.size() + notFound.size());
				double f = (2 * p * r / (p + r));
				System.out.println("Prec: " + p);
				System.out.println("Rec : " + r);
				System.out.println("F   : " + f);

				System.out.println("--------");
				
				//
				// all.append("NUM: " + textNo + "  p=" + p + " r=" + r + " f="
				// + f
				// + "  found=" + found.size() + " notFound=" + notFound.size()
				// +
				// " wrong=" + wrong.size() );
				// all.append("\n");
				// all.append("all:    p=" + tp + " r=" + tr + " f=" + tf+
				// "  found=" + numFound + " notFound=" + numNotFound +
				// " wrong=" +
				// numWrong );
				// all.append("\n");
				System.out.println(all.toString());

				System.out.println("++++++++++++");
				for (String foo : found) {
					System.out.println("found:  " + foo);
				}
				for (String goo : goldset) {
					System.out.println("gold:  " + goo);
				}
				for (String noo : notFound) {
					System.out.println("notfound:  " + noo);
				}
				for (String woo : wrong) {
					System.out.println("wrong:  " + woo);
				}
				System.out.println("++++++++++++");
				// break;

				double tp = 1.0 * numFound / (numFound + numWrong);
				double tr = 1.0 * numFound / (numFound + numNotFound);
				double tf = (2 * tp * tr / (tp + tr));
				System.out.println("total Prec: " + tp);
				System.out.println("total Rec : " + tr);
				System.out.println("total F   : " + tf);

				System.out.println("--\n todo : " + TODO--);
				System.out.println("++++++++++++");
				System.out.println("Timing: ");
				Map<String, Long> timings = resolver.getScoreGenerator().getTimings();
				for (String m : timings.keySet()) {
					System.out.println(m + ": " + timings.get(m));
				}
			}
		}
		double tp = 1.0 * numFound / (numFound + numWrong);
		double tr = 1.0 * numFound / (numFound + numNotFound);
		EvalResult res = new EvalResult();
		res.prec = tp;
		res.rec = tr;
		res.f = (2 * tp * tr / (tp + tr));
		res.scorers = params.getStringArray(Parameters.SCORERS);
		resolver.getScoreGenerator().shutdown();
		return res;
	}


	public static boolean inGold(Candidate suri, Set<String> goldset) throws UnsupportedEncodingException {
		boolean gold = false;

		try {
			String candidate = URLDecoder.decode(suri.getIri(), "UTF-8");
			gold = goldset.contains(candidate);
		} catch (IllegalArgumentException e) {

		}
		return gold;
	}

	@SuppressWarnings("unused")
	private void dumpURI(ScoredCandidate suri, Set<String> goldset) throws UnsupportedEncodingException {

		System.out.print(" >> " + suri.getIri() + "\tClassifier: " + suri.isClassifierAccepted());

		System.out.println("\tin gold:" + inGold(suri, goldset) + " ");
		List<String> methods = new ArrayList<String>(suri.getScores().keySet());
		Collections.sort(methods);

		for (String m : methods) {
			System.out.println("\t" + m + " " + f.format(suri.getScores().get(m).getNormalizedValue()) + " ");
			// System.out.println("\t\t  value (normalized)");
			for (int i = 0; i < suri.getScores().get(m).getVector().size(); i++) {
				System.out.println("\t\t" + f.format(suri.getScores().get(m).getNormalizedVector().get(i)) + " (" + f.format(suri.getScores().get(m).getVector().get(i)) + ")");
			}
			System.out.println("\t\tscore:= sum / num --> " + f.format(suri.getScores().get(m).getNormalizedValue()) + " (" + f.format(suri.getScores().get(m).getValue()) + ")");
		}
		System.out.println("     final= sumAll / numAll --> " + f.format(suri.getTotalScore()));
		System.out.println();

	}

	private void dumpURI2(ScoredCandidate suri, Set<String> goldset) throws UnsupportedEncodingException {
		System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.println(" >> " + suri.getIri() + "\n\t   classifier: " + suri.isClassifierAccepted());
		System.out.println("\t   in gold:" + inGold(suri, goldset) + "\n");
		// find the table height ()
		int height = 0;
		List<String> methods = new ArrayList<String>(suri.getScores().keySet());
		for (String m : methods) {
			height = Math.max(height, suri.getScores().get(m).getNormalizedVector().size());
		}
		// init table columnwise
		String[][] columns = new String[height + 3][methods.size() + 1];
		// for each method, fill a column
		int colNo = 1;
		for (String m : methods) {
			String headerCell = m.replace("Scorer", "").replace("StringDistance", "StrDist").replace("Suffix", "Sfx");
			columns[0][colNo] = headerCell;

			for (int i = 0; i < suri.getScores().get(m).getVector().size(); i++) {
				String contentCell = f.format(suri.getScores().get(m).getNormalizedVector().get(i)) + " (" + f.format(suri.getScores().get(m).getVector().get(i)) + ")";
				columns[i + 1][colNo] = contentCell;
			}
			String footerCell = f.format(suri.getScores().get(m).getNormalizedValue()) + " (" + f.format(suri.getScores().get(m).getValue()) + ")";
			columns[height + 2][colNo] = footerCell;
			colNo++;
		}
		columns[0][0] = "Score";
		columns[height + 2][0] = f.format(suri.getTotalScore()) + " <--";

		// now we have filled the columns
		// now print row wise
		for (int i = 0; i < height + 3; i++) {
			for (int j = 0; j <= methods.size(); j++) {
				String cell = columns[i][j];
				if (cell == null)
					cell = "";
				if (j > 0) {
					System.out.printf("%-18s", cell);
				} else {
					System.out.printf("%-11s", cell);
				}

			}
			System.out.println();
		}
		
	}

	public static Set<String> genGold(String text) throws UnsupportedEncodingException {
		Set<String> result = new HashSet<String>();
		String[] tokens = text.split("<a ");
		for (String t : tokens) {
			if (t.startsWith("href='")) {
				t=t.replace("href='","");
				String url = URLDecoder.decode(t.substring(0, t.indexOf("'")), "UTF-8").replaceAll("", "");
				// System.out.println("gold: " + url);
				result.add(url);
			}
		}
		return result;
	}

	public static String stripHTML(String string) {
		return string.replaceAll("<(.|\\n)*?>", "");
	}

	

	public static void main(String[] args) throws Exception {
		EvaluationTool e = new EvaluationTool();
		
		Parameters p = ParameterPresets.getDefaultParameters();
		p.setAdditionalLinks(new HashSet<String>(Arrays.asList("Apollo_11\tHarry_S._Truman")));
		p.setAdditionalSurfaces(new HashSet<String>(Arrays.asList("harry\tHarry_S._Truman")));
		e.evaluate(p);
	}

}
