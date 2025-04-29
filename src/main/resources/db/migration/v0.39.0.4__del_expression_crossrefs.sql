-- MGI
DELETE FROM geneexpressionannotation_crossreference geac WHERE  geac.crossreferences_id IN (
SELECT gea_cr.crossreferences_id FROM geneexpressionannotation_crossreference gea_cr, geneexpressionannotation gea
WHERE
 gea.dataprovider_id = 20308680 AND
 gea.id = gea_cr.geneexpressionannotation_id
);

DELETE FROM geneexpressionexperiment_crossreference geec WHERE  geec.crossreferences_id IN (
SELECT gee_cr.crossreferences_id FROM geneexpressionexperiment_crossreference gee_cr, geneexpressionexperiment gee
WHERE
 gee.dataprovider_id = 20308680 AND
 gee.id = gee_cr.geneexpressionexperiment_id);

-- WB
DELETE FROM geneexpressionannotation_crossreference geac WHERE  geac.crossreferences_id IN (
SELECT gea_cr.crossreferences_id FROM geneexpressionannotation_crossreference gea_cr, geneexpressionannotation gea
WHERE
 gea.dataprovider_id = 20308683 AND
 gea.id = gea_cr.geneexpressionannotation_id
);

DELETE FROM geneexpressionexperiment_crossreference geec WHERE  geec.crossreferences_id IN (
SELECT gee_cr.crossreferences_id FROM geneexpressionexperiment_crossreference gee_cr, geneexpressionexperiment gee
WHERE
 gee.dataprovider_id = 20308683 AND
 gee.id = gee_cr.geneexpressionexperiment_id);

-- ZFIN
DELETE FROM geneexpressionannotation_crossreference geac WHERE  geac.crossreferences_id IN (
SELECT gea_cr.crossreferences_id FROM geneexpressionannotation_crossreference gea_cr, geneexpressionannotation gea
WHERE
 gea.dataprovider_id = 20308685 AND
 gea.id = gea_cr.geneexpressionannotation_id
);

DELETE FROM geneexpressionexperiment_crossreference geec WHERE  geec.crossreferences_id IN (
SELECT gee_cr.crossreferences_id FROM geneexpressionexperiment_crossreference gee_cr, geneexpressionexperiment gee
WHERE
 gee.dataprovider_id = 20308685 AND
 gee.id = gee_cr.geneexpressionexperiment_id);


