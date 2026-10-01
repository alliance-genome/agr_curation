-- SCRUM-6584: Delete the ZFIN gene cross references whose referenced curie has no prefix
-- (ZDB-GENE-020731-3 rather than ZFIN:ZDB-GENE-020731-3).
--
-- They were created by the ZFIN gene loads of 2024-05-21/22, before the curie format was
-- enforced, on the gene, gene/references, gene/expression, gene/expression_images and
-- gene/wild_type_expression pages. Every one belongs to a gene that is now obsolete (79 genes,
-- 194 cross references on production and beta, 229 on alpha), so no later load rewrote them,
-- and none of those genes has a prefixed twin on the same page. Each is referenced only from
-- genomicentity_crossreference.
--
-- Scoped to ZDB- curies so unrelated unprefixed values (e.g. curator test input) are left alone.
-- The cross reference itself is only deleted once nothing references it, checked against every
-- column with a foreign key to crossreference (verified against pg_constraint).

CREATE TEMPORARY TABLE unprefixed_zfin_crossreference ON COMMIT DROP AS
	SELECT id FROM crossreference
	WHERE referencedcurie NOT LIKE '%:%' AND referencedcurie LIKE 'ZDB-%';

DELETE FROM genomicentity_crossreference x
	USING unprefixed_zfin_crossreference u
	WHERE x.crossreferences_id = u.id;

DELETE FROM crossreference c
	USING unprefixed_zfin_crossreference u
	WHERE c.id = u.id
	AND NOT EXISTS (SELECT 1 FROM antibody_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM biologicalentity x WHERE x.dataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM chromosome x WHERE x.dataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM diseaseannotation x WHERE x.dataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM diseaseannotation x WHERE x.secondarydataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM externaldatabaseentity x WHERE x.preferredcrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM externaldatabaseentity_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM gene x WHERE x.gcrpcrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM geneexpressionannotation x WHERE x.dataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM geneexpressionannotation_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM geneexpressionexperiment x WHERE x.dataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM geneexpressionexperiment_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM genegeneticinteraction_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM genemolecularinteraction_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM genomeassembly_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM genomicentity_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM htpexpressiondatasetannotation x WHERE x.dataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM htpexpressiondatasetsampleannotation x WHERE x.dataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM ontologyterm_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM phenotypeannotation x WHERE x.crossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM phenotypeannotation x WHERE x.dataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM reagent x WHERE x.dataprovidercrossreference_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM reference_crossreference x WHERE x.crossreferences_id = c.id)
	AND NOT EXISTS (SELECT 1 FROM species x WHERE x.dataprovidercrossreference_id = c.id);
