ALTER TABLE gene ADD gcrpcrossreference_id bigint;

ALTER TABLE gene ADD CONSTRAINT gene_gcrpcrossreference_id_fk FOREIGN KEY (gcrpcrossreference_id) REFERENCES crossreference(id);

CREATE INDEX gene_gcrpcrossreference_index ON gene USING btree (gcrpcrossreference_id);