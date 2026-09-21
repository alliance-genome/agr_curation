-- SCRUM-6254: Retire SARS-CoV-2 from the persistent store.
--
-- The 28 SARS-CoV-2 genes deliberately stay. Per the ticket discussion they should live in
-- the database the way other non-core species' genes do; what goes is everything that lets
-- SARS-CoV-2 act as an Alliance species with loadable entities of its own. The ~38k
-- molecular interactions involving these genes are a deliberate follow-up.
--
-- Dropping the species row does not orphan the genes: biological entities carry their taxon
-- as ontologyterm.taxon_id and never reference species. Only bulkload.species_id and
-- species_commonnames point at species, and no bulkload is tied to this species row.
--
-- It is also what keeps these genes off the public site. GeneDAO.getAllIds() inner joins
-- species on taxon_id (SCRUM-5853), so a gene whose taxon has no species row is never
-- handed to the indexer and gets no gene page. Obsoleting the FlyBase annotations below
-- drops them from AGMDiseaseAnnotationDAO's indexer query the same way.

-- FlyBase transgenic fly models (P{UAS-SARS-CoV-2-*}) annotated to DOID:0080599
-- "Coronavirus infectious disease", each asserting exactly one SARS-CoV-2 gene. Obsoleted
-- rather than deleted because they are FB-submitted curation; FlyBase has been asked to
-- resubmit them as obsoletes if it keeps sending them. Obsoleting also leaves the asserted
-- gene links intact, so a reload updates these rows instead of creating duplicates.
UPDATE diseaseannotation
SET obsolete = true
WHERE id IN (
	SELECT adag.agmdiseaseannotation_id
	FROM agmdiseaseannotation_gene adag
	JOIN biologicalentity be ON be.id = adag.assertedgenes_id
	JOIN ontologyterm ot ON ot.id = be.taxon_id
	WHERE ot.curie = 'NCBITaxon:2697049'
);

-- Added by v0.33.0.1. Nothing in bulkloadfilehistory, bulkload_dependencies or
-- bulkscheduledload references it, and it is the only load with dataprovider 'SARSCoV2'.
DELETE FROM bulkmanualload
WHERE id IN (SELECT id FROM bulkload WHERE name = 'SARS-CoV-2 Gene Load');

DELETE FROM bulkload
WHERE name = 'SARS-CoV-2 Gene Load';

-- Added by v0.29.0.2, along with its 13 common names.
DELETE FROM species_commonnames
WHERE species_id IN (
	SELECT s.id FROM species s
	JOIN ontologyterm ot ON ot.id = s.taxon_id
	WHERE ot.curie = 'NCBITaxon:2697049'
);

DELETE FROM species
WHERE taxon_id = (SELECT id FROM ontologyterm WHERE curie = 'NCBITaxon:2697049');
