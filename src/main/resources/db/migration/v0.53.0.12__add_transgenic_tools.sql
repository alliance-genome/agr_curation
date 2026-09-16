-- SCRUM-6535: TransgenicTool, the first entity of the new Constructs model (epic SCRUM-6382).
--
-- A TransgenicTool is_a Reagent in LinkML, so under the JOINED inheritance strategy its table holds
-- only its own fields and keys back to reagent, the same arrangement construct and antibody use.
-- It declares no scalar columns of its own: symbol, full name, synonyms and uses are all slot
-- annotations, and references and cross references are join tables.
--
-- Version note: v0.53.0.11 is taken by SCRUM-6561 on its own branch, so this starts at .12 to avoid
-- the duplicate-version startup failure Flyway raises when two migrations share a version.

CREATE TABLE transgenictool (
	id bigint PRIMARY KEY
);

ALTER TABLE transgenictool
	ADD CONSTRAINT transgenictool_id_fk FOREIGN KEY (id) REFERENCES reagent (id);

-- Slot annotations point back at their tool from the shared slotannotation table, as every other
-- slot annotation subtype does.
ALTER TABLE slotannotation ADD COLUMN singletransgenictool_id bigint;

ALTER TABLE slotannotation
	ADD CONSTRAINT slotannotation_singletransgenictool_id_fk
	FOREIGN KEY (singletransgenictool_id) REFERENCES transgenictool (id);

CREATE INDEX slotannotation_singletransgenictool_index
	ON slotannotation USING btree (singletransgenictool_id);

CREATE TABLE transgenictool_reference (
	transgenictool_id bigint NOT NULL,
	references_id bigint NOT NULL
);

ALTER TABLE transgenictool_reference
	ADD CONSTRAINT transgenictool_reference_transgenictool_id_fk FOREIGN KEY (transgenictool_id) REFERENCES transgenictool (id);
ALTER TABLE transgenictool_reference
	ADD CONSTRAINT transgenictool_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES informationcontententity (id);
CREATE INDEX transgenictool_reference_transgenictool_index ON transgenictool_reference USING btree (transgenictool_id);
CREATE INDEX transgenictool_reference_references_index ON transgenictool_reference USING btree (references_id);

CREATE TABLE transgenictool_crossreference (
	transgenictool_id bigint NOT NULL,
	crossreferences_id bigint NOT NULL
);

ALTER TABLE transgenictool_crossreference
	ADD CONSTRAINT transgenictool_crossreference_transgenictool_id_fk FOREIGN KEY (transgenictool_id) REFERENCES transgenictool (id);
ALTER TABLE transgenictool_crossreference
	ADD CONSTRAINT transgenictool_crossreference_crossreferences_id_fk FOREIGN KEY (crossreferences_id) REFERENCES crossreference (id);
CREATE INDEX transgenictool_crossreference_transgenictool_index ON transgenictool_crossreference USING btree (transgenictool_id);
CREATE INDEX transgenictool_crossreference_crossreferences_index ON transgenictool_crossreference USING btree (crossreferences_id);

-- TransgenicToolUseSlotAnnotation.uses -> FBcv terms in the 'experimental_tool_descriptor' namespace.
-- Named for ontologyterm, the table FBCVTerm actually lives in, and prefixed with the owning
-- subclass because the Hibernate default (slotannotation_ontologyterm) is taken by
-- AlleleMutationTypeSlotAnnotation.mutationTypes.
CREATE TABLE transgenictooluseslotannotation_ontologyterm (
	slotannotation_id bigint NOT NULL,
	uses_id bigint NOT NULL
);

ALTER TABLE transgenictooluseslotannotation_ontologyterm
	ADD CONSTRAINT ttuseslotannotation_slotannotation_id_fk FOREIGN KEY (slotannotation_id) REFERENCES slotannotation (id);
ALTER TABLE transgenictooluseslotannotation_ontologyterm
	ADD CONSTRAINT ttuseslotannotation_uses_id_fk FOREIGN KEY (uses_id) REFERENCES ontologyterm (id);
CREATE INDEX ttuseslotannotation_slotannotation_index ON transgenictooluseslotannotation_ontologyterm USING btree (slotannotation_id);
CREATE INDEX ttuseslotannotation_uses_index ON transgenictooluseslotannotation_ontologyterm USING btree (uses_id);
