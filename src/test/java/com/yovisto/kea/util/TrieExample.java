package com.yovisto.kea.util;

import java.util.Collection;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.junit.Test;

public class TrieExample {

	@Test
	public void test() {

		Trie t = new Trie();
		
		t.addKeyword("the times");
		t.addKeyword("news");
		t.addKeyword("paper");
		t.addKeyword("the");		
		
		Collection<Emit> emit = t.parseText("the times are the nice newspaper"); 
		
		System.out.println("emits: " + emit.size() + " \n") ;
		for (Emit e : emit){
			System.out.println( e.getKeyword() + " " + e.getStart() + "-" + e.getEnd() );
		}
		
		
	}

}
