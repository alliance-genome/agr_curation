ALTER TABLE reference
	ADD COLUMN IF NOT EXISTS displayxref varchar (255),
	ADD COLUMN IF NOT EXISTS title varchar (500);

ALTER TABLE reference
	ADD CONSTRAINT reference_displayxref_uk UNIQUE (displayxref);

ALTER TABLE reference_aud
	ADD COLUMN IF NOT EXISTS displayxref varchar (255),
	ADD COLUMN IF NOT EXISTS title varchar (500);
	
UPDATE reference SET displayxref = curie;

CREATE TABLE IF NOT EXISTS reference_crossreference (
	reference_curie varchar (255),
	crossreferences_curie varchar (255)
	);
	
CREATE TABLE IF NOT EXISTS reference_crossreference_aud (
	rev integer,
	reference_curie varchar (255),
	crossreferences_curie varchar (255),
	revtype smallint
	);
	
ALTER TABLE note_reference
	DROP CONSTRAINT IF EXISTS "fkpjpycg6lduif89o5ahp4d8u8";

ALTER TABLE note_reference
	RENAME references_curie TO references_displayxref;
	
ALTER TABLE note_reference_aud
	RENAME references_curie TO references_displayxref;
	
UPDATE note_reference n
	SET references_displayxref = (SELECT displayxref FROM reference WHERE curie = n.references_displayxref);	
	
ALTER TABLE reference_crossreference
	DROP CONSTRAINT IF EXISTS "fkkpoxc23w4fnw0wyaytu6vs00g";

ALTER TABLE reference_crossreference
	RENAME reference_curie TO reference_displayxref;
	
ALTER TABLE reference_crossreference_aud
	RENAME reference_curie TO reference_displayxref;
	
UPDATE reference_crossreference r
	SET reference_displayxref = (SELECT displayxref FROM reference WHERE curie = r.reference_displayxref);	
	
ALTER TABLE diseaseannotation		
	DROP CONSTRAINT IF EXISTS "fkk6hg8sfqhqhlsdjmyex63bvo7";

ALTER TABLE diseaseannotation
	DROP CONSTRAINT IF EXISTS "fk77fmab327prjh1sb7gk6na6ak";

ALTER TABLE diseaseannotation
	DROP COLUMN reference_curie;
	
ALTER TABLE diseaseannotation_aud
	DROP COLUMN reference_curie;

ALTER TABLE diseaseannotation
	RENAME singlereference_curie TO singlereference_displayxref;
	
ALTER TABLE diseaseannotation_aud
	RENAME singlereference_curie TO singelreference_displayxref;
	
UPDATE diseaseannotation d
	SET singlereference_displayxref = (SELECT displayxref FROM reference WHERE curie = d.singlereference_displayxref);

ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_singlereference_displayxref_fk
	FOREIGN KEY (singlereference_displayxref) REFERENCES reference(displayxref);
	
ALTER TABLE conditionrelation	
	DROP CONSTRAINT IF EXISTS "fkq7oftj89x5jfekjhhc0lah3j3";

ALTER TABLE conditionrelation
	RENAME singlereference_curie TO singlereference_displayxref;

ALTER TABLE conditionrelation_aud
	RENAME singlereference_curie TO singlereference_displayxref;
	
UPDATE conditionrelation c
	SET singlereference_displayxref = (SELECT displayxref FROM reference WHERE curie = c.singlereference_displayxref);

ALTER TABLE conditionrelation
	ADD CONSTRAINT conditionrelation_singlereference_displayxref_fk
	FOREIGN KEY (singlereference_displayxref) REFERENCES reference(displayxref);
	
ALTER TABLE paperhandle
	DROP CONSTRAINT IF EXISTS "fkb11h1yvb7lchgw07wxspntpsc";
	
ALTER TABLE paperhandle
	RENAME reference_curie TO reference_displayxref;
	
ALTER TABLE paperhandle_aud
	RENAME reference_curie TO reference_displayxref;

UPDATE paperhandle p
	SET reference_displayxref = (SELECT displayxref FROM reference WHERE curie = p.reference_displayxref);

ALTER TABLE paperhandle
	ADD CONSTRAINT paperhandle_reference_displayxref_fk
	FOREIGN KEY (reference_displayxref) REFERENCES reference(displayxref);
	
ALTER TABLE reference
	DROP CONSTRAINT reference_pkey;
	
ALTER TABLE reference
	ADD PRIMARY KEY (displayxref);