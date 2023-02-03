ALTER TABLE affectedgenomicmodel
	ADD COLUMN subtype_id bigint;
	
ALTER TABLE affectedgenomicmodel
	ADD CONSTRAINT affectedgenomicmodel_subtype_id_fk
		FOREIGN KEY (subtype_id) REFERENCES vocabularyterm (id);
		
ALTER TABLE affectedgenomicmodel_aud
	ADD COLUMN subtype_id bigint;
	
INSERT INTO vocabulary (id, name)
	VALUES
		(nextval('hibernate_sequence'), 'Affected genomic model subtypes');
		
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'fish', id FROM vocabulary WHERE name = 'Affected genomic model subtypes';

INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'genotype', id FROM vocabulary WHERE name = 'Affected genomic model subtypes';

INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'strain', id FROM vocabulary WHERE name = 'Affected genomic model subtypes';
