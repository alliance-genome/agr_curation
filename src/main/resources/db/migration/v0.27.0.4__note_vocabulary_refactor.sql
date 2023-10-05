-- Create vocabulary for all note types
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('hibernate_sequence'), 'Note Type', 'note_type');

-- Create sets for each note type (use temporary table to avoid not null constraint)

CREATE TABLE tmp_vocab_set (
	id bigint,
	name varchar(255),
	vocabularytermsetdescription varchar(255),
	vocabularylabel varchar(255),
	vocabularytermsetvocabulary_id bigint);

INSERT INTO tmp_vocab_set (id, name, vocabularytermsetdescription, vocabularylabel)
	SELECT nextval('hibernate_sequence'), name, vocabularydescription, vocabularylabel
	FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_note_type'
		OR vocabulary.vocabularylabel = 'da_note_type'
		OR vocabulary.vocabularylabel = 'gene_note_type'
		OR vocabulary.vocabularylabel = 'construct_component_note_type';
	
UPDATE tmp_vocab_set SET vocabularytermsetvocabulary_id = subquery.id 
	FROM (SELECT id FROM vocabulary WHERE vocabularylabel = 'note_type') AS subquery
	WHERE vocabularylabel = 'allele_note_type'
		OR vocabularylabel = 'da_note_type'
		OR vocabularylabel = 'gene_note_type'
		OR vocabularylabel = 'construct_component_note_type';

INSERT INTO vocabularytermset (id, name, vocabularytermsetdescription, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT id, name, vocabularytermsetdescription, vocabularylabel, vocabularytermsetvocabulary_id FROM tmp_vocab_set;
	
DELETE FROM tmp_vocab_set;

-- Resolve duplicate comment term
UPDATE note SET notetype_id = subquery.id
	FROM (SELECT id FROM vocabularyterm WHERE name = 'comment' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'allele_note_type'
	)) as subquery
	WHERE notetype_id = (SELECT id FROM vocabularyterm WHERE name = 'comment' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'construct_component_note_type'
	));
	
DELETE FROM vocabularyterm WHERE name = 'comment' AND vocabulary_id = (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'construct_component_note_type'
	);
	
UPDATE vocabularyterm SET definition = 'general comment' WHERE name = 'comment';

-- Create temp table for links between new sets and terms	
CREATE TABLE tmp_vocab_link (
	vocabularytermsets_id bigint,
	memberterms_id bigint);

INSERT INTO tmp_vocab_link (memberterms_id)
	SELECT id FROM vocabularyterm WHERE vocabulary_id = (
		SELECT id from vocabulary where vocabularylabel = 'allele_note_type'
	);
	
UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'allele_note_type') AS subquery
	WHERE vocabularytermsets_id IS NULL;


INSERT INTO tmp_vocab_link (memberterms_id)
	SELECT id FROM vocabularyterm WHERE vocabulary_id = (
		SELECT id from vocabulary where vocabularylabel = 'gene_note_type'
	);
	
UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'gene_note_type') AS subquery
	WHERE vocabularytermsets_id IS NULL;
	

INSERT INTO tmp_vocab_link (memberterms_id)
	SELECT id FROM vocabularyterm WHERE vocabulary_id = (
		SELECT id from vocabulary where vocabularylabel = 'da_note_type'
	);
	
UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'da_note_type') AS subquery
	WHERE vocabularytermsets_id IS NULL;
	
INSERT INTO tmp_vocab_link (memberterms_id)
	SELECT id FROM vocabularyterm WHERE vocabulary_id = (
		SELECT id from vocabulary where vocabularylabel = 'construct_component_note_type'
	);

INSERT INTO tmp_vocab_link (memberterms_id)
	SELECT id FROM vocabularyterm WHERE name = 'comment' AND vocabulary_id = (
		SELECT id from vocabulary where vocabularylabel = 'allele_note_type'
	);
	
UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'construct_component_note_type') AS subquery
	WHERE vocabularytermsets_id IS NULL;
	
INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT vocabularytermsets_id, memberterms_id FROM tmp_vocab_link;
	
DELETE FROM tmp_vocab_link;

-- Set all note terms to belong to general note vocabulary
UPDATE vocabularyterm SET vocabulary_id = subquery.id 
	FROM (SELECT id FROM vocabulary WHERE vocabularylabel = 'note_type') AS subquery
	WHERE vocabularyterm.vocabulary_id IN (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'allele_note_type'
			OR vocabularylabel = 'da_note_type'
			OR vocabularylabel = 'gene_note_type'
			OR vocabularylabel = 'construct_component_note_type'
	);	
	
-- Delete old vocabularies
DELETE FROM vocabulary WHERE vocabularylabel = 'allele_note_type'
	OR vocabularylabel = 'da_note_type'
	OR vocabularylabel = 'gene_note_type'
	OR vocabularylabel = 'construct_component_note_type';
	
-- Consolidate construct relations
UPDATE vocabulary SET vocabularylabel = 'construct_relation', name = 'Construct Relation' WHERE vocabularylabel = 'construct_genomic_entity_predicate';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT nextval('hibernate_sequence'), 'Construct Genomic Entity Relation', 'construct_genomic_entity_relation', id
	FROM vocabulary where vocabularylabel = 'construct_relation';

-- Create vocabulary for all allele relation types
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('hibernate_sequence'), 'Allele Relation', 'allele_relation');

-- Create sets for each allele relation type (use temporary table to avoid not null constraint)

INSERT INTO tmp_vocab_set (id, name, vocabularylabel)
	SELECT nextval('hibernate_sequence'), name, vocabularylabel
	FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_allele_predicate'
		OR vocabulary.vocabularylabel = 'allele_construct_predicate'
		OR vocabulary.vocabularylabel = 'allele_generation_method_predicate';
	
UPDATE tmp_vocab_set SET vocabularytermsetvocabulary_id = subquery.id 
	FROM (SELECT id FROM vocabulary WHERE vocabularylabel = 'allele_relation') AS subquery
	WHERE vocabularytermsetvocabulary_id IS NULL;
	
UPDATE tmp_vocab_set SET vocabularylabel = 'allele_allele_relation', name = 'Allele Allele Association Relation' WHERE vocabularylabel = 'allele_allele_predicate';
UPDATE tmp_vocab_set SET vocabularylabel = 'allele_construct_relation', name = 'Allele Construct Association Relation' WHERE vocabularylabel = 'allele_construct_predicate';
UPDATE tmp_vocab_set SET vocabularylabel = 'allele_generation_method_relation', name = 'Allele Generation Method Association Relation' WHERE vocabularylabel = 'allele_generation_method_predicate';

INSERT INTO vocabularytermset (id, name, vocabularytermsetdescription, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT id, name, vocabularytermsetdescription, vocabularylabel, vocabularytermsetvocabulary_id FROM tmp_vocab_set;
	
DROP TABLE tmp_vocab_set;

-- Populate temp table with links between new allele relation sets and terms	
INSERT INTO tmp_vocab_link (memberterms_id)
	SELECT id FROM vocabularyterm WHERE vocabulary_id = (
		SELECT id from vocabulary where vocabularylabel = 'allele_allele_predicate'
	);
	
UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'allele_allele_relation') AS subquery
	WHERE vocabularytermsets_id IS NULL;


INSERT INTO tmp_vocab_link (memberterms_id)
	SELECT id FROM vocabularyterm WHERE vocabulary_id = (
		SELECT id from vocabulary where vocabularylabel = 'allele_construct_predicate'
	);
	
UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'allele_construct_relation') AS subquery
	WHERE vocabularytermsets_id IS NULL;
	

INSERT INTO tmp_vocab_link (memberterms_id)
	SELECT id FROM vocabularyterm WHERE vocabulary_id = (
		SELECT id from vocabulary where vocabularylabel = 'allele_generation_method_predicate'
	);
	
UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'allele_generation_method_relation') AS subquery
	WHERE vocabularytermsets_id IS NULL;
	
INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT vocabularytermsets_id, memberterms_id FROM tmp_vocab_link;
	
DROP TABLE tmp_vocab_link;

-- Set all note terms to belong to general note vocabulary
UPDATE vocabularyterm SET vocabulary_id = subquery.id 
	FROM (SELECT id FROM vocabulary WHERE vocabularylabel = 'allele_relation') AS subquery
	WHERE vocabularyterm.vocabulary_id IN (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'allele_allele_predicate'
			OR vocabularylabel = 'allele_construct_predicate'
			OR vocabularylabel = 'allele_generation_method_predicate'
	);	
	
-- Delete old vocabularies
DELETE FROM vocabulary WHERE vocabularylabel = 'allele_allele_predicate'
	OR vocabularylabel = 'allele_construct_predicate'
	OR vocabularylabel = 'allele_generation_method_predicate';

