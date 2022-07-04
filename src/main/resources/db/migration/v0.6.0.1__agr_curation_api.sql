ALTER TABLE reference
	ADD COLUMN IF NOT EXISTS primarycrossreference varchar (255);

ALTER TABLE reference
	ADD CONSTRAINT reference_primarycrossreference_uk UNIQUE (primarycrossreference);

ALTER TABLE reference_aud
	ADD COLUMN IF NOT EXISTS primarycrossreference varchar (255);
	
UPDATE reference SET primarycrossreference = curie;

CREATE TABLE IF NOT EXISTS reference_secondarycrossreferences (
	reference_primarycrossreference varchar (255),
	secondarycrossreferences varchar (255)
	);
	
CREATE TABLE IF NOT EXISTS reference_secondarycrossreferences_aud (
	rev integer,
	reference_primarycrossreference varchar (255),
	secondarycrossreferences varchar (255),
	revtype smallint
	);
	
ALTER TABLE reference_secondarycrossreferences
	ADD CONSTRAINT reference_secondarycrossreferences_primarycrossreference_fk
	FOREIGN KEY (reference_primarycrossreference) REFERENCES reference(primarycrossreference);
	
CREATE TABLE IF NOT EXISTS note_reference (
	note_id bigint,
	references_curie varchar (255)
	);

CREATE TABLE IF NOT EXISTS note_reference_aud (
	rev integer,
	note_id bigint,
	references_curie varchar (255),
	revtype smallint
	);
	
ALTER TABLE note_reference
	DROP CONSTRAINT IF EXISTS "fkpjpycg6lduif89o5ahp4d8u8";

ALTER TABLE note_reference
	RENAME references_curie TO references_primarycrossreference;
	
ALTER TABLE note_reference_aud
	RENAME references_curie TO references_primarycrossreference;
	
UPDATE note_reference n
	SET references_primarycrossreference = (SELECT primarycrossreference FROM reference WHERE curie = n.references_primarycrossreference);	
	
ALTER TABLE diseaseannotation		
	DROP CONSTRAINT IF EXISTS "fkk6hg8sfqhqhlsdjmyex63bvo7";

ALTER TABLE diseaseannotation
	DROP CONSTRAINT IF EXISTS "fk77fmab327prjh1sb7gk6na6ak";

ALTER TABLE diseaseannotation
	DROP COLUMN IF EXISTS reference_curie;
	
ALTER TABLE diseaseannotation_aud
	DROP COLUMN IF EXISTS reference_curie;

ALTER TABLE diseaseannotation
	RENAME singlereference_curie TO singlereference_primarycrossreference;
	
ALTER TABLE diseaseannotation_aud
	RENAME singlereference_curie TO singelreference_primarycrossreference;
	
UPDATE diseaseannotation d
	SET singlereference_primarycrossreference = (SELECT primarycrossreference FROM reference WHERE curie = d.singlereference_primarycrossreference);

ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_singlereference_primarycrossreference_fk
	FOREIGN KEY (singlereference_primarycrossreference) REFERENCES reference(primarycrossreference);
	
ALTER TABLE conditionrelation	
	DROP CONSTRAINT IF EXISTS "fkq7oftj89x5jfekjhhc0lah3j3";

ALTER TABLE conditionrelation
	ADD COLUMN IF NOT EXISTS singlereference_curie varchar (255);
	
ALTER TABLE conditionrelation_aud
	ADD COLUMN IF NOT EXISTS singlereference_curie varchar (255);

ALTER TABLE conditionrelation
	RENAME singlereference_curie TO singlereference_primarycrossreference;

ALTER TABLE conditionrelation_aud
	RENAME singlereference_curie TO singlereference_primarycrossreference;
	
UPDATE conditionrelation c
	SET singlereference_primarycrossreference = (SELECT primarycrossreference FROM reference WHERE curie = c.singlereference_primarycrossreference);

ALTER TABLE conditionrelation
	ADD CONSTRAINT conditionrelation_singlereference_primarycrossreference_fk
	FOREIGN KEY (singlereference_primarycrossreference) REFERENCES reference(primarycrossreference);
	
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
	DROP CONSTRAINT IF EXISTS "fkb11h1yvb7lchgw07wxspntpsc";
	
ALTER TABLE paperhandle
	RENAME reference_curie TO reference_primarycrossreference;
	
ALTER TABLE paperhandle_aud
	RENAME reference_curie TO reference_primarycrossreference;

UPDATE paperhandle p
	SET reference_primarycrossreference = (SELECT primarycrossreference FROM reference WHERE curie = p.reference_primarycrossreference);

ALTER TABLE paperhandle
	ADD CONSTRAINT paperhandle_reference_primarycrossreference_fk
	FOREIGN KEY (reference_primarycrossreference) REFERENCES reference(primarycrossreference);
	
ALTER TABLE reference
	DROP CONSTRAINT reference_pkey;
	
ALTER TABLE reference
	ADD PRIMARY KEY (primarycrossreference);