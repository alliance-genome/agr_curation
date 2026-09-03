-- SCRUM-6463 — the fourth of the classes tracked by this ticket gets its curie column, so the
-- htp_expression_sample backfill endpoint has somewhere to write.
--
-- Split from v0.53.0.2 only because that migration was already committed. Same treatment as the
-- other three: nullable, indexed, no unique constraint.
--
-- Note HTPExpressionDatasetSampleAnnotation extends AuditedObject in the Java entity model, not
-- SubmittedObject as its LinkML counterpart does, so it inherits no curie and needs its own column.

ALTER TABLE htpexpressiondatasetsampleannotation ADD COLUMN curie varchar(255);

CREATE INDEX htpdatasample_curie_index ON htpexpressiondatasetsampleannotation USING btree (curie);
