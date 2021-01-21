package com.yovisto.kea.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;

import com.yovisto.kea.ParameterPresets;
import com.yovisto.kea.commons.Parameters;

public class IndexAccessImpl implements IndexAccess {

	protected final Logger L = Logger.getLogger(getClass());

	private static String LINKS_INDEX = "/var/indices/lucene/links";
	private static String LABEL_INDEX = "/var/indices/lucene/labels";

	private IndexReader linksReader = null;
	private IndexSearcher linksSearcher = null;
	private IndexReader labelsReader = null;
	private IndexSearcher labelsSearcher = null;

	private static final String CLEAN_PATTERN = "[^a-zA-Z_öäüßÖÄÜéèÈÉ0-9\\s\\-]+";

	private Map<String, String> trainedSurfaces = null;

	@Override
	public synchronized List<String> getIrisForLabel(String surfaceForm, HashSet<String> additionalSurfaces) {
		if (trainedSurfaces == null && additionalSurfaces != null) {
			trainedSurfaces = new HashMap<String, String>();
			for (String line : additionalSurfaces) {
				StringTokenizer st2 = new StringTokenizer(line, "\t");
				if (st2.countTokens() == 2) {
					String surface = st2.nextToken();
					String uri = st2.nextToken();
					trainedSurfaces.put(surface.toLowerCase().trim().replaceAll(" ", "_"), uri);
				} else {
					L.info("skipping: " + line);
				}

			}
		}
		return getIrisForLabel(surfaceForm);
	}

	@Override
	public List<String> getIrisForLabel(String surfaceForm) {

		List<String> result = new ArrayList<String>(
				searchIRIsForLabel(surfaceForm.toLowerCase().replace(" ", "_").replaceAll(CLEAN_PATTERN, "")));

		if (trainedSurfaces != null && trainedSurfaces.containsKey(surfaceForm.toLowerCase())) {
			String uri = trainedSurfaces.get(surfaceForm.toLowerCase());
			result.add(uri);
		}

		return result;
	}

	@Override
	public void setup(Parameters params) {
		LINKS_INDEX = params.getString(Parameters.DATA_PATH) + "/links";
		LABEL_INDEX = params.getString(Parameters.DATA_PATH) + "/labels";

		if (linksReader == null) {

			// L.info("Setting up lucene access");
			try {
				File linksDir = new File(LINKS_INDEX);
				if (linksDir.exists()) {
					linksReader = DirectoryReader.open(NIOFSDirectory.open(linksDir));
					linksSearcher = new IndexSearcher(linksReader);
				}

				File labelsDir = new File(LABEL_INDEX);
				if (labelsDir.exists()) {
					labelsReader = DirectoryReader.open(NIOFSDirectory.open(new File(LABEL_INDEX)));
					labelsSearcher = new IndexSearcher(labelsReader);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private Map<String, List<String>> trainedLinks = null;

	@Override
	public void resetTraining() {
		trainedLinks = null;
		trainedSurfaces = null;
	};

	@Override
	public synchronized List<String> getLinks(String uri, HashSet<String> additionalLinks) {
		if (trainedLinks == null && additionalLinks != null) {
			trainedLinks = new HashMap<String, List<String>>();
			for (Object obj : additionalLinks) {
				String line = (String) obj;
				if (line.split("\t").length == 2) {
					String from = line.split("\t")[0];
					String to = line.split("\t")[1];

					if (trainedLinks.containsKey(from)) {
						trainedLinks.get(from).add(to);
					} else {
						trainedLinks.put(from, new ArrayList<String>(Arrays.asList(to)));
					}

					// and the back direction
					if (trainedLinks.containsKey(to)) {
						trainedLinks.get(to).add(from);
					} else {
						trainedLinks.put(to, new ArrayList<String>(Arrays.asList(from)));
					}
				}
			}
			L.info("Added " + trainedLinks.size() + " trained links from list.");
		}
		return getLinks(uri);
	}

	@Override
	public List<String> getLinks(String uri) {

		List<String> result = new ArrayList<String>();
		try {
			QueryParser parser = new QueryParser(Version.LUCENE_45, "entity", new KeywordAnalyzer());
			Query query = parser.parse("\"" + escape(uri) + "\"");
			TotalHitCountCollector counter = new TotalHitCountCollector();
			linksSearcher.search(query, counter);
			int num = counter.getTotalHits();
			if (num > 0) {
				TopDocs docs = linksSearcher.search(query, num);
				for (ScoreDoc hit : docs.scoreDocs) {
					Document doc = linksSearcher.doc(hit.doc);
					String links = doc.get("links");

					for (String link : links.split(" ")) {
						result.add(link);
					}
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (trainedLinks != null) {
			try {
				result.addAll(trainedLinks.get(uri));
			} catch (Exception e) {

			}
		}
		return result;
	}

	private String escape(String uri) {
		uri = uri.replaceAll("\\\"", "\\\\\"");
		return uri;
	}

	private Set<String> searchIRIsForLabel(String surfaceForm) {
		Set<String> result = new TreeSet<String>();
		try {
			QueryParser parser = new QueryParser(Version.LUCENE_45, "l", new KeywordAnalyzer());
			Query query = parser.parse("\"" + surfaceForm.toLowerCase() + "\"");
			TotalHitCountCollector counter = new TotalHitCountCollector();
			labelsSearcher.search(query, counter);
			int num = counter.getTotalHits();
			if (num > 0) {
				TopDocs docs = labelsSearcher.search(query, num);
				for (ScoreDoc hit : docs.scoreDocs) {
					Document doc = labelsSearcher.doc(hit.doc);
					result.add(doc.get("iri"));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public Set<String> getLabelsForIRI(String iri) {
		Set<String> result = new TreeSet<String>();
		try {
			QueryParser parser = new QueryParser(Version.LUCENE_45, "iri", new KeywordAnalyzer());
			Query query = parser.parse("\"" + iri + "\"");
			TotalHitCountCollector counter = new TotalHitCountCollector();
			labelsSearcher.search(query, counter);
			int num = counter.getTotalHits();
			if (num > 0) {
				TopDocs docs = labelsSearcher.search(query, num);
				for (ScoreDoc hit : docs.scoreDocs) {
					Document doc = labelsSearcher.doc(hit.doc);
					for (IndexableField f : doc.getFields("l")) {
						result.add(f.stringValue());
					}

				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unused")
	private void indexLinks() throws IOException {
		Directory dir = FSDirectory.open(new File(LINKS_INDEX));
		Analyzer analyzer = new KeywordAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_45, analyzer);
		IndexWriter writer = new IndexWriter(dir, iwc);
		int count = 0;
		FileInputStream fis = null;
		try {

			fis = new FileInputStream(LINKS_INDEX + ".txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF8"));

			String strLine;
			String currentEntity = "";
			String currentData = "";
			String splt = " ";
			while ((strLine = br.readLine()) != null) {
				if (strLine.split(splt).length == 2) {

					String entity = strLine.split(splt)[0].trim();
					String link = strLine.split(splt)[1].trim();
					entity = StringEscapeUtils.unescapeHtml(entity);
					entity = StringEscapeUtils.unescapeHtml(entity);
					entity = entity.replaceAll("\\<.*?>", "");
					entity = entity.replace(" ", "_");

					link = StringEscapeUtils.unescapeHtml(link);
					link = StringEscapeUtils.unescapeHtml(link);
					link = link.replaceAll("\\<.*?>", "");
					link = link.replace(" ", "_");

					if (currentEntity != null && !currentEntity.equals(entity)) {
						// L.info(currentEntity + " " +
						// currentData);

						Document doc = new Document();
						Field field = new StringField("entity", currentEntity, Field.Store.YES);
						doc.add(field);
						Field links = new StringField("links", currentData, Field.Store.YES);
						doc.add(links);
						writer.addDocument(doc);

						currentEntity = entity;
						currentData = link;
					} else {
						currentData = currentData + " " + link;
					}
				}
				if (count % 1000000 == 0 && count != 0) {
					L.info(count);
					// break;
				}
				count++;
			}
			br.close();
		} catch (FileNotFoundException fnfe) {

		} finally {
			if (fis != null)
				fis.close();

			if (writer != null)
				writer.close();
		}
	}

	public void indexLabels() throws IOException {
		Directory dir = FSDirectory.open(new File(LABEL_INDEX));
		Analyzer analyzer = new WhitespaceAnalyzer(Version.LUCENE_45);
		IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_45, analyzer);
		IndexWriter writer = new IndexWriter(dir, iwc);
		int count = 0;
		FileInputStream fis = null;
		try {
			// needs to be sorted by IRI !!!
			// ---> LC_ALL=c sort -u <file>
			fis = new FileInputStream(LABEL_INDEX + ".txt");
			DataInputStream in = new DataInputStream(fis);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			String currentDoc = "`)"; // the first doc in the file
			List<Field> currentFields = new ArrayList<Field>();
			while ((strLine = br.readLine()) != null) {
				if (strLine.length() >= 2 && !strLine.startsWith(" ") && !strLine.startsWith("#")) {

					// wenn pipe: splitte an leerzeichen: rechts-> label links->entity
					// wenn im entity hash vorhanden: splitte -> links = entity
					// ansonsten: label = entity (mainlabel)
					strLine = StringEscapeUtils.unescapeHtml(strLine);
					strLine = StringEscapeUtils.unescapeHtml(strLine);
					strLine = strLine.replaceAll("\\<.*?>", "");
					String iri = "";
					String label = null;
					int splitIdx = strLine.indexOf(" ");
					if (splitIdx >= 0) {
						String potentialIri = strLine.substring(0, splitIdx);
						int hashIdx = potentialIri.indexOf("#");
						if (hashIdx >= 0) {
							iri = potentialIri.substring(0, hashIdx);
						} else {
							iri = potentialIri;
						}
						label = strLine.substring(splitIdx + 1);
					} else {
						iri = strLine;
						label = strLine;
					}

					String extra = "";
					if (label.contains("(")) {
						extra = label.substring(0, label.indexOf("("));
					}

					label = label.replaceAll(CLEAN_PATTERN, "");
					if (!iri.trim().equals("") && !label.trim().equals("")) {

						iri = iri.replace(" ", "_");

						String text = label.toLowerCase().replace(" ", "_");
						if (!extra.equals("")) {
							text = text + " " + extra.toLowerCase().replace(" ", "_");
						}

						if (!currentDoc.equals(iri)) {

							Document doc = new Document();
							doc.add(new StringField("iri", currentDoc, Field.Store.YES));
							for (Field f : currentFields) {
								doc.add(f);
							}
							writer.addDocument(doc);
							currentFields = new ArrayList<Field>();
							currentDoc = iri;

							if (count % 100000 == 0) {
								L.info(count);
							}
							count++;
						}
						String[] tokens = { iri, text, "", "" };
						addToCurrentFields(currentFields, tokens);
					}
				}
			}
			br.close();
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} finally {
			if (fis != null)
				fis.close();

			if (writer != null)
				writer.close();
		}
	}

	private static void addToCurrentFields(List<Field> currentFields, String[] tokens) {
		String f = tokens[1] + " " + tokens[2] + " " + tokens[3];
		currentFields.add(new TextField("l", f, Field.Store.YES));

		// to catch CamelCase for e.g. Tweets
		if (tokens[1].contains("_")) {
			String label = tokens[1].replaceAll("_", "");
			String f2 = label + " " + tokens[2] + " " + tokens[3];
			currentFields.add(new TextField("l", f2, Field.Store.YES));

		}
	}

	public static void main(String[] args) throws IOException {
		IndexAccessImpl i = new IndexAccessImpl();
		i.setup(ParameterPresets.getDefaultParameters());
		System.out.println("Creating links index ...");
		i.indexLinks();
		System.out.println("Creating labels index ...");
		i.indexLabels();
		System.out.println("Done.");
	}
}
