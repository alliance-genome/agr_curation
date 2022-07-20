ALTER TABLE reference
	ADD COLUMN IF NOT EXISTS submittedcrossreference varchar (255),
	ADD COLUMN IF NOT EXISTS primarycrossreference varchar (255);

ALTER TABLE reference
	ADD CONSTRAINT reference_submittedcrossreference_uk UNIQUE (submittedcrossreference);

ALTER TABLE reference_aud
	ADD COLUMN IF NOT EXISTS submittedcrossreference varchar (255),
	ADD COLUMN IF NOT EXISTS primarycrossreference varchar (255);
	
UPDATE reference SET submittedcrossreference = curie;

UPDATE reference SET primarycrossreference = curie;

CREATE TABLE IF NOT EXISTS reference_secondarycrossreferences (
	reference_submittedcrossreference varchar (255),
	secondarycrossreferences varchar (255)
	);
	
CREATE TABLE IF NOT EXISTS reference_secondarycrossreferences_aud (
	rev integer,
	reference_submittedcrossreference varchar (255),
	secondarycrossreferences varchar (255),
	revtype smallint
	);
	
ALTER TABLE reference_secondarycrossreferences
	ADD CONSTRAINT reference_secondarycrossreferences_submittedcrossreference_fk
	FOREIGN KEY (reference_submittedcrossreference) REFERENCES reference(submittedcrossreference);
	
ALTER TABLE note_reference
	DROP CONSTRAINT IF EXISTS "fkpjpycg6lduif89o5ahp4d8u8";

ALTER TABLE note_reference
	RENAME references_curie TO references_submittedcrossreference;
	
ALTER TABLE note_reference_aud
	RENAME references_curie TO references_submittedcrossreference;
	
UPDATE note_reference n
	SET references_submittedcrossreference = (SELECT submittedcrossreference FROM reference WHERE curie = n.references_submittedcrossreference);	
	
ALTER TABLE diseaseannotation		
	DROP CONSTRAINT IF EXISTS "fkk6hg8sfqhqhlsdjmyex63bvo7";

ALTER TABLE diseaseannotation
	RENAME singlereference_curie TO singlereference_submittedcrossreference;
	
ALTER TABLE diseaseannotation_aud
	RENAME singlereference_curie TO singlereference_submittedcrossreference;
	
UPDATE diseaseannotation d
	SET singlereference_submittedcrossreference = (SELECT submittedcrossreference FROM reference WHERE curie = d.singlereference_submittedcrossreference);

ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_singlereference_submittedcrossreference_fk
	FOREIGN KEY (singlereference_submittedcrossreference) REFERENCES reference(submittedcrossreference);
	
ALTER TABLE conditionrelation	
	DROP CONSTRAINT IF EXISTS "fkq7oftj89x5jfekjhhc0lah3j3";

ALTER TABLE conditionrelation
	RENAME singlereference_curie TO singlereference_submittedcrossreference;

ALTER TABLE conditionrelation_aud
	RENAME singlereference_curie TO singlereference_submittedcrossreference;
	
UPDATE conditionrelation c
	SET singlereference_submittedcrossreference = (SELECT submittedcrossreference FROM reference WHERE curie = c.singlereference_submittedcrossreference);

ALTER TABLE conditionrelation
	ADD CONSTRAINT conditionrelation_singlereference_submittedcrossreference_fk
	FOREIGN KEY (singlereference_submittedcrossreference) REFERENCES reference(submittedcrossreference);
	
CREATE TABLE IF NOT EXISTS paperhandle (
	handle varchar (255),
	reference_curie varchar (255)
	);
	
CREATE TABLE IF NOT EXISTS paperhandle_aud (
	rev integer,
	handle varchar (255),
	reference_curie varchar (255),
	revtype smallint
	);
	
ALTER TABLE paperhandle
	RENAME reference_curie TO reference_submittedcrossreference;
	
ALTER TABLE paperhandle_aud
	RENAME reference_curie TO reference_submittedcrossreference;

UPDATE paperhandle p
	SET reference_submittedcrossreference = (SELECT submittedcrossreference FROM reference WHERE curie = p.reference_submittedcrossreference);

ALTER TABLE paperhandle
	ADD CONSTRAINT paperhandle_reference_submittedcrossreference_fk
	FOREIGN KEY (reference_submittedcrossreference) REFERENCES reference(submittedcrossreference);
	
ALTER TABLE reference
	DROP CONSTRAINT reference_pkey;
	
ALTER TABLE reference
	ADD PRIMARY KEY (submittedcrossreference);
