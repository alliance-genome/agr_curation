UPDATE diseaseannotation
	SET uniqueid = CONCAT(uniqueid, '|true')
	WHERE negated = True AND uniqueid LIKE 'FB%'; 
	
UPDATE diseaseannotation
	SET uniqueid = CONCAT(uniqueid, '|false')
	WHERE negated = False AND uniqueid LIKE 'FB%'; 
	
UPDATE diseaseannotation 
	SET uniqueid = CONCAT(uniqueid, '|', vocabularyterm.name)
	FROM vocabularyterm
	WHERE diseaseannotation.diseasegeneticmodifierrelation_id = vocabularyterm.id AND uniqueid LIKE 'FB%';
	
UPDATE diseaseannotation
	SET uniqueid = CONCAT(uniqueid, '|', diseasegeneticmodifier_curie)
	WHERE diseasegeneticmodifier_curie IS NOT NULL AND uniqueid LIKE 'FB%';