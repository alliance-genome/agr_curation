CREATE TABLE allelegenomicentityassociation (
	id bigint PRIMARY KEY,
	evidencecode_curie varchar(255),
	relatednote_id bigint,
	relation_id bigint
	);
	
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_id_fk FOREIGN KEY (id) REFERENCES association (id);
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_evidencecode_curie_fk FOREIGN KEY (evidencecode_curie) REFERENCES ecoterm (curie);
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_relatednote_id_fk FOREIGN KEY (relatednote_id) REFERENCES note (id);
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);

CREATE INDEX allelegenomicentityassociation_relation_index ON allelegenomicentityassociation USING btree (relation_id);

CREATE TABLE allelegenomicentityassociation_aud (
	id bigint NOT NULL,
	evidencecode_curie varchar(255),
	relatednote_id bigint,
	relation_id bigint,
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
	);

ALTER TABLE allelegenomicentityassociation_aud ADD CONSTRAINT allelegenomicentityassociation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES evidenceassociation_aud (id, rev);

CREATE TABLE allelegeneassociation (
	id bigint PRIMARY KEY,
	subject_curie varchar(255),
	object_curie varchar(255)
	);
	
ALTER TABLE allelegeneassociation ADD CONSTRAINT allelegeneassociation_id_fk FOREIGN KEY (id) REFERENCES allelegenomicentityassociation (id);
ALTER TABLE allelegeneassociation ADD CONSTRAINT allelegeneassociation_subject_curie_fk FOREIGN KEY (subject_curie) REFERENCES allele (curie);
ALTER TABLE allelegeneassociation ADD CONSTRAINT allelegeneassociation_object_curie_fk FOREIGN KEY (object_curie) REFERENCES gene (curie);

CREATE INDEX allelegeneassociation_subject_index ON allelegeneassociation USING btree (subject_curie);
CREATE INDEX allelegeneassociation_object_index ON allelegeneassociation USING btree (object_curie);

CREATE TABLE allelegeneassociation_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	subject_curie varchar(255),
	object_curie varchar(255),
	PRIMARY KEY (id, rev)
	);

ALTER TABLE allelegeneassociation_aud ADD CONSTRAINT allelegeneassociation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES allelegenomicentityassociation_aud (id, rev);

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'is_allele_of', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'mutation_excludes', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'mutation_does_not_delete', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'mutation_does_not_duplicate', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'mutation_does_not_invert', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'mutation_involves', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'aneuploid_chromosome', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'decreased_translational_product_level', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'deletion', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'full_deletion', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'partial_deletion', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), '3''_deletion', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), '5''_deletion', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'duplication', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'full_duplication', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'partial_duplication', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), '3''_duplication', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), '5''_duplication', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'gene_fusion', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'inversion', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'inversion_breakpoint', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'normal', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';

INSERT INTO vocabularytermset(id, name, vocabularylabel, vocabularytermsetvocabulary_id) SELECT nextval('hibernate_sequence'), 'Allele Gene Association Relation', 'allele_gene_relation', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';

CREATE TABLE tmp_vocab_link (
	vocabularytermsets_id bigint,
	memberterms_id bigint
	);
	
INSERT INTO tmp_vocab_link (memberterms_id) SELECT id FROM vocabularyterm WHERE (name = 'is_allele_of'
	OR name = 'mutation_excludes'
	OR name = 'mutation_does_not_delete'
	OR name = 'mutation_does_not_duplicate'
	OR name = 'mutation_does_not_invert'
	OR name = 'mutation_involves'
	OR name = 'aneuploid_chromosome'
	OR name = 'decreased_translational_product_level'
	OR name = 'deletion'
	OR name = 'full_deletion'
	OR name = 'partial_deletion'
	OR name = '3''_deletion'
	OR name = '5''_deletion'
	OR name = 'duplication'
	OR name = 'full_duplication'
	OR name = 'partial_duplication'
	OR name = '3''_duplication'
	OR name = '5''_duplication'
	OR name = 'gene_fusion'
	OR name = 'inversion'
	OR name = 'inversion_breakpoint'
	OR name = 'normal') AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'allele_relation');
	
UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'allele_gene_relation') AS subquery
	WHERE vocabularytermsets_id IS NULL;
	
INSERT INTO vocabularytermset(id, name, vocabularylabel, vocabularytermsetvocabulary_id) SELECT nextval('hibernate_sequence'), 'Allele Genomic Entity Association Note Type', 'allele_genomic_entity_association_note_type', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'note_type';

INSERT INTO tmp_vocab_link (memberterms_id) SELECT id FROM vocabularyterm WHERE name = 'comment' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'note_type');

UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'allele_genomic_entity_association_note_type') AS subquery
	WHERE vocabularytermsets_id IS NULL;
	
INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT vocabularytermsets_id, memberterms_id FROM tmp_vocab_link;
	
DROP TABLE tmp_vocab_link;