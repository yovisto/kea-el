package com.yovisto.kea.ner;

import org.junit.Assert;
import org.junit.Test;

import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.util.KeaResourceException;

public class PosTaggerProviderTest {

	@Test
	public void test() {
		
		PosTaggerProvider p = new StandardPOSTaggerProvider();

		try {			
			Assert.assertNotNull(p.getTagger(Lang.EN));
			Assert.assertNotNull(p.getTagger(Lang.DE));
		} catch (KeaResourceException e) {
			Assert.fail(e.getMessage());
		}
	}
}
