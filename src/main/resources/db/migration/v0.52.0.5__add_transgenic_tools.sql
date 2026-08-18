CREATE TABLE transgenictool (
	id bigint PRIMARY KEY
);

ALTER TABLE transgenictool ADD CONSTRAINT transgenictool_id_fk FOREIGN KEY (id) REFERENCES reagent (id);

ALTER TABLE slotannotation ADD COLUMN singletransgenictool_id bigint;
ALTER TABLE slotannotation ADD CONSTRAINT slotannotation_singletransgenictool_id_fk FOREIGN KEY (singletransgenictool_id) REFERENCES transgenictool (id);
CREATE INDEX slotannotation_singletransgenictool_index ON slotannotation USING btree (singletransgenictool_id);

CREATE TABLE transgenictool_reference (
	transgenictool_id bigint NOT NULL,
	references_id bigint NOT NULL
);

ALTER TABLE transgenictool_reference ADD CONSTRAINT transgenictool_reference_transgenictool_id_fk FOREIGN KEY (transgenictool_id) REFERENCES transgenictool (id);
ALTER TABLE transgenictool_reference ADD CONSTRAINT transgenictool_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES reference (id);

CREATE INDEX transgenictool_reference_transgenictool_index ON transgenictool_reference USING btree (transgenictool_id);
CREATE INDEX transgenictool_reference_references_index ON transgenictool_reference USING btree (references_id);

CREATE TABLE transgenictool_crossreference (
	transgenictool_id bigint NOT NULL,
	crossreferences_id bigint NOT NULL
);

ALTER TABLE transgenictool_crossreference ADD CONSTRAINT transgenictool_crossreference_transgenictool_id_fk FOREIGN KEY (transgenictool_id) REFERENCES transgenictool (id);
ALTER TABLE transgenictool_crossreference ADD CONSTRAINT transgenictool_crossreference_crossreferences_id_fk FOREIGN KEY (crossreferences_id) REFERENCES crossreference (id);

CREATE INDEX transgenictool_crossreference_transgenictool_index ON transgenictool_crossreference USING btree (transgenictool_id);
CREATE INDEX transgenictool_crossreference_crossreferences_index ON transgenictool_crossreference USING btree (crossreferences_id);

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Transgenic Tool Note Type', 'transgenic_tool_note_type');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'comment', id FROM vocabulary WHERE vocabularylabel = 'transgenic_tool_note_type';

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Transgenic Tool Use Note Type', 'transgenic_tool_use_note_type');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'comment', id FROM vocabulary WHERE vocabularylabel = 'transgenic_tool_use_note_type';

-- No pre-existing "transgenic_tool_relation" vocabulary exists to seed real relation terms from.
-- This creates an empty, self-contained vocabulary/term set pair for curators to populate with
-- real relation terms once the Transgenic Tool LinkML model defines them.
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Transgenic Tool Use Relation', 'transgenic_tool_use_relation');
INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Transgenic Tool Use Relation', 'transgenic_tool_use_relation', id, 'Relations applicable between a transgenic tool and its uses' FROM vocabulary WHERE vocabularylabel = 'transgenic_tool_use_relation';
