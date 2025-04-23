CREATE TABLE agmphenotypeannotation_allele (
	agmphenotypeannotation_id bigint,
	assertedalleles_id bigint
);

ALTER TABLE agmphenotypeannotation_allele ADD CONSTRAINT agmphenotypeannotation_allele_agmphenotypeannotation_id_fk FOREIGN KEY (agmphenotypeannotation_id) REFERENCES agmphenotypeannotation (id);
ALTER TABLE agmphenotypeannotation_allele ADD CONSTRAINT agmphenotypeannotation_allele_assertedalleles_id_fk FOREIGN KEY (assertedalleles_id) REFERENCES allele (id);
CREATE INDEX agmphenotypeannotation_allele_agmphenotypeannotation_index ON agmphenotypeannotation_allele USING btree (agmphenotypeannotation_id);
CREATE INDEX agmphenotypeannotation_allele_assertedalleles_index ON agmphenotypeannotation_allele USING btree (assertedalleles_id);

CREATE TABLE agmdiseaseannotation_allele (
	agmdiseaseannotation_id bigint,
	assertedalleles_id bigint
);

ALTER TABLE agmdiseaseannotation_allele ADD CONSTRAINT agmdiseaseannotation_allele_agmdiseaseannotation_id_fk FOREIGN KEY (agmdiseaseannotation_id) REFERENCES agmdiseaseannotation (id);
ALTER TABLE agmdiseaseannotation_allele ADD CONSTRAINT agmdiseaseannotation_allele_assertedalleles_id_fk FOREIGN KEY (assertedalleles_id) REFERENCES allele (id);
CREATE INDEX agmdiseaseannotation_allele_agmdiseaseannotation_index ON agmdiseaseannotation_allele USING btree (agmdiseaseannotation_id);
CREATE INDEX agmdiseaseannotation_allele_assertedalleles_index ON agmdiseaseannotation_allele USING btree (assertedalleles_id);

INSERT INTO agmphenotypeannotation_allele (agmphenotypeannotation_id, assertedalleles_id) SELECT id, assertedallele_id FROM agmphenotypeannotation WHERE assertedallele_id IS NOT NULL;
INSERT INTO agmdiseaseannotation_allele (agmdiseaseannotation_id, assertedalleles_id) SELECT id, assertedallele_id FROM agmdiseaseannotation WHERE assertedallele_id IS NOT NULL;

ALTER TABLE agmphenotypeannotation DROP COLUMN assertedallele_id;
ALTER TABLE agmdiseaseannotation DROP COLUMN assertedallele_id;
