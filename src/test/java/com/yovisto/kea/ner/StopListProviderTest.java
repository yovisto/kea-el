package com.yovisto.kea.ner;

import org.junit.Assert;
import org.junit.Test;

import com.yovisto.kea.commons.Lang;
import com.yovisto.kea.util.KeaResourceException;

public class StopListProviderTest {

	@Test
	public void testDefaultStoplistsExisting() {
		StopListProvider p = new StandardStopListProvider();
			
		try {
			Assert.assertNotNull(p.getStopList(Lang.DE));
			Assert.assertNotNull(p.getStopList(Lang.EN));
		} catch (KeaResourceException e) {			
			Assert.fail(e.getMessage());
		}	
	}
}
