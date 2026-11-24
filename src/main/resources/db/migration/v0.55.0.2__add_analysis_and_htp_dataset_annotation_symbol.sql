-- SCRUM-6714: FB scRNA-seq additions to HTPExpressionDatasetAnnotation - a plain symbol column,
-- and a new cascade-owned Analysis sub-object (one per dataset annotation, not independently
-- curatable - same pattern as biosampleage/microarraysampledetails).

ALTER TABLE htpexpressiondatasetannotation ADD COLUMN symbol character varying(255);

CREATE TABLE analysis (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	analysistype_id bigint,
	dataprovider_id bigint,
	dataprovidercrossreference_id bigint,
	createdby_id bigint,
	updatedby_id bigint
);

CREATE SEQUENCE analysis_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE analysis
	ADD CONSTRAINT analysis_pkey PRIMARY KEY (id);

ALTER TABLE htpexpressiondatasetannotation ADD COLUMN analysis_id bigint;

CREATE TABLE analysis_note (
	analysis_id bigint NOT NULL,
	relatednotes_id bigint NOT NULL
);

ALTER TABLE analysis_note
	ADD CONSTRAINT analysis_relatednotes_id_uk UNIQUE (relatednotes_id);

CREATE INDEX analysis_analysistype_index ON analysis USING btree (analysistype_id);
CREATE INDEX analysis_dataprovider_index ON analysis USING btree (dataprovider_id);
CREATE INDEX analysis_dataprovidercrossreference_index ON analysis USING btree (dataprovidercrossreference_id);
CREATE INDEX htpdatasetannotation_analysis_index ON htpexpressiondatasetannotation USING btree (analysis_id);
CREATE INDEX analysis_note_analysis_index ON analysis_note USING btree (analysis_id);
CREATE INDEX analysis_note_relatednotes_index ON analysis_note USING btree (relatednotes_id);

-- FBCVTerm has no table of its own: OntologyTerm uses single-table inheritance, so every ontology
-- term subtype (FBCVTerm included) lives in the shared ontologyterm table.
ALTER TABLE analysis
	ADD CONSTRAINT analysis_analysistype_id_fk FOREIGN KEY (analysistype_id) REFERENCES ontologyterm(id);
ALTER TABLE analysis
	ADD CONSTRAINT analysis_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES organization(id);
ALTER TABLE analysis
	ADD CONSTRAINT analysis_dataprovidercrossreference_id_fk FOREIGN KEY (dataprovidercrossreference_id) REFERENCES crossreference(id);
ALTER TABLE analysis
	ADD CONSTRAINT analysis_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE analysis
	ADD CONSTRAINT analysis_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE htpexpressiondatasetannotation
	ADD CONSTRAINT htpdatasetannotation_analysis_id_fk FOREIGN KEY (analysis_id) REFERENCES analysis(id);
ALTER TABLE analysis_note
	ADD CONSTRAINT analysis_note_analysis_id_fk FOREIGN KEY (analysis_id) REFERENCES analysis(id);
ALTER TABLE analysis_note
	ADD CONSTRAINT analysis_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note(id);
