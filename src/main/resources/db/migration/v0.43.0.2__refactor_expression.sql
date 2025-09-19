ALTER TABLE geneexpressionannotation DROP CONSTRAINT IF EXISTS fk5d64is4egiofaj16ewsxl7vdh;

ALTER TABLE geneexpressionannotation
	ADD COLUMN IF NOT EXISTS expressionexperiment_id bigint;
	
UPDATE geneexpressionannotation g
	SET expressionexperiment_id = j.geneexpressionexperiment_id
	FROM geneexpressionexperiment_geneexpressionannotation j
	WHERE g.id = j.expressionannotations_id;
	
ALTER TABLE geneexpressionannotation
	ADD CONSTRAINT geneexpressionannotation_expressionexperiment_id_fk
	FOREIGN KEY (expressionexperiment_id)
	REFERENCES geneexpressionexperiment (id);
	
DROP TABLE geneexpressionexperiment_geneexpressionannotation;

CREATE INDEX geneexpressionannotation_expressionexperiment_index
	ON geneexpressionannotation USING btree (expressionexperiment_id);