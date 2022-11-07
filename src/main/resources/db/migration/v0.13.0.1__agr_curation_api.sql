-- Move uniqueId from Association to DiseaseAnnotation and add curie
ALTER TABLE diseaseannotation
	ADD COLUMN curie varchar(255),
	ADD COLUMN uniqueid varchar(2000) UNIQUE NOT NULL;
	
ALTER TABLE diseaseannotation_aud
	ADD COLUMN curie varchar(255),
	ADD COLUMN uniqueid varchar(2000);

UPDATE diseaseannotation
	SET uniqueid = subquery.uniqueid
	FROM (
		SELECT id, uniqueid
			FROM association
		) AS subquery
	WHERE diseaseannotation.id = subquery.id;
	
ALTER TABLE association
	DROP COLUMN uniqueid;

-- Delete diseaseAnnotationCurie
ALTER TABLE diseaseannotation 
	DROP COLUMN diseaseannotationcurie;
	
ALTER TABLE diseaseannotation_aud
	DROP COLUMN diseaseannotationcurie;
	
-- Create InformationContentEntity tables and move data from Reference tables
CREATE TABLE informationcontententity (
	curie varchar(255) CONSTRAINT informationcontententity_pkey PRIMARY KEY,
	datecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint
);

CREATE TABLE informationcontententity_aud (
	curie varchar(255) NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	PRIMARY KEY (curie, rev)
);

INSERT INTO informationcontententity (curie)
	SELECT curie FROM reference;

UPDATE informationcontententity
	SET datecreated = subquery.datecreated, dateupdated = subquery.dateupdated,
		createdby_id = subquery.createdby_id, updatedby_id = subquery.updatedby_id,
		internal = subquery.internal, obsolete = subquery.obsolete,
		dbdatecreated = subquery.dbdatecreated, dbdateupdated = subquery.dbdateupdated	
	FROM (
		SELECT curie, datecreated, dateupdated, createdby_id, updatedby_id, internal,
			obsolete, dbdatecreated, dbdateupdated
			FROM reference
		) AS subquery
	WHERE informationcontententity.curie = subquery.curie;
	
ALTER TABLE informationcontententity
	ADD CONSTRAINT informationcontententity_createdby_id_fk
		FOREIGN KEY (createdby_id) REFERENCES person (id);	

ALTER TABLE informationcontententity
	ADD CONSTRAINT informationcontententity_updatedby_id_fk
		FOREIGN KEY (updatedby_id) REFERENCES person (id);
	
INSERT INTO informationcontententity_aud (curie, rev, revtype)
	SELECT curie, rev, revtype FROM reference_aud;

ALTER TABLE informationcontententity_aud
	ADD CONSTRAINT informationcontententity_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);
	
ALTER TABLE reference_aud
	ADD CONSTRAINT reference_aud_curie_rev_fk
		FOREIGN KEY (curie, rev) REFERENCES informationcontententity_aud;
		
ALTER TABLE reference_aud
	DROP COLUMN revtype;
	
ALTER TABLE reference
	DROP COLUMN datecreated,
	DROP COLUMN dateupdated,
	DROP COLUMN internal,
	DROP COLUMN obsolete,
	DROP COLUMN createdby_id,
	DROP COLUMN updatedby_id,
	DROP COLUMN dbdatecreated,
	DROP COLUMN dbdateupdated;
	
CREATE INDEX informationcontententity_createdby_index
	ON public.informationcontententity USING btree (createdby_id);

CREATE INDEX informationcontententity_updatedby_index
	ON public.informationcontententity USING btree (updatedby_id);

-- Deprecate Allele sequencingStatus	
DROP INDEX allele_sequencingstatus_index;

ALTER TABLE allele
	DROP COLUMN sequencingstatus_id;
	
ALTER TABLE allele_aud
	DROP COLUMN sequencingstatus_id;
	
DELETE FROM vocabularyterm
	WHERE vocabulary_id = (
		SELECT id FROM vocabulary WHERE name = 'Sequencing status vocabulary'
	);
	
DELETE FROM vocabulary
	WHERE name = 'Sequencing status vocabulary';
	
-- Add evidence to Association
CREATE TABLE association_informationcontententity (
	association_id bigint,
	evidence_curie varchar(255),
	PRIMARY KEY (association_id, evidence_curie)
);

ALTER TABLE association_informationcontententity
	ADD CONSTRAINT association_informationcontententity_association_id_fk
		FOREIGN KEY (association_id) REFERENCES association (id);

ALTER TABLE association_informationcontententity
	ADD CONSTRAINT association_informationcontententity_evidence_curie_fk
		FOREIGN KEY (evidence_curie) REFERENCES informationcontententity (curie);
		
CREATE TABLE association_informationcontententity_aud (
	association_id bigint,
	evidence_curie varchar(255),
	rev integer NOT NULL,
	revtype smallint,
	PRIMARY KEY (rev, association_id, evidence_curie)
);

ALTER TABLE association_informationcontententity_aud
	ADD CONSTRAINT association_informationcontententity_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);
		
-- Create SlotAnnotation tables
CREATE TABLE slotannotation (
	id bigint CONSTRAINT slotannotation_pkey PRIMARY KEY,
	datecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint
);

ALTER TABLE slotannotation
	ADD CONSTRAINT slotannotation_createdby_id_fk
		FOREIGN KEY (createdby_id) REFERENCES person (id);	

ALTER TABLE slotannotation
	ADD CONSTRAINT slotannotation_updatedby_id_fk
		FOREIGN KEY (updatedby_id) REFERENCES person (id);

CREATE TABLE slotannotation_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	PRIMARY KEY (id, rev)
);

ALTER TABLE slotannotation_aud
	ADD CONSTRAINT slotannotation_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE TABLE slotannotation_informationcontententity (
	slotannotation_id bigint,
	evidence_curie varchar(255),
	PRIMARY KEY (slotannotation_id, evidence_curie)
);

ALTER TABLE slotannotation_informationcontententity
	ADD CONSTRAINT slotannotation_informationcontententity_association_id_fk
		FOREIGN KEY (slotannotation_id) REFERENCES slotannotation (id);

ALTER TABLE slotannotation_informationcontententity
	ADD CONSTRAINT slotannotation_informationcontententity_evidence_curie_fk
		FOREIGN KEY (evidence_curie) REFERENCES informationcontententity (curie);
		
CREATE TABLE slotannotation_informationcontententity_aud (
	slotannotation_id bigint,
	evidence_curie varchar(255),
	rev integer NOT NULL,
	revtype smallint,
	PRIMARY KEY (rev, slotannotation_id, evidence_curie)
);

ALTER TABLE slotannotation_informationcontententity_aud
	ADD CONSTRAINT slotannotation_informationcontententity_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);