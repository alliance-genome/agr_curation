-- SCRUM-6535: Cassette, the second entity of the new Constructs model (epic SCRUM-6382).
--
-- A Cassette is a segment of a Construct. Like TransgenicTool it is_a Reagent, so under the JOINED
-- strategy its table holds only a key back to reagent; everything LinkML puts on the class is a
-- slot annotation, a join table or an association.
--
-- Cassette components come in two forms that sit side by side: the three association tables below,
-- for components that are curated entities with a curie, and cassettecomponentslotannotation, for
-- components named only by symbol.
--
-- The association tables follow the current convention (see v0.38.0.34), not the v0.27 construct
-- ones: each is standalone with its own audit columns and sequence rather than keyed into a shared
-- association table, and there are no _aud tables.

CREATE TABLE cassette (
	id bigint PRIMARY KEY
);

ALTER TABLE cassette
	ADD CONSTRAINT cassette_id_fk FOREIGN KEY (id) REFERENCES reagent (id);

-- SlotAnnotation is SINGLE_TABLE, so every cassette slot annotation lives in slotannotation and
-- points back at its cassette through this column, exactly as singleconstruct_id does.
ALTER TABLE slotannotation ADD COLUMN singlecassette_id bigint;

ALTER TABLE slotannotation
	ADD CONSTRAINT slotannotation_singlecassette_id_fk
	FOREIGN KEY (singlecassette_id) REFERENCES cassette (id);

CREATE INDEX slotannotation_singlecassette_index ON slotannotation USING btree (singlecassette_id);

CREATE TABLE cassette_reference (
	cassette_id bigint NOT NULL,
	references_id bigint NOT NULL
);

ALTER TABLE cassette_reference
	ADD CONSTRAINT cassette_reference_cassette_id_fk FOREIGN KEY (cassette_id) REFERENCES cassette (id);
ALTER TABLE cassette_reference
	ADD CONSTRAINT cassette_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES informationcontententity (id);
CREATE INDEX cassette_reference_cassette_index ON cassette_reference USING btree (cassette_id);
CREATE INDEX cassette_reference_references_index ON cassette_reference USING btree (references_id);

-- CassetteComponentSlotAnnotation.relatedNotes. Named explicitly: the Hibernate default for a note
-- collection owned by a slot annotation is slotannotation_note, which is already in use.
CREATE TABLE cassettecomponentslotannotation_note (
	slotannotation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

ALTER TABLE cassettecomponentslotannotation_note
	ADD CONSTRAINT ccsa_note_slotannotation_id_fk FOREIGN KEY (slotannotation_id) REFERENCES slotannotation (id);
ALTER TABLE cassettecomponentslotannotation_note
	ADD CONSTRAINT ccsa_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);
CREATE INDEX cassettecomponent_note_ccsa_index ON cassettecomponentslotannotation_note USING btree (slotannotation_id);
CREATE INDEX cassettecomponent_note_relatednotes_index ON cassettecomponentslotannotation_note USING btree (relatednotes_id);

-- CassetteUseSlotAnnotation.uses -> FBcv terms in the 'experimental_tool_descriptor' namespace.
-- Named for ontologyterm, the table FBCVTerm actually lives in, and prefixed with the owning
-- subclass because the Hibernate default (slotannotation_ontologyterm) is taken by
-- AlleleMutationTypeSlotAnnotation.mutationTypes.
CREATE TABLE cassetteuseslotannotation_ontologyterm (
	slotannotation_id bigint NOT NULL,
	uses_id bigint NOT NULL
);

ALTER TABLE cassetteuseslotannotation_ontologyterm
	ADD CONSTRAINT cassetteuse_slotannotation_id_fk FOREIGN KEY (slotannotation_id) REFERENCES slotannotation (id);
ALTER TABLE cassetteuseslotannotation_ontologyterm
	ADD CONSTRAINT cassetteuse_uses_id_fk FOREIGN KEY (uses_id) REFERENCES ontologyterm (id);
CREATE INDEX cassetteuseslotannotation_slotannotation_index ON cassetteuseslotannotation_ontologyterm USING btree (slotannotation_id);
CREATE INDEX cassetteuseslotannotation_uses_index ON cassetteuseslotannotation_ontologyterm USING btree (uses_id);


-- ---------------------------------------------------------------------------------------------
-- CassetteGenomicEntityAssociation
-- ---------------------------------------------------------------------------------------------

CREATE TABLE cassettegenomicentityassociation (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
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

ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegenomicentityassociation_pkey PRIMARY KEY (id);

CREATE INDEX cassettegea_subject_index ON cassettegenomicentityassociation USING btree (cassetteassociationsubject_id);
CREATE INDEX cassettegea_object_index ON cassettegenomicentityassociation USING btree (cassettegenomicentityassociationobject_id);
CREATE INDEX cassettegea_relation_index ON cassettegenomicentityassociation USING btree (relation_id);
CREATE INDEX cassettegea_createdby_index ON cassettegenomicentityassociation USING btree (createdby_id);
CREATE INDEX cassettegea_updatedby_index ON cassettegenomicentityassociation USING btree (updatedby_id);
CREATE INDEX cassettegea_internal_index ON cassettegenomicentityassociation USING btree (internal);
CREATE INDEX cassettegea_obsolete_index ON cassettegenomicentityassociation USING btree (obsolete);

ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegea_subject_id_fk FOREIGN KEY (cassetteassociationsubject_id) REFERENCES cassette (id);
ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegea_object_id_fk FOREIGN KEY (cassettegenomicentityassociationobject_id) REFERENCES genomicentity (id);
ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegea_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);
ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegea_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE cassettegenomicentityassociation ADD CONSTRAINT cassettegea_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);

CREATE TABLE cassettegenomicentityassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE INDEX cassettegea_ice_association_index ON cassettegenomicentityassociation_informationcontententity USING btree (association_id);
CREATE INDEX cassettegea_ice_evidence_index ON cassettegenomicentityassociation_informationcontententity USING btree (evidence_id);

ALTER TABLE cassettegenomicentityassociation_informationcontententity ADD CONSTRAINT cassettegea_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES cassettegenomicentityassociation (id);
ALTER TABLE cassettegenomicentityassociation_informationcontententity ADD CONSTRAINT cassettegea_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity (id);

CREATE TABLE cassettegenomicentityassociation_note (
	cassettegenomicentityassociation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

CREATE INDEX cassettegeassociation_note_cgea_index ON cassettegenomicentityassociation_note USING btree (cassettegenomicentityassociation_id);
CREATE INDEX cassettegeassociation_note_relatednotes_index ON cassettegenomicentityassociation_note USING btree (relatednotes_id);

ALTER TABLE cassettegenomicentityassociation_note ADD CONSTRAINT cassettegea_note_assoc_id_fk FOREIGN KEY (cassettegenomicentityassociation_id) REFERENCES cassettegenomicentityassociation (id);
ALTER TABLE cassettegenomicentityassociation_note ADD CONSTRAINT cassettegea_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);

-- componentTypes: SO terms describing the nature of the component, as used by FlyBase et al.
CREATE TABLE cassettegenomicentityassociation_soterm (
	cassettegenomicentityassociation_id bigint NOT NULL,
	componenttypes_id bigint NOT NULL
);

CREATE INDEX cassettegeassociation_soterm_cgea_index ON cassettegenomicentityassociation_soterm USING btree (cassettegenomicentityassociation_id);
CREATE INDEX cassettegeassociation_soterm_componenttypes_index ON cassettegenomicentityassociation_soterm USING btree (componenttypes_id);

ALTER TABLE cassettegenomicentityassociation_soterm ADD CONSTRAINT cassettegea_soterm_assoc_id_fk FOREIGN KEY (cassettegenomicentityassociation_id) REFERENCES cassettegenomicentityassociation (id);
ALTER TABLE cassettegenomicentityassociation_soterm ADD CONSTRAINT cassettegea_soterm_componenttypes_id_fk FOREIGN KEY (componenttypes_id) REFERENCES ontologyterm (id);


-- ---------------------------------------------------------------------------------------------
-- CassetteTransgenicToolAssociation
-- ---------------------------------------------------------------------------------------------

CREATE TABLE cassettetransgenictoolassociation (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	cassetteassociationsubject_id bigint,
	cassettetransgenictoolassociationobject_id bigint,
	relation_id bigint
);

CREATE SEQUENCE cassettetransgenictoolassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE cassettetransgenictoolassociation ADD CONSTRAINT cassettetransgenictoolassociation_pkey PRIMARY KEY (id);

CREATE INDEX cassettettassoc_subject_index ON cassettetransgenictoolassociation USING btree (cassetteassociationsubject_id);
CREATE INDEX cassettettassoc_object_index ON cassettetransgenictoolassociation USING btree (cassettetransgenictoolassociationobject_id);
CREATE INDEX cassettettassoc_relation_index ON cassettetransgenictoolassociation USING btree (relation_id);
CREATE INDEX cassettettassoc_createdby_index ON cassettetransgenictoolassociation USING btree (createdby_id);
CREATE INDEX cassettettassoc_updatedby_index ON cassettetransgenictoolassociation USING btree (updatedby_id);
CREATE INDEX cassettettassoc_internal_index ON cassettetransgenictoolassociation USING btree (internal);
CREATE INDEX cassettettassoc_obsolete_index ON cassettetransgenictoolassociation USING btree (obsolete);

ALTER TABLE cassettetransgenictoolassociation ADD CONSTRAINT cassettettassoc_subject_id_fk FOREIGN KEY (cassetteassociationsubject_id) REFERENCES cassette (id);
ALTER TABLE cassettetransgenictoolassociation ADD CONSTRAINT cassettettassoc_object_id_fk FOREIGN KEY (cassettetransgenictoolassociationobject_id) REFERENCES transgenictool (id);
ALTER TABLE cassettetransgenictoolassociation ADD CONSTRAINT cassettettassoc_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);
ALTER TABLE cassettetransgenictoolassociation ADD CONSTRAINT cassettettassoc_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE cassettetransgenictoolassociation ADD CONSTRAINT cassettettassoc_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);

CREATE TABLE cassettetransgenictoolassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE INDEX cassettettassoc_ice_association_index ON cassettetransgenictoolassociation_informationcontententity USING btree (association_id);
CREATE INDEX cassettettassoc_ice_evidence_index ON cassettetransgenictoolassociation_informationcontententity USING btree (evidence_id);

ALTER TABLE cassettetransgenictoolassociation_informationcontententity ADD CONSTRAINT cassettettassoc_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES cassettetransgenictoolassociation (id);
ALTER TABLE cassettetransgenictoolassociation_informationcontententity ADD CONSTRAINT cassettettassoc_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity (id);

CREATE TABLE cassettetransgenictoolassociation_note (
	cassettetransgenictoolassociation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

CREATE INDEX cassettettassoc_note_assoc_index ON cassettetransgenictoolassociation_note USING btree (cassettetransgenictoolassociation_id);
CREATE INDEX cassettettassoc_note_relatednotes_index ON cassettetransgenictoolassociation_note USING btree (relatednotes_id);

ALTER TABLE cassettetransgenictoolassociation_note ADD CONSTRAINT cassettettassoc_note_assoc_id_fk FOREIGN KEY (cassettetransgenictoolassociation_id) REFERENCES cassettetransgenictoolassociation (id);
ALTER TABLE cassettetransgenictoolassociation_note ADD CONSTRAINT cassettettassoc_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);


-- ---------------------------------------------------------------------------------------------
-- CassetteStrAssociation
-- ---------------------------------------------------------------------------------------------

CREATE TABLE cassettestrassociation (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	cassetteassociationsubject_id bigint,
	cassettestrassociationobject_id bigint,
	relation_id bigint
);

CREATE SEQUENCE cassettestrassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE cassettestrassociation ADD CONSTRAINT cassettestrassociation_pkey PRIMARY KEY (id);

CREATE INDEX cassettestrassoc_subject_index ON cassettestrassociation USING btree (cassetteassociationsubject_id);
CREATE INDEX cassettestrassoc_object_index ON cassettestrassociation USING btree (cassettestrassociationobject_id);
CREATE INDEX cassettestrassoc_relation_index ON cassettestrassociation USING btree (relation_id);
CREATE INDEX cassettestrassoc_createdby_index ON cassettestrassociation USING btree (createdby_id);
CREATE INDEX cassettestrassoc_updatedby_index ON cassettestrassociation USING btree (updatedby_id);
CREATE INDEX cassettestrassoc_internal_index ON cassettestrassociation USING btree (internal);
CREATE INDEX cassettestrassoc_obsolete_index ON cassettestrassociation USING btree (obsolete);

ALTER TABLE cassettestrassociation ADD CONSTRAINT cassettestrassoc_subject_id_fk FOREIGN KEY (cassetteassociationsubject_id) REFERENCES cassette (id);
ALTER TABLE cassettestrassociation ADD CONSTRAINT cassettestrassoc_object_id_fk FOREIGN KEY (cassettestrassociationobject_id) REFERENCES sequencetargetingreagent (id);
ALTER TABLE cassettestrassociation ADD CONSTRAINT cassettestrassoc_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);
ALTER TABLE cassettestrassociation ADD CONSTRAINT cassettestrassoc_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE cassettestrassociation ADD CONSTRAINT cassettestrassoc_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);

CREATE TABLE cassettestrassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE INDEX cassettestrassoc_ice_association_index ON cassettestrassociation_informationcontententity USING btree (association_id);
CREATE INDEX cassettestrassoc_ice_evidence_index ON cassettestrassociation_informationcontententity USING btree (evidence_id);

ALTER TABLE cassettestrassociation_informationcontententity ADD CONSTRAINT cassettestrassoc_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES cassettestrassociation (id);
ALTER TABLE cassettestrassociation_informationcontententity ADD CONSTRAINT cassettestrassoc_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity (id);

CREATE TABLE cassettestrassociation_note (
	cassettestrassociation_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

CREATE INDEX cassettestrassoc_note_assoc_index ON cassettestrassociation_note USING btree (cassettestrassociation_id);
CREATE INDEX cassettestrassoc_note_relatednotes_index ON cassettestrassociation_note USING btree (relatednotes_id);

ALTER TABLE cassettestrassociation_note ADD CONSTRAINT cassettestrassoc_note_assoc_id_fk FOREIGN KEY (cassettestrassociation_id) REFERENCES cassettestrassociation (id);
ALTER TABLE cassettestrassociation_note ADD CONSTRAINT cassettestrassoc_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);
