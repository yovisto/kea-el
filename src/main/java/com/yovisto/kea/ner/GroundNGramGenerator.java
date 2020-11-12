package com.yovisto.kea.ner;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yovisto.kea.commons.Parameters;
import com.yovisto.kea.commons.Term;
import com.yovisto.kea.commons.TermImpl;
import com.yovisto.kea.commons.Word;
import com.yovisto.kea.commons.WordImpl;
import com.yovisto.kea.util.KeaResourceException;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;

public class GroundNGramGenerator implements NGramGenerator {

	private static final long serialVersionUID = 7907775945028395585L;
	
	protected final Logger L = Logger.getLogger(getClass());
	
	private CRFClassifier<CoreLabel> c;
	
	@SuppressWarnings("unchecked")
	public GroundNGramGenerator() {
		try {
			c = CRFClassifier.getClassifier("english.conll.4class.distsim.crf.ser.gz");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public List<Term> generateGrams(String text, Parameters params) throws KeaResourceException {

        	
    	List<CoreLabel> labels = new ArrayList<CoreLabel>();			
    	List<Word> allWords = new ArrayList<Word>();
    	
        // we expect the text to be an "<a href" annotated text.
        List<Term> result = new ArrayList<Term>();
        String[] tokens = text.split("</a>");
        for (String t : tokens) {
            if (t.contains("'>")) {

                int position = 0;
                int positionIncrement = 1;
                String analysisType = "word";
                String surfaceForm = t.substring(t.indexOf("'>") + 2);
                String partOfSpeech = "";
                int startOffset = 0;
                int endOffset = 0;
                int positionLength = 0;

                Term term = new TermImpl( position,  positionIncrement,  analysisType,  surfaceForm,  partOfSpeech, startOffset,  endOffset,  positionLength) ;
                List<Word> words = new ArrayList<Word>();
                for (String termTextToken : surfaceForm.split(" ")) {
                    Word word = new WordImpl();
                    word.setValue(termTextToken.trim());
                    if (!termTextToken.trim().equals("")) {
                        words.add(word);
                        
                        CoreLabel l = new CoreLabel();					
        				l.setWord(word.getValue());
        				labels.add(l);
        				word.setTerm(term);
                        allWords.add(word);
                        term.setNamedEntityType("");
                    }
                }
                term.setWords(words);
                result.add(term);
            }
        }
        
        
        List<CoreLabel> map = c.classify(labels);

        int idx = 0;
		for (CoreLabel word : map) {
			System.out.print(word.word() + '/' + word.get(AnswerAnnotation.class) + ' ');		
			String NET = new String(word.get(AnswerAnnotation.class).toCharArray());
			String currentNET = allWords.get(idx).getTerm().getNamedEntityType(); 
			allWords.get(idx).getTerm().setNamedEntityType(currentNET + NET + " ");						
			idx++;
		}        
		
        return result;
    }
}
