
UPDATE geneexpressionannotation SET dataprovidercrossreference_id = NULL;

CREATE TABLE geneexpressionannotation_crossreference (
    geneexpressionannotation_id bigint NOT NULL,
    crossreferences_id bigint NOT NULL,
    CONSTRAINT gea_crossrerence_annotation_id_fk FOREIGN KEY (geneexpressionannotation_id)
        REFERENCES public.geneexpressionannotation (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT gea_crossreference_crossrefs_id_fk FOREIGN KEY (crossreferences_id)
        REFERENCES public.crossreference (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE INDEX gea_crossreference_geneexpressionannotation_index
    ON geneexpressionannotation_crossreference USING btree (geneexpressionannotation_id ASC NULLS LAST);

CREATE INDEX gea_crossreference_crossreferences_index
    ON geneexpressionannotation_crossreference USING btree (crossreferences_id ASC NULLS LAST);
