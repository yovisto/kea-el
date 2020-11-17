package com.yovisto.kea.ned;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.yovisto.kea.commons.Candidate;
import com.yovisto.kea.commons.CandidateImpl;
import com.yovisto.kea.commons.Context;
import com.yovisto.kea.commons.ContextImpl;
import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.commons.MappedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.commons.Word;
import com.yovisto.kea.ner.StandardStopListProvider;
import com.yovisto.kea.util.IndexAccess;
import com.yovisto.kea.util.JaroWinklerDistance;
import com.yovisto.kea.util.MD5;
import com.yovisto.kea.util.StringSupport;

import grph.DefaultIntSet;
import grph.Grph;
import grph.in_memory.InMemoryGrph;
import it.unimi.dsi.fastutil.ints.IntSet;

public class StandardGraphGenerator implements GraphGenerator, Serializable {

	private static final long serialVersionUID = -588421785915041160L;
	
	protected final Logger L = Logger.getLogger(getClass());

	@Inject
	private IndexAccess lucene;

	private Set<String> classesBlackList = new TreeSet<String>();

	private static final StandardStopListProvider sl = new StandardStopListProvider();

	//private String serFileName;
	//private String serFileNameStats;
	
	public Context createGraphContext(List<MappedTerm> mappedTerms, Parameters params) {
	

		lucene.setup(params);

		Grph g = new InMemoryGrph();

		Map<String, Map<String, Double>> stats = new TreeMap<String, Map<String, Double>>();

		// temporal helpful lists and sets
		Map<Integer, String> idToTerm = new TreeMap<Integer, String>();
		Map<Integer, String> idToUri = new TreeMap<Integer, String>();
		Map<String, Integer> termToId = new TreeMap<String, Integer>();
		Map<String, Integer> uriToId = new TreeMap<String, Integer>();

		Set<Integer> termIds = new TreeSet<Integer>();
		Set<Integer> candidateIds = new TreeSet<Integer>();
		Set<Integer> linkNodeIds = new TreeSet<Integer>();

		List<String> candidateUris = new ArrayList<String>();

		// the total value of each node (instead of storing it in the graph)
		@Deprecated
		Map<Integer, Double> values = new TreeMap<Integer, Double>();

		Map<Term, Set<Integer>> termVertices = new TreeMap<Term, Set<Integer>>();

		int runningNodeId = 0;
		Collections.sort(mappedTerms);
		for (MappedTerm t : mappedTerms) {

			Set<Integer> vertices = new TreeSet<Integer>();
			if (!termToId.containsKey(t.getSurfaceForm())) {
				int currentNodeId = runningNodeId++;

				// add to the indexes
				termIds.add(currentNodeId);
				//termToId.put(t.getSurfaceForm().toLowerCase(), currentNodeId);
				//idToTerm.put(currentNodeId, t.getSurfaceForm().toLowerCase());
				termToId.put(t.getSurfaceForm(), currentNodeId);
				idToTerm.put(currentNodeId, t.getSurfaceForm());

				// add to grph
				g.addVertex(currentNodeId);

				g.getVertexLabelProperty().setValue(currentNodeId, "TERM: " + t.getSurfaceForm());

				ArrayList<Candidate> uris = (ArrayList<Candidate>) t.getCandidates();
				Collections.sort(uris);
				for (Candidate uri : uris) {

					// check, if URI has an ID
					int candidateId;
					if (uriToId.containsKey(uri.getIri())) {
						// take the existing candidate ID
						candidateId = uriToId.get(uri.getIri());
					} else {
						// create a new candidate ID
						candidateId = runningNodeId++;

						// add to the indexes
						uriToId.put(uri.getIri(), candidateId);
						idToUri.put(candidateId, uri.getIri());
						candidateIds.add(candidateId);
						candidateUris.add(uri.getIri());
					}
					// add the edge between term and candidate
					g.addUndirectedSimpleEdge(currentNodeId, candidateId);
					vertices.add(candidateId);

				}
			}
			termVertices.put(t, vertices);
		}

		List<Integer> vertrices = new ArrayList<Integer>(g.getVertices()) ;
		Collections.sort(vertrices);
		//L.info("t: " + ((System.currentTimeMillis()-statTime)/1000) + "s");
		TreeSet<String> existingLinks = new TreeSet<String>();
		L.info("Initial Graph : " + g);

		// get links
		Set<String> keys = new TreeSet<String>(uriToId.keySet());
		int count = keys.size();
		Map<String, List<String>> linksFromIndex = getLinksParallel(keys, params);

		for (String uri1 : keys) {

			List<String> links = linksFromIndex.get(uri1);
			for (String link : links) {

				String from = uri1.replaceAll("\\n", "");
				String to = link.replaceAll("\\n", "");
				if (!blacklisted(from) && !blacklisted(to)) {
					if (!existingLinks.contains(from + to) && !existingLinks.contains(to + from)) {
						// check if this is a good one
						int fromId = runningNodeId++;
						int toId = runningNodeId++;

						if (!uriToId.containsKey(from)) {
							uriToId.put(from, fromId);
							idToUri.put(fromId, from);
						} else {
							fromId = uriToId.get(from);
						}
						if (!uriToId.containsKey(to)) {
							uriToId.put(to, toId);
							idToUri.put(toId, to);
						} else {
							toId = uriToId.get(to);
						}
						existingLinks.add(from + to);
						g.addUndirectedSimpleEdge(fromId, toId);

						// check if there is an interlink node
						// interlink nodes are not candidate nodes
						if (!candidateIds.contains(fromId)) {
							linkNodeIds.add(fromId);
						}
						if (!candidateIds.contains(toId)) {
							linkNodeIds.add(toId);
						}

					}
				}

			}
			if (count % 1000 == 0) {
				L.info("Loading nodes: " + count);
			}
			count--;
		}



		L.info("Graph with linking nodes: " + g);
		L.info("Term nodes: " + termIds.size());
		L.info("Candidate nodes: " + candidateIds.size());
		L.info("Linking nodes: " + linkNodeIds.size());
		L.info("ID to Term map: " + idToTerm.size());
		L.info("ID to URI map: " + idToUri.size());
		L.info("Term to ID map: " + termToId.size());
		L.info("URI to ID map: " + uriToId.size());
		
		
		Map<Integer, Integer> numOfLinkedTerms = new TreeMap<Integer, Integer>();
		Map<Integer, Integer> countUriAlls = new TreeMap<Integer, Integer>();
		
		
		

		List<Integer> noTermVertices = new ArrayList<Integer>();
		noTermVertices.addAll(candidateIds);
		noTermVertices.addAll(linkNodeIds);

		for (Integer i : noTermVertices) {
			values.put(i, 0.0);
		}

		// der graph, ohne terme (kann Einzelknoten enthalten)
		IntSet noTermIntSet = new DefaultIntSet(noTermVertices.size());
		noTermIntSet.addAll(noTermVertices);
		Grph noTermGraph = g.getSubgraphInducedByVertices(noTermIntSet);
		L.info("No term graph: " + noTermGraph + " v=" + noTermGraph.getSize());

		// wir benötigen jetzt einen graphen, bei dem die Interlink Knoten des
		// ingrades = 1 entfernt werden
		List<Integer> nois = new ArrayList<Integer>();
		for (int v : noTermGraph.getVertices()) {
			if (!linkNodeIds.contains(v)) {
				nois.add(v);
			} else {
				if (noTermGraph.getVertexDegree(v) > 2) {
					nois.add(v);
				}
			}
		}
		//L.info("t: " + ((System.currentTimeMillis()-statTime)/1000) + "s");
		// enthält keine Einzelknoten mehr, aber evtl. blätter
		IntSet noisIntSet = new DefaultIntSet(nois.size());
		noisIntSet.addAll(nois);
		Grph componentGraph = noTermGraph.getSubgraphInducedByVertices(noisIntSet);
		L.info("Component graph (large): " + componentGraph + " v=" + componentGraph.getSize());
		//L.info("t: " + ((System.currentTimeMillis()-statTime)/1000) + "s");
		Collection<IntSet> components = componentGraph.getConnectedComponents();
		L.info("Components found (large): " + components.size());
		//L.info("t: " + ((System.currentTimeMillis()-statTime)/1000) + "s");
		// jeder candidate knoten bekommt eine 1
		for (IntSet component : components) {
			// L.info("comp: " + component.size());
			for (int v : component) {
				if (linkNodeIds.contains(v)) {
					values.put(v, 0.0);
				} else {
					values.put(v, 1.0);
				}
			}
		}
		//L.info("t: " + ((System.currentTimeMillis()-statTime)/1000) + "s");
		Map<Integer, IntSet> componentsMap = new TreeMap<Integer, IntSet>();
		L.info("Working on component ");
		int componentNumber = 0;
		for (IntSet component : components) {
//			L.info("Component " + componentNumber);
			componentsMap.put(componentNumber++, component);

			// besorge den Graphen der Komponente
			Grph singleComponentGraph = g.getSubgraphInducedByVertices(component);

			/**
			 * // ausgehend von jedem Knoten eines Terms // füge Term + Knoten
			 * in Pfad ein (rechts) // lege Term + Knoten auf todo-Stack //
			 * solange Stack Elemente hat: // nehme das oberste Stack Element //
			 * füge es dem Pfad hinzu // wenn: hat nachbarn und nachbarterm
			 * wurden noch nicht besucht (sind nicht im Pfad, oder im Stack) //
			 * lege nachbarn auf den Stack // ansonsten: (hier gehts also nicht
			 * weiter) // speichere Pfad ab (denn jetzt gehts wieder zurück) //
			 * entferne das letzten Pfadelement
			 */


			// wie viele Terme gehören zu dieser Komponente?
			// je mehr desto besser
			int numTerms = 0;

			// für jeden Knoten in der Komponente
			for (int v : component) {

				// finde den dazugehörigen Term
				String term = "";
				for (Term t : termVertices.keySet()) {
					for (Integer i : termVertices.get(t)) {
						if (i == v) {
							term = t.getSurfaceForm();
							numTerms++;
						}
					}
				}

				int componentSize = component.size();
				
				// interpretationscore
				double numInterpretationScore = 0.0;
				int interpretationFreq = 0;

				// berechne String-Distanz zum Term
				double stringDistance = JaroWinklerDistance.similarity(term, idToUri.get(v));
				int distance = (int) Math.round(stringDistance * 0.1 * componentSize);

				// sammle statistiken auf
				int vertexDegree = singleComponentGraph.getVertexDegree(v);
				double current = values.get(v);
				
				values.put(v, current + vertexDegree + distance + numInterpretationScore + interpretationFreq );

				// jetzt die statistiken besfüllen
				Map<String, Double> statItem = new TreeMap<String, Double>();
				statItem.put("componentSize", componentSize * 1.0);
				statItem.put("numComponents", components.size() * 1.0);
				statItem.put("vertexDegree", vertexDegree * 1.0);
				statItem.put("stringDistance", stringDistance);
				statItem.put("purity", 1.0);
				statItem.put("numTermsOfComponent", numTerms * 1.0);
				statItem.put("max1", 0.0);
				statItem.put("numInterpretationScore", numInterpretationScore);
				statItem.put("interpretationFreq", interpretationFreq * 1.0);
				
				statItem.put("totalNumberOfTerms", termIds.size() * 1.0);
				if (numOfLinkedTerms.containsKey(v)){
					statItem.put("numberOfLinkedTerms", numOfLinkedTerms.get(v) * 1.0);
					statItem.put("basicDegree", countUriAlls.get(v) * 1.0);
				}else{
					statItem.put("numberOfLinkedTerms", 0.0);
					statItem.put("basicDegree", 0.0);
				}
				
				stats.put(idToUri.get(v), statItem); 
			}
		}
		//L.info("t: " + ((System.currentTimeMillis()-statTime)/1000) + "s");
		L.info("Checking purity.");

		// check, term purity
		for (int term : idToTerm.keySet()) {
			// check, if two or more terms neighbors belong to the same
			// component

			IntSet candidateVertices = g.getNeighbours(term);
			Map<Integer, List<Integer>> purity = new TreeMap<Integer, List<Integer>>();
			for (int c : candidateVertices) {

				// what is the component, this items belongs to
				for (Integer componentId : componentsMap.keySet()) {
					IntSet component = componentsMap.get(componentId);
					if (component.contains(c)) {
						try {
							purity.get(componentId).add(c);
						} catch (Exception e) {
							purity.put(componentId, new ArrayList<Integer>(Arrays.asList(c)));
						}
					}
				}
			}

			List<Integer> maxUnpures = new ArrayList<Integer>();
			double max1 = 0;

			// print the purity for this term
			for (Integer component : purity.keySet()) {
				if (purity.get(component).size() > 1) {

					for (Integer i : purity.get(component)) {
						if (max1 < values.get(i)) {

							max1 = values.get(i);
							maxUnpures = new ArrayList<Integer>();
							maxUnpures.add(i);
						}
						if (max1 == values.get(i)) {
							if (!maxUnpures.contains(i)) {
								maxUnpures.add(i);
							}
						}

					}
				}
			}

			if (maxUnpures.size() > 1) {
				//L.info("term: " + idToTerm.get(term));
				for (int i : maxUnpures) {
					double similarity = JaroWinklerDistance.similarity(idToTerm.get(term), idToUri.get(i));
					double factor = Math.round(similarity * 100);
					//L.info("to further disambiguate: " + i + " " + idToUri.get(i) + " v:" + values.get(i) + " m1:" + max1 + " string dist: " + factor + " = " + (values.get(i) + factor));
					double newWeight = values.get(i) + factor;
					values.put(i, newWeight);
					stats.get(idToUri.get(i)).put("purity", maxUnpures.size() * 1.0);
					stats.get(idToUri.get(i)).put("max1", max1);
				}
			}
		}
		//L.info("t: " + ((System.currentTimeMillis()-statTime)/1000) + "s");
		for (Integer id : values.keySet()) {

			if (idToUri.get(id) != null)
				if (stats.get(idToUri.get(id)) != null)
					stats.get(idToUri.get(id)).put("aggregated", values.get(id));
		}

		GraphElements e = new GraphElementsImpl();
		e.setStatistics(stats);
		e.setGraph(g);
		
		//L.info("t: " + ((System.currentTimeMillis()-statTime)/1000) + "s");
		int cnt = 0;
		for (int v : componentGraph.getVertices()) {
			boolean found = false;
			if (idToUri.containsKey(v)) {
				found = true;				
			}
			if (idToTerm.containsKey(v)) {
				found = true;
				componentGraph.getVertexLabelProperty().setValue(v, "TERM: " + idToTerm.get(v));
				componentGraph.getVertexColorProperty().setValue(v, 9);
				componentGraph.getVertexShapeProperty().setValue(v, 4);
			}
			if (!found) {
				L.info(cnt + " " + v);
			}
			cnt++;
		}

		
//		PrintWriter writer = null;
//		try {
//			writer = new PrintWriter("graph.dot", "UTF-8");
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (UnsupportedEncodingException e1) {
//			e1.printStackTrace();
//		}
//		L.info("wrote: graph.dot");
//		writer.write(componentGraph.toDot());
//		writer.close();

		Context context = new ContextImpl();
		context.setTerms(mappedTerms);
		context.setGraphElements(e);
		//L.info("t: " + ((System.currentTimeMillis()-statTime)/1000) + "s");
		//SerializationUtil.doSave(context.getGraphElements().getGraph().toGrphText(), serFileName);
		//SerializationUtil.doSave(context.getGraphElements().getStatistics(), serFileNameStats);
		return context;
	}

	@SuppressWarnings("unused")
	private String genId(List<MappedTerm> mappedTerms) {		
		List<String> tokens = new ArrayList<String>();		
    	for (Term t : mappedTerms){    		
    		for (Word w: t.getWords()){
    			tokens.add(w.getValue());
    		}
    	}    	
    	Collections.sort(tokens);    	
    	L.info(StringUtils.join(tokens, ""));
		return MD5.getInstance().toMD5(StringUtils.join(tokens, ""));
	
	}

	@SuppressWarnings("unused")
	private String getTerm(Integer v, Map<Term, Set<Integer>> termVertices) {
		String term = null;
		for (Term t : termVertices.keySet()) {
			for (Integer i : termVertices.get(t)) {
				if (i.intValue() == v.intValue()) {
					term = t.getSurfaceForm();
				}
			}
		}
		return term;
	}

	private long candidateTime = 0; 
	@SuppressWarnings("unused")
	private Map<String, Term> checkAdditionalCandidates(Map<String, List<String>> linksFromIndex, List<MappedTerm> mappedTerms, List<String> candidateUris) {
		long start = System.currentTimeMillis();
		Map<String, Term> additionalCandidates = new HashMap<String, Term>();
		try {
			for (List<String> links : linksFromIndex.values()) {
				for (String link : links) {
					link = link.replaceAll("\\n", "");
					//String[] linkTokens = link.toLowerCase().split("_");
					String linkToken = link.replaceAll("_", " ");
					if (!candidateUris.contains(link)) {
						//int degree = sary.getIndegree(link);
						//if (degree > 2) {

							for (MappedTerm t : mappedTerms) {
								//String[] surfaceTokens = t.getSurfaceForm().toLowerCase().split(" ");
								String surfaceToken = t.getSurfaceForm();
								//for (String linkToken : linkTokens) {
									if (!sl.getStopList(Lang.EN).contains(linkToken.toCharArray()) && !sl.getStopList(Lang.DE).contains(linkToken.toCharArray())) {
										//for (String surfaceToken : surfaceTokens) {
//											L.info(linkToken + "  contains  " + surfaceToken  + "  ?" );
											if (linkToken.contains(surfaceToken)) {
												boolean candidateExists = false;
												for (MappedTerm t2 : mappedTerms) {
													for (Candidate uri : t2.getCandidates()) {
														if (uri.getIri().equals(link)) {
															candidateExists = true;
															break;
														}
													}
												}
												if (!candidateExists) {
													Candidate uri = new CandidateImpl(link);
													uri.setWords(StringSupport.toWordList(link));
													t.getCandidates().add(uri);
													//L.info("additional: " + link);
													additionalCandidates.put(link, t);
												}
											}
										}
									}
//								}
							//}
//						}
					}
				}
			}
		} catch (Exception e) {

		}
		
		long est = System.currentTimeMillis() - start;
		candidateTime = candidateTime + est;
		L.info("total time for additional candidates: " + candidateTime/1000);
		return additionalCandidates;
	}

	private Map<String, List<String>> getLinksParallel(Set<String> keys, Parameters params) {
		ExecutorService executorLucene = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		// ExecutorService executorLucene = Executors.newFixedThreadPool(1);
		Set<Future<TO>> setLucene = new HashSet<Future<TO>>();
		for (String key : keys) {
			Callable<TO> worker = new LuceneWorker(key, params);
			Future<TO> future = executorLucene.submit(worker);
			setLucene.add(future);

		}

		executorLucene.shutdown();
		while (!executorLucene.isTerminated()) {
		}
		Map<String, List<String>> results = new TreeMap<String, List<String>>();

		for (Future<TO> future : setLucene) {
			try {
				if (results.containsKey(future.get().uri)) {
					results.get(future.get().uri).addAll(future.get().links);
				} else {
					results.put(future.get().uri, future.get().links);
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return results;
	}

	private boolean blacklisted(String uri) {
		if (classesBlackList.contains(uri)) {
			return true;
		}
		return false;
	}

	public class TO implements Serializable {

		private static final long serialVersionUID = -6898354892644734967L;
		String uri;
		List<String> links;
	}

	public class LuceneWorker implements Callable<TO> {

		String text = "";
		Parameters params ;

		public LuceneWorker(String text, Parameters params) {
			this.text = text;
			this.params = params;
		}

		@Override
		public TO call() throws Exception {

			List<String> result = lucene.getLinks( text, params.getAdditionalLinks());
			TO to = new TO();
			to.links = result;
			to.uri = text;
			return to;
		}
	}
}
