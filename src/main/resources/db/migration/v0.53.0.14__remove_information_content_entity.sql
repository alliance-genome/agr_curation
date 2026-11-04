-- SCRUM-6720: remove InformationContentEntity and ExternalDatabaseReference (LinkML schema PR
-- agr_curation_schema#345). Reference becomes a root entity whose table holds the curie and the audit
-- columns, and every evidence foreign key points at reference.
--
-- The external database references are the MIM and ORPHA ids that RGD's human phenotype file gives as
-- evidence, and phenotype annotations are the only rows that cite them. Each such annotation gets the
-- id as its own cross reference (crossreference_id, unused until now) and, as its evidence item, the
-- generic ABC reference AGRKB:101000000829523. The cross reference uses the resource's disease page
-- where it has one (MIM), else the page the external database reference had (Orphanet default), as
-- the load does. The uniqueId, which embeds the MIM or ORPHA id, is left as it is, so the load still
-- finds the annotation.
--
-- The *_informationcontententity join tables of EvidenceAssociation and SlotAnnotation become
-- *_reference, the names Hibernate derives once evidence is a list of Reference, and
-- informationcontententity_seq becomes reference_seq, the default sequence of the new root entity.
-- The foreign keys and join tables are read from the catalog, because alpha, which runs this out of
-- order after its 0.54 migrations, has cassette tables that production does not have yet.
--
-- Flyway runs the script in one transaction; the checks raise an exception, and so roll everything
-- back, on an inconsistency. The gene phenotype annotation search index needs a reindex afterwards
-- (/api/gene-phenotype-annotation/reindex).

-- ---------------------------------------------------------------------------------------------
-- 0. Checks
-- ---------------------------------------------------------------------------------------------

DO $$
DECLARE
	con record;
	n bigint;
BEGIN
	SELECT count(*) INTO n
	FROM informationcontententity ice
	WHERE NOT EXISTS (SELECT 1 FROM reference r WHERE r.id = ice.id)
		AND NOT EXISTS (SELECT 1 FROM externaldatabasereference e WHERE e.id = ice.id);
	IF n > 0 THEN
		RAISE EXCEPTION '% informationcontententity rows are neither a reference nor an external database reference', n;
	END IF;

	FOR con IN
		SELECT c.conrelid::regclass AS tbl, a.attname AS col
		FROM pg_constraint c
		JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = c.conkey[1]
		WHERE c.contype = 'f'
			AND c.confrelid = 'informationcontententity'::regclass
			AND c.conrelid NOT IN ('reference'::regclass, 'externaldatabasereference'::regclass)
			AND NOT (c.conrelid = 'phenotypeannotation'::regclass AND a.attname = 'evidenceitem_id')
	LOOP
		EXECUTE format('SELECT count(*) FROM %s t JOIN externaldatabasereference e ON e.id = t.%I', con.tbl, con.col) INTO n;
		IF n > 0 THEN
			RAISE EXCEPTION '%.% cites % external database references; only phenotype annotations are converted', con.tbl, con.col, n;
		END IF;
	END LOOP;

	SELECT count(*) INTO n
	FROM phenotypeannotation pa
	JOIN externaldatabasereference e ON e.id = pa.evidenceitem_id
	WHERE pa.crossreference_id IS NOT NULL;
	IF n > 0 THEN
		RAISE EXCEPTION '% phenotype annotations citing an external database reference already have a cross reference', n;
	END IF;
END $$;

-- ---------------------------------------------------------------------------------------------
-- 1. Phenotype annotations citing an external database reference
--
-- crossReference is a one-to-one with orphan removal, so every annotation gets its own row. Ids are
-- allocated as a block above the sequence, which is pooled by 50, and the sequence is then moved past them.
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_xref ON COMMIT DROP AS
SELECT pa.id AS annotation_id,
       ice.curie,
       coalesce(dp.id, e.resourcedescriptorpage_id) AS page_id,
       (SELECT greatest(max(id), (SELECT last_value FROM crossreference_seq)) FROM crossreference)
         + 1000 + row_number() OVER (ORDER BY pa.id) AS new_id
FROM phenotypeannotation pa
JOIN externaldatabasereference e ON e.id = pa.evidenceitem_id
JOIN informationcontententity ice ON ice.id = e.id
LEFT JOIN resourcedescriptorpage ep ON ep.id = e.resourcedescriptorpage_id
LEFT JOIN resourcedescriptorpage dp ON dp.resourcedescriptor_id = ep.resourcedescriptor_id AND dp.name = 'disease';

DO $$
DECLARE
	n bigint;
BEGIN
	SELECT count(*) - count(DISTINCT annotation_id) INTO n FROM mig_xref;
	IF n > 0 THEN
		RAISE EXCEPTION '% phenotype annotations matched more than one disease page', n;
	END IF;

	IF EXISTS (SELECT 1 FROM mig_xref)
		AND NOT EXISTS (SELECT 1 FROM reference r JOIN informationcontententity ice ON ice.id = r.id WHERE ice.curie = 'AGRKB:101000000829523') THEN
		RAISE EXCEPTION 'reference AGRKB:101000000829523 is not in the reference table';
	END IF;
END $$;

INSERT INTO crossreference (id, referencedcurie, displayname, resourcedescriptorpage_id, internal, obsolete,
                            dbdatecreated, dbdateupdated)
SELECT m.new_id, m.curie, m.curie, m.page_id, false, false, now(), now()
FROM mig_xref m;

UPDATE phenotypeannotation pa
SET crossreference_id = m.new_id,
    evidenceitem_id = (SELECT ice.id FROM informationcontententity ice WHERE ice.curie = 'AGRKB:101000000829523'),
    dbdateupdated = now()
FROM mig_xref m
WHERE pa.id = m.annotation_id;

SELECT setval('crossreference_seq', (SELECT max(id) + 50 FROM crossreference));

-- ---------------------------------------------------------------------------------------------
-- 2. Foreign keys into informationcontententity
--
-- The evidence ones are re-created on reference in step 5. reference.id and externaldatabasereference.id
-- are the subclass keys, and genegla_ice_evidence_id_fk (v0.38.0.3) points
-- genegenomiclocationassociation_informationcontententity.association_id at the wrong table; the
-- table already has the right association foreign key, so that one is not re-created.
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_fk ON COMMIT DROP AS
SELECT c.conname::text AS conname, c.conrelid::regclass::text AS tbl, a.attname::text AS col
FROM pg_constraint c
JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = c.conkey[1]
WHERE c.contype = 'f' AND c.confrelid = 'informationcontententity'::regclass;

DO $$
DECLARE
	fk record;
BEGIN
	FOR fk IN SELECT * FROM mig_fk ORDER BY tbl, conname LOOP
		IF fk.col NOT IN ('evidence_id', 'evidenceitem_id')
			AND NOT (fk.tbl IN ('reference', 'externaldatabasereference') AND fk.col = 'id')
			AND NOT (fk.tbl = 'genegenomiclocationassociation_informationcontententity' AND fk.col = 'association_id') THEN
			RAISE EXCEPTION 'unexpected foreign key % on %.% into informationcontententity', fk.conname, fk.tbl, fk.col;
		END IF;
		EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', fk.tbl, fk.conname);
	END LOOP;
END $$;

-- ---------------------------------------------------------------------------------------------
-- 3. External database references
-- ---------------------------------------------------------------------------------------------

CREATE TEMP TABLE mig_edr ON COMMIT DROP AS
SELECT id FROM externaldatabasereference;

DROP TABLE externaldatabasereference;

DELETE FROM informationcontententity ice USING mig_edr e WHERE ice.id = e.id;

-- ---------------------------------------------------------------------------------------------
-- 4. The curie and audit columns move onto reference
-- ---------------------------------------------------------------------------------------------

ALTER TABLE reference
	ADD COLUMN curie varchar(255),
	ADD COLUMN datecreated timestamp without time zone,
	ADD COLUMN dateupdated timestamp without time zone,
	ADD COLUMN dbdatecreated timestamp without time zone,
	ADD COLUMN dbdateupdated timestamp without time zone,
	ADD COLUMN internal boolean DEFAULT false,
	ADD COLUMN obsolete boolean DEFAULT false,
	ADD COLUMN createdby_id bigint,
	ADD COLUMN updatedby_id bigint;

UPDATE reference r
SET curie = ice.curie,
    datecreated = ice.datecreated,
    dateupdated = ice.dateupdated,
    dbdatecreated = ice.dbdatecreated,
    dbdateupdated = ice.dbdateupdated,
    internal = ice.internal,
    obsolete = ice.obsolete,
    createdby_id = ice.createdby_id,
    updatedby_id = ice.updatedby_id
FROM informationcontententity ice
WHERE ice.id = r.id;

ALTER TABLE reference ALTER COLUMN curie SET NOT NULL;
ALTER TABLE reference ADD CONSTRAINT reference_curie_uk UNIQUE (curie);
ALTER TABLE reference ADD CONSTRAINT reference_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE reference ADD CONSTRAINT reference_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);

CREATE INDEX reference_curie_index ON reference USING btree (curie);
CREATE INDEX reference_createdby_index ON reference USING btree (createdby_id);
CREATE INDEX reference_updatedby_index ON reference USING btree (updatedby_id);
CREATE INDEX reference_curie_trgm_idx
ON reference USING gin (UPPER(curie) gin_trgm_ops)
WHERE curie IS NOT NULL
	AND internal = false
	AND obsolete = false;

-- ---------------------------------------------------------------------------------------------
-- 5. Evidence foreign keys onto reference
-- ---------------------------------------------------------------------------------------------

DO $$
DECLARE
	fk record;
BEGIN
	FOR fk IN SELECT * FROM mig_fk WHERE col IN ('evidence_id', 'evidenceitem_id') ORDER BY tbl, conname LOOP
		EXECUTE format('ALTER TABLE %s ADD CONSTRAINT %I FOREIGN KEY (%I) REFERENCES reference (id)',
			fk.tbl, replace(replace(fk.conname, '_informationcontententity_', '_reference_'), '_ice_', '_reference_'), fk.col);
	END LOOP;
END $$;

-- ---------------------------------------------------------------------------------------------
-- 6. Join tables: <x>_informationcontententity becomes <x>_reference
--
-- Only the two indexes SlotAnnotation names in Java are renamed; the others keep their names.
-- ---------------------------------------------------------------------------------------------

DO $$
DECLARE
	jt record;
BEGIN
	FOR jt IN
		SELECT c.relname
		FROM pg_class c
		WHERE c.relkind = 'r'
			AND c.relnamespace = 'public'::regnamespace
			AND c.relname LIKE '%\_informationcontententity'
		ORDER BY c.relname
	LOOP
		EXECUTE format('ALTER TABLE %I RENAME TO %I', jt.relname, left(jt.relname, -length('_informationcontententity')) || '_reference');
	END LOOP;
END $$;

ALTER INDEX IF EXISTS slotannotation_informationcontententity_slotannotation_index RENAME TO slotannotation_reference_slotannotation_index;
ALTER INDEX IF EXISTS slotannotation_informationcontententity_evidence_index RENAME TO slotannotation_reference_evidence_index;

-- ---------------------------------------------------------------------------------------------
-- 7. informationcontententity goes; its sequence keeps its position as reference_seq
-- ---------------------------------------------------------------------------------------------

ALTER SEQUENCE informationcontententity_seq RENAME TO reference_seq;

DROP TABLE informationcontententity;

-- ---------------------------------------------------------------------------------------------
-- 8. Verify
-- ---------------------------------------------------------------------------------------------

DO $$
DECLARE
	n bigint;
BEGIN
	SELECT count(*) INTO n
	FROM pg_constraint c
	JOIN pg_attribute a ON a.attrelid = c.conrelid AND a.attnum = c.conkey[1]
	WHERE c.contype = 'f' AND c.confrelid = 'reference'::regclass AND a.attname IN ('evidence_id', 'evidenceitem_id');
	IF n <> (SELECT count(*) FROM mig_fk WHERE col IN ('evidence_id', 'evidenceitem_id')) THEN
		RAISE EXCEPTION '% evidence foreign keys on reference, expected %', n, (SELECT count(*) FROM mig_fk WHERE col IN ('evidence_id', 'evidenceitem_id'));
	END IF;

	SELECT count(*) INTO n
	FROM pg_class c
	WHERE c.relnamespace = 'public'::regnamespace AND c.relname LIKE '%\_informationcontententity';
	IF n > 0 THEN
		RAISE EXCEPTION '% tables are still named *_informationcontententity', n;
	END IF;

	SELECT count(*) INTO n
	FROM phenotypeannotation pa
	JOIN mig_xref m ON m.annotation_id = pa.id
	WHERE pa.crossreference_id IS DISTINCT FROM m.new_id OR pa.evidenceitem_id IS NULL;
	IF n > 0 THEN
		RAISE EXCEPTION '% converted phenotype annotations lack their cross reference or evidence item', n;
	END IF;
END $$;

ANALYZE reference;
ANALYZE phenotypeannotation;
ANALYZE crossreference;
