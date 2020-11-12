package com.yovisto.kea.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.yovisto.kea.commons.Property;
import com.yovisto.kea.commons.PropertyImpl;
import com.yovisto.kea.commons.Surface;
import com.yovisto.kea.commons.Term;

public class PropertyMapper {

	protected final Logger L = Logger.getLogger(getClass());

	/**
	 * 
	 * SELECT ?prop ?lab ?rep ?frequency WHERE { ?sense
	 * <http://lemon-model.net/lemon#reference> ?prop . ?sense
	 * <http://www.w3.org/ns/prov#generatedBy> ?activity . ?activity
	 * <http://www.w3.org/ns/prov#frequency> ?frequency . ?canon
	 * <http://lemon-model.net/lemon#writtenRep> ?rep . ?verb
	 * <http://lemon-model.net/lemon#sense> ?sense . ?verb
	 * <http://lemon-model.net/lemon#language> "en" . ?verb
	 * <http://www.w3.org/2000/01/rdf-schema#label> ?lab . ?verb
	 * <http://lemon-model.net/lemon#canonicalForm> ?canon . filter
	 * regex(str(?prop), "^http://dbpedia.org") }
	 * 
	 */

	private Map<String, PropertyImpl> properties = new HashMap<String, PropertyImpl>();

	private Map<String, List<PropertyImpl>> surfaceIndex = new HashMap<String, List<PropertyImpl>>();

	private int maxAdjacency = 3;

	public PropertyMapper() {
		// create surface index
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("propsurfaces.csv").getFile());
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {

				String prop = line.split(",")[0].replaceAll("\"", "");

				String lab = line.split(",")[1].replaceAll("\"", "").replaceAll("\\(.*\\)", "").trim();
				int freq = Integer.parseInt(line.split(",")[3].replaceAll("\"", "")
						.replace("^^<http://www.w3.org/2001/XMLSchema#int>", ""));

				Surface s = new Surface();
				s.setFrequency(freq);
				s.setLabel(lab);

				if (properties.containsKey(prop)) {
					Property pp = properties.get(prop);
					pp.getSurfaces().add(s);
				} else {
					PropertyImpl p = new PropertyImpl();
					p.setSurfaces(new ArrayList<Surface>(Arrays.asList(s)));
					p.setIri(prop);
					properties.put(prop, p);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (String key : properties.keySet()) {
			PropertyImpl p = properties.get(key);
			for (Surface s : p.getSurfaces()) {
				if (surfaceIndex.containsKey(s.getLabel())) {
					if (!surfaceIndex.get(s.getLabel()).contains(p)) {
						surfaceIndex.get(s.getLabel()).add(p);
					}
				} else {
					surfaceIndex.put(s.getLabel(), new ArrayList<PropertyImpl>(Arrays.asList(p)));
				}
			}
		}
	}

	public List<Term> mapProperties(List<Term> terms) {
		// for every term
		// inject property candidates
		for (Term t : terms) {
			L.info("TERM : " + t.getSurfaceForm());
			String surface = StringUtils.join(t.getWords(), " ").trim();
			if (surfaceIndex.containsKey(surface)) {
				List<PropertyImpl> propList = surfaceIndex.get(surface);
				t.setPropertyCandidates(propList);
				for (Property p : propList) {
					for (Surface s : p.getSurfaces()) {
						if (s.getLabel().equals(surface)) {
							p.setMatchingSurface(s);
						}
					}
				}
				L.info("Found prop surface for '" + surface + "'. Adding " + surfaceIndex.get(surface).size()
						+ " props.");
			}
		}

		// for every properterty term
		// identify adjacents and inject property info
		List<PropertyImpl> stack = new ArrayList<PropertyImpl>();
		List<PropertyImpl> newStack = new ArrayList<PropertyImpl>();

		List<Term> sorted = new ArrayList<Term>();
		sorted.addAll(terms);

		// Collections.sort(sorted, new Comparator<Term>() {
		// @Override
		// public int compare(Term o1, Term o2) {
		// return o1.getPosition() - o2.getPosition();
		//
		// }
		// });

		for (Term t : sorted) {
			List<PropertyImpl> zero = getZeroProp(t.getPropertyCandidates());
			if (zero != null && zero.size() > 0) {
				// put copy of zero to stack
				for (Property p : zero)
					newStack.add(new PropertyImpl(p));

			} else {

				// increase stack
				for (Property p : stack) {
					if (p.getAdjacency() < maxAdjacency) {
						p.setAdjacency(p.getAdjacency() + 1);
						newStack.add(new PropertyImpl(p));
					}
				}
				// put newstack to term
				t.setPropertyCandidates(newStack);
			}
			stack = new ArrayList<PropertyImpl>();
			stack.addAll(newStack);
			newStack = new ArrayList<PropertyImpl>();
		}

		// now the same but with reversed set
		Collections.reverse(sorted);
		for (Term t : sorted) {
			List<PropertyImpl> zero = getZeroProp(t.getPropertyCandidates());
			if (zero != null && zero.size() > 0) {
				// put copy of zero to stack
				for (Property p : zero)
					newStack.add(new PropertyImpl(p));
			} else {

				// increase stack
				for (Property p : stack) {
					if (p.getAdjacency() < maxAdjacency) {
						p.setAdjacency(p.getAdjacency() + 1);
						newStack.add(new PropertyImpl(p));
					}
				}
				// put newstack to term
				t.getPropertyCandidates().addAll(newStack);
			}
			stack = new ArrayList<PropertyImpl>();
			stack.addAll(newStack);
			newStack = new ArrayList<PropertyImpl>();
		}

		Collections.reverse(sorted);
		for (Term t : terms) {
			System.out.print(t.getSurfaceForm() + " ");
			for (Property p : t.getPropertyCandidates()) {
				L.info(p.getIri() + " (" + p.getAdjacency() + ") [" + p.getMatchingSurface().getLabel() + " "
						+ p.getMatchingSurface().getFrequency() + "]");

			}

		}

		return sorted;
	}

	private List<PropertyImpl> getZeroProp(List<PropertyImpl> props) {
		List<PropertyImpl> list = new ArrayList<PropertyImpl>();
		if (props != null)
			for (PropertyImpl prop : props) {
				if (prop.getAdjacency() == 0) {
					list.add(prop);
				}
			}
		return list;
	}

}
