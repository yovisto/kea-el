package com.yovisto.kea.ned;

import com.yovisto.kea.commons.KeaComponent;
import com.yovisto.kea.util.KeaResourceException;

public class ScorerFactory implements KeaComponent {

	private static final long serialVersionUID = -4509784452379556491L;

	public static Scorer createScorer(String method) throws KeaResourceException {
		Scorer scorer;
		String className = method.split("\\^")[0];
		double boost =  Double.parseDouble(method.split("\\^")[1]);
		try {
			
			Class<?> scorerClass = Class.forName("com.yovisto.kea.ned.scorers." + className);
			scorer = (Scorer) scorerClass.newInstance();
			scorer.setBoost(boost);
		} catch (ClassNotFoundException e) {
			throw new KeaResourceException("Cannot find implementation for scorer '" + className + "'");
		} catch (InstantiationException e) {
			throw new KeaResourceException("Cannot instatiate implementation for scorer '" + className + "'");
		} catch (IllegalAccessException e) {
			throw new KeaResourceException("Cannot access implementation for scorer '" + className + "'");
		}

		return scorer;
	}
}
