-- SCRUM-6585 — sample annotations submitted without a sampleId (MGI) are now matched on
-- (dataProvider, htpExpressionSampleTitle, datasetIds) during bulk load. Index the title so the
-- ~256k lookups per MGI load do not seq-scan the table.

CREATE INDEX htpdatasample_htpExpressionSampleTitle_index ON htpexpressiondatasetsampleannotation USING btree (htpExpressionSampleTitle);
