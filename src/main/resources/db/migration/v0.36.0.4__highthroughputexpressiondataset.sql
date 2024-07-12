-- To create bulk loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'HTP Expression Dataset Annotation Bulk Loads');

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTP_EXPRESSION_DATASET_ANNOTATION', 'FB HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTP_EXPRESSION_DATASET_ANNOTATION', 'ZFIN HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTP_EXPRESSION_DATASET_ANNOTATION', 'MGI HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTP_EXPRESSION_DATASET_ANNOTATION', 'RGD HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTP_EXPRESSION_DATASET_ANNOTATION', 'SGD HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'HTP_EXPRESSION_DATASET_ANNOTATION', 'WB HTP Expression Dataset Annotation Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'HTP Expression Dataset Annotation Bulk Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'HTP_EXPRESSION_DATASET_ANNOTATION';

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTP_EXPRESSION_DATASET_ANNOTATION', 'FB' FROM bulkload WHERE name = 'FB HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTP_EXPRESSION_DATASET_ANNOTATION', 'MGI' FROM bulkload WHERE name = 'MGI HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTP_EXPRESSION_DATASET_ANNOTATION', 'RGD' FROM bulkload WHERE name = 'RGD HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTP_EXPRESSION_DATASET_ANNOTATION', 'SGD' FROM bulkload WHERE name = 'SGD HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTP_EXPRESSION_DATASET_ANNOTATION', 'WB' FROM bulkload WHERE name = 'WB HTP Expression Dataset Annotation Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'HTP_EXPRESSION_DATASET_ANNOTATION', 'ZFIN' FROM bulkload WHERE name = 'ZFIN HTP Expression Dataset Annotation Load';


--Adding 3 extra vocabulary terms for HTPTags

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'epigenome', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'functional genomics and proteomics', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'karyotyping', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';

-- Adding highthroughputexpressiondatasetannotation

CREATE SEQUENCE highthroughputexpressiondatasetannotation_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE highthroughputexpressiondatasetannotation (
     id bigint NOT NULL,
     datecreated timestamp(6) with time zone,
     dateupdated timestamp(6) with time zone,
     dbdatecreated timestamp(6) with time zone,
     dbdateupdated timestamp(6) with time zone,
     internal boolean DEFAULT false NOT NULL,
     obsolete boolean DEFAULT false NOT NULL,
     curie character varying(255),
     modentityid character varying(255),
     modinternalid character varying(255),
     name character varying(255),
     numberofchannels integer,
     realtednote character varying(255),
     createdby_id bigint,
     updatedby_id bigint,
     dataprovider_id bigint
);

CREATE TABLE highthroughputexpressiondatasetannotation_categorytags (
     highthroughputexpressiondatasetannotation_id bigint NOT NULL,
     categorytags_id bigint NOT NULL
 );

CREATE TABLE highthroughputexpressiondatasetannotation_reference (
     highthroughputexpressiondatasetannotation_id bigint NOT NULL,
     references_id bigint NOT NULL
 );

ALTER TABLE highthroughputexpressiondatasetannotation ADD CONSTRAINT highthroughputexpressiondatasetannotation_pkey PRIMARY KEY (id);

CREATE INDEX htpdatasetannotation_reference_htpdataset_index ON highthroughputexpressiondatasetannotation_reference USING btree (highthroughputexpressiondatasetannotation_id);

CREATE INDEX htpdatasetannotation_reference_references_index ON highthroughputexpressiondatasetannotation_reference USING btree (references_id);

CREATE INDEX htpdatasetannotation_categorytags_index ON highthroughputexpressiondatasetannotation_categorytags USING btree (categorytags_id);

CREATE INDEX htpdatasetannotation_htpdatasetid_index ON highthroughputexpressiondatasetannotation_categorytags USING btree (highthroughputexpressiondatasetannotation_id);

ALTER TABLE highthroughputexpressiondatasetannotation_categorytags
    ADD CONSTRAINT highthroughputexpressiondatasetannotation_categorytags_highthroughputexpressiondatasetannotation_id_fk FOREIGN KEY (highthroughputexpressiondatasetannotation_id) REFERENCES highthroughputexpressiondatasetannotation(id);

ALTER TABLE highthroughputexpressiondatasetannotation
    ADD CONSTRAINT highthroughputexpressiondatasetannotation_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);

ALTER TABLE highthroughputexpressiondatasetannotation_categorytags
    ADD CONSTRAINT htpdatasetannotation_categorytags_categorytags_id_fk FOREIGN KEY (categorytags_id) REFERENCES vocabularyterm(id);

ALTER TABLE highthroughputexpressiondatasetannotation_reference
    ADD CONSTRAINT htpdatasetannotation_reference_htpdatasetannotation_id_fk FOREIGN KEY (highthroughputexpressiondatasetannotation_id) REFERENCES highthroughputexpressiondatasetannotation(id);

ALTER TABLE highthroughputexpressiondatasetannotation_reference
    ADD CONSTRAINT htpdatasetannotation_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES reference(id);

ALTER TABLE highthroughputexpressiondatasetannotation
    ADD CONSTRAINT htpdatasetannotation_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE highthroughputexpressiondatasetannotation
    ADD CONSTRAINT htpdatasetannotation_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);