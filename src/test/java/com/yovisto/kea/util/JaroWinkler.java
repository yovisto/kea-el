package com.yovisto.kea.util;

import org.junit.Test;

import junit.framework.Assert;

public class JaroWinkler {

//	@Test
	public void test() {
				
		String s = "Stuttgart 21";
		String uriSuffix = s.replaceAll("\\(.*\\)", "_").trim().toLowerCase();
		
		
		double r1 = JaroWinklerDistance.similarity(uriSuffix, "Stuttgart 21".toLowerCase());
		System.out.println( r1 );
		double r2 = JaroWinklerDistance.similarity(uriSuffix, "Stuttgart_21".toLowerCase());
		System.out.println( r2 );
		double r3 =  JaroWinklerDistance.similarity(uriSuffix, "Stuttgart".toLowerCase());
		System.out.println( r3 );
		double r4 = JaroWinklerDistance.similarity(uriSuffix, "Stuttgart (Amt)".toLowerCase());
		System.out.println( r4 );
		 
		Assert.assertTrue(r1>r2);
		Assert.assertTrue(r2>r3);
		Assert.assertTrue(r3>r4);
	}
	
	@Test
	public void test2() {
				
		String s = "Katharina von Bora";
		String uriSuffix = s.replaceAll("\\(.*\\)", "_").trim().toLowerCase();
		
		
		double r1 = JaroWinklerDistance.similarity(uriSuffix, "Katharina".toLowerCase());
		System.out.println( r1 );
		double r2 = JaroWinklerDistance.similarity(uriSuffix, "Katharina von".toLowerCase());
		System.out.println( r2 );
		double r3 =  JaroWinklerDistance.similarity(uriSuffix, "von Bora".toLowerCase());
		System.out.println( r3 );
		double r4 = JaroWinklerDistance.similarity(uriSuffix, "Bora, Katharina von".toLowerCase());
		System.out.println( r4 );
		 
		Assert.assertTrue(r1>r2);
		Assert.assertTrue(r2>r3);
		Assert.assertTrue(r3>r4);
	}

}
