-- SCRUM-6535: migrate the existing construct data of ZFIN, WB and MGI into the cassette model.
--
-- FB is excluded because FlyBase submits its own cassettes.
--
-- One cassette per construct that has components or genomic entity associations. The construct's
-- grouping of its components is information with no other home, so splitting per component would
-- invent structure the data does not contain. A construct already linked to a cassette is skipped,
-- so the script is a no-op where the migration has been run before (e.g. by hand on a local copy).
--
-- Everything is copied; the constructs are left exactly as they were. Their components and genomic
-- entity associations are still read from the construct: by AlleleDAO for the transgenic allele
-- components, by the construct load, which rebuilds them from each submission, and by
-- ConstructValidator for the construct uniqueId. Moving them would empty the first and have the
-- next load put them back. The cassettes are therefore a snapshot of the constructs at the time of
-- the migration; keeping them in step with later construct submissions is left to the loads.
--
-- Nothing the construct keeps is shared with the cassette. Notes and cross references are owned
-- rows with orphan removal on the entity side, so a row linked from both would be deleted by
-- whichever one drops it first, and the other then fails on its foreign key. The data provider
-- cross reference and all notes are therefore cloned, not linked.
--
-- Flyway runs the script in one transaction; the checks in step 8b raise an exception, and so roll
-- everything back, if a component or association is not copied or a row ends up shared. The cassette and
-- construct search indexes need a reindex afterwards (/api/cassette/reindex, /api/construct/reindex).

-- ---------------------------------------------------------------------------------------------
-- 0. Scope
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_construct ON COMMIT DROP AS
SELECT c.id AS construct_id,
       r.primaryexternalid,
       r.modinternalid,
       r.dataprovider_id,
       r.dataprovidercrossreference_id,
       r.internal,
       r.obsolete,
       r.createdby_id,
       r.updatedby_id,
       r.datecreated,
       r.dateupdated
FROM construct c
JOIN reagent r ON r.id = c.id
JOIN organization o ON o.id = r.dataprovider_id
WHERE o.abbreviation IN ('ZFIN', 'WB', 'MGI')
AND NOT EXISTS (SELECT 1 FROM constructcassetteassociation a WHERE a.constructassociationsubject_id = c.id)
AND (EXISTS (SELECT 1 FROM slotannotation s
              WHERE s.singleconstruct_id = c.id AND s.slotannotationtype = 'ConstructComponentSlotAnnotation')
  OR EXISTS (SELECT 1 FROM constructgenomicentityassociation g
              WHERE g.constructassociationsubject_id = c.id));

-- ---------------------------------------------------------------------------------------------
-- 1. Allocate cassette ids
--
-- Ids come from a block past both the sequence position and the highest id in use, rather than
-- nextval per row: reagent_seq increments by 50 for Hibernate's pooled optimizer, so calling it
-- 8,332 times would burn 400,000 values, and reusing values inside a block Hibernate has already
-- handed out would collide. The sequence is advanced past the block at the end.
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_cassette ON COMMIT DROP AS
SELECT m.*,
       (SELECT greatest(max(id), (SELECT last_value FROM reagent_seq)) FROM reagent)
         + 1000 + row_number() OVER (ORDER BY m.construct_id) AS cassette_id
FROM mig_construct m;

-- ---------------------------------------------------------------------------------------------
-- 1b. Data provider cross references: one clone per cassette
--
-- Ids are allocated as a block for the same reason as the cassette ids; crossreference_seq is
-- also pooled by 50.
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_xref ON COMMIT DROP AS
SELECT mc.cassette_id,
       mc.dataprovidercrossreference_id AS source_id,
       (SELECT greatest(max(id), (SELECT last_value FROM crossreference_seq)) FROM crossreference)
         + 1000 + row_number() OVER (ORDER BY mc.cassette_id) AS new_id
FROM mig_cassette mc
WHERE mc.dataprovidercrossreference_id IS NOT NULL;

INSERT INTO crossreference (id, referencedcurie, displayname, resourcedescriptorpage_id, internal, obsolete,
                            createdby_id, updatedby_id, datecreated, dateupdated, dbdatecreated, dbdateupdated)
SELECT mx.new_id, x.referencedcurie, x.displayname, x.resourcedescriptorpage_id,
       coalesce(x.internal, false), coalesce(x.obsolete, false),
       x.createdby_id, x.updatedby_id, x.datecreated, x.dateupdated, now(), now()
FROM mig_xref mx
JOIN crossreference x ON x.id = mx.source_id;

-- ---------------------------------------------------------------------------------------------
-- 2. The cassettes themselves
--
-- primary_external_id follows FlyBase's own convention of suffixing _cas, so a generated id is
-- predictable and, where a MOD later submits cassettes, converges with theirs rather than
-- duplicating. curie stays null: MaTI has no cassette subdomain.
-- ---------------------------------------------------------------------------------------------

INSERT INTO reagent (id, primaryexternalid, modinternalid, dataprovider_id, dataprovidercrossreference_id,
                     internal, obsolete, createdby_id, updatedby_id, datecreated, dateupdated,
                     dbdatecreated, dbdateupdated)
SELECT mc.cassette_id,
       -- Both identifiers are suffixed, not just the public one. A MOD uses one or the other -
       -- MGI's constructs carry only modinternalid, ZFIN/WB/FB only primaryexternalid - so
       -- suffixing only primaryexternalid would leave every MGI cassette with no identifier at
       -- all, while copying modinternalid verbatim collides with the construct on
       -- reagent_modinternalid_uk. Concatenation is null safe, so each stays null where it was.
       mc.primaryexternalid || '_cas',
       mc.modinternalid || '_cas',
       mc.dataprovider_id,
       mx.new_id,
       coalesce(mc.internal, false),
       coalesce(mc.obsolete, false),
       mc.createdby_id, mc.updatedby_id, mc.datecreated, mc.dateupdated,
       now(), now()
FROM mig_cassette mc
LEFT JOIN mig_xref mx ON mx.cassette_id = mc.cassette_id;

INSERT INTO cassette (id) SELECT cassette_id FROM mig_cassette;

-- ---------------------------------------------------------------------------------------------
-- 3. Symbol
--
-- Copied, not moved: the construct keeps its own symbol. A cassette requires one, and the
-- construct's is the only sensible source. Evidence links are copied with it.
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_symbol ON COMMIT DROP AS
SELECT s.id AS source_id,
       mc.cassette_id,
       (SELECT greatest(max(id), (SELECT last_value FROM slotannotation_seq)) FROM slotannotation)
         + 1000 + row_number() OVER (ORDER BY s.id) AS new_id,
       s.displaytext, s.formattext, s.synonymurl, s.nametype_id, s.synonymscope_id,
       s.internal, s.obsolete, s.createdby_id, s.updatedby_id, s.datecreated, s.dateupdated
FROM mig_cassette mc
JOIN slotannotation s ON s.singleconstruct_id = mc.construct_id
                     AND s.slotannotationtype = 'ConstructSymbolSlotAnnotation';

INSERT INTO slotannotation (id, slotannotationtype, singlecassette_id, displaytext, formattext, synonymurl,
                            nametype_id, synonymscope_id, internal, obsolete, createdby_id, updatedby_id,
                            datecreated, dateupdated, dbdatecreated, dbdateupdated)
SELECT new_id, 'CassetteSymbolSlotAnnotation', cassette_id, displaytext, formattext, synonymurl,
       nametype_id, synonymscope_id, coalesce(internal, false), coalesce(obsolete, false),
       createdby_id, updatedby_id, datecreated, dateupdated, now(), now()
FROM mig_symbol;

INSERT INTO slotannotation_informationcontententity (slotannotation_id, evidence_id)
SELECT ms.new_id, si.evidence_id
FROM mig_symbol ms
JOIN slotannotation_informationcontententity si ON si.slotannotation_id = ms.source_id;

-- ---------------------------------------------------------------------------------------------
-- 3b. Full name
--
-- Copied for the same reason as the symbol, and because CassetteUniqueIdHelper folds the full name
-- into the unique id between the symbol and the components. Omitting it collapses constructs that
-- differ only by full name onto one cassette unique id: on WB and MGI that is 38,721 of 55,325
-- constructs, and dropping it doubled the colliding groups from 44 to 83.
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_fullname ON COMMIT DROP AS
SELECT s.id AS source_id,
       mc.cassette_id,
       (SELECT greatest(max(id), (SELECT last_value FROM slotannotation_seq)) FROM slotannotation)
         + 500000 + row_number() OVER (ORDER BY s.id) AS new_id,
       s.displaytext, s.formattext, s.synonymurl, s.nametype_id, s.synonymscope_id,
       s.internal, s.obsolete, s.createdby_id, s.updatedby_id, s.datecreated, s.dateupdated
FROM mig_cassette mc
JOIN slotannotation s ON s.singleconstruct_id = mc.construct_id
                     AND s.slotannotationtype = 'ConstructFullNameSlotAnnotation';

INSERT INTO slotannotation (id, slotannotationtype, singlecassette_id, displaytext, formattext, synonymurl,
                            nametype_id, synonymscope_id, internal, obsolete, createdby_id, updatedby_id,
                            datecreated, dateupdated, dbdatecreated, dbdateupdated)
SELECT new_id, 'CassetteFullNameSlotAnnotation', cassette_id, displaytext, formattext, synonymurl,
       nametype_id, synonymscope_id, coalesce(internal, false), coalesce(obsolete, false),
       createdby_id, updatedby_id, datecreated, dateupdated, now(), now()
FROM mig_fullname;

INSERT INTO slotannotation_informationcontententity (slotannotation_id, evidence_id)
SELECT mf.new_id, si.evidence_id
FROM mig_fullname mf
JOIN slotannotation_informationcontententity si ON si.slotannotation_id = mf.source_id;

-- ---------------------------------------------------------------------------------------------
-- 4. Components: copied, with their evidence and clones of their notes, the relation remapped to
--    cassette_relation
--
-- A component whose relation has no cassette_relation counterpart is not copied rather than copied
-- with a wrong or null relation; step 8b fails the migration if there is any.
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_component ON COMMIT DROP AS
SELECT s.id AS source_id,
       mc.cassette_id,
       (SELECT greatest(max(id), (SELECT last_value FROM slotannotation_seq)) FROM slotannotation)
         + 1000 + row_number() OVER (ORDER BY s.id) AS new_id,
       cr.id AS new_relation_id,
       s.componentsymbol, s.taxon_id, s.taxontext,
       s.internal, s.obsolete, s.createdby_id, s.updatedby_id, s.datecreated, s.dateupdated
FROM mig_cassette mc
JOIN slotannotation s ON s.singleconstruct_id = mc.construct_id
                     AND s.slotannotationtype = 'ConstructComponentSlotAnnotation'
JOIN vocabularyterm old ON old.id = s.relation_id
JOIN vocabulary cv ON cv.vocabularylabel = 'cassette_relation'
JOIN vocabularyterm cr ON cr.vocabulary_id = cv.id AND cr.name = old.name;

INSERT INTO slotannotation (id, slotannotationtype, singlecassette_id, componentsymbol, taxon_id, taxontext,
                            relation_id, internal, obsolete, createdby_id, updatedby_id,
                            datecreated, dateupdated, dbdatecreated, dbdateupdated)
SELECT new_id, 'CassetteComponentSlotAnnotation', cassette_id, componentsymbol, taxon_id, taxontext,
       new_relation_id, coalesce(internal, false), coalesce(obsolete, false), createdby_id, updatedby_id,
       datecreated, dateupdated, now(), now()
FROM mig_component;

INSERT INTO slotannotation_informationcontententity (slotannotation_id, evidence_id)
SELECT mc.new_id, si.evidence_id
FROM mig_component mc
JOIN slotannotation_informationcontententity si ON si.slotannotation_id = mc.source_id;

CREATE TEMP TABLE mig_component_note ON COMMIT DROP AS
SELECT mc.new_id AS component_id,
       sn.relatednotes_id AS source_id,
       (SELECT greatest(max(id), (SELECT last_value FROM note_seq)) FROM note)
         + 1000 + row_number() OVER (ORDER BY mc.new_id, sn.relatednotes_id) AS new_id
FROM mig_component mc
JOIN slotannotation_note sn ON sn.slotannotation_id = mc.source_id;

INSERT INTO note (id, freetext, notetype_id, internal, obsolete,
                  createdby_id, updatedby_id, datecreated, dateupdated, dbdatecreated, dbdateupdated)
SELECT mn.new_id, n.freetext, n.notetype_id, coalesce(n.internal, false), coalesce(n.obsolete, false),
       n.createdby_id, n.updatedby_id, n.datecreated, n.dateupdated, now(), now()
FROM mig_component_note mn
JOIN note n ON n.id = mn.source_id;

INSERT INTO note_reference (note_id, references_id)
SELECT mn.new_id, nr.references_id
FROM mig_component_note mn
JOIN note_reference nr ON nr.note_id = mn.source_id;

INSERT INTO cassettecomponentslotannotation_note (slotannotation_id, relatednotes_id)
SELECT component_id, new_id FROM mig_component_note;

-- ---------------------------------------------------------------------------------------------
-- 5. Genomic entity associations: copied, with their evidence and clones of their notes, the
--    relation remapped to cassette_relation
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_gea ON COMMIT DROP AS
SELECT g.id AS source_id,
       mc.cassette_id,
       nextval('cassettegenomicentityassociation_seq') AS new_id,
       g.constructgenomicentityassociationobject_id AS object_id,
       cr.id AS new_relation_id,
       g.internal, g.obsolete, g.createdby_id, g.updatedby_id, g.datecreated, g.dateupdated
FROM mig_cassette mc
JOIN constructgenomicentityassociation g ON g.constructassociationsubject_id = mc.construct_id
JOIN vocabularyterm old ON old.id = g.relation_id
JOIN vocabulary cv ON cv.vocabularylabel = 'cassette_relation'
JOIN vocabularyterm cr ON cr.vocabulary_id = cv.id AND cr.name = old.name;

INSERT INTO cassettegenomicentityassociation
       (id, cassetteassociationsubject_id, cassettegenomicentityassociationobject_id, relation_id,
        internal, obsolete, createdby_id, updatedby_id, datecreated, dateupdated, dbdatecreated, dbdateupdated)
SELECT new_id, cassette_id, object_id, new_relation_id,
       coalesce(internal, false), coalesce(obsolete, false),
       createdby_id, updatedby_id, datecreated, dateupdated, now(), now()
FROM mig_gea;

CREATE TEMP TABLE mig_gea_note ON COMMIT DROP AS
SELECT mg.new_id AS association_id,
       n.relatednotes_id AS source_id,
       (SELECT greatest(max(id), (SELECT last_value FROM note_seq)) FROM note)
         + 1000 + row_number() OVER (ORDER BY mg.new_id, n.relatednotes_id) AS new_id
FROM mig_gea mg
JOIN constructgenomicentityassociation_note n ON n.constructgenomicentityassociation_id = mg.source_id;

INSERT INTO note (id, freetext, notetype_id, internal, obsolete,
                  createdby_id, updatedby_id, datecreated, dateupdated, dbdatecreated, dbdateupdated)
SELECT mn.new_id, n.freetext, n.notetype_id, coalesce(n.internal, false), coalesce(n.obsolete, false),
       n.createdby_id, n.updatedby_id, n.datecreated, n.dateupdated, now(), now()
FROM mig_gea_note mn
JOIN note n ON n.id = mn.source_id;

INSERT INTO note_reference (note_id, references_id)
SELECT mn.new_id, nr.references_id
FROM mig_gea_note mn
JOIN note_reference nr ON nr.note_id = mn.source_id;

INSERT INTO cassettegenomicentityassociation_note (cassettegenomicentityassociation_id, relatednotes_id)
SELECT association_id, new_id FROM mig_gea_note;

INSERT INTO cassettegenomicentityassociation_informationcontententity (association_id, evidence_id)
SELECT mg.new_id, e.evidence_id
FROM mig_gea mg JOIN constructgenomicentityassociation_informationcontententity e ON e.association_id = mg.source_id;

-- ---------------------------------------------------------------------------------------------
-- 6. Link each construct to its cassette
-- ---------------------------------------------------------------------------------------------

INSERT INTO constructcassetteassociation
       (id, constructassociationsubject_id, constructcassetteassociationobject_id, relation_id,
        internal, obsolete, dbdatecreated, dbdateupdated)
SELECT nextval('constructcassetteassociation_seq'), mc.construct_id, mc.cassette_id, vt.id,
       false, false, now(), now()
FROM mig_cassette mc
JOIN vocabulary v ON v.vocabularylabel = 'construct_cassette_relation'
JOIN vocabularyterm vt ON vt.vocabulary_id = v.id AND vt.name = 'has_component';

-- ---------------------------------------------------------------------------------------------
-- 7. uniqueId
--
-- Must equal what CassetteUniqueIdHelper computes from the stored entity, or the next load and
-- any curator edit will fail to match the row and insert a duplicate. The helper joins with '|',
-- skips blanks, and follows the symbol and full name with each component's symbol, taxon curie and
-- taxon text, sorted.
-- ---------------------------------------------------------------------------------------------

UPDATE reagent r
   SET uniqueid = u.unique_id
FROM (
  -- UniqueIdGeneratorHelper skips blank values but keeps the others untrimmed, and the components
  -- are sorted with Collections.sort, i.e. by code point: hence the CASE rather than btrim, and
  -- COLLATE "C" rather than the database's en_US collation.
  SELECT mc.cassette_id,
         concat_ws('|',
           CASE WHEN btrim(sym.formattext) <> '' THEN sym.formattext END,
           CASE WHEN btrim(fn.formattext) <> '' THEN fn.formattext END,
           (SELECT string_agg(comp_uid, '|' ORDER BY comp_uid COLLATE "C")
              FROM (SELECT concat_ws('|',
                             CASE WHEN btrim(s.componentsymbol) <> '' THEN s.componentsymbol END,
                             CASE WHEN btrim(t.curie) <> '' THEN t.curie END,
                             CASE WHEN btrim(s.taxontext) <> '' THEN s.taxontext END) AS comp_uid
                      FROM slotannotation s
                      LEFT JOIN ontologyterm t ON t.id = s.taxon_id
                     WHERE s.singlecassette_id = mc.cassette_id
                       AND s.slotannotationtype = 'CassetteComponentSlotAnnotation') c)
         ) AS unique_id
  FROM mig_cassette mc
  LEFT JOIN slotannotation sym ON sym.singlecassette_id = mc.cassette_id
                              AND sym.slotannotationtype = 'CassetteSymbolSlotAnnotation'
  LEFT JOIN slotannotation fn ON fn.singlecassette_id = mc.cassette_id
                             AND fn.slotannotationtype = 'CassetteFullNameSlotAnnotation'
) u
WHERE r.id = u.cassette_id;

-- ---------------------------------------------------------------------------------------------
-- 8. Advance the sequences past the ids allocated above
-- ---------------------------------------------------------------------------------------------

SELECT setval('reagent_seq',        (SELECT max(id) + 50 FROM reagent));
SELECT setval('slotannotation_seq', (SELECT max(id) + 50 FROM slotannotation));
SELECT setval('crossreference_seq', (SELECT max(id) + 50 FROM crossreference));
SELECT setval('note_seq',           (SELECT max(id) + 50 FROM note));

-- ---------------------------------------------------------------------------------------------
-- 8b. Verify. Any failure aborts the migration and rolls it back.
-- ---------------------------------------------------------------------------------------------

DO $$
DECLARE
  n_constructs  bigint;
  n_cassettes   bigint;
  n_links       bigint;
  n_comp_source bigint;
  n_comp_copied bigint;
  n_gea_source  bigint;
  n_gea_copied  bigint;
  n_no_uniqueid bigint;
  n_shared_xref bigint;
  n_shared_note bigint;
BEGIN
  SELECT count(*) INTO n_constructs FROM mig_construct;
  SELECT count(*) INTO n_cassettes FROM cassette WHERE id IN (SELECT cassette_id FROM mig_cassette);
  SELECT count(*) INTO n_links FROM constructcassetteassociation
   WHERE constructcassetteassociationobject_id IN (SELECT cassette_id FROM mig_cassette);
  -- components and associations whose relation has no cassette_relation counterpart are not copied
  SELECT count(*) INTO n_comp_source FROM slotannotation
   WHERE slotannotationtype = 'ConstructComponentSlotAnnotation'
     AND singleconstruct_id IN (SELECT construct_id FROM mig_construct);
  SELECT count(*) INTO n_comp_copied FROM mig_component;
  SELECT count(*) INTO n_gea_source FROM constructgenomicentityassociation
   WHERE constructassociationsubject_id IN (SELECT construct_id FROM mig_construct);
  SELECT count(*) INTO n_gea_copied FROM mig_gea;
  SELECT count(*) INTO n_no_uniqueid FROM reagent
   WHERE id IN (SELECT cassette_id FROM mig_cassette) AND (uniqueid IS NULL OR uniqueid = '');
  SELECT count(*) INTO n_shared_xref
    FROM reagent c JOIN reagent o ON o.dataprovidercrossreference_id = c.dataprovidercrossreference_id AND o.id <> c.id
   WHERE c.id IN (SELECT cassette_id FROM mig_cassette);
  SELECT count(*) INTO n_shared_note
    FROM cassettegenomicentityassociation_note cn
    JOIN constructgenomicentityassociation_note gn ON gn.relatednotes_id = cn.relatednotes_id
   WHERE cn.cassettegenomicentityassociation_id IN (SELECT new_id FROM mig_gea);
  SELECT n_shared_note + count(*) INTO n_shared_note
    FROM cassettecomponentslotannotation_note cn
    JOIN slotannotation_note sn ON sn.relatednotes_id = cn.relatednotes_id
   WHERE cn.slotannotation_id IN (SELECT new_id FROM mig_component);

  RAISE NOTICE 'constructs: %, cassettes: %, construct-cassette links: %, components copied: % of %, genomic entity associations copied: % of %',
    n_constructs, n_cassettes, n_links, n_comp_copied, n_comp_source, n_gea_copied, n_gea_source;

  IF n_cassettes <> n_constructs OR n_links <> n_constructs THEN
    RAISE EXCEPTION 'expected % cassettes and links, found % and %', n_constructs, n_cassettes, n_links;
  END IF;
  IF n_comp_copied <> n_comp_source OR n_gea_copied <> n_gea_source THEN
    RAISE EXCEPTION 'relations without a cassette_relation counterpart: % of % components and % of % genomic entity associations copied',
      n_comp_copied, n_comp_source, n_gea_copied, n_gea_source;
  END IF;
  IF n_no_uniqueid > 0 THEN
    RAISE EXCEPTION '% cassettes without a uniqueid', n_no_uniqueid;
  END IF;
  IF n_shared_xref > 0 OR n_shared_note > 0 THEN
    RAISE EXCEPTION 'rows shared with constructs: % cross references, % notes', n_shared_xref, n_shared_note;
  END IF;
END $$;

-- ---------------------------------------------------------------------------------------------
-- 9. Refresh planner statistics
--
-- Autoanalyze waits for 10% of a table to change, which on slotannotation is about two million
-- rows, so after this script it can be weeks before the new columns get statistics. Until then
-- the planner guesses around 100,000 rows for every singlecassette_id lookup. That estimate
-- pushes each foreign key check and each Hibernate collection load past jit_above_cost, so every
-- one of them pays a JIT compile: deleting 230,000 cassettes did not finish in ten minutes, and
-- took about a minute and a half once the table was analysed. Loads suffer the same way.
--
-- ANALYZE is allowed in a transaction and sees the rows this transaction wrote.
-- ---------------------------------------------------------------------------------------------

ANALYZE reagent;
ANALYZE cassette;
ANALYZE crossreference;
ANALYZE slotannotation;
ANALYZE slotannotation_informationcontententity;
ANALYZE slotannotation_note;
ANALYZE cassettecomponentslotannotation_note;
ANALYZE note;
ANALYZE note_reference;
ANALYZE cassettegenomicentityassociation;
ANALYZE cassettegenomicentityassociation_note;
ANALYZE cassettegenomicentityassociation_informationcontententity;
ANALYZE constructcassetteassociation;
