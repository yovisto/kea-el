package com.yovisto.kea.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.yovisto.kea.EntityResolver;
import com.yovisto.kea.StandardEntityResolver;
import com.yovisto.kea.ned.CategoryFilter;
import com.yovisto.kea.ned.CategoryFilterImpl;
import com.yovisto.kea.ned.CoreferenceResolver;
import com.yovisto.kea.ned.CoreferenceResolverImpl;
import com.yovisto.kea.ned.Disambiguator;
import com.yovisto.kea.ned.GraphGenerator;
import com.yovisto.kea.ned.MaxOfMeanDisambiguator;
import com.yovisto.kea.ned.Normalizer;
import com.yovisto.kea.ned.ScoreGenerator;
import com.yovisto.kea.ned.ScoreGeneratorImpl;
import com.yovisto.kea.ned.StandardGraphGenerator;
import com.yovisto.kea.ned.StandardNormalizer;
import com.yovisto.kea.ner.MappedTermFilter;
import com.yovisto.kea.ner.MappedTermMerger;
import com.yovisto.kea.ner.Mapper;
import com.yovisto.kea.ner.NGramGenerator;
import com.yovisto.kea.ner.ShingleTermFilter;
import com.yovisto.kea.ner.StandardFilter;
import com.yovisto.kea.ner.StandardMapper;
import com.yovisto.kea.ner.StandardMerger;
import com.yovisto.kea.ner.StandardNGramGenerator;
import com.yovisto.kea.ner.TermFilter;
import com.yovisto.kea.util.IndexAccess;
import com.yovisto.kea.util.IndexAccessImpl;

public class KeaModule extends AbstractModule{

	@Override
	protected void configure() {
		
		bind(IndexAccess.class).toInstance(new IndexAccessImpl());
		bind(NGramGenerator.class).to(StandardNGramGenerator.class).in(Scopes.SINGLETON);
		
		bind(Mapper.class).to(StandardMapper.class).in(Scopes.SINGLETON);
		
		bind(MappedTermMerger.class).to(StandardMerger.class).in(Scopes.SINGLETON);
		bind(TermFilter.class).to(ShingleTermFilter.class).in(Scopes.SINGLETON);
		bind(MappedTermFilter.class).to(StandardFilter.class).in(Scopes.SINGLETON);
        bind(ScoreGenerator.class).to(ScoreGeneratorImpl.class).in(Scopes.SINGLETON);
		
        bind(Normalizer.class).to(StandardNormalizer.class).in(Scopes.SINGLETON);
		
		bind(Disambiguator.class).to(MaxOfMeanDisambiguator.class).in(Scopes.SINGLETON);
		bind(EntityResolver.class).to(StandardEntityResolver.class).in(Scopes.SINGLETON);
		
		bind(GraphGenerator.class).to(StandardGraphGenerator.class).in(Scopes.SINGLETON);
		
		bind(CoreferenceResolver.class).to(CoreferenceResolverImpl.class).in(Scopes.SINGLETON);
		bind(CategoryFilter.class).to(CategoryFilterImpl.class).in(Scopes.SINGLETON);
	}

}
