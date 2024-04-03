-- Create gene molecular interaction tables and indexes

CREATE TABLE genegeneassociation (
	id bigint PRIMARY KEY,
	geneassociationsubject_id bigint,
	genegeneassociationobject_id bigint,
	relation_id bigint
);

ALTER TABLE genegeneassociation ADD CONSTRAINT genegeneassociation_id_fk FOREIGN KEY (id) REFERENCES evidenceassociation (id);
ALTER TABLE genegeneassociation ADD CONSTRAINT genegeneassociation_geneassociationsubject_id_fk FOREIGN KEY (geneassociationsubject_id) REFERENCES gene (id);
ALTER TABLE genegeneassociation ADD CONSTRAINT genegeneassociation_genegeneassociationobjectid_fk FOREIGN KEY (genegeneassociationobject_id) REFERENCES gene (id);
ALTER TABLE genegeneassociation ADD CONSTRAINT genegeneassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);
CREATE INDEX genegeneassociation_geneassociationsubject_index ON genegeneassociation USING btree (geneassociationsubject_id);
CREATE INDEX genegeneassociation_genegeneassociationobject_index ON genegeneassociation USING btree (genegeneassociationobject_id);
CREATE INDEX genegeneassociation_relation_index ON genegeneassociation USING btree (relation_id);

CREATE TABLE geneinteraction (
	id bigint PRIMARY KEY,
	interactionid varchar (255),
	uniqueid varchar (2000),
	interactionsource_id bigint,
	interactiontype_id bigint,
	interactorarole_id bigint,
	interactorbrole_id bigint,
	interactoratype_id bigint,
	interactorbtype_id bigint
);

ALTER TABLE geneinteraction ADD CONSTRAINT geneinteraction_id_fk FOREIGN KEY (id) REFERENCES genegeneassociation (id);
ALTER TABLE geneinteraction ADD CONSTRAINT geneinteraction_interactionsource_id_fk FOREIGN KEY (interactionsource_id) REFERENCES ontologyterm (id);
ALTER TABLE geneinteraction ADD CONSTRAINT geneinteraction_interactiontype_id_fk FOREIGN KEY (interactiontype_id) REFERENCES ontologyterm (id);
ALTER TABLE geneinteraction ADD CONSTRAINT geneinteraction_interactorarole_id_fk FOREIGN KEY (interactorarole_id) REFERENCES ontologyterm (id);
ALTER TABLE geneinteraction ADD CONSTRAINT geneinteraction_interactorbrole_id_fk FOREIGN KEY (interactorbrole_id) REFERENCES ontologyterm (id);
ALTER TABLE geneinteraction ADD CONSTRAINT geneinteraction_interactoratype_id_fk FOREIGN KEY (interactoratype_id) REFERENCES ontologyterm (id);
ALTER TABLE geneinteraction ADD CONSTRAINT geneinteraction_interactorbtype_id_fk FOREIGN KEY (interactorbtype_id) REFERENCES ontologyterm (id);
CREATE INDEX geneinteraction_interactionsource_index ON geneinteraction USING btree (interactionsource_id);
CREATE INDEX geneinteraction_interactiontype_index ON geneinteraction USING btree (interactiontype_id);
CREATE INDEX geneinteraction_interactorarole_index ON geneinteraction USING btree (interactorarole_id);
CREATE INDEX geneinteraction_interactorbrole_index ON geneinteraction USING btree (interactorbrole_id);
CREATE INDEX geneinteraction_interactoratype_index ON geneinteraction USING btree (interactoratype_id);
CREATE INDEX geneinteraction_interactorbtype_index ON geneinteraction USING btree (interactorbtype_id);
ALTER TABLE geneinteraction ADD CONSTRAINT geneinteraction_interactionid_uk UNIQUE (interactionid);
ALTER TABLE geneinteraction ADD CONSTRAINT geneinteraction_uniqueid_uk UNIQUE (uniqueid);

CREATE TABLE genemolecularinteraction (
	id bigint PRIMARY KEY,
	aggregationdatabase_id bigint,
	detectionmethod_id bigint
);

ALTER TABLE genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_id_fk FOREIGN KEY (id) REFERENCES geneinteraction (id);
ALTER TABLE genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_aggregationdatabase_id_fk FOREIGN KEY (aggregationdatabase_id) REFERENCES ontologyterm (id);
ALTER TABLE genemolecularinteraction ADD CONSTRAINT genemolecularinteraction_detectionmethod_id_fk FOREIGN KEY (detectionmethod_id) REFERENCES ontologyterm (id);
CREATE INDEX genemolecularinteraction_aggregationdatabase_index ON genemolecularinteraction USING btree (aggregationdatabase_id);
CREATE INDEX genemolecularinteraction_detectionmethod_index ON genemolecularinteraction USING btree (detectionmethod_id);

CREATE TABLE geneinteraction_crossreference (
	geneinteraction_id bigint,
	crossreferences_id bigint
);
ALTER TABLE geneinteraction_crossreference ADD CONSTRAINT geneinteraction_crossreference_geneinteraction_id_fk FOREIGN KEY (geneinteraction_id) REFERENCES geneinteraction (id);
ALTER TABLE geneinteraction_crossreference ADD CONSTRAINT geneinteraction_crossreference_crossreferences_id_fk FOREIGN KEY (crossreferences_id) REFERENCES crossreference (id);
CREATE INDEX geneinteraction_crossreference_geneinteraction_index ON geneinteraction_crossreference USING btree (geneinteraction_id);
CREATE INDEX geneinteraction_crossreference_crossreferences_index ON geneinteraction_crossreference USING btree (crossreferences_id);
CREATE INDEX geneinteraction_crossreference_gi_xref_index ON geneinteraction_crossreference USING btree (geneinteraction_id, crossreferences_id);


-- Add relation vocabulary with 'has_phenotype' term

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Gene Interaction Relation', 'gene_interaction_relation');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'genetically_interacts_with', id FROM vocabulary WHERE vocabularylabel = 'gene_interaction_relation';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'physically_interacts_with', id FROM vocabulary WHERE vocabularylabel = 'gene_interaction_relation';
INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT nextval('vocabularytermset_seq'), 'Gene Genetic Interaction Relation', 'gene_genetic_interaction_relation', id FROM vocabulary WHERE vocabularylabel = 'gene_interaction_relation';
INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id)
	SELECT nextval('vocabularytermset_seq'), 'Gene Molecular Interaction Relation', 'gene_molecular_interaction_relation', id FROM vocabulary WHERE vocabularylabel = 'gene_interaction_relation';

CREATE TABLE tmp_vocab_link (
	vocabularytermsets_id bigint,
	memberterms_id bigint
);
INSERT INTO tmp_vocab_link(vocabularytermsets_id) SELECT id FROM vocabularytermset where vocabularylabel = 'gene_genetic_interaction_relation';
UPDATE tmp_vocab_link SET memberterms_id = subquery.id
	FROM (SELECT id FROM vocabularyterm WHERE name = 'genetically_interacts_with') AS subquery
	WHERE memberterms_id IS NULL;
INSERT INTO tmp_vocab_link(vocabularytermsets_id) SELECT id FROM vocabularytermset where vocabularylabel = 'gene_molecular_interaction_relation';
UPDATE tmp_vocab_link SET memberterms_id = subquery.id
	FROM (SELECT id FROM vocabularyterm WHERE name = 'physically_interacts_with') AS subquery
	WHERE memberterms_id IS NULL;
INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT vocabularytermsets_id, memberterms_id FROM tmp_vocab_link;
DROP TABLE tmp_vocab_link;


-- Create bulk loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'Interaction Bulk Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'INTERACTION_MOL', 'Molecular Interaction Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Interaction Bulk Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'INTERACTION_MOL';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'INTERACTION-MOL', 'COMBINED' FROM bulkload WHERE name = 'Molecular Interaction Load';