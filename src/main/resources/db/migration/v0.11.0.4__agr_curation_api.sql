ALTER TABLE diseaseannotation 
	RENAME COLUMN alliancecurie TO diseaseannotationcurie;

ALTER TABLE diseaseannotation_aud
	RENAME COLUMN alliancecurie TO diseaseannotationcurie;