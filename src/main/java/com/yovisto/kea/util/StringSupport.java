package com.yovisto.kea.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import com.yovisto.kea.commons.Label;
import com.yovisto.kea.commons.LabelImpl;
import com.yovisto.kea.commons.Word;
import com.yovisto.kea.commons.WordImpl;

import edu.stanford.nlp.ling.TaggedWord;

/**
 * The StringSupport utility class.
 */
public class StringSupport {

	/**
	 * Convert a simple string into a list of Words using whitespace as token
	 * delimiter.
	 * 
	 * @param text
	 *            the text
	 * @return the list
	 */
	public static List<Word> toWordList(String text) {
		List<Word> words = new ArrayList<Word>();

		String[] tokens = text.split(" ");
		for (int i = 0; i < tokens.length; i++) {
			Word word = new WordImpl();
			word.setValue(tokens[i].trim());
			words.add(word);
		}
		return words;
	}

	/**
	 * Normalize SQL string quotations.
	 * 
	 * @param string
	 *            the string
	 * @return the string
	 */
	public static String sqlNormalize(String string) {
		return string.replaceAll("'", "\\\\'");
	}

	public static List<Word> resultToWordList(String input) {
		List<Word> words = new ArrayList<Word>();

		input = input.replaceAll("\\[", "");
		input = input.replaceAll("\\]", "");

		List<String> strings = Arrays.asList(input.split(","));
		for (String string : strings) {
			Word word = new WordImpl();
			word.setValue(string.trim());
			words.add(word);
		}

		return words;
	}

	/**
	 * Convert List of Words to List of Labels
	 * 
	 * @param wordList
	 * @return the List of Labels
	 */

	public static List<Label> toLabelList(List<Word> wordList) {
		List<Label> labelList = new ArrayList<Label>();
		for (Word word : wordList) {
			Label label = new LabelImpl();
			label.setValue(word.getValue());
			labelList.add(label);
		}
		return labelList;
	}

	public static String labelListToString(List<Label> labelList) {
		String string = "";
		for (Label label : labelList) {
			string = string + " " + label.getValue();
		}
		string = string.trim();
		return string;
	}

	public static List<Word> removeSmallWords(List<Word> words, int wordLength) {
		List<Word> wordsCleaned = new ArrayList<Word>();

		for (Word word : words) {
			if (word.getValue().length() > wordLength) {
				wordsCleaned.add(word);
			}
		}
		return wordsCleaned;
	}


	public static List<String> getStructuredText(String input) {
		List<String> outputList = new ArrayList<String>();
		if (input.contains(".")) {
			List<String> sentences = Arrays.asList(input.split("\\."));
			for (String sentence : sentences) {
				if (sentence.contains(";")) {
					List<String> sSentences = Arrays.asList(sentence.split(";"));
					for (String sSentence : sSentences) {
						if (sSentence.contains(",")) {
							List<String> subsentences = Arrays.asList(sSentence.split(","));
							for (String subsentence : subsentences) {
								if (subsentence.contains("(")) {
									List<String> subsubsentences = Arrays.asList(subsentence.split("\\("));
									for (String subsubsentence : subsubsentences) {
										List<String> subsubsubsentences = Arrays.asList(subsubsentence.split("\\)"));
										for (String subsubsubsentence : subsubsubsentences) {
											outputList.add(subsubsubsentence.trim());
										}
									}
								} else {
									outputList.add(subsentence.trim());
								}
							}
						} else {
							if (sSentence.contains("(")) {
								List<String> subsentences = Arrays.asList(sSentence.split("\\("));
								for (String subsentence : subsentences) {
									List<String> subsubsentences = Arrays.asList(subsentence.split("\\)"));
									for (String subsubsentence : subsubsentences) {
										outputList.add(subsubsentence.trim());
									}
								}
							} else {
								outputList.add(sSentence.trim());
							}
						}
					}
				} else {
					if (sentence.contains(",")) {
						List<String> subsentences = Arrays.asList(sentence.split(","));
						for (String subsentence : subsentences) {
							if (subsentence.contains("(")) {
								List<String> subsubsentences = Arrays.asList(subsentence.split("\\("));
								for (String subsubsentence : subsubsentences) {
									List<String> subsubsubsentences = Arrays.asList(subsubsentence.split("\\)"));
									for (String subsubsubsentence : subsubsubsentences) {
										outputList.add(subsubsubsentence.trim());
									}
								}
							} else {
								outputList.add(subsentence.trim());
							}
						}
					} else {
						if (sentence.contains("(")) {
							List<String> subsentences = Arrays.asList(sentence.split("\\("));
							for (String subsentence : subsentences) {
								List<String> subsubsentences = Arrays.asList(subsentence.split("\\)"));
								for (String subsubsentence : subsubsentences) {
									outputList.add(subsubsentence.trim());
								}
							}
						} else {
							outputList.add(sentence.trim());
						}
					}
				}
			}
		} else {
			if (input.contains(";")) {
				List<String> sentences = Arrays.asList(input.split(";"));
				for (String sentence : sentences) {
					if (sentence.contains(",")) {
						List<String> subsentences = Arrays.asList(sentence.split(","));
						for (String subsentence : subsentences) {
							if (subsentence.contains("(")) {
								List<String> subsubsentences = Arrays.asList(subsentence.split("\\("));
								for (String subsubsentence : subsubsentences) {
									List<String> subsubsubsentences = Arrays.asList(subsubsentence.split("\\)"));
									for (String subsubsubsentence : subsubsubsentences) {
										outputList.add(subsubsubsentence.trim());
									}
								}
							} else {
								outputList.add(subsentence = subsentence.trim());
							}
						}
					} else {
						if (sentence.contains("(")) {
							List<String> subsentences = Arrays.asList(input.split("\\("));
							for (String subsentence : subsentences) {
								List<String> subsubsentences = Arrays.asList(subsentence.split("\\)"));
								for (String subsubsentence : subsubsentences) {
									outputList.add(subsubsentence.trim());
								}
							}
						} else {
							outputList.add(input.trim());
						}
					}
				}
			} else {
				if (input.contains(",")) {
					List<String> subsentences = Arrays.asList(input.split(","));
					for (String subsentence : subsentences) {
						if (subsentence.contains("(")) {
							List<String> subsubsentences = Arrays.asList(subsentence.split("\\("));
							for (String subsubsentence : subsubsentences) {
								List<String> subsubsubsentences = Arrays.asList(subsubsentence.split("\\)"));
								for (String subsubsubsentence : subsubsubsentences) {
									outputList.add(subsubsubsentence.trim());
								}
							}
						} else {
							outputList.add(subsentence = subsentence.trim());
						}
					}
				} else {
					if (input.contains("(")) {
						List<String> subsentences = Arrays.asList(input.split("\\("));
						for (String subsentence : subsentences) {
							List<String> subsubsentences = Arrays.asList(subsentence.split("\\)"));
							for (String subsubsentence : subsubsentences) {
								outputList.add(subsubsentence.trim());
							}
						}
					} else {
						outputList.add(input.trim());
					}
				}
			}
		}

		return outputList;
	}

	/**
	 * @param input
	 * @return
	 */
	public static List<TaggedWord> toTaggedWordList(String input) {
		List<TaggedWord> output = Lists.newArrayList();
		int old_index = 0;
		int index = input.indexOf(" ");
		while (index > 0) {
			TaggedWord tw = new TaggedWord();

			tw.setEndPosition(index);
			if (old_index == 0) {
				tw.setBeginPosition(old_index);
				tw.setWord(input.substring(old_index, index));
			} else {
				tw.setBeginPosition(old_index);
				tw.setWord(input.substring(old_index, index));
			}
			output.add(tw);
			old_index = index + 1;
			index = input.indexOf(" ", index + 1);
		}
		TaggedWord tw = new TaggedWord();
		tw.setBeginPosition(old_index);
		tw.setEndPosition(input.length());
		tw.setWord(input.substring(old_index, input.length()));
		output.add(tw);

		return output;
	}

	public static String wordsToString(List<Word> words) {
		StringBuffer string = new StringBuffer();
		for (Word w: words){
			string.append(w);
			string.append(" ");
		}
		return string.toString().trim();
	}
}
