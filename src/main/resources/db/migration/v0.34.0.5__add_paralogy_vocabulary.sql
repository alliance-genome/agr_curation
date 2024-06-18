
UPDATE vocabulary SET vocabularylabel = 'homology_confidence', name = 'Homology confidence' WHERE vocabularylabel = 'ortho_confidence';

UPDATE vocabulary SET vocabularylabel = 'homology_prediction_method', name = 'Homology prediction methods' WHERE vocabularylabel = 'ortho_prediction_method';

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'SGD', id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method';

DELETE from vocabularyterm where vocabulary_id = (select id from vocabulary where vocabularylabel = 'paralogy_confidence');

DELETE from vocabularyterm where vocabulary_id = (select id from vocabulary where vocabularylabel = 'paralogy_prediction_method');

DELETE from vocabulary where vocabularylabel = 'paralogy_confidence';

DELETE from vocabulary where vocabularylabel = 'paralogy_prediction_method';    


INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Paralogy prediction method', 'paralogy_prediction_method', id, 'Prediction methods that are valid for paralogy' FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Orthology prediction method', 'orthology_prediction_method', id, 'Prediction methods that are valid for orthology' FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method';


INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'SGD' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'Ensembl Compara' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'HGNC' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'InParanoid' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'OMA' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'OrthoFinder' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'OrthoInspector' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'PANTHER' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'PhylomeDB' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'paralogy_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'SonicParanoid' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'Ensembl Compara' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'HGNC' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'Hieranoid' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'InParanoid' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'OMA' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'OrthoFinder' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'OrthoInspector' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'PANTHER' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'PhylomeDB' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'SonicParanoid' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'ZFIN' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'orthology_prediction_method'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'Xenbase' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;