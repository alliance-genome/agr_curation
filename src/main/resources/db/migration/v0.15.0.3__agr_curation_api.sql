CREATE TABLE newcrossreference (
	id bigint,
	referencedcurie varchar(255),
	displayname varchar(255),
	resourcedescriptorpage_id bigint,
	dateupdated timestamp without time zone,
	datecreated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint
	);
	
CREATE TABLE newcrossreference_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	referencedcurie varchar(255),
	displayname varchar(255),
	resourcedescriptorpage_id bigint,
	PRIMARY KEY (id, rev)
	);
	
INSERT INTO newcrossreference (id, referencedcurie, displayname)
	SELECT nextval('hibernate_sequence'), curie, curie FROM crossreference;
	
ALTER TABLE genomicentity_crossreference
	ADD COLUMN crossreferences_id bigint;
	
ALTER TABLE ontologyterm_crossreference
	ADD COLUMN crossreferences_id bigint;
	
ALTER TABLE vocabularyterm_crossreference
	ADD COLUMN crossreferences_id bigint;
	
ALTER TABLE reference_crossreference
	ADD COLUMN crossreferences_id bigint;
	
ALTER TABLE genomicentity_crossreference_aud
	ADD COLUMN crossreferences_id bigint;
	
ALTER TABLE ontologyterm_crossreference_aud
	ADD COLUMN crossreferences_id bigint;
	
ALTER TABLE vocabularyterm_crossreference_aud
	ADD COLUMN crossreferences_id bigint;
	
ALTER TABLE reference_crossreference_aud
	ADD COLUMN crossreferences_id bigint;
	
UPDATE genomicentity_crossreference
	SET crossreferences_id = newcrossreference.id
	FROM newcrossreference
	WHERE genomicentity_crossreference.crossreferences_curie = newcrossreference.referencedcurie;
	
UPDATE ontologyterm_crossreference
	SET crossreferences_id = newcrossreference.id
	FROM newcrossreference
	WHERE ontologyterm_crossreference.crossreferences_curie = newcrossreference.referencedcurie;
	
UPDATE vocabularyterm_crossreference
	SET crossreferences_id = newcrossreference.id
	FROM newcrossreference
	WHERE vocabularyterm_crossreference.crossreferences_curie = newcrossreference.referencedcurie;
	
UPDATE reference_crossreference
	SET crossreferences_id = newcrossreference.id
	FROM newcrossreference
	WHERE reference_crossreference.crossreferences_curie = newcrossreference.referencedcurie;
	
DROP INDEX genomicentity_crossreference_crossreferences_curie_index;

DROP INDEX idxfxhndon9oi3648gb3s6oua4fd;	
	
DROP INDEX idx41w2gn9m2s5mjdydwbsqjhfox;

DROP INDEX idxsdesyork9yoruo27pe2cetjog;
	
DROP INDEX idx9ej6fab264th2fst1yq26s5vg;
	
ALTER TABLE genomicentity_crossreference
	DROP CONSTRAINT fk3fiksr8hbcttuwaiorcgeh5ip;
	
ALTER TABLE ontologyterm_crossreference
	DROP CONSTRAINT fk9508vlhm2u3xpuf5041d9ye8y;
	
ALTER TABLE vocabularyterm_crossreference
	DROP CONSTRAINT fk9uin0anwbl5mibr82471byv8r;
	
ALTER TABLE reference_crossreference
	DROP CONSTRAINT reference_crossreference_crossreferences_curie_fk;
	
DROP TABLE crossreference_pageareas;

DROP TABLE crossreference_pageareas_aud;	
	
DROP TABLE crossreference;

DROP TABLE crossreference_aud;

ALTER TABLE newcrossreference
	RENAME TO crossreference;

ALTER TABLE newcrossreference_aud
	RENAME TO crossreference_aud;
	
ALTER TABLE crossreference
  ADD CONSTRAINT crossreference_pkey
    PRIMARY KEY (id);
    
ALTER TABLE crossreference
	ADD CONSTRAINT resourcedescriptorpage_id_fk
		FOREIGN KEY (resourcedescriptorpage_id) REFERENCES resourcedescriptorpage (id);
		
ALTER TABLE genomicentity_crossreference
	ADD CONSTRAINT genomicentitycrossreference_crossreferences_id_fk
		FOREIGN KEY (crossreferences_id) REFERENCES crossreference (id);
		
ALTER TABLE ontologyterm_crossreference
	ADD CONSTRAINT ontologytermcrossreference_crossreferences_id_fk
		FOREIGN KEY (crossreferences_id) REFERENCES crossreference (id);
		
ALTER TABLE vocabularyterm_crossreference
	ADD CONSTRAINT vocabularytermcrossreference_crossreferences_id_fk
		FOREIGN KEY (crossreferences_id) REFERENCES crossreference (id);
		
ALTER TABLE reference_crossreference
	ADD CONSTRAINT referencecrossreference_crossreferences_id_fk
		FOREIGN KEY (crossreferences_id) REFERENCES crossreference (id);
		
CREATE INDEX genomicentity_crossreference_crossreferences_id_index ON genomicentity_crossreference USING btree (crossreferences_id);

CREATE INDEX genomicentity_crossreference_ge_curie_xref_id_index ON genomicentity_crossreference USING btree (genomicentity_curie, crossreferences_id);

CREATE INDEX ontologyterm_crossreference_crossreferences_id_index ON ontologyterm_crossreference USING btree (crossreferences_id);

CREATE INDEX vocabularyterm_crossreference_crossreferences_id_index ON vocabularyterm_crossreference USING btree (crossreferences_id);

CREATE INDEX reference_crossreference_crossreferences_id_index ON reference_crossreference USING btree (crossreferences_id);

ALTER TABLE genomicentity_crossreference
	DROP COLUMN crossreferences_curie;
	
ALTER TABLE ontologyterm_crossreference
	DROP COLUMN crossreferences_curie;
	
ALTER TABLE vocabularyterm_crossreference
	DROP COLUMN crossreferences_curie;
	
ALTER TABLE reference_crossreference
	DROP COLUMN crossreferences_curie;

ALTER TABLE genomicentity_crossreference_aud
	DROP COLUMN crossreferences_curie;
	
ALTER TABLE ontologyterm_crossreference_aud
	DROP COLUMN crossreferences_curie;
	
ALTER TABLE vocabularyterm_crossreference_aud
	DROP COLUMN crossreferences_curie;
	
ALTER TABLE reference_crossreference_aud
	DROP COLUMN crossreferences_curie;
	
CREATE TABLE dataprovider (
	id bigint CONSTRAINT dataprovider_pkey PRIMARY KEY,
	sourceorganization_id bigint,
	crossreference_id bigint,
	dateupdated timestamp without time zone,
	datecreated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint
	);

CREATE TABLE dataprovider_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	sourceorganization_id bigint,
	crossreference_id bigint,
	PRIMARY KEY (id, rev)
	);
	
INSERT INTO dataprovider (id, sourceorganization_id)
	SELECT nextval('hibernate_sequence'), id
		FROM organization
		WHERE organization.abbreviation IN ('FB', 'WB', 'RGD', 'SGD', 'MGI', 'ZFIN', 'OMIM');
	
ALTER TABLE dataprovider
	ADD CONSTRAINT dataprovider_crossreference_id_fk
		FOREIGN KEY (crossreference_id) REFERENCES crossreference (id);
		
ALTER TABLE dataprovider
	ADD CONSTRAINT dataprovider_sourceorganization_id_fk
		FOREIGN KEY (sourceorganization_id) REFERENCES organization (id);

ALTER TABLE diseaseannotation
	DROP CONSTRAINT diseaseannotation_dataprovider_id_fk;
	
ALTER TABLE diseaseannotation
	DROP CONSTRAINT diseaseannotation_secondarydataprovider_id_fk;
	
UPDATE diseaseannotation
	SET dataprovider_id = dataprovider.id
	FROM dataprovider
	WHERE dataprovider.sourceorganization_id = dataprovider_id;

UPDATE diseaseannotation
	SET secondarydataprovider_id = dataprovider.id
	FROM dataprovider
	WHERE dataprovider.sourceorganization_id = secondarydataprovider_id;
	
ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_dataprovider_id_fk
		FOREIGN KEY (dataprovider_id) REFERENCES dataprovider (id);
		
ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_secondarydataprovider_id_fk
		FOREIGN KEY (secondarydataprovider_id) REFERENCES dataprovider (id);

ALTER TABLE organization
	ADD COLUMN homepageresourcedescriptorpage_id bigint;
	
ALTER TABLE organization_aud
	ADD COLUMN homepageresourcedescriptorpage_id bigint;

ALTER TABLE organization
	ADD CONSTRAINT organization_homepageresourcedescriptorpage_id_fk
		FOREIGN KEY (homepageresourcedescriptorpage_id) REFERENCES resourcedescriptorpage (id);
		
UPDATE organization
	SET homepageresourcedescriptorpage_id = resourcedescriptorpage.id
	FROM resourcedescriptorpage INNER JOIN resourcedescriptor ON resourcedescriptorpage.resourcedescriptor_id = resourcedescriptor.id
	WHERE resourcedescriptor.prefix = organization.abbreviation AND resourcedescriptorpage.name = 'homepage'; 