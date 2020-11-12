package com.yovisto.kea.commons;

import java.util.List;

public class PropertyImpl implements Property {
		
	public PropertyImpl(){
		
	}
	
	public PropertyImpl(Property p){
		this.iri = p.getIri();
		this.adjacency = p.getAdjacency();
		this.surfaces = p.getSurfaces();
		this.matchingSurface = p.getMatchingSurface();
	}
	
	private int adjacency = 0;
	
	private String iri;
	
	private List<Surface> surfaces ;

	/* (non-Javadoc)
	 * @see com.yovisto.kea.commons.Property#getSurfaces()
	 */
	@Override
	public List<Surface> getSurfaces() {
		return surfaces;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.commons.Property#setSurfaces(java.util.List)
	 */
	@Override
	public void setSurfaces(List<Surface> surfaces) {
		this.surfaces = surfaces;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.commons.Property#getIri()
	 */
	@Override
	public String getIri() {
		return iri;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.commons.Property#setIri(java.lang.String)
	 */
	@Override
	public void setIri(String iri) {
		this.iri = iri;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.commons.Property#getAdjacency()
	 */
	@Override
	public int getAdjacency() {
		return adjacency;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.commons.Property#setAdjacency(int)
	 */
	@Override
	public void setAdjacency(int adjacency) {
		this.adjacency = adjacency;
	}
	
	/* (non-Javadoc)
	 * @see com.yovisto.kea.commons.Property#getMatchingSurface()
	 */
	@Override
	public Surface getMatchingSurface() {
		return matchingSurface;
	}

	/* (non-Javadoc)
	 * @see com.yovisto.kea.commons.Property#setMatchingSurface(com.yovisto.kea.commons.Surface)
	 */
	@Override
	public void setMatchingSurface(Surface matchingSurface) {
		this.matchingSurface = matchingSurface;
	}

	private Surface matchingSurface;

	
}
