CREATE SEQUENCE public.expressionannotation_seq         START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE expressionannotation (
	id bigint CONSTRAINT expressionannotation_pkey PRIMARY KEY,
	relation_id bigint,
	whenexpressedstagename VARCHAR(2000),
	whereexpressedstatement VARCHAR(2000)
);

ALTER TABLE expressionannotation
	ADD CONSTRAINT  expressionannotation_relation_id_fk
		FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);

CREATE INDEX expressionannotation_whenexpressedstagename_index ON expressionannotation USING btree (whenexpressedstagename);
CREATE INDEX expressionannotation_whereexpressedstatement_index ON expressionannotation USING btree (whereexpressedstatement);

CREATE SEQUENCE public.geneexpressionannotation_seq         START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE geneexpressionannotation (
	id bigint CONSTRAINT geneexpressionannotation_pkey PRIMARY KEY,
	expressionannotationsubject_id bigint,
	expressionassayused_id bigint
);

ALTER TABLE geneexpressionannotation
	ADD CONSTRAINT  geneexpressionannotation_expressionannotationsubject_id_fk
		FOREIGN KEY (expressionannotationsubject_id) REFERENCES gene(id);

ALTER TABLE geneexpressionannotation
	ADD CONSTRAINT geneexpressionannotation_expressionassayused_id_fk
		FOREIGN KEY (expressionassayused_id) REFERENCES ontologyterm(id);

CREATE INDEX geneexpressionannotation_expression_annotation_subject_index ON geneexpressionannotation USING btree (expressionannotationsubject_id);
CREATE INDEX geneexpressionannotation_expression_assay_used_index ON geneexpressionannotation USING btree (expressionassayused_id);


INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'Expression Bulk Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'EXPRESSION', 'FB Expression Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Expression Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'EXPRESSION', 'MGI Expression Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Expression Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'EXPRESSION', 'RGD Expression Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Expression Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'EXPRESSION', 'SGD Expression Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Expression Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'EXPRESSION', 'WB Expression Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Expression Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'EXPRESSION', 'XBXT Expression Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Expression Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'EXPRESSION', 'ZFIN Expression Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Expression Bulk Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'EXPRESSION';

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'EXPRESSION', 'FB' FROM bulkload WHERE name = 'FB Expression Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'EXPRESSION', 'MGI' FROM bulkload WHERE name = 'MGI Expression Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'EXPRESSION', 'RGD' FROM bulkload WHERE name = 'RGD Expression Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'EXPRESSION', 'SGD' FROM bulkload WHERE name = 'SGD Expression Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'EXPRESSION', 'WB' FROM bulkload WHERE name = 'WB Expression Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'EXPRESSION', 'XBXT' FROM bulkload WHERE name = 'XBXT Expression Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'EXPRESSION', 'ZFIN' FROM bulkload WHERE name = 'ZFIN Expression Load';
