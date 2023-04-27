package org.alliancegenome.curation_api.config;

import javax.inject.Named;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

import jakarta.enterprise.context.Dependent;

@Dependent
@Named("ApplicationAnalysisConfig")
public class AnalysisConfigurer implements ElasticsearchAnalysisConfigurer {

	@Override
	public void configure(ElasticsearchAnalysisConfigurationContext context) {
		context.analyzer("autocompleteAnalyzer").custom().tokenizer("ngram_tokenizer").tokenFilters("asciifolding", "lowercase");
		context.analyzer("autocompleteSearchAnalyzer").custom().tokenizer("search_query_tokenizer").tokenFilters("asciifolding", "lowercase");
		context.tokenizer("ngram_tokenizer").type("ngram").param("min_gram", 1).param("max_gram", 32).param("token_chars", "punctuation", "letter", "digit");
		context.tokenizer("search_query_tokenizer").type("whitespace");
		context.tokenFilter("ngram_filter").type("ngram").param("min_gram", 1).param("max_gram", 32);
		context.normalizer("sortNormalizer").custom().tokenFilters("asciifolding", "lowercase");
	}
}