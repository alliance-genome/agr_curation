CREATE TABLE tmp_da_ids_to_remove AS
	SELECT d.id FROM diseaseannotation d
		INNER JOIN organization o ON o.id = d.dataprovider_id
		WHERE d.dataprovider_id = d.secondarydataprovider_id
			AND o.abbreviation = 'OMIM';

DELETE FROM diseaseannotation_ontologyterm
	WHERE diseaseannotation_id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

DELETE FROM diseaseannotation_vocabularyterm
	WHERE diseaseannotation_id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

DELETE FROM diseaseannotation_modifieragm
	WHERE diseaseannotation_id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

DELETE FROM diseaseannotation_modifierallele
	WHERE diseaseannotation_id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

DELETE FROM diseaseannotation_modifiergene
	WHERE diseaseannotation_id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

DELETE FROM diseaseannotation_conditionrelation
	WHERE annotation_id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

DELETE FROM diseaseannotation_gene
	WHERE diseaseannotation_id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

CREATE TABLE tmp_notes_to_remove AS
	SELECT relatednotes_id FROM diseaseannotation_note WHERE annotation_id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

DELETE FROM diseaseannotation_note
	WHERE relatednotes_id IN (
		SELECT relatednotes_id FROM tmp_notes_to_remove
	);

DELETE FROM note
	WHERE id IN (
		SELECT relatednotes_id FROM tmp_notes_to_remove
	);
	
DROP TABLE tmp_notes_to_remove;

DELETE FROM genediseaseannotation
	WHERE id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

DELETE FROM diseaseannotation
	WHERE id IN (
		SELECT id FROM tmp_da_ids_to_remove
	);

DROP TABLE tmp_da_ids_to_remove;