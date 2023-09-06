-- Create singlereferenceassociation and evidenceassociation tables

CREATE TABLE singlereferenceassociation (
	id bigint PRIMARY KEY,
	singlereference_curie varchar(255)
	);
	
CREATE TABLE singlereferenceassociation_aud (
	id bigint NOT NULL,
	rev integer NOT NuLL,
	singlereference_curie varchar(255),
	PRIMARY KEY (id, rev)
	);

CREATE TABLE evidenceeassociation (
	id bigint PRIMARY KEY,
	singlereference_curie varchar(255)
	);
	
CREATE TABLE evidenceeassociation_aud (
	id bigint NOT NULL,
	rev integer NOT NuLL,
	PRIMARY KEY (id, rev)
	);

-- Change association_informationcontententity table to evidenceassociation_informationcontententity

ALTER TABLE association_informationcontententity DROP CONSTRAINT association_informationcontententity_association_id_fk;
ALTER TABLE association_informationcontententity DROP CONSTRAINT association_informationcontententity_evidence_curie_fk;
ALTER TABLE association_informationcontententity RENAME TO evidenceassociation_informationcontententity;
ALTER TABLE evidenceassociation_informationcontententity ADD CONSTRAINT evidenceassociation_informationcontententity_evidenceassociation_id_fk FOREIGN KEY (evidenceassociation_id) REFERENCES evidenceassociation (id);
ALTER TABLE evidenceassociation_informationcontententity ADD CONSTRAINT evidenceassociation_informationcontententity_evidence_curie_fk FOREIGN KEY (evidence_curie) REFERENCES informationcontententity (curie);
CREATE INDEX evidenceassociation_informationcontententity_evidenceassociation_id_index ON evidenceassociation_informationcontententity  USING btree (evidenceassociation_id);
CREATE INDEX evidenceassociation_informationcontententity_evidence_curie_index ON evidenceassociation_informationcontententity  USING btree (evidence_curie);

ALTER TABLE association_informationcontententity_aud RENAME CONSTRAINT association_informationcontententity_aud_pkey TO evidenceassociation_informationcontententity_aud_pkey;
ALTER TABLE association_informationcontententity_aud RENAME CONSTRAINT association_informationcontententity_aud_rev_fk TO evidenceassociation_informationcontententity_aud_rev_fk;
ALTER TABLE association_informationcontententity_aud RENAME TO evidenceassociation_informationcontententity_aud;

-- Copy data from annotation to singlereferenceassociation and association tables

INSERT INTO singlereferenceassociation (id, singlereference_curie) SELECT id, singlereference_curie FROM annotation;
INSERT INTO association (id, date_created, dateupdated, internal, obsolete, createdby_id, updatedby_id, dbdatecreated, dbdateupdated) SELECT id, date_created, dateupdated, internal, obsolete, createdby_id, updatedby_id, dbdatecreated, dbdateupdated FROM annotation;

INSERT INTO singlereferenceassociation_aud (id, rev, singlereference_curie) SELECT id, rev, singlreference_curie FROM annotation_aud;
INSERT INTO association_aud (id, rev, revtype) SELECT (id, rev, revtype) FROM annotation_aud;

-- Remove columns from annotation tables

ALTER TABLE annotation
	DROP COLUMN singlereference_curie,,
	DROP COLUMN date_created,
	DROP COLUMN date_updated,
	DROP COLUMN internal,
	DROP COLUMN obsolete,
	DROP COLUMN createdby_id,
	DROP COLUMN updatedby_id,
	DROP COLUMN dbdatecreated,
	DROP COLUMN dbdateupdated;
	
ALTER TABLE annotation_aud
	DROP COLUMN singlereference_curie,
	DROP COLUMN revtype;
ALTER TABLE annotation_aud DROP CONSTRAINT annotation_aud_rev_fk;
ALTER TABLE annotation_aud ADD CONSTRAINT annotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES singlereferenceassociation_aud (id, rev);

-- Add missing constraints and indexes

ALTER TABLE singlereferenceassociation ADD CONSTRAINT singlereferenceassociation_id_fk FOREIGN KEY (id) REFERENCES association (id);
ALTER TABLE singlereferenceassociation ADD CONSTRAINT singlereferenceassociation_singlereference_curie_fk FOREIGN KEY (singlereference_curie) REFERENCES reference (curie);
ALTER TABLE evidenceassociation ADD CONSTRAINT evidenceassociation_id_fk FOREIGN KEY (id) REFERENCES association (id);
ALTER TABLE annotation ADD CONSTRAINT annotation_id_fk FOREIGN KEY (id) REFERENCES singlereferenceassociation (id);

ALTER TABLE singlereferenceassociation_aud ADD CONSTRAINT singlereferenceassociation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES association_aud (id, rev);
ALTER TABLE evidenceassociation_aud ADD CONSTRAINT evidenceassociation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES association_aud (id, rev);

CREATE INDEX singlereferenceassociation_singlereference_curie_index ON singlereferenceassociation USING btree (singlereference_curie);