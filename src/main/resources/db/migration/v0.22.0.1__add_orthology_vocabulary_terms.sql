INSERT INTO vocabulary (id, name) VALUES (nextval('hibernate_sequence'), 'Orthology best score');
INSERT INTO vocabulary (id, name) VALUES (nextval('hibernate_sequence'), 'Orthology confidence');
INSERT INTO vocabulary (id, name) VALUES (nextval('hibernate_sequence'), 'Orthology prediction methods');
INSERT INTO vocabularytermset (id, name, vocabularytermsetdescription, vocabularytermsetvocabulary_id) SELECT nextval('hibernate_sequence'), 'Orthology best reverse score', 'Subset of ''Orthology best score'' terms that apply to the reverse score', id FROM vocabulary WHERE name = 'Orthology best score';

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'Yes', id FROM vocabulary WHERE name = 'Orthology best score';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'No', id FROM vocabulary WHERE name = 'Orthology best score';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'Yes_Adjusted', id FROM vocabulary WHERE name = 'Orthology best score';

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'moderate', id FROM vocabulary WHERE name = 'Orthology confidence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'high', id FROM vocabulary WHERE name = 'Orthology confidence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'low', id FROM vocabulary WHERE name = 'Orthology confidence';

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'Ensembl Compara', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'HGNC', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'Hieranoid', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'InParanoid', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'OMA', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'OrthoFinder', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'OrthoInspector', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'PANTHER', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'PhylomeDB', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'SonicParanoid', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'ZFIN', id FROM vocabulary WHERE name = 'Orthology prediction methods';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'Xenbase', id FROM vocabulary WHERE name = 'Orthology prediction methods';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Orthology best reverse score'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'Yes' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Orthology best score'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;
 
INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Orthology best reverse score'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'No' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Orthology best score'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;
 