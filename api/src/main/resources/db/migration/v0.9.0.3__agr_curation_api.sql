DELETE FROM diseaseannotation_conditionrelation
	WHERE diseaseannotation_id IN (
		SELECT id FROM diseaseannotation
			WHERE id NOT IN (SELECT id from genediseaseannotation)
				AND id NOT IN (SELECT id FROM allelediseaseannotation)
				AND id NOT IN (SELECT id FROM agmdiseaseannotation));
				
DELETE FROM diseaseannotation_ecoterm
	WHERE diseaseannotation_id IN (
		SELECT id FROM diseaseannotation
			WHERE id NOT IN (SELECT id from genediseaseannotation)
				AND id NOT IN (SELECT id FROM allelediseaseannotation)
				AND id NOT IN (SELECT id FROM agmdiseaseannotation));
				
DELETE FROM diseaseannotation_gene
	WHERE diseaseannotation_id IN (
		SELECT id FROM diseaseannotation
			WHERE id NOT IN (SELECT id from genediseaseannotation)
				AND id NOT IN (SELECT id FROM allelediseaseannotation)
				AND id NOT IN (SELECT id FROM agmdiseaseannotation));
				
DELETE FROM diseaseannotation_note
	WHERE diseaseannotation_id IN (
		SELECT id FROM diseaseannotation
			WHERE id NOT IN (SELECT id from genediseaseannotation)
				AND id NOT IN (SELECT id FROM allelediseaseannotation)
				AND id NOT IN (SELECT id FROM agmdiseaseannotation));
				
DELETE FROM diseaseannotation_vocabularyterm
	WHERE diseaseannotation_id IN (
		SELECT id FROM diseaseannotation
			WHERE id NOT IN (SELECT id from genediseaseannotation)
				AND id NOT IN (SELECT id FROM allelediseaseannotation)
				AND id NOT IN (SELECT id FROM agmdiseaseannotation));		
				
DELETE FROM diseaseannotation
	WHERE id NOT IN (SELECT id from genediseaseannotation)
		AND id NOT IN (SELECT id FROM allelediseaseannotation)
		AND id NOT IN (SELECT id FROM agmdiseaseannotation);
		
DELETE FROM association
	WHERE id NOT IN (SELECT id from genediseaseannotation)
		AND id NOT IN (SELECT id FROM allelediseaseannotation)
		AND id NOT IN (SELECT id FROM agmdiseaseannotation);
				
		