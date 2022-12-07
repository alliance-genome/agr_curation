-- Remove redundant vocabulary and terms
DELETE FROM vocabularyterm
	WHERE vocabulary_id = (SELECT id FROM vocabulary WHERE name = 'Allele Name Types');
	
DELETE FROM vocabulary WHERE name = 'Allele Name Types';

-- Create required Vocabulary, VocabularyTerm, and VocabularyTermSet entries

INSERT INTO vocabulary (id, name, vocabularydescription)
	VALUES
		(nextval('hibernate_sequence'), 'Name type', 'Type of name represented by a name annotation'),
		(nextval('hibernate_sequence'), 'Synonym scope', 'Scope of the synonym respresented by a name annotation');

INSERT INTO vocabularyterm (id, name, definition, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'nomenclature_symbol', 'A symbol for an object: e.g., pax6<sup>Leca2</sup>.', id FROM vocabulary WHERE name = 'Name type';
	
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'full_name', 'The full length name of an entity: e.g., broad angular dumpy.', id FROM vocabulary WHERE name = 'Name type';
	
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'systematic_name', 'A systematic name: e.g., CG4889<sup>1</sup>.', id FROM vocabulary WHERE name = 'Name type';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'ncbi_protein_name', id FROM vocabulary WHERE name = 'Name type';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'uniform', id FROM vocabulary WHERE name = 'Name type';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'non_uniform', id FROM vocabulary WHERE name = 'Name type';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'retired_name', id FROM vocabulary WHERE name = 'Name type';
	
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'unspecified', 'Unclassified name', id FROM vocabulary WHERE name = 'Name type';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'exact', id FROM vocabulary WHERE name = 'Synonym scope';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'broad', id FROM vocabulary WHERE name = 'Synonym scope';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'narrow', id FROM vocabulary WHERE name = 'Synonym scope';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'related', id FROM vocabulary WHERE name = 'Synonym scope';

INSERT INTO vocabularytermset (id, name, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('hibernate_sequence'), 'Symbol name types', id, 'Name types that are valid for symbols' FROM vocabulary WHERE name = 'Name type';
	
INSERT INTO vocabularytermset (id, name, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('hibernate_sequence'), 'Full name types', id, 'Name types that are valid for full names' FROM vocabulary WHERE name = 'Name type';

INSERT INTO vocabularytermset (id, name, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('hibernate_sequence'), 'Systematic name types', id, 'Name types that are valid for systematic names' FROM vocabulary WHERE name = 'Name type';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Symbol name types'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'nomenclature_symbol' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Name type'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Symbol name types'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'systematic_name' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Name type'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Systematic name types'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'systematic_name' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Name type'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Full name types'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'full_name' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Name type'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;
  	
-- Create NameSlotAnnotation tables
CREATE TABLE nameslotannotation (
	id bigint CONSTRAINT nameslotannotation_pkey PRIMARY KEY,
	displaytext varchar(255),
	formattext varchar(255),
	synonymurl varchar(255),
	nametype_id bigint,
	synonymscope_id bigint
	);
		
ALTER TABLE nameslotannotation
	ADD CONSTRAINT nameslotannotation_nametype_id_fk
		FOREIGN KEY (nametype_id) REFERENCES vocabularyterm (id);
		
ALTER TABLE nameslotannotation
	ADD CONSTRAINT nameslotannotation_synonymscope_id_fk
		FOREIGN KEY (synonymscope_id) REFERENCES vocabularyterm (id);
		
CREATE TABLE nameslotannotation_aud (
	id bigint,
	rev integer NOT NULL,
	displaytext varchar(255),
	formattext varchar(255),
	synonymurl varchar(255),
	nametype_id bigint,
	synonymscope_id bigint,
	PRIMARY KEY (id, rev)
);

ALTER TABLE nameslotannotation_aud
	ADD CONSTRAINT nameslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES slotannotation_aud;

-- Create AlleleSymbolSlotAnnotation tables
CREATE TABLE allelesymbolslotannotation (
	id bigint CONSTRAINT allelesymbolslotannotation_pkey PRIMARY KEY,
	singleallele_curie varchar(255)
	);
		
ALTER TABLE allelesymbolslotannotation
	ADD CONSTRAINT allelesymbolslotannotation_singleallele_curie_fk
		FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);	

CREATE TABLE allelesymbolslotannotation_aud (
	id bigint,
	singleallele_curie varchar(255),
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
);

ALTER TABLE allelesymbolslotannotation_aud
	ADD CONSTRAINT allelesymbolslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud;

-- Create AlleleFullNameSlotAnnotation tables
CREATE TABLE allelefullnameslotannotation (
	id bigint CONSTRAINT allelefullnameslotannotation_pkey PRIMARY KEY,
	singleallele_curie varchar(255)
	);
		
ALTER TABLE allelefullnameslotannotation
	ADD CONSTRAINT allelefullnameslotannotation_singleallele_curie_fk
		FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);	

CREATE TABLE allelefullnameslotannotation_aud (
	id bigint,
	singleallele_curie varchar(255),
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
);

ALTER TABLE allelefullnameslotannotation_aud
	ADD CONSTRAINT allelefullnameslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud;

-- Create AlleleSynonymSlotAnnotation tables
CREATE TABLE allelesynonymslotannotation (
	id bigint CONSTRAINT allelesynonymslotannotation_pkey PRIMARY KEY,
	singleallele_curie varchar(255)
	);

ALTER TABLE allelesynonymslotannotation
	ADD CONSTRAINT allelesynonymslotannotation_id_fk
		FOREIGN KEY (id) REFERENCES nameslotannotation (id);
		
ALTER TABLE allelesynonymslotannotation
	ADD CONSTRAINT allelesynonymslotannotation_singleallele_curie_fk
		FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);	

CREATE TABLE allelesynonymslotannotation_aud (
	id bigint,
	singleallele_curie varchar(255),
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
);

ALTER TABLE allelesynonymslotannotation_aud
	ADD CONSTRAINT allelesynonymslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud;


-- Create GeneSymbolSlotAnnotation tables
CREATE TABLE genesymbolslotannotation (
	id bigint CONSTRAINT genesymbolslotannotation_pkey PRIMARY KEY,
	singlegene_curie varchar(255)
	);
		
ALTER TABLE genesymbolslotannotation
	ADD CONSTRAINT genesymbolslotannotation_singlegene_curie_fk
		FOREIGN KEY (singlegene_curie) REFERENCES gene (curie);	

CREATE TABLE genesymbolslotannotation_aud (
	id bigint,
	singlegene_curie varchar(255),
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
);

ALTER TABLE genesymbolslotannotation_aud
	ADD CONSTRAINT genesymbolslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud;

-- Create GeneFullNameSlotAnnotation tables
CREATE TABLE genefullnameslotannotation (
	id bigint CONSTRAINT genefullnameslotannotation_pkey PRIMARY KEY,
	singlegene_curie varchar(255)
	);
		
ALTER TABLE genefullnameslotannotation
	ADD CONSTRAINT genefullnameslotannotation_singlegene_curie_fk
		FOREIGN KEY (singlegene_curie) REFERENCES gene (curie);	

CREATE TABLE genefullnameslotannotation_aud (
	id bigint,
	singlegene_curie varchar(255),
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
);

ALTER TABLE genefullnameslotannotation_aud
	ADD CONSTRAINT genefullnameslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud;

-- Create GeneSystematicNameSlotAnnotation tables
CREATE TABLE genesystematicnameslotannotation (
	id bigint CONSTRAINT genesystematicnameslotannotation_pkey PRIMARY KEY,
	singlegene_curie varchar(255)
	);

ALTER TABLE genesystematicnameslotannotation
	ADD CONSTRAINT genesystematicnameslotannotation_id_fk
		FOREIGN KEY (id) REFERENCES nameslotannotation (id);
		
ALTER TABLE genesystematicnameslotannotation
	ADD CONSTRAINT genesystematicnameslotannotation_singlegene_curie_fk
		FOREIGN KEY (singlegene_curie) REFERENCES gene (curie);	

CREATE TABLE genesystematicnameslotannotation_aud (
	id bigint,
	singlegene_curie varchar(255),
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
);

ALTER TABLE genesystematicnameslotannotation_aud
	ADD CONSTRAINT genesystematicnameslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud;

-- Create GeneSynonymSlotAnnotation tables
CREATE TABLE genesynonymslotannotation (
	id bigint CONSTRAINT genesynonymslotannotation_pkey PRIMARY KEY,
	singlegene_curie varchar(255)
	);
		
ALTER TABLE genesynonymslotannotation
	ADD CONSTRAINT genesynonymslotannotation_singlegene_curie_fk
		FOREIGN KEY (singlegene_curie) REFERENCES gene (curie);	

ALTER TABLE genesynonymslotannotation
	ADD CONSTRAINT genesynonymslotannotation_id_fk
		FOREIGN KEY (id) REFERENCES nameslotannotation (id);

CREATE TABLE genesynonymslotannotation_aud (
	id bigint,
	singlegene_curie varchar(255),
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
);

ALTER TABLE genesynonymslotannotation_aud
	ADD CONSTRAINT genesynonymslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud;
		
-- Migrate existing allele symbols
INSERT INTO allelesymbolslotannotation (id, singleallele_curie)
	SELECT nextval('hibernate_sequence'), curie FROM allele WHERE symbol IS NOT NULL;

INSERT INTO nameslotannotation(id)
	SELECT id FROM allelesymbolslotannotation;
	
INSERT INTO slotannotation(id)
	SELECT id FROM allelesymbolslotannotation;
	
UPDATE nameslotannotation
	SET displaytext = subquery.symbol, formattext = subquery.symbol
	FROM (
		SELECT a.symbol as symbol, s.id as id
			FROM allele a
				INNER JOIN allelesymbolslotannotation s ON a.curie = s.singleallele_curie
		) AS subquery
	WHERE nameslotannotation.id = subquery.id;
	
UPDATE nameslotannotation
	SET nametype_id = (
		SELECT id FROM vocabularyterm WHERE name = 'nomenclature_symbol'
		);

ALTER TABLE allelesymbolslotannotation
	ADD CONSTRAINT allelesymbolslotannotation_id_fk
		FOREIGN KEY (id) REFERENCES nameslotannotation (id);


-- Migrate existing allele names
INSERT INTO allelefullnameslotannotation (id, singleallele_curie)
	SELECT nextval('hibernate_sequence'), a.curie
		FROM allele a
			INNER JOIN genomicentity g on a.curie = g.curie
			WHERE g.name IS NOT NULL;

INSERT INTO nameslotannotation(id)
	SELECT id FROM allelefullnameslotannotation;
	
INSERT INTO slotannotation(id)
	SELECT id FROM allelefullnameslotannotation;
	
UPDATE nameslotannotation
	SET displaytext = subquery.name, formattext = subquery.name
	FROM (
		SELECT g.name as name, f.id as id
			FROM genomicentity g
				INNER JOIN allelefullnameslotannotation f ON g.curie = f.singleallele_curie
		) AS subquery
	WHERE nameslotannotation.id = subquery.id;
	
UPDATE nameslotannotation
	SET nametype_id = (
		SELECT id FROM vocabularyterm WHERE name = 'full_name'
		)
	WHERE nametype_id IS NULL;

ALTER TABLE allelefullnameslotannotation
	ADD CONSTRAINT allelefullnameslotannotation_id_fk
		FOREIGN KEY (id) REFERENCES nameslotannotation (id);
		
-- Migrate existing gene symbols
INSERT INTO genesymbolslotannotation (id, singlegene_curie)
	SELECT nextval('hibernate_sequence'), curie FROM gene WHERE symbol IS NOT NULL;

INSERT INTO nameslotannotation(id)
	SELECT id FROM genesymbolslotannotation;
	
INSERT INTO slotannotation(id)
	SELECT id FROM genesymbolslotannotation;
	
UPDATE nameslotannotation
	SET displaytext = subquery.symbol, formattext = subquery.symbol
	FROM (
		SELECT g.symbol as symbol, s.id as id
			FROM gene g
				INNER JOIN genesymbolslotannotation s ON g.curie = s.singlegene_curie
		) AS subquery
	WHERE nameslotannotation.id = subquery.id;
	
UPDATE nameslotannotation
	SET nametype_id = (
		SELECT id FROM vocabularyterm WHERE name = 'nomenclature_symbol'
			AND vocabulary_id = (SELECT id FROM vocabulary WHERE name = 'Name type')
		)
	WHERE nametype_id IS NULL;

ALTER TABLE genesymbolslotannotation
	ADD CONSTRAINT genesymbolslotannotation_id_fk
		FOREIGN KEY (id) REFERENCES nameslotannotation (id);

-- Migrate existing gene names
INSERT INTO genefullnameslotannotation (id, singlegene_curie)
	SELECT nextval('hibernate_sequence'), g.curie
		FROM gene g
			INNER JOIN genomicentity e on g.curie = e.curie
			WHERE e.name IS NOT NULL;

INSERT INTO nameslotannotation(id)
	SELECT id FROM genefullnameslotannotation;

INSERT INTO slotannotation(id)
	SELECT id FROM genefullnameslotannotation;

UPDATE nameslotannotation
	SET displaytext = subquery.name, formattext = subquery.name
	FROM (
		SELECT g.name as name, f.id as id
			FROM genomicentity g
				INNER JOIN genefullnameslotannotation f ON g.curie = f.singlegene_curie
		) AS subquery
	WHERE nameslotannotation.id = subquery.id;
	
UPDATE nameslotannotation
	SET nametype_id = (
		SELECT id FROM vocabularyterm WHERE name = 'full_name'
			AND vocabulary_id = (SELECT id FROM vocabulary WHERE name = 'Name type')
		)
	WHERE nametype_id IS NULL;

ALTER TABLE genefullnameslotannotation
	ADD CONSTRAINT genefullnameslotannotation_id_fk
		FOREIGN KEY (id) REFERENCES nameslotannotation (id);

ALTER TABLE nameslotannotation
	ADD CONSTRAINT nameslotannotation_id_fk
		FOREIGN KEY (id) REFERENCES slotannotation (id);
	
-- Move AGM names to affectedgenomicmodeltable
ALTER TABLE affectedgenomicmodel
	ADD COLUMN name text;
	
ALTER TABLE affectedgenomicmodel_aud
	ADD COLUMN name text;

UPDATE affectedgenomicmodel
	SET name = subquery.name
	FROM (
		SELECT curie, name
			FROM genomicentity
		) AS subquery
	WHERE affectedgenomicmodel.curie = subquery.curie;

-- Remove deprecated columns
ALTER TABLE allele
	DROP COLUMN symbol;
	
ALTER TABLE allele_aud
	DROP COLUMN symbol;
	
ALTER TABLE gene
	DROP COLUMN symbol;
	
ALTER TABLE gene_aud
	DROP COLUMN symbol;
	
ALTER TABLE genomicentity
	DROP COLUMN name;
	
ALTER TABLE genomicentity_aud
	DROP COLUMN name;