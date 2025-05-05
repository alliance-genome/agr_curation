INSERT INTO slotannotation (id, slotannotationtype, singleagm_id, displaytext, formattext, nametype_id)
  	WITH
  		t1 AS (
			SELECT id, name FROM affectedgenomicmodel WHERE name IS NOT NULL
		),
		t2 AS (
			SELECT id FROM vocabularyterm WHERE name = 'full_name'
		)
	SELECT nextval('slotannotation_seq'), 'AgmFullNameSlotAnnotation', t1.id, t1.name, t1.name, t2.id
	FROM t1, t2;
	
ALTER TABLE affectedgenomicmodel DROP COLUMN name;

DROP TABLE affectedgenomicmodel_synonyms;