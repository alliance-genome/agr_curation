-- SCRUM-6268: these join tables back-reference htpexpressiondatasetannotation without ON DELETE CASCADE,
-- so deleting an obsolete annotation fails with a FK violation whenever categoryTags (required on every
-- annotation), references, or subSeries are populated. Cascading only the join rows (never the shared
-- VocabularyTerm/Reference/ExternalDataBaseEntity targets) matches the cascade semantics already declared
-- on these fields in HTPExpressionDatasetAnnotation.java.

ALTER TABLE htpexpressiondatasetannotation_categorytags
	DROP CONSTRAINT htpdatasetannotation_categorytags_htpdatasetannotation_id_fk;
ALTER TABLE htpexpressiondatasetannotation_categorytags
	ADD CONSTRAINT htpdatasetannotation_categorytags_htpdatasetannotation_id_fk FOREIGN KEY (htpexpressiondatasetannotation_id) REFERENCES htpexpressiondatasetannotation(id) ON DELETE CASCADE;

ALTER TABLE htpexpressiondatasetannotation_reference
	DROP CONSTRAINT htpdatasetannotation_reference_htpdatasetannotation_id_fk;
ALTER TABLE htpexpressiondatasetannotation_reference
	ADD CONSTRAINT htpdatasetannotation_reference_htpdatasetannotation_id_fk FOREIGN KEY (htpexpressiondatasetannotation_id) REFERENCES htpexpressiondatasetannotation(id) ON DELETE CASCADE;

ALTER TABLE htpexpressiondatasetannotation_externaldatabaseentity
	DROP CONSTRAINT htpannotation_externaldbentity_htpannotation_id_fk;
ALTER TABLE htpexpressiondatasetannotation_externaldatabaseentity
	ADD CONSTRAINT htpannotation_externaldbentity_htpannotation_id_fk FOREIGN KEY (htpexpressiondatasetannotation_id) REFERENCES htpexpressiondatasetannotation(id) ON DELETE CASCADE;
