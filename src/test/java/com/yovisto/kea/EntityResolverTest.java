package com.yovisto.kea;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.ScoredCandidate;
import com.yovisto.kea.guice.KeaModule;

public class EntityResolverTest {

	@Test
	public void test() throws Exception {

		Parameters params = ParameterPresets.getDefaultParametersWithExplain();

		Injector injector = Guice.createInjector(new KeaModule());

		EntityResolver resolver = injector.getInstance(EntityResolver.class);

		params.setProperty(Parameters.NO_TOKENIZING, true);

		for (DisambiguatedTerm dTerm : resolver.resolve("Armstrong", params)) {
			System.out.println(dTerm.getSurfaceForm());
			Assert.assertNotNull(dTerm.getSurfaceForm());
			Assert.assertNotNull(dTerm.getScoredCandidates());
			for (ScoredCandidate cand : dTerm.getScoredCandidates()) {
				System.out.println("\t" + cand.getIri());
				for (String scorer : cand.getScores().keySet()) {
					Assert.assertTrue(cand.getScores().get(scorer).getValue()>=0.0);
					Assert.assertTrue(cand.getScores().get(scorer).getNormalizedValue()>=0.0);
					Assert.assertTrue(cand.getScores().get(scorer).getNormalizedValue()<=1.0);
					Assert.assertTrue(cand.getScores().get(scorer).getVector().size()>0);
					System.out.println("\t\t" + scorer + " = " + cand.getScores().get(scorer).getValue() + " " + cand.getScores().get(scorer).getVector());
					System.out.println("\t\t" + scorer + " = " + cand.getScores().get(scorer).getNormalizedValue() + " " + cand.getScores().get(scorer).getNormalizedVector());
				}
				System.out.println("\t" + cand.getTotalScore());
			}
			System.out.println("->" + dTerm.getCandidate().getIri() + " " + ((ScoredCandidate) dTerm.getCandidate()).getTotalScore());
		}
	}
}
