-- SCRUM-6715: FB scRNA-seq additions to HTPExpressionDatasetSampleAnnotation - a plain symbol
-- column, a cascade-owned Analysis sub-object (reuses the analysis table SCRUM-6714 already
-- created - one row per sample annotation, not independently curatable), and a cascade-owned
-- biologicalReference ExternalDataBaseEntity (one row per sample annotation, same pattern as the
-- existing htpExpressionSample column).

ALTER TABLE htpexpressiondatasetsampleannotation ADD COLUMN symbol character varying(255);
ALTER TABLE htpexpressiondatasetsampleannotation ADD COLUMN analysis_id bigint;
ALTER TABLE htpexpressiondatasetsampleannotation ADD COLUMN biologicalreference_id bigint;

CREATE INDEX htpdatasample_analysis_index ON htpexpressiondatasetsampleannotation USING btree (analysis_id);
CREATE INDEX htpdatasample_biologicalreference_index ON htpexpressiondatasetsampleannotation USING btree (biologicalreference_id);

ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_analysis_id_fk FOREIGN KEY (analysis_id) REFERENCES analysis(id);
ALTER TABLE htpexpressiondatasetsampleannotation
	ADD CONSTRAINT htpdatasample_biologicalreference_id_fk FOREIGN KEY (biologicalreference_id) REFERENCES externaldatabaseentity(id);
