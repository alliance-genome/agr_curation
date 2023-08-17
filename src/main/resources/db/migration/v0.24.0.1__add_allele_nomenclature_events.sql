CREATE TABLE allelenomenclatureeventslotannotation (
	id bigint PRIMARY KEY,
	singleallele_curie varchar(255),
	nomenclatureevent_id bigint
);

ALTER TABLE allelenomenclatureeventslotannotation ADD CONSTRAINT allelenomenclatureeventslotannotation_id_fk FOREIGN KEY (id) REFERENCES slotannotation (id);
ALTER TABLE allelenomenclatureeventslotannotation ADD CONSTRAINT allelenomenclatureeventslotannotation_singleallele_curie_fk FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);
ALTER TABLE allelenomenclatureeventslotannotation ADD CONSTRAINT allelenomenclatureeventslotannotation_nomenclatureevent_id_fk FOREIGN KEY (nomenclatureevent_id) REFERENCES vocabularyterm (id);

CREATE INDEX allelenomenclatureevent_singleallele_curie_index ON allelenomenclatureeventslotannotation USING btree (singleallele_curie);
CREATE INDEX allelenomenclatureevent_nomenclatureevent_id_index ON allelenomenclatureeventslotannotation USING btree (nomenclatureevent_id);

CREATE TABLE allelenomenclatureeventslotannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
	singleallele_curie varchar(255),
	nomenclatureevent_id bigint
);

ALTER TABLE allelenomenclatureeventslotannotation_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE allelenomenclatureeventslotannotation_aud ADD CONSTRAINT allelenomenclatureeventslotannotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES slotannotation_aud (id, rev);
	
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('hibernate_sequence'), 'Allele Nomenclature Event', 'allele_nomenclature_event');

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'data_merged', id FROM vocabulary WHERE vocabularylabel = 'allele_nomenclature_event';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'gene_nomenclature_extended_to_allele', id FROM vocabulary WHERE vocabularylabel = 'allele_nomenclature_event';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'symbol_updated', id FROM vocabulary WHERE vocabularylabel = 'allele_nomenclature_event';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'name_updated', id FROM vocabulary WHERE vocabularylabel = 'allele_nomenclature_event';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'symbol_and_name_updated', id FROM vocabulary WHERE vocabularylabel = 'allele_nomenclature_event';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'symbol_and_name_updated_at_request_of_researcher', id FROM vocabulary WHERE vocabularylabel = 'allele_nomenclature_event';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'symbol_and_name_status_set_to_approved', id FROM vocabulary WHERE vocabularylabel = 'allele_nomenclature_event';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'symbol_and_name_status_set_to_provisional', id FROM vocabulary WHERE vocabularylabel = 'allele_nomenclature_event';