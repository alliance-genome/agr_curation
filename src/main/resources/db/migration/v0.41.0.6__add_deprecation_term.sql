INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('vocabularyterm_seq'), 'deprecation_reason', id FROM vocabulary WHERE vocabularylabel = 'note_type';
	
INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'gene_note_type'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'deprecation_reason' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'note_type'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;