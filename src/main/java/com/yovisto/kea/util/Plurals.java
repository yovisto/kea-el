package com.yovisto.kea.util;

import java.util.ResourceBundle;

public class Plurals {

	private final ResourceBundle plurals;

	public Plurals() {
		plurals = ResourceBundle.getBundle("plurals");
	}

	public String getSingular(String plural) {
		String singular = null;
		try {
			singular = plurals.getString(plural);
		} catch (Exception e) {

		}

		if (singular != null && !singular.equals(plural)) {
			return singular;
		}

		if (plural.endsWith("ies")) {
			singular = plural.substring(0, plural.lastIndexOf("ies")) + "y";
			if (singular != plural) {
				return singular;
			}
		}
		if (plural.endsWith("ves")) {
			singular = plural.substring(0, plural.lastIndexOf("ves")) + "f";
			if (singular != plural) {
				return singular;
			}
		}
		if (plural.endsWith("es")) {
			singular = plural.substring(0, plural.lastIndexOf("es"));
			if (singular != plural) {
				return singular;
			}
		}
		if (plural.endsWith("ss")) {
			return plural;
		}
		if (plural.endsWith("s")) {
			singular = plural.substring(0, plural.lastIndexOf("s"));
			if (singular != plural) {
				return singular;
			}
		}
		if (plural.endsWith("n")) {
			singular = plural.substring(0, plural.lastIndexOf("n"));
			if (singular != plural) {
				return singular;
			}
		}

		return plural;
	}
}
