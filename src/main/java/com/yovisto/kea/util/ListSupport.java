package com.yovisto.kea.util;

import java.util.List;

import com.yovisto.kea.commons.Word;

public class ListSupport {

	public static String joinWordList(List<Word> input, String delimiter) {
		StringBuffer joined = new StringBuffer();

		for (Word word : input) {
			joined.append(word.getValue().toString());
			joined.append(delimiter);
		}
		String result = joined.toString();

		if (result.trim().equals(""))
			return "";
		return result.substring(0, result.length() - delimiter.length());
	}

}
