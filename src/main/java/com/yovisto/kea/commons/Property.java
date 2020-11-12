package com.yovisto.kea.commons;

import java.util.List;

public interface Property {

	List<Surface> getSurfaces();

	void setSurfaces(List<Surface> surfaces);

	String getIri();

	void setIri(String iri);

	int getAdjacency();

	void setAdjacency(int adjacency);

	Surface getMatchingSurface();

	void setMatchingSurface(Surface matchingSurface);

}