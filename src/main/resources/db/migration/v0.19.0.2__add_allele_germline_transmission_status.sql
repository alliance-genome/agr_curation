CREATE TABLE allelegermlinetransmissionstatusslotannotation (
	id bigint PRIMARY KEY,
	singleallele_curie varchar(255),
	germlinetransmissionstatus_id bigint
);

ALTER TABLE allelegermlinetransmissionstatusslotannotation ADD CONSTRAINT allelegermlinetransmissionstatus_singleallele_curie_fk FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);
ALTER TABLE allelegermlinetransmissionstatusslotannotation ADD CONSTRAINT allelegermlinetransmissionstatus_status_id_fk FOREIGN KEY (germlinetransmissionstatus_id) REFERENCES vocabularyterm (id);

CREATE INDEX allelegermlinetransmissionstatus_singleallele_curie_index ON allelegermlinetransmissionstatusslotannotation USING btree (singleallele_curie);
CREATE INDEX allelegermlinetransmissionstatus_status_id_index ON allelegermlinetransmissionstatusslotannotation USING btree (germlinetransmissionstatus_id);

CREATE TABLE allelegermlinetransmissionstatusslotannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
	singleallele_curie varchar(255),
	germlinetransmissionstatus_id bigint
);

ALTER TABLE allelegermlinetransmissionstatusslotannotation_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE allelegermlinetransmissionstatusslotannotation_aud ADD CONSTRAINT allelegermlinetransmissionstatusslotannotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES slotannotation_aud (id, rev);

INSERT INTO vocabulary (id, name) VALUES (nextval('hibernate_sequence'), 'Allele Germline Transmission Status');
	
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'cell_line', id, 'Allele was created in a stem cell line and no report of germ line transmission has been annotated' FROM vocabulary WHERE name = 'Allele Germline Transmission Status';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'chimeric', id, 'Allele was created in a stem cell line and a report of chimeric but not germ line transmission has been annotated' FROM vocabulary WHERE name = 'Allele Germline Transmission Status';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'germline', id, 'Allele was created in a stem cell line and a report of transmission through the germline has been annotated' FROM vocabulary WHERE name = 'Allele Germline Transmission Status';
