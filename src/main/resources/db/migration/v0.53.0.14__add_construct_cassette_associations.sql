-- SCRUM-6535: ConstructCassetteAssociation, joining a Construct to the Cassettes that are part of
-- it (epic SCRUM-6382). This is what makes the cassettes added in v0.53.0.13 reachable from a
-- construct.
--
-- Follows the current association convention, as in v0.38.0.34: a standalone table with its own
-- audit columns and sequence rather than a key into a shared association table, and no _aud table.
-- The v0.27 construct association migrations no longer describe the live schema.

CREATE TABLE constructcassetteassociation (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	constructassociationsubject_id bigint,
	constructcassetteassociationobject_id bigint,
	relation_id bigint
);

CREATE SEQUENCE constructcassetteassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE constructcassetteassociation ADD CONSTRAINT constructcassetteassociation_pkey PRIMARY KEY (id);

CREATE INDEX constructcassetteassoc_subject_index ON constructcassetteassociation USING btree (constructassociationsubject_id);
CREATE INDEX constructcassetteassoc_object_index ON constructcassetteassociation USING btree (constructcassetteassociationobject_id);
CREATE INDEX constructcassetteassoc_relation_index ON constructcassetteassociation USING btree (relation_id);
CREATE INDEX constructcassetteassoc_createdby_index ON constructcassetteassociation USING btree (createdby_id);
CREATE INDEX constructcassetteassoc_updatedby_index ON constructcassetteassociation USING btree (updatedby_id);
CREATE INDEX constructcassetteassoc_internal_index ON constructcassetteassociation USING btree (internal);
CREATE INDEX constructcassetteassoc_obsolete_index ON constructcassetteassociation USING btree (obsolete);

ALTER TABLE constructcassetteassociation ADD CONSTRAINT constructcassetteassoc_subject_id_fk FOREIGN KEY (constructassociationsubject_id) REFERENCES construct (id);
ALTER TABLE constructcassetteassociation ADD CONSTRAINT constructcassetteassoc_object_id_fk FOREIGN KEY (constructcassetteassociationobject_id) REFERENCES cassette (id);
ALTER TABLE constructcassetteassociation ADD CONSTRAINT constructcassetteassoc_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);
ALTER TABLE constructcassetteassociation ADD CONSTRAINT constructcassetteassoc_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE constructcassetteassociation ADD CONSTRAINT constructcassetteassoc_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);

CREATE TABLE constructcassetteassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE INDEX constructcassetteassoc_ice_association_index ON constructcassetteassociation_informationcontententity USING btree (association_id);
CREATE INDEX constructcassetteassoc_ice_evidence_index ON constructcassetteassociation_informationcontententity USING btree (evidence_id);

ALTER TABLE constructcassetteassociation_informationcontententity ADD CONSTRAINT constructcassetteassoc_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES constructcassetteassociation (id);
ALTER TABLE constructcassetteassociation_informationcontententity ADD CONSTRAINT constructcassetteassoc_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity (id);

CREATE TABLE constructcassetteassociation_note (
	constructcassetteassociation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

CREATE INDEX constructcassetteassoc_note_cca_index ON constructcassetteassociation_note USING btree (constructcassetteassociation_id);
CREATE INDEX constructcassetteassoc_note_relatednotes_index ON constructcassetteassociation_note USING btree (relatednotes_id);

ALTER TABLE constructcassetteassociation_note ADD CONSTRAINT constructcassetteassoc_note_assoc_id_fk FOREIGN KEY (constructcassetteassociation_id) REFERENCES constructcassetteassociation (id);
ALTER TABLE constructcassetteassociation_note ADD CONSTRAINT constructcassetteassoc_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);
