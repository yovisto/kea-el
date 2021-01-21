package com.yovisto.kea.ner;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.yovisto.kea.ParameterPresets;
import com.yovisto.kea.util.IndexAccessImpl;

import junit.framework.Assert;

public class MapperTest {

	@Test
	public void test() {
		
		IndexAccessImpl l = new IndexAccessImpl();		
		l.setup(ParameterPresets.getDefaultParameters());		
		
		// all 4 variants should return the same result
		String r1 = StringUtils.join( l.getIrisForLabel("Fake News") , " ");		
		String r2 = StringUtils.join( l.getIrisForLabel("Fake_News") , " ");
		String r3 = StringUtils.join( l.getIrisForLabel("fake news") , " ");
		String r4 = StringUtils.join( l.getIrisForLabel("fake_news") , " ");
		
		Assert.assertEquals(r1, r2);
		Assert.assertEquals(r1, r3);
		Assert.assertEquals(r1, r4);
		
		System.out.println( StringUtils.join( l.getIrisForLabel("bora,_katharina_von") , " ") );
		
	}

}
