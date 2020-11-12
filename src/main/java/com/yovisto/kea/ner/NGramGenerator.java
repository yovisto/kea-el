package com.yovisto.kea.ner;

import java.util.List;

import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.util.KeaResourceException;

public interface NGramGenerator extends KeaComponent{
	
	public abstract List<Term> generateGrams(String text, Parameters params) throws KeaResourceException ;

}
