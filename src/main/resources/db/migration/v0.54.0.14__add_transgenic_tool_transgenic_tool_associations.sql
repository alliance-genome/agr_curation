-- SCRUM-6543: associations between two compatible transgenic tools (TransgenicToolTransgenicToolAssociation).
-- The LinkML DTO carries no evidence or notes, so unlike the cassette associations there are no
-- evidence or note join tables. Relations come from transgenic_tool_association_relation, seeded in
-- v0.54.0.8. Numbered after v0.54.0.13 (reagent placeholder, a separate PR); flyway runs out of order.

CREATE TABLE transgenictooltransgenictoolassociation (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	transgenictoolassociationsubject_id bigint,
	transgenictooltransgenictoolassociationobject_id bigint,
	relation_id bigint
);

CREATE SEQUENCE transgenictooltransgenictoolassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE transgenictooltransgenictoolassociation ADD CONSTRAINT transgenictooltransgenictoolassociation_pkey PRIMARY KEY (id);

CREATE INDEX tttoolassoc_subject_index ON transgenictooltransgenictoolassociation USING btree (transgenictoolassociationsubject_id);
CREATE INDEX tttoolassoc_object_index ON transgenictooltransgenictoolassociation USING btree (transgenictooltransgenictoolassociationobject_id);
CREATE INDEX tttoolassoc_relation_index ON transgenictooltransgenictoolassociation USING btree (relation_id);
CREATE INDEX tttoolassoc_createdby_index ON transgenictooltransgenictoolassociation USING btree (createdby_id);
CREATE INDEX tttoolassoc_updatedby_index ON transgenictooltransgenictoolassociation USING btree (updatedby_id);
CREATE INDEX tttoolassoc_internal_index ON transgenictooltransgenictoolassociation USING btree (internal);
CREATE INDEX tttoolassoc_obsolete_index ON transgenictooltransgenictoolassociation USING btree (obsolete);

ALTER TABLE transgenictooltransgenictoolassociation ADD CONSTRAINT tttoolassoc_subject_id_fk FOREIGN KEY (transgenictoolassociationsubject_id) REFERENCES transgenictool (id);
ALTER TABLE transgenictooltransgenictoolassociation ADD CONSTRAINT tttoolassoc_object_id_fk FOREIGN KEY (transgenictooltransgenictoolassociationobject_id) REFERENCES transgenictool (id);
ALTER TABLE transgenictooltransgenictoolassociation ADD CONSTRAINT tttoolassoc_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);
ALTER TABLE transgenictooltransgenictoolassociation ADD CONSTRAINT tttoolassoc_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE transgenictooltransgenictoolassociation ADD CONSTRAINT tttoolassoc_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);
