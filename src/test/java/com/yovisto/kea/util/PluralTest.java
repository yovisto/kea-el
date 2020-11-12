package com.yovisto.kea.util;

import org.junit.Test;

public class PluralTest {

	@Test
	public void test() {
		Plurals p  = new Plurals();		
		System.out.println(p.getSingular("tomatoes"));
		System.out.println(p.getSingular("babies"));
		System.out.println(p.getSingular("leaves"));
		System.out.println(p.getSingular("boxes"));
		System.out.println(p.getSingular("bernie"));
		System.out.println(p.getSingular("box"));
		System.out.println(p.getSingular("foxes"));
		System.out.println(p.getSingular("echoes"));
		
		
		// TODO: implement plurals for German Language 
		System.out.println(p.getSingular("Katzen")); // OK
		System.out.println(p.getSingular("Hunde"));  // <--- nicht OK
	}

}
