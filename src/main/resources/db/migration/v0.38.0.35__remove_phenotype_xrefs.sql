CREATE TABLE tmp AS SELECT DISTINCT crossreference_id FROM phenotypeannotation;

UPDATE phenotypeannotation SET crossreference_id = null;

DELETE FROM crossreference WHERE id IN (SELECT crossreference_id FROM tmp);

DROP TABLE tmp;