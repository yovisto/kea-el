package com.yovisto.kea.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import grph.DefaultIntSet;
import grph.Grph;
import grph.in_memory.InMemoryGrph;
import it.unimi.dsi.fastutil.ints.IntSet;

public class GrphExample {

	@Test
	public void test() throws KeaResourceException, IOException {
		ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);

		Grph g = new InMemoryGrph();

		List<Integer> terms = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4));
		List<Integer> candidates = new ArrayList<Integer>(Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25));
		List<Integer> interlinkNodes = new ArrayList<Integer>(Arrays.asList(30, 31));

		// term 1
		// candidates
		g.addSimpleEdge(1, 10, false);
		g.addSimpleEdge(1, 11, false);
		g.addSimpleEdge(1, 12, false);
		g.addSimpleEdge(1, 13, false);

		// term 2
		g.addSimpleEdge(2, 14, false);
		g.addSimpleEdge(2, 15, false);
		g.addSimpleEdge(2, 16, false);
		g.addSimpleEdge(2, 17, false);

		// term 3
		g.addSimpleEdge(3, 18, false);
		g.addSimpleEdge(3, 19, false);
		g.addSimpleEdge(3, 20, false);
		g.addSimpleEdge(3, 21, false);

		// term 4
		g.addSimpleEdge(4, 22, false);
		g.addSimpleEdge(4, 23, false);
		g.addSimpleEdge(4, 24, false);
		g.addSimpleEdge(4, 25, false);

		// term colorize node wit colour 1
		g.getVertexColorProperty().setValue(1, 1);
		g.getVertexColorProperty().setValue(2, 1);
		g.getVertexColorProperty().setValue(3, 1);
		g.getVertexColorProperty().setValue(4, 1);

		// links amongst candidates
		g.addSimpleEdge(13, 15, false);
		g.addSimpleEdge(15, 19, false);
		g.addSimpleEdge(19, 25, false);
		g.addSimpleEdge(25, 13, false);
		g.addSimpleEdge(24, 20, false);
		g.addSimpleEdge(25, 15, false);

		// interlinks
		//g.addSimpleEdge(25, 30, false);
		g.addSimpleEdge(30, 24, false);
		g.addSimpleEdge(30, 31, false);

		List<Integer> noTermVertices = candidates;
		noTermVertices.addAll(interlinkNodes);
		
		IntSet inni = new DefaultIntSet(noTermVertices.size());
		inni.addAll(noTermVertices);
		// the graph, without terms  (might contain single nodes)
		Grph noTermGraph = g.getSubgraphInducedByVertices(inni);	
		
		
		Collection<IntSet> components = noTermGraph.getConnectedComponents();		
		
		// // calculate component span
		for (IntSet component : components) {
			
			Set<Integer> termsOfComponent = new TreeSet<Integer>();
			for (int v : component) {
				for (int t : terms) {
					if (g.getNeighbours(t).contains(v)) {
						termsOfComponent.add(t);
						break;
					}
				}
			}
			//L.info("Term span: " + termsOfComponent.size());
		}

		Grph componentGraph = g.getSubgraphInducedByVertices(noTermGraph.getVerticesOfDegreeAtLeast(1));

		componentGraph.display();
		
		
		// give all nodes the size as label:
		for (IntSet component : components) {
			for (int v : component) {
				if (interlinkNodes.contains(v)){
					componentGraph.getVertexSizeProperty().setValue(v, 0);
				}else{
					componentGraph.getVertexSizeProperty().setValue(v, 10);
				}
			}
		}
		for (int i = 0 ; i< 4 ; i++){
			for (int v : componentGraph.getVertices()) {
				for (int n : componentGraph.getNeighbours(v)) {
					double factor = 0.2;
					if (interlinkNodes.contains(v)){
						factor = 0.1;
					}					
					long oldSize = componentGraph.getVertexSizeProperty().getValue(n);
					long newSize = oldSize + Math.round(componentGraph.getVertexSizeProperty().getValue(v) * factor);
					componentGraph.getVertexSizeProperty().setValue(n, newSize);
				}
			}
		}

		for (IntSet component : components) {
			for (int v : component) {				
				g.getVertexLabelProperty().setValue(v, v + ":" + (componentGraph.getVertexSizeProperty().getValue(v)));
				componentGraph.getVertexSizeProperty().setValue(v, 10);
			}
		}
		
		//g.display();
		//DataInputStream input = new DataInputStream(System.in);
		//String string = input.readLine();

	}
}
