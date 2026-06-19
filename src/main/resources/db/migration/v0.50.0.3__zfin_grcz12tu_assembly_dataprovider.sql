-- SCRUM-6205: set the ZFIN data provider on the GRCz12tu genome assembly.
--
-- v0.50.0.2 created the GRCz12tu GenomeAssembly without a dataprovider.
-- GenomeAssemblyService.getOrCreate() looks the assembly up by
-- primaryExternalId + dataProvider + taxon during the GFF load, so a null
-- dataprovider means it cannot find this row, tries to insert a second
-- 'GRCz12tu', and fails the load on the biologicalentity primaryexternalid
-- unique constraint (which in turn poisons the per-load assembly-component
-- cache and aborts every gene location on the new chromosomes).
--
-- Mirror GRCz11, whose assembly carries the ZFIN organization as its
-- dataprovider. Idempotent and env-agnostic (resolved by abbreviation).
UPDATE biologicalentity
SET dataprovider_id = (SELECT id FROM organization WHERE abbreviation = 'ZFIN')
WHERE primaryexternalid = 'GRCz12tu'
  AND dataprovider_id IS NULL;
