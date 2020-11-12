package com.yovisto.kea.ner;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.yovisto.kea.ParameterPresets;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.util.KeaResourceException;

public class StandardNGramGeneratorTest {

	@Test
	public void test() throws KeaResourceException {
		long startTime = System.currentTimeMillis();
		NGramGenerator g = new StandardNGramGenerator();
		long initTime = System.currentTimeMillis() - startTime;

		List<Term> terms = g.generateGrams("Armstrong landet on the moon.", ParameterPresets.getDefaultParameters());

		Assert.assertNotNull(terms);
		Assert.assertTrue(terms.size() > 0);
		Assert.assertTrue("Initialization took more than 1000ms.", initTime < 1000);

		startTime = System.currentTimeMillis();
		terms = g.generateGrams("The Mars is not the Venus.", ParameterPresets.getDefaultParameters());
		long runTime = System.currentTimeMillis() - startTime;

		Assert.assertNotNull(terms);
		Assert.assertTrue(terms.size() > 0);
		Assert.assertTrue("Run took more than 1000ms.", runTime < 1000);

	}

}
