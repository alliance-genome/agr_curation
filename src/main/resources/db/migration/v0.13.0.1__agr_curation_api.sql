ALTER TABLE diseaseannotation
	ADD COLUMN uniqueid varchar(2000) UNIQUE NOT NULL,
	ADD COLUMN datecreated timestamp without time zone,
	ADD COLUMN dateupdated timestamp without time zone,
	ADD COLUMN createdby_id bigint,
	ADD COLUMN updatedby_id bigint,
	ADD COLUMN internal boolean DEFAULT false,
	ADD COLUMN obsolete boolean DEFAULT false,
	ADD COLUMN dbdatecreated timestamp without time zone,
	ADD COLUMN dbdateupdated timestamp without time zone;
	
ALTER TABLE diseaseannotation 
	DROP COLUMN diseaseannotationcurie;
	
ALTER TABLE diseaseannotation_aud
	DROP COLUMN diseaseannotationcurie;

ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_createdby_id_fk
		FOREIGN KEY (createdby_id) REFERENCES person (id);
		
	
ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_updatedby_id_fk
		FOREIGN KEY (by_id) REFERENCES person (id);

UPDATE diseaseannotation
	SET datecreated = subquery.datecreated, dateupdated = subquery.dateupdated,
		createdby_id = subquery.createdby_id, updatedby_id = subquery.updatedby_id,
		internal = subquery.internal, obsolete = subquery.obsolete,
		dbdatecreated = subquery.dbdatecreated, dbdateupdated = subquery.dbdateupdated	
	FROM (
		SELECT id, datecreated, dateupdated, createdby_id, updatedby_id, internal,
			obsolete, dbdatecreated, dbdateupdated
			FROM association
		) AS subquery
	WHERE diseaseannotation.id = subquery.id;
	
ALTER TABLE diseaseannotation
	DROP CONSTRAINT fk5a3i0leqdmstsdfpq1j1b15el;
	
DROP TABLE association_aud, association;

CREATE TABLE informationcontententity (
	curie varchar(255) CONSTRAINT informationcontententity_pkey PRIMARY KEY,
	datecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	vocabularytermsetdescription varchar(255),
	name varchar(255) NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	vocabularytermsetvocabulary_id bigint NOT NULL
);

CREATE TABLE informationcontententity_aud (
	curie varchar(255) NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	PRIMARY KEY (rev, curie)
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
	
INSERT INTO informationcontententity_aud (curie, rev, revtype)
	SELECT curie, rev, revtype FROM reference;

ALTER TABLE informationcontententity_aud
	ADD CONSTRAINT informationcontententity_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);
	
ALTER TABLE reference
	ADD CONSTRAINT reference_curie_fk
		FOREIGN KEY (curie) REFERENCES informationcontententity (curie);
		
ALTER TABLE reference_aud
	ADD CONSTRAINT reference_aud_curie_fk
		FOREIGN KEY (curie) REFERENCES informationcontententity_aud (curie);
		
ALTER TABLE reference_aud
	ADD CONSTRAINT reference_aud_rev_fk
		FOREIGN KEY (curie) REFERENCES informationcontententity_aud (rev);
		
ALTER TABLE reference_aud
	DROP COLUMN revtype;