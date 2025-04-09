CREATE TABLE geneexpressionexperiment_crossreference (
    geneexpressionexperiment_id bigint NOT NULL,
    crossreferences_id bigint NOT NULL,
    CONSTRAINT gee_crossrefence_experiment_id_fk FOREIGN KEY (geneexpressionexperiment_id)
        REFERENCES geneexpressionexperiment (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT gee_crossreference_crossrefs_id_fk FOREIGN KEY (crossreferences_id)
        REFERENCES crossreference (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE INDEX gee_crossreference_geneexpressionexperiment_index
    ON geneexpressionexperiment_crossreference USING btree (geneexpressionexperiment_id ASC NULLS LAST);

CREATE INDEX gee_crossreference_crossreferences_index
    ON geneexpressionexperiment_crossreference USING btree (crossreferences_id ASC NULLS LAST);





