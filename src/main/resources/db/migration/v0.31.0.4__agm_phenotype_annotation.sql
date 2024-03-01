-- Create phenotypeannotation tables and indexes

CREATE TABLE phenotypeannotation (
	id bigint PRIMARY KEY,
	phenotypeannotationobject varchar(255),
	relation_id bigint
);

ALTER TABLE phenotypeannotation ADD CONSTRAINT phenotypeannotation_id_fk FOREIGN KEY (id) REFERENCES annotation (id);
ALTER TABLE phenotypeannotation ADD CONSTRAINT phenotypeannotation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);
CREATE INDEX phenotypeannotation_relation_index ON phenotypeannotation USING btree(relation_id);

CREATE TABLE phenotypeannotation_ontologyterm (
	phenotypeannotation_id bigint,
	phenotypeterms_id bigint
);

ALTER TABLE phenotypeannotation_ontologyterm ADD CONSTRAINT phenotypeannotation_ontologyterm_phenotypeannotation_id_fk FOREIGN KEY (phenotypeannotation_id) REFERENCES phenotypeannotation (id);
ALTER TABLE phenotypeannotation_ontologyterm ADD CONSTRAINT phenotypeannotation_ontologyterm_phenotypeterms_id_fh_fk FOREIGN KEY (phenotypeterms_id) REFERENCES ontologyterm (id);
CREATE INDEX phenotypeannotation_ontologyterm_phenotypeannotation_index ON phenotypeannotation_ontologyterm USING btree (phenotypeannotation_id);
CREATE INDEX phenotypeannotation_ontologyterm_phenotypeterms_index ON phenotypeannotation_ontologyterm USING btree (phenotypeterms_id);

CREATE TABLE agmphenotypeannotation (
	id bigint PRIMARY KEY,
	phenotypeannotationsubject_id bigint,
	assertedallele_id bigint,
	inferredallele_id bigint,
	inferredgene_id bigint
);

ALTER TABLE agmphenotypeannotation ADD CONSTRAINT agmphenotypeannotation_id_fk FOREIGN KEY (id) REFERENCES phenotypeannotation (id);
ALTER TABLE agmphenotypeannotation ADD CONSTRAINT agmphenotypeannotation_phenotypeannotationsubject_id_fk FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES affectedgenomicmodel (id);
ALTER TABLE agmphenotypeannotation ADD CONSTRAINT agmphenotypeannotation_assertedallele_id_fk FOREIGN KEY (assertedallele_id) REFERENCES allele (id);
ALTER TABLE agmphenotypeannotation ADD CONSTRAINT agmphenotypeannotation_inferredallele_id_fk FOREIGN KEY (inferredallele_id) REFERENCES allele (id);
ALTER TABLE agmphenotypeannotation ADD CONSTRAINT agmphenotypeannotation_inferredgene_id_fk FOREIGN KEY (inferredgene_id) REFERENCES gene (id);
CREATE INDEX agmphenotypeannotation_phenotypeannotationsubject_index ON agmphenotypeannotation USING btree (phenotypeannotationsubject_id);
CREATE INDEX agmphenotypeannotation_assertedallele_index ON agmphenotypeannotation USING btree (assertedallele_id);
CREATE INDEX agmphenotypeannotation_inferredallele_index ON agmphenotypeannotation USING btree (inferredallele_id);
CREATE INDEX agmphenotypeannotation_inferredgene_index ON agmphenotypeannotation USING btree (inferredgene_id);

CREATE TABLE agmphenotypeannotation_gene (
	agmphenotypeannotation_id bigint,
	assertedgenes_id bigint
);

ALTER TABLE agmphenotypeannotation_gene ADD CONSTRAINT agmphenotypeannotation_gene_agmphenotypeannotation_id_fk FOREIGN KEY (agmphenotypeannotation_id) REFERENCES agmphenotypeannotation (id);
ALTER TABLE agmphenotypeannotation_gene ADD CONSTRAINT agmphenotypeannotation_gene_assertedgenes_id_fk FOREIGN KEY (assertedgenes_id) REFERENCES gene (id);
CREATE INDEX agmphenotypeannotation_gene_agmphenotypeannotation_index ON agmphenotypeannotation_gene USING btree (agmphenotypeannotation_id);

CREATE TABLE allelephenotypeannotation (
	id bigint PRIMARY KEY,
	phenotypeannotationsubject_id bigint,
	inferredgene_id bigint
);

ALTER TABLE allelephenotypeannotation ADD CONSTRAINT agmphenotypeannotation_id_fk FOREIGN KEY (id) REFERENCES phenotypeannotation (id);
ALTER TABLE allelephenotypeannotation ADD CONSTRAINT agmphenotypeannotation_phenotypeannotationsubject_id_fk FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES allele (id);
ALTER TABLE allelephenotypeannotation ADD CONSTRAINT agmphenotypeannotation_inferredgene_id_fk FOREIGN KEY (inferredgene_id) REFERENCES gene (id);
CREATE INDEX allelephenotypeannotation_phenotypeannotationsubject_index ON allelephenotypeannotation USING btree (phenotypeannotationsubject_id);
CREATE INDEX allelephenotypeannotation_inferredgene_index ON allelephenotypeannotation USING btree (inferredgene_id);

CREATE TABLE allelephenotypeannotation_gene (
	allelephenotypeannotation_id bigint,
	assertedgenes_id bigint
);

ALTER TABLE allelephenotypeannotation_gene ADD CONSTRAINT allelephenotypeannotation_gene_gene_allelephenotypeannotation_id_fk FOREIGN KEY (allelephenotypeannotation_id) REFERENCES allelephenotypeannotation (id);
ALTER TABLE allelephenotypeannotation_gene ADD CONSTRAINT allelephenotypeannotation_gene_assertedgenes_id_fk FOREIGN KEY (assertedgenes_id) REFERENCES gene (id);
CREATE INDEX allelephenotypeannotation_gene_allelephenotypeannotation_index ON allelephenotypeannotation_gene USING btree (allelephenotypeannotation_id);

CREATE TABLE genephenotypeannotation (
	id bigint PRIMARY KEY,
	phenotypeannotationsubject_id bigint,
	sgdstrainbackground_id bigint
);

ALTER TABLE genephenotypeannotation ADD CONSTRAINT genephenotypeannotation_id_fk FOREIGN KEY (id) REFERENCES phenotypeannotation (id);
ALTER TABLE genephenotypeannotation ADD CONSTRAINT genephenotypeannotation_phenotypeannotationsubject_id_fk FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES gene (id);
ALTER TABLE genephenotypeannotation ADD CONSTRAINT genephenotypeannotation_sgdstrainbackground_id_fk FOREIGN KEY (sgdstrainbackground_id) REFERENCES affectedgenomicmodel (id);
CREATE INDEX genephenotypeannotation_phenotypeannotationsubject_index ON genephenotypeannotation USING btree (phenotypeannotationsubject_id);
CREATE INDEX genephenotypeannotation_sgdstrainbackground_index ON genephenotypeannotation USING btree (sgdstrainbackground_id);

-- Add relation vocabulary with 'has_phenotype' term

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Phenotype Relation', 'phenotype_relation');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'has_phenotype', id FROM vocabulary WHERE vocabularylabel = 'phenotype_relation';

-- Create bulk loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'Phenotype Bulk Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'PHENOTYPE', 'FB Phenotype Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Phenotype Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'PHENOTYPE', 'HUMAN Phenotype Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Phenotype Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'PHENOTYPE', 'MGI Phenotype Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Phenotype Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'PHENOTYPE', 'RGD Phenotype Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Phenotype Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'PHENOTYPE', 'SGD Phenotype Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Phenotype Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'PHENOTYPE', 'WB Phenotype Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Phenotype Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'PHENOTYPE', 'XBXL Phenotype Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Phenotype Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'PHENOTYPE', 'XBXT Phenotype Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Phenotype Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'PHENOTYPE', 'ZFIN Phenotype Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Phenotype Bulk Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', true FROM bulkload WHERE backendbulkloadtype = 'PHENOTYPE';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'PHENOTYPE', 'FB' FROM bulkload WHERE name = 'FB Phenotype Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'PHENOTYPE', 'HUMAN' FROM bulkload WHERE name = 'HUMAN Phenotype Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'PHENOTYPE', 'MGI' FROM bulkload WHERE name = 'MGI Phenotype Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'PHENOTYPE', 'RGD' FROM bulkload WHERE name = 'RGD Phenotype Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'PHENOTYPE', 'SGD' FROM bulkload WHERE name = 'SGD Phenotype Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'PHENOTYPE', 'WB' FROM bulkload WHERE name = 'WB Phenotype Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'PHENOTYPE', 'XBXL' FROM bulkload WHERE name = 'XBXL Phenotype Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'PHENOTYPE', 'XBXT' FROM bulkload WHERE name = 'XBXT Phenotype Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'PHENOTYPE', 'ZFIN' FROM bulkload WHERE name = 'ZFIN Phenotype Load';