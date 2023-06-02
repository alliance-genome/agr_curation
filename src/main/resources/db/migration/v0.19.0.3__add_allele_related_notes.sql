CREATE TABLE allele_note (
	allele_curie varchar(255),
	relatednotes_id bigint UNIQUE
);

ALTER TABLE allele_note ADD CONSTRAINT allele_note_allele_curie_fk FOREIGN KEY (allele_curie) REFERENCES allele (curie);
ALTER TABLE allele_note ADD CONSTRAINT allele_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);

CREATE TABLE allele_note_aud (
    rev integer NOT NULL,
	allele_curie varchar(255),
	relatednotes_id bigint,
	revtype smallint
);

ALTER TABLE allele_note_aud ADD PRIMARY KEY (allele_curie, relatednotes_id, rev);

ALTER TABLE allele_note_aud ADD CONSTRAINT allele_note_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

INSERT INTO vocabulary (id, name) VALUES (nextval('hibernate_sequence'), 'Allele note types');
	
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'mutation_description', id, 'description of the nature of the mutation, often includes detials about how the allele was made and what you can do with the allele, part of the minimal data submission' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'comment', id, 'general note about the allele, not part of the mutation description' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'cytology', id, 'Free text comments about the cytology of the allele (visible by polytene squash) [FB only]' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'notes_on_origin', id, 'Free text comments about the origin of the allele. E.g. information that it was simultaneously induced with another mutation [FB only]' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'indel_verification', id, 'If indel allele, a free-text description of how it was vertified' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'indel_description', id, 'Description of the genomic position of the indel' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'restriction_allele', id, 'Allele was detected using restriction enzyme digest, eg RFLP or RAD-tags' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'detection_method', id, 'Method first used to detect an allele, eg sequencing or restriction enzymes' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'flanking_sequences', id, 'In order to fix the variant to the genome, we have flanking sequences for all variants, to enable lift-over to another genome build' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'coinjection_other', id, 'Free text description of what was coinjected with a primary construct (declared elsewhere) to generate a transgene (transgenic allele)' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'marker_for', id, 'Free text description of what biological entity (anatomical entity including cell type(s), cellular component(s), pathway output, etc.) the transgene acts as a marker for' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'positive_clone', id, 'A clone which is positive for an allele for which the exact genomic sequence is not known' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'transgene_content_summary', id, 'A summary of the transgene''s content, e.g. ''[T04B2.6::YFP + unc-119(+)]''' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'transgene_construction_summary', id, 'A summary of the manner in which the transgene was constructed.' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'inducer', id, 'Compound use to induce expression or activity' FROM vocabulary WHERE name = 'Allele note types';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'user_submitted_note', id, 'notes for an alele submitted by external users' FROM vocabulary WHERE name = 'Allele note types';