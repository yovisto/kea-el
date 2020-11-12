package com.yovisto.kea.ner.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.analysis.util.FilteringTokenFilter;
import org.apache.lucene.util.Version;


public final class RemoveTextTypeTokenFilter extends FilteringTokenFilter {

	private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
	
	public RemoveTextTypeTokenFilter(Version version, TokenStream in) {
		super(version, in);		
	}

	@Override
	protected boolean accept() throws IOException {				
		return !typeAtt.type().equals("text");
	}

}