package com.yovisto.kea;

import java.util.List;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.yovisto.kea.commons.DisambiguatedTerm;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.guice.KeaModule;

public class Start {

	public static void main(String[] args) throws Exception {
		Injector injector = Guice.createInjector(new KeaModule());

		EntityResolver resolver = injector.getInstance(EntityResolver.class);
		Parameters p = ParameterPresets.getDefaultParametersWithExplain();

		List<DisambiguatedTerm> result1 = resolver.resolve("Das Angelina hat in ganz Frankreich die besten Teilchen.", p);

		for (DisambiguatedTerm term : result1) {
			System.out.println(term.getSurfaceForm() + " -> " + term.getCandidate().getIri() + " "
					+ term.getStartOffset() + "-" + term.getEndOffset());
		}

		if (result1.size() > 0) {
			System.out.println(result1.get(0).getEssentialCategories());
		}

	}

}
