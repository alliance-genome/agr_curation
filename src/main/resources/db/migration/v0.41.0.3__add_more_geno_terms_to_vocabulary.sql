INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GENO:0000918', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'agm_allele_association_geno_terms';
