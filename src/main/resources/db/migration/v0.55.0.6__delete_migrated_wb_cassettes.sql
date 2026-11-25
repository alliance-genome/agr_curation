-- SCRUM-6890: delete the WB cassettes made by the v0.54.0.16 construct-to-cassette migration that WB has since
-- submitted under the same identifier.
--
-- v0.54.0.16 gave each construct a cassette named after the construct's identifier plus "_cas". WB constructs
-- carry a primaryExternalId (WB:WBCnstr...), so their migrated cassettes got primaryExternalId WB:WBCnstr..._cas -
-- exactly the identifier WB now submits for its own cassettes, as modInternalId. The WB Construct load therefore
-- created WB's cassettes as second cassettes, and then failed deleting the migrated ones, which are still the
-- object of their construct-cassette association (constructcassetteassoc_object_id_fk). Every such identifier
-- matches two cassettes, so the WB Construct Association load rejects it ("cassette_identifier - Not a valid
-- entry"). On production 37,635 identifiers were affected.
--
-- Deleted: WB cassettes whose primaryExternalId ends in "_cas" that
--   - have no modInternalId and no curie,
--   - are the object of exactly one construct-cassette association, from the construct whose primaryExternalId
--     plus "_cas" is the cassette's primaryExternalId - i.e. exactly what the migration made, and
--   - have a twin: a WB cassette with that same identifier as its modInternalId.
-- So only cassettes that WB's own have replaced are deleted; where the WB Construct load has not run since the
-- migration (no twins), or a migrated cassette has been given a curie, nothing is deleted.
--
-- With each, everything it owns is deleted: its construct-cassette association, its cassette genomic entity /
-- transgenic tool / STR associations and its slot annotations, with their evidence, notes and terms; its own
-- notes, secondary identifiers and references; and the data provider cross reference cloned for it. The
-- constructs, WB's cassettes, and the references and terms that evidence points at are not touched. Anything
-- still in use elsewhere fails the migration on a foreign key rather than being left dangling.
--
-- Afterwards: reindex cassettes and constructs (/api/cassette/reindex, /api/construct/reindex; the construct
-- index embeds its construct-cassette associations), then load the WB Construct Association file again, which
-- recreates the construct-cassette and cassette genomic entity associations on WB's cassettes.

CREATE TEMP TABLE del_cassette ON COMMIT DROP AS
SELECT r.id AS cassette_id,
       r.dataprovidercrossreference_id
FROM cassette c
JOIN reagent r ON r.id = c.id
JOIN organization o ON o.id = r.dataprovider_id
WHERE o.abbreviation = 'WB'
  AND r.primaryexternalid LIKE 'WB:%\_cas'
  AND r.modinternalid IS NULL
  AND r.curie IS NULL
  -- the object of exactly one construct-cassette association, from the construct it was made from
  AND (SELECT count(*) FROM constructcassetteassociation a WHERE a.constructcassetteassociationobject_id = r.id) = 1
  AND EXISTS (
	SELECT 1
	FROM constructcassetteassociation a
	JOIN reagent cr ON cr.id = a.constructassociationsubject_id
	WHERE a.constructcassetteassociationobject_id = r.id
	  AND cr.primaryexternalid || '_cas' = r.primaryexternalid)
  -- replaced by the cassette WB submitted under the same identifier
  AND EXISTS (
	SELECT 1
	FROM reagent t
	JOIN cassette tc ON tc.id = t.id
	WHERE t.modinternalid = r.primaryexternalid
	  AND t.dataprovider_id = r.dataprovider_id
	  AND t.id <> r.id);

CREATE UNIQUE INDEX ON del_cassette (cassette_id);

CREATE TEMP TABLE del_cca ON COMMIT DROP AS
SELECT a.id FROM constructcassetteassociation a JOIN del_cassette d ON d.cassette_id = a.constructcassetteassociationobject_id;

CREATE TEMP TABLE del_gea ON COMMIT DROP AS
SELECT a.id FROM cassettegenomicentityassociation a JOIN del_cassette d ON d.cassette_id = a.cassetteassociationsubject_id;

CREATE TEMP TABLE del_ctt ON COMMIT DROP AS
SELECT a.id FROM cassettetransgenictoolassociation a JOIN del_cassette d ON d.cassette_id = a.cassetteassociationsubject_id;

CREATE TEMP TABLE del_cstr ON COMMIT DROP AS
SELECT a.id FROM cassettestrassociation a JOIN del_cassette d ON d.cassette_id = a.cassetteassociationsubject_id;

CREATE TEMP TABLE del_slot ON COMMIT DROP AS
SELECT s.id FROM slotannotation s JOIN del_cassette d ON d.cassette_id = s.singlecassette_id;

CREATE TEMP TABLE del_note ON COMMIT DROP AS
SELECT relatednotes_id AS id FROM constructcassetteassociation_note WHERE constructcassetteassociation_id IN (SELECT id FROM del_cca)
UNION
SELECT relatednotes_id FROM cassettegenomicentityassociation_note WHERE cassettegenomicentityassociation_id IN (SELECT id FROM del_gea)
UNION
SELECT relatednotes_id FROM cassettetransgenictoolassociation_note WHERE cassettetransgenictoolassociation_id IN (SELECT id FROM del_ctt)
UNION
SELECT relatednotes_id FROM cassettestrassociation_note WHERE cassettestrassociation_id IN (SELECT id FROM del_cstr)
UNION
SELECT relatednotes_id FROM cassettecomponentslotannotation_note WHERE slotannotation_id IN (SELECT id FROM del_slot)
UNION
SELECT relatednotes_id FROM slotannotation_note WHERE slotannotation_id IN (SELECT id FROM del_slot)
UNION
SELECT relatednotes_id FROM reagent_note WHERE submittedobject_id IN (SELECT cassette_id FROM del_cassette);

CREATE TEMP TABLE del_xref ON COMMIT DROP AS
SELECT DISTINCT dataprovidercrossreference_id AS id FROM del_cassette WHERE dataprovidercrossreference_id IS NOT NULL;

-- ---------------------------------------------------------------------------------------------
-- 1. Construct-cassette associations
-- ---------------------------------------------------------------------------------------------

DELETE FROM constructcassetteassociation_reference WHERE association_id IN (SELECT id FROM del_cca);
DELETE FROM constructcassetteassociation_note WHERE constructcassetteassociation_id IN (SELECT id FROM del_cca);
DELETE FROM constructcassetteassociation WHERE id IN (SELECT id FROM del_cca);

-- ---------------------------------------------------------------------------------------------
-- 2. The cassettes' own associations
-- ---------------------------------------------------------------------------------------------

DELETE FROM cassettegenomicentityassociation_reference WHERE association_id IN (SELECT id FROM del_gea);
DELETE FROM cassettegenomicentityassociation_note WHERE cassettegenomicentityassociation_id IN (SELECT id FROM del_gea);
DELETE FROM cassettegenomicentityassociation_soterm WHERE cassettegenomicentityassociation_id IN (SELECT id FROM del_gea);
DELETE FROM cassettegenomicentityassociation WHERE id IN (SELECT id FROM del_gea);

DELETE FROM cassettetransgenictoolassociation_reference WHERE association_id IN (SELECT id FROM del_ctt);
DELETE FROM cassettetransgenictoolassociation_note WHERE cassettetransgenictoolassociation_id IN (SELECT id FROM del_ctt);
DELETE FROM cassettetransgenictoolassociation WHERE id IN (SELECT id FROM del_ctt);

DELETE FROM cassettestrassociation_reference WHERE association_id IN (SELECT id FROM del_cstr);
DELETE FROM cassettestrassociation_note WHERE cassettestrassociation_id IN (SELECT id FROM del_cstr);
DELETE FROM cassettestrassociation WHERE id IN (SELECT id FROM del_cstr);

-- ---------------------------------------------------------------------------------------------
-- 3. Slot annotations
-- ---------------------------------------------------------------------------------------------

DELETE FROM slotannotation_reference WHERE slotannotation_id IN (SELECT id FROM del_slot);
DELETE FROM slotannotation_note WHERE slotannotation_id IN (SELECT id FROM del_slot);
DELETE FROM cassettecomponentslotannotation_note WHERE slotannotation_id IN (SELECT id FROM del_slot);
DELETE FROM cassetteuseslotannotation_ontologyterm WHERE slotannotation_id IN (SELECT id FROM del_slot);
DELETE FROM slotannotation_ontologyterm WHERE slotannotation_id IN (SELECT id FROM del_slot);
DELETE FROM slotannotation_vocabularyterm WHERE slotannotation_id IN (SELECT id FROM del_slot);
DELETE FROM slotannotation WHERE id IN (SELECT id FROM del_slot);

-- ---------------------------------------------------------------------------------------------
-- 4. The cassettes
-- ---------------------------------------------------------------------------------------------

DELETE FROM reagent_note WHERE submittedobject_id IN (SELECT cassette_id FROM del_cassette);
DELETE FROM reagent_secondaryidentifiers WHERE reagent_id IN (SELECT cassette_id FROM del_cassette);
DELETE FROM cassette_reference WHERE cassette_id IN (SELECT cassette_id FROM del_cassette);
DELETE FROM cassette WHERE id IN (SELECT cassette_id FROM del_cassette);
DELETE FROM reagent WHERE id IN (SELECT cassette_id FROM del_cassette);

-- ---------------------------------------------------------------------------------------------
-- 5. Notes and cross references owned by what was deleted. A foreign key fails the migration if one is still
--    used elsewhere.
-- ---------------------------------------------------------------------------------------------

DELETE FROM note_reference WHERE note_id IN (SELECT id FROM del_note);
DELETE FROM note WHERE id IN (SELECT id FROM del_note);
DELETE FROM crossreference WHERE id IN (SELECT id FROM del_xref);
