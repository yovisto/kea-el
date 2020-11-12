package com.yovisto.kea.ned;

import grph.Grph;

/*
 * put this somwhere else
 * <!-- dependency>
<groupId>grph</groupId>
<artifactId>grph</artifactId>
<version>1.5.20</version>
</dependency -->

*/

import java.io.Serializable;
import java.util.Map;

public interface GraphElements extends Serializable{

	public abstract Grph getGraph() ;
	
	public abstract void setGraph(Grph graph); 
	
	public abstract Map<String, Map<String,Double>> getStatistics();
	
	public abstract void setStatistics(Map<String, Map<String,Double>> termStatistics) ;
}