CREATE TABLE antibody (
	id bigint NOT NULL,
	name text,
	clonality_id bigint,
	heavychainisotype_id bigint,
	lightchainisotype_id bigint,
	antigentaxon_id bigint,
	taxon_id bigint,
	originalreference_id bigint
);

ALTER TABLE antibody ADD CONSTRAINT antibody_pkey PRIMARY KEY (id);
ALTER TABLE antibody ADD CONSTRAINT antibody_id_fk FOREIGN KEY (id) REFERENCES reagent (id);
ALTER TABLE antibody ADD CONSTRAINT antibody_clonality_id_fk FOREIGN KEY (clonality_id) REFERENCES vocabularyterm (id);
ALTER TABLE antibody ADD CONSTRAINT antibody_heavychainisotype_id_fk FOREIGN KEY (heavychainisotype_id) REFERENCES vocabularyterm (id);
ALTER TABLE antibody ADD CONSTRAINT antibody_lightchainisotype_id_fk FOREIGN KEY (lightchainisotype_id) REFERENCES vocabularyterm (id);
ALTER TABLE antibody ADD CONSTRAINT antibody_antigentaxon_id_fk FOREIGN KEY (antigentaxon_id) REFERENCES ontologyterm (id);
ALTER TABLE antibody ADD CONSTRAINT antibody_taxon_id_fk FOREIGN KEY (taxon_id) REFERENCES ontologyterm (id);
ALTER TABLE antibody ADD CONSTRAINT antibody_originalreference_id_fk FOREIGN KEY (originalreference_id) REFERENCES reference (id);

CREATE INDEX antibody_clonality_index ON antibody USING btree (clonality_id);
CREATE INDEX antibody_heavychainisotype_index ON antibody USING btree (heavychainisotype_id);
CREATE INDEX antibody_lightchainisotype_index ON antibody USING btree (lightchainisotype_id);
CREATE INDEX antibody_antigentaxon_index ON antibody USING btree (antigentaxon_id);
CREATE INDEX antibody_taxon_index ON antibody USING btree (taxon_id);
CREATE INDEX antibody_originalreference_index ON antibody USING btree (originalreference_id);

CREATE TABLE antibody_gene (
	antibody_id bigint NOT NULL,
	antibodytargetgenes_id bigint NOT NULL
);

ALTER TABLE antibody_gene ADD CONSTRAINT antibody_gene_antibody_id_fk FOREIGN KEY (antibody_id) REFERENCES antibody (id);
ALTER TABLE antibody_gene ADD CONSTRAINT antibody_gene_gene_id_fk FOREIGN KEY (antibodytargetgenes_id) REFERENCES gene (id);

CREATE INDEX antibody_gene_antibody_index ON antibody_gene USING btree (antibody_id);
CREATE INDEX antibody_gene_gene_index ON antibody_gene USING btree (antibodytargetgenes_id);

CREATE TABLE antibody_reference (
	antibody_id bigint NOT NULL,
	references_id bigint NOT NULL
);

ALTER TABLE antibody_reference ADD CONSTRAINT antibody_reference_antibody_id_fk FOREIGN KEY (antibody_id) REFERENCES antibody (id);
ALTER TABLE antibody_reference ADD CONSTRAINT antibody_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES reference (id);

CREATE INDEX antibody_reference_antibody_index ON antibody_reference USING btree (antibody_id);
CREATE INDEX antibody_reference_references_index ON antibody_reference USING btree (references_id);

CREATE TABLE antibody_crossreference (
	antibody_id bigint NOT NULL,
	crossreferences_id bigint NOT NULL
);

ALTER TABLE antibody_crossreference ADD CONSTRAINT antibody_crossreference_antibody_id_fk FOREIGN KEY (antibody_id) REFERENCES antibody (id);
ALTER TABLE antibody_crossreference ADD CONSTRAINT antibody_crossreference_crossreferences_id_fk FOREIGN KEY (crossreferences_id) REFERENCES crossreference (id);

CREATE INDEX antibody_crossreference_antibody_index ON antibody_crossreference USING btree (antibody_id);
CREATE INDEX antibody_crossreference_crossreferences_index ON antibody_crossreference USING btree (crossreferences_id);

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Antibody clonality', 'antibody_clonality');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'monoclonal', id FROM vocabulary WHERE vocabularylabel = 'antibody_clonality';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'polyclonal', id FROM vocabulary WHERE vocabularylabel = 'antibody_clonality';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'not_specified', id FROM vocabulary WHERE vocabularylabel = 'antibody_clonality';

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Antibody heavy chain isotype', 'antibody_heavy_chain_isotype');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgA', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgA1', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgA2', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgD', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgE', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgG', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgG1', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgG2', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgG2a', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgG2b', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgG2c', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgG3', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgG4', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgM', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgN', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgR', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgW', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgX', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'IgY', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'Fab', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'VHH', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'not_specified', id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype';

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Antibody light chain isotype', 'antibody_light_chain_isotype');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'k', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'l', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'l1', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'l2', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'l3', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'l4', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'r', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 's', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'i', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'i1', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'i2', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'i3', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'i4', id FROM vocabulary WHERE vocabularylabel = 'antibody_light_chain_isotype';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Antibody note types', 'antibody_note_type', id, 'Note types applicable to antibodies' FROM vocabulary WHERE vocabularylabel = 'note_type';

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'Direct (LinkML) DQM Antibody Loads');

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'ANTIBODY', 'WB Antibody Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) DQM Antibody Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'ANTIBODY', 'MGI Antibody Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) DQM Antibody Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'ANTIBODY', 'FB Antibody Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) DQM Antibody Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'ANTIBODY', 'XB Antibody Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) DQM Antibody Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'ANTIBODY', 'ZFIN Antibody Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) DQM Antibody Loads';

INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'WB' FROM bulkload WHERE name = 'WB Antibody Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'MGI' FROM bulkload WHERE name = 'MGI Antibody Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'FB' FROM bulkload WHERE name = 'FB Antibody Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'XB' FROM bulkload WHERE name = 'XB Antibody Load';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'ZFIN' FROM bulkload WHERE name = 'ZFIN Antibody Load';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'antibody_note_type'), vt.id
	FROM vocabularyterm vt JOIN vocabulary v ON v.id = vt.vocabulary_id
	WHERE v.vocabularylabel = 'note_type' AND vt.name = 'comment';
