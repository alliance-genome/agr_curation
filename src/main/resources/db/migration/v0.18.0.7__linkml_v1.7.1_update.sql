ALTER TABLE diseaseannotation
	ADD COLUMN modinternalid varchar(255) UNIQUE;
	
ALTER TABLE diseaseannotation_aud
	ADD COLUMN modinternalid varchar(255);
	
CREATE TABLE diseaseannotation_biologicalentity (
	diseaseannotation_id bigint,
	diseasegeneticmodifiers_curie varchar(255)
	);
	
CREATE TABLE diseaseannotation_biologicalentity_aud (
	rev integer,
	diseaseannotation_id bigint,
	diseasegeneticmodifiers_curie varchar(255),
	revtype smallint,
	CONSTRAINT diseaseannotation_biologicalentity_aud_pkey PRIMARY KEY(diseaseannotation_id, diseasegeneticmodifiers_curie, rev)
	);
	
ALTER TABLE diseaseannotation_biologicalentity
	ADD CONSTRAINT diseaseannotation_biologicalentity_diseaseannotation_id_fk
	FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation(id);
	
ALTER TABLE diseaseannotation_biologicalentity
	ADD CONSTRAINT diseaseannotation_biologicalentity_dgm_curie_fk
	FOREIGN KEY (diseasegeneticmodifiers_curie) REFERENCES biologicalentity(curie);
		
ALTER TABLE diseaseannotation_biologicalentity_aud
	ADD CONSTRAINT diseaseannotation_biologicalentity_aud_rev_fk
	FOREIGN KEY (rev) REFERENCES revinfo(rev);
	
ALTER TABLE diseaseannotation
	DROP CONSTRAINT uk_7hierauo01uy17h3g1okxfbhd;
	
INSERT INTO diseaseannotation_biologicalentity (diseaseannotation_id, diseasegeneticmodifiers_curie)
	SELECT id, diseasegeneticmodifier_curie FROM diseaseannotation WHERE diseasegeneticmodifier_curie IS NOT NULL;
		
ALTER TABLE diseaseannotation
	DROP COLUMN diseasegeneticmodifier_curie;
	
ALTER TABLE diseaseannotation_aud
	DROP COLUMN diseasegeneticmodifier_curie;
	
UPDATE diseaseannotation d
	SET uniqueid = concat(subject_curie, '|', object_curie, '|', singlereference_curie)
	FROM genediseaseannotation g
	WHERE g.id = d.id AND uniqueid LIKE 'WBDOannot%';
	
UPDATE diseaseannotation d
	SET uniqueid = concat(subject_curie, '|', object_curie, '|', singlereference_curie)
	FROM agmdiseaseannotation a
	WHERE a.id = d.id AND uniqueid LIKE 'WBDOannot%';
	
UPDATE diseaseannotation d
	SET uniqueid = concat(subject_curie, '|', object_curie, '|', singlereference_curie)
	FROM allelediseaseannotation a
	WHERE a.id = d.id AND uniqueid LIKE 'WBDOannot%';
	
ALTER TABLE vocabularyterm_textsynonyms
	RENAME TO vocabularyterm_synonyms;
	
ALTER TABLE vocabularyterm_textsynonyms_aud
	RENAME TO vocabularyterm_synonyms_aud;
	
ALTER TABLE vocabularyterm_synonyms
	RENAME COLUMN textsynonyms TO synonyms;
	
ALTER TABLE vocabularyterm_synonyms_aud
	RENAME COLUMN textsynonyms TO synonyms;