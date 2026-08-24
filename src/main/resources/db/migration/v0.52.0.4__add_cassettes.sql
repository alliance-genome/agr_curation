CREATE TABLE cassette (
	id bigint PRIMARY KEY
);

ALTER TABLE cassette ADD CONSTRAINT cassette_id_fk FOREIGN KEY (id) REFERENCES reagent (id);

ALTER TABLE slotannotation ADD COLUMN singlecassette_id bigint;
ALTER TABLE slotannotation ADD CONSTRAINT slotannotation_singlecassette_id_fk FOREIGN KEY (singlecassette_id) REFERENCES cassette (id);
CREATE INDEX slotannotation_singlecassette_index ON slotannotation USING btree (singlecassette_id);

CREATE TABLE cassette_reference (
	cassette_id bigint NOT NULL,
	references_id bigint NOT NULL
);

ALTER TABLE cassette_reference ADD CONSTRAINT cassette_reference_cassette_id_fk FOREIGN KEY (cassette_id) REFERENCES cassette (id);
ALTER TABLE cassette_reference ADD CONSTRAINT cassette_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES reference (id);

CREATE INDEX cassette_reference_cassette_index ON cassette_reference USING btree (cassette_id);
CREATE INDEX cassette_reference_references_index ON cassette_reference USING btree (references_id);

CREATE TABLE cassetteassociation (
	id bigint PRIMARY KEY,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean NOT NULL DEFAULT false,
	obsolete boolean NOT NULL DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint,
	cassetteassociationsubject_id bigint,
	cassetteassociationobject_id bigint,
	relation_id bigint
);

CREATE SEQUENCE cassetteassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE cassetteassociation ADD CONSTRAINT cassetteassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE cassetteassociation ADD CONSTRAINT cassetteassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);
ALTER TABLE cassetteassociation ADD CONSTRAINT cassetteassociation_subject_id_fk FOREIGN KEY (cassetteassociationsubject_id) REFERENCES cassette (id);
ALTER TABLE cassetteassociation ADD CONSTRAINT cassetteassociation_object_id_fk FOREIGN KEY (cassetteassociationobject_id) REFERENCES genomicentity (id);
ALTER TABLE cassetteassociation ADD CONSTRAINT cassetteassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);

CREATE INDEX cassetteassociation_internal_index ON cassetteassociation USING btree (internal);
CREATE INDEX cassetteassociation_obsolete_index ON cassetteassociation USING btree (obsolete);
CREATE INDEX cassetteassociation_createdby_index ON cassetteassociation USING btree (createdby_id);
CREATE INDEX cassetteassociation_updatedby_index ON cassetteassociation USING btree (updatedby_id);
CREATE INDEX cassetteassociation_subject_index ON cassetteassociation USING btree (cassetteassociationsubject_id);
CREATE INDEX cassetteassociation_object_index ON cassetteassociation USING btree (cassetteassociationobject_id);
CREATE INDEX cassetteassociation_relation_index ON cassetteassociation USING btree (relation_id);

CREATE TABLE cassetteassociation_note (
	cassetteassociation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL UNIQUE
);

ALTER TABLE cassetteassociation_note ADD CONSTRAINT cassetteassociation_note_ca_id_fk FOREIGN KEY (cassetteassociation_id) REFERENCES cassetteassociation (id);
ALTER TABLE cassetteassociation_note ADD CONSTRAINT cassetteassociation_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);

CREATE INDEX cassetteassociation_note_ca_index ON cassetteassociation_note USING btree (cassetteassociation_id);
CREATE INDEX cassetteassociation_note_relatednotes_index ON cassetteassociation_note USING btree (relatednotes_id);

CREATE TABLE cassetteassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

ALTER TABLE cassetteassociation_informationcontententity ADD CONSTRAINT cassetteassociation_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES cassetteassociation (id);
ALTER TABLE cassetteassociation_informationcontententity ADD CONSTRAINT cassetteassociation_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity (id);

CREATE INDEX cassetteassociation_ice_association_index ON cassetteassociation_informationcontententity USING btree (association_id);
CREATE INDEX cassetteassociation_ice_evidence_index ON cassetteassociation_informationcontententity USING btree (evidence_id);

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Cassette Note Type', 'cassette_note_type');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'comment', id FROM vocabulary WHERE vocabularylabel = 'cassette_note_type';

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Cassette Component Note Type', 'cassette_component_note_type');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'comment', id FROM vocabulary WHERE vocabularylabel = 'cassette_component_note_type';

-- No pre-existing "cassette_relation" vocabulary exists to seed real relation terms from
-- (unlike construct_genomic_entity_relation, which copies from the long-standing construct_relation
-- vocabulary). This creates an empty, self-contained vocabulary/term set pair for curators to
-- populate with real relation terms once the Cassette LinkML model defines them.
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Cassette Genomic Entity Relation', 'cassette_genomic_entity_relation');
INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Cassette Genomic Entity Relation', 'cassette_genomic_entity_relation', id, 'Relations applicable between a cassette and its genomic entity components' FROM vocabulary WHERE vocabularylabel = 'cassette_genomic_entity_relation';
