-- SCRUM-6586: Clean up orphaned crossreference rows.
-- These are unreferenced crossreference rows left behind by loads that failed partway
-- through processing a record -- the DTO validator/service methods that create a
-- record's cross-references ran (and committed, since the crossreference insert
-- happens in the same @Transactional unit of work) before the record's own accumulated
-- validation errors were thrown, so the record itself was never persisted/updated but
-- its already-inserted crossreference rows were not rolled back. (See the @Rollback
-- addition to ValidationException in this same change, which fixes this going
-- forward.) Most of these are unreferenced duplicates of a still-live crossreference
-- with the same curie.
--
-- An orphan here is a crossreference row that none of the columns with a foreign key to
-- crossreference (verified against pg_constraint) reference.

DELETE FROM crossreference c
WHERE NOT EXISTS (SELECT 1 FROM antibody_crossreference x WHERE x.crossreferences_id = c.id)
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
