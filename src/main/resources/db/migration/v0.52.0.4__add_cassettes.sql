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

CREATE TABLE cassettegenomicentityassociation (
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
	cassettegenomicentityassociationobject_id bigint,
	relation_id bigint
);

CREATE SEQUENCE cassettegenomicentityassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegenomicentityassociation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegenomicentityassociation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);
ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegenomicentityassociation_subject_id_fk FOREIGN KEY (cassetteassociationsubject_id) REFERENCES cassette (id);
ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegenomicentityassociation_object_id_fk FOREIGN KEY (cassettegenomicentityassociationobject_id) REFERENCES genomicentity (id);
ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegenomicentityassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);

CREATE INDEX cassettegenomicentityassociation_internal_index ON cassettegenomicentityassociation USING btree (internal);
CREATE INDEX cassettegenomicentityassociation_obsolete_index ON cassettegenomicentityassociation USING btree (obsolete);
CREATE INDEX cassettegenomicentityassociation_createdby_index ON cassettegenomicentityassociation USING btree (createdby_id);
CREATE INDEX cassettegenomicentityassociation_updatedby_index ON cassettegenomicentityassociation USING btree (updatedby_id);
CREATE INDEX cassettegenomicentityassociation_subject_index ON cassettegenomicentityassociation USING btree (cassetteassociationsubject_id);
CREATE INDEX cassettegenomicentityassociation_object_index ON cassettegenomicentityassociation USING btree (cassettegenomicentityassociationobject_id);
CREATE INDEX cassettegenomicentityassociation_relation_index ON cassettegenomicentityassociation USING btree (relation_id);

CREATE TABLE cassettegenomicentityassociation_note (
	cassettegenomicentityassociation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL UNIQUE
);

ALTER TABLE cassettegenomicentityassociation_note ADD CONSTRAINT cgeassociation_note_cgea_id_fk FOREIGN KEY (cassettegenomicentityassociation_id) REFERENCES cassettegenomicentityassociation (id);
ALTER TABLE cassettegenomicentityassociation_note ADD CONSTRAINT cgeassociation_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);

CREATE INDEX cgeassociation_note_cgea_index ON cassettegenomicentityassociation_note USING btree (cassettegenomicentityassociation_id);
CREATE INDEX cgeassociation_note_relatednotes_index ON cassettegenomicentityassociation_note USING btree (relatednotes_id);

CREATE TABLE cassettegenomicentityassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

ALTER TABLE cassettegenomicentityassociation_informationcontententity ADD CONSTRAINT cgeassociation_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES cassettegenomicentityassociation (id);
ALTER TABLE cassettegenomicentityassociation_informationcontententity ADD CONSTRAINT cgeassociation_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity (id);

CREATE INDEX cgeassociation_ice_association_index ON cassettegenomicentityassociation_informationcontententity USING btree (association_id);
CREATE INDEX cgeassociation_ice_evidence_index ON cassettegenomicentityassociation_informationcontententity USING btree (evidence_id);

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
