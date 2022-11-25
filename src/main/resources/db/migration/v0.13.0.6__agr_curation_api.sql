-- Create Organization tables
CREATE TABLE organization (
	id bigint CONSTRAINT organization_pkey PRIMARY KEY,
	uniqueid varchar(2000) UNIQUE NOT NULL,
	abbreviation varchar(255) UNIQUE NOT NULL,
	fullname varchar(255),
	shortname varchar(255),
	datecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint
	);
	
CREATE TABLE organization_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	abbreviation varchar(255) NOT NULL,
	fullname varchar(255),
	shortname varchar(255),
	PRIMARY KEY (id, rev)
);
	
ALTER TABLE organization
	ADD CONSTRAINT organization_createdby_id_fk
		FOREIGN KEY (createdby_id) REFERENCES person (id);	

ALTER TABLE organization
	ADD CONSTRAINT organization_updatedby_id_fk
		FOREIGN KEY (updatedby_id) REFERENCES person (id);

ALTER TABLE organization_aud
	ADD CONSTRAINT organization_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);
		
ALTER TABLE organization_aud
	ADD CONSTRAINT organization_aud_id_fk
		FOREIGN KEY (id) REFERENCES organization (id);

-- Create AllianceMemberTables
CREATE TABLE alliancemember (
	id bigint CONSTRAINT alliancemember_pkey PRIMARY KEY
	);

CREATE TABLE alliancemember_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
	);

ALTER TABLE alliancemember
	ADD CONSTRAINT alliancemember_id_fk
		FOREIGN KEY (id) REFERENCES organization (id);	
		
ALTER TABLE alliancemember_aud
	ADD CONSTRAINT alliancemember_aud_id_fk
		FOREIGN KEY (id) REFERENCES alliancemember (id);
		
-- Create MOD entries
INSERT INTO organization (id, uniqueid, abbreviation, fullname)
	VALUES
		(nextval('hibernate_sequence'), 'FB', 'FB', 'FlyBase'),
		(nextval('hibernate_sequence'), 'MGI', 'MGI', 'Mouse Genome Informatics'),
		(nextval('hibernate_sequence'), 'RGD', 'RGD', 'Rat Genome Database'),
		(nextval('hibernate_sequence'), 'SGD', 'SGD', 'Saccharomyces Genome Database'),
		(nextval('hibernate_sequence'), 'WB', 'WB', 'WormBase'),
		(nextval('hibernate_sequence'), 'XB', 'XB', 'Xenbase'),
		(nextval('hibernate_sequence'), 'ZFIN', 'ZFIN', 'Zebrafish Information Network'),
		(nextval('hibernate_sequence'), 'GO', 'GO', 'Gene Ontology Consortium'),
		(nextval('hibernate_sequence'), 'OMIM', 'OMIM', 'Online Mendelian Inheritance in Man');

-- Create AllianceMember entries
INSERT INTO alliancemember (id)
	SELECT id FROM organization WHERE abbreviation in ('FB', 'MGI', 'RGD', 'SGD', 'WB', 'XB', 'ZFIN', 'GO');

-- Update DiseaseAnnotation table
ALTER TABLE diseaseannotation
	ADD COLUMN dataprovider_id bigint,
	ADD COLUMN secondarydataprovider_id bigint;
	
ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_dataprovider_id_fk
		FOREIGN KEY (dataprovider_id) REFERENCES organization (id);
	
ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_secondarydataprovider_id_fk
		FOREIGN KEY (secondarydataprovider_id) REFERENCES organization (id);
		
ALTER TABLE diseaseannotation_aud
	ADD COLUMN dataprovider_id bigint,
	ADD COLUMN secondarydataprovider_id bigint;
	
UPDATE diseaseannotation d
	SET dataprovider_id = o.id
	FROM organization o
	WHERE d.dataprovider = o.abbreviation;
	
UPDATE diseaseannotation d
	SET secondarydataprovider_id = o.id
	FROM organization o
	WHERE d.secondarydataprovider = o.abbreviation;
	
UPDATE diseaseannotation_aud d
	SET dataprovider_id = o.id
	FROM organization o
	WHERE d.dataprovider = o.abbreviation;
	
UPDATE diseaseannotation_aud d
	SET secondarydataprovider_id = o.id
	FROM organization o
	WHERE d.secondarydataprovider = o.abbreviation;
	
ALTER TABLE diseaseannotation
	DROP COLUMN dataprovider,
	DROP COLUMN secondarydataprovider;
	
ALTER TABLE diseaseannotation_aud
	DROP COLUMN dataprovider,
	DROP COLUMN secondarydataprovider;
