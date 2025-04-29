-- MGI
DELETE FROM geneexpressionannotation_crossreference geac WHERE  geac.crossreferences_id IN (
SELECT gea_cr.crossreferences_id FROM geneexpressionannotation_crossreference gea_cr, geneexpressionannotation gea
WHERE
 gea.dataprovider_id = (SELECT id FROM organization WHERE abbreviation = 'MGI') AND
 gea.id = gea_cr.geneexpressionannotation_id
);

DELETE FROM geneexpressionexperiment_crossreference geec WHERE  geec.crossreferences_id IN (
SELECT gee_cr.crossreferences_id FROM geneexpressionexperiment_crossreference gee_cr, geneexpressionexperiment gee
WHERE
 gee.dataprovider_id = (SELECT id FROM organization WHERE abbreviation = 'MGI') AND
 gee.id = gee_cr.geneexpressionexperiment_id);

-- WB
DELETE FROM geneexpressionannotation_crossreference geac WHERE  geac.crossreferences_id IN (
SELECT gea_cr.crossreferences_id FROM geneexpressionannotation_crossreference gea_cr, geneexpressionannotation gea
WHERE
 gea.dataprovider_id = (SELECT id FROM organization WHERE abbreviation = 'WB') AND
 gea.id = gea_cr.geneexpressionannotation_id
);

DELETE FROM geneexpressionexperiment_crossreference geec WHERE  geec.crossreferences_id IN (
SELECT gee_cr.crossreferences_id FROM geneexpressionexperiment_crossreference gee_cr, geneexpressionexperiment gee
WHERE
 gee.dataprovider_id = (SELECT id FROM organization WHERE abbreviation = 'WB') AND
 gee.id = gee_cr.geneexpressionexperiment_id);

-- ZFIN
DELETE FROM geneexpressionannotation_crossreference geac WHERE  geac.crossreferences_id IN (
SELECT gea_cr.crossreferences_id FROM geneexpressionannotation_crossreference gea_cr, geneexpressionannotation gea
WHERE
 gea.dataprovider_id = (SELECT id FROM organization WHERE abbreviation = 'ZFIN') AND
 gea.id = gea_cr.geneexpressionannotation_id
);

DELETE FROM geneexpressionexperiment_crossreference geec WHERE  geec.crossreferences_id IN (
SELECT gee_cr.crossreferences_id FROM geneexpressionexperiment_crossreference gee_cr, geneexpressionexperiment gee
WHERE
 gee.dataprovider_id = (SELECT id FROM organization WHERE abbreviation = 'ZFIN') AND
 gee.id = gee_cr.geneexpressionexperiment_id);
