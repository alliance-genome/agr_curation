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
	
DROP TABLE tmp_vocab_set;

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
	
DROP TABLE tmp_vocab_link;

-- Set all note terms to belong to general note vocabulary
UPDATE vocabularyterm SET vocabulary_id = subquery.id 
	FROM (SELECT id FROM vocabulary WHERE vocabularylabel = 'note_type') AS subquery
	WHERE vocabularyterm.vocabulary_id IN (
		SELECT id FROM vocabulary WHERE vocabularylabel = 'allele_note_type'
			OR vocabularylabel = 'da_note_type'
			OR vocabularylabel = 'gene_note_type'
			OR vocabularylabel = 'construct_component_note_type'
	);	
	
