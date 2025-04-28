CREATE TABLE externaldatabasereference (
	id BIGINT CONSTRAINT externaldatabasereference_pkey PRIMARY KEY
);

ALTER TABLE externaldatabasereference ADD CONSTRAINT externaldatabasereference_id_fk FOREIGN KEY (id) REFERENCES informationcontententity(id);

ALTER TABLE diseaseannotation RENAME COLUMN singlereference_id TO evidenceitem_id;
ALTER TABLE geneexpressionannotation RENAME COLUMN singlereference_id TO evidenceitem_id;
ALTER TABLE phenotypeannotation RENAME COLUMN singlereference_id TO evidenceitem_id;

ALTER TABLE diseaseannotation DROP CONSTRAINT diseaseannotation_singlereference_id_fk;
ALTER TABLE geneexpressionannotation DROP CONSTRAINT geneexpressionannotation_singlereference_id_fk;
ALTER TABLE phenotypeannotation DROP CONSTRAINT phenotypeannotation_singlereference_id_fk;

ALTER TABLE diseaseannotation ADD CONSTRAINT diseaseannotation_evidenceitem_id_fk FOREIGN KEY (evidenceitem_id) REFERENCES informationcontententity (id);
ALTER TABLE geneexpressionannotation ADD CONSTRAINT geneexpressionannotation_evidenceitem_id_fk FOREIGN KEY (evidenceitem_id) REFERENCES informationcontententity (id);
ALTER TABLE phenotypeannotation ADD CONSTRAINT phenotypeannotation_evidenceitem_id_fk FOREIGN KEY (evidenceitem_id) REFERENCES informationcontententity (id);

ALTER INDEX diseaseannotation_singlereference_index RENAME TO diseaseannotation_evidenceitem_index;
ALTER INDEX geneexpressionannotation_singlereference_index RENAME TO geneexpressionannotation_evidenceitem_index;
ALTER INDEX phenotypeannotation_singlereference_index RENAME TO phenotypeannotation_evidenceitem_index;

-- From migrations that won't get run due to hotfix on production
-- from file v0.38.0.36__gene_expression_crossreferences.sql
UPDATE geneexpressionannotation SET dataprovidercrossreference_id = NULL;

CREATE TABLE IF NOT EXISTS geneexpressionannotation_crossreference (
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

CREATE INDEX IF NOT EXISTS gea_crossreference_geneexpressionannotation_index
    ON geneexpressionannotation_crossreference USING btree (geneexpressionannotation_id ASC NULLS LAST);

CREATE INDEX IF NOT EXISTS gea_crossreference_crossreferences_index
    ON geneexpressionannotation_crossreference USING btree (crossreferences_id ASC NULLS LAST);

--from file v0.38.0.37__add_bto_ontology.sql
INSERT INTO bulkload (id, backendbulkloadtype, name, ontologytype, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
	SELECT nextval('bulkload_seq'), 'ONTOLOGY', 'BTO Ontology Load', 'BTO', false, false, id, now(), 'STOPPED'
	FROM bulkloadgroup WHERE name = 'Ontology Bulk Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', true
	FROM bulkload where name = 'BTO Ontology Load';

INSERT INTO bulkurlload (id, bulkloadurl)
	SELECT id, 'http://purl.obolibrary.org/obo/bto.owl'
	FROM bulkload where name = 'BTO Ontology Load';

