CREATE TABLE alleledatabasestatusslotannotation (
	id bigint PRIMARY KEY,
	singleallele_curie varchar(255),
	databasestatus_id bigint
);

ALTER TABLE alleledatabasestatusslotannotation ADD CONSTRAINT alleledatabasestatus_singleallele_curie_fk FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);
ALTER TABLE alleledatabasestatusslotannotation ADD CONSTRAINT alleledatabasestatus_databasestatus_id_fk FOREIGN KEY (databasestatus_id) REFERENCES vocabularyterm (id);

CREATE INDEX alleledatabasestatus_singleallele_curie_index ON alleledatabasestatusslotannotation USING btree (singleallele_curie);
CREATE INDEX alleledatabasestatus_databasestatus_id_index ON alleledatabasestatusslotannotation USING btree (databasestatus_id);

CREATE TABLE alleledatabasestatusslotannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
	singleallele_curie varchar(255),
	databasestatus_id bigint
);

ALTER TABLE alleledatabasestatusslotannotation_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE alleledatabasestatusslotannotation_aud ADD CONSTRAINT alleledatabasestatus_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES slotannotation_aud (id, rev);

INSERT INTO vocabulary (id, name) VALUES (nextval('hibernate_sequence'), 'Allele database status vocabulary');
	
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'approved', id FROM vocabulary WHERE name = 'Allele database status vocabulary';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'deleted', id FROM vocabulary WHERE name = 'Allele database status vocabulary';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'reserved', id FROM vocabulary WHERE name = 'Allele database status vocabulary';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'autoloaded', id FROM vocabulary WHERE name = 'Allele database status vocabulary';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'in_progress', id FROM vocabulary WHERE name = 'Allele database status vocabulary';
