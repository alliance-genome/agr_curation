-- SCRUM-6565: Replace the two flat antibody_host_taxon/antibody_antigen_taxon vocabularies
-- with a single unified antibody_taxon vocabulary, plus two VocabularyTermSets (subsets)
-- restricting which of its terms apply in the host vs antigen context. The two old
-- vocabularies overlap heavily (mouse, rat, chicken, etc. appear -- always with identical
-- name/definition -- in both), which is the redundancy this ticket removes.
--
-- The term-set labels reuse the old vocabulary label strings (antibody_host_taxon,
-- antibody_antigen_taxon) since those old vocabulary rows are deleted later in this same
-- migration and vocabularytermset.vocabularylabel is a separate table/constraint -- no
-- collision, and it keeps the existing VocabularyConstants string values stable.

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Antibody taxon', 'antibody_taxon');

-- Non-redundant union: every name appearing in either old vocabulary has an identical
-- definition in both where it appears in both (verified against the source migration),
-- so a plain DISTINCT is safe.
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id)
SELECT nextval('vocabularyterm_seq'), dedup.name, dedup.definition, nv.id
FROM (
	SELECT DISTINCT vt.name, vt.definition
	FROM vocabularyterm vt
	JOIN vocabulary v ON v.id = vt.vocabulary_id
	WHERE v.vocabularylabel IN ('antibody_host_taxon', 'antibody_antigen_taxon')
) dedup
JOIN vocabulary nv ON nv.vocabularylabel = 'antibody_taxon';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Antibody host taxon', 'antibody_host_taxon', id, 'Taxon terms applicable to the host species of an antibody'
	FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Antibody antigen taxon', 'antibody_antigen_taxon', id, 'Taxon terms applicable to the antigen source species of an antibody'
	FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
SELECT (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'antibody_host_taxon'), newvt.id
FROM vocabularyterm oldvt
JOIN vocabulary oldv ON oldv.id = oldvt.vocabulary_id AND oldv.vocabularylabel = 'antibody_host_taxon'
JOIN vocabularyterm newvt ON newvt.name = oldvt.name
JOIN vocabulary newv ON newv.id = newvt.vocabulary_id AND newv.vocabularylabel = 'antibody_taxon';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
SELECT (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'antibody_antigen_taxon'), newvt.id
FROM vocabularyterm oldvt
JOIN vocabulary oldv ON oldv.id = oldvt.vocabulary_id AND oldv.vocabularylabel = 'antibody_antigen_taxon'
JOIN vocabularyterm newvt ON newvt.name = oldvt.name
JOIN vocabulary newv ON newv.id = newvt.vocabulary_id AND newv.vocabularylabel = 'antibody_taxon';

-- Backfill Antibody's FKs from the old vocab terms to the new unified vocab's terms
-- (matching by name, which is guaranteed to exist in the new vocab by construction).
UPDATE antibody a
SET hosttaxonterm_id = newvt.id
FROM vocabularyterm oldvt
JOIN vocabularyterm newvt ON newvt.name = oldvt.name
JOIN vocabulary newv ON newv.id = newvt.vocabulary_id AND newv.vocabularylabel = 'antibody_taxon'
WHERE a.hosttaxonterm_id = oldvt.id;

UPDATE antibody a
SET antigentaxonterm_id = newvt.id
FROM vocabularyterm oldvt
JOIN vocabularyterm newvt ON newvt.name = oldvt.name
JOIN vocabulary newv ON newv.id = newvt.vocabulary_id AND newv.vocabularylabel = 'antibody_taxon'
WHERE a.antigentaxonterm_id = oldvt.id;

-- Fail loudly rather than silently losing data: by construction every old-vocab term
-- name exists in the new unified vocab, so this should never fire -- but the DELETEs
-- below are irreversible once run, so keep the same safety net used throughout this
-- migration series.
DO $$
DECLARE unmapped bigint;
BEGIN
	SELECT count(*) INTO unmapped FROM antibody a
	WHERE (a.hosttaxonterm_id IS NOT NULL AND a.hosttaxonterm_id NOT IN (
		SELECT vt.id FROM vocabularyterm vt JOIN vocabulary v ON v.id = vt.vocabulary_id WHERE v.vocabularylabel = 'antibody_taxon'
	))
	OR (a.antigentaxonterm_id IS NOT NULL AND a.antigentaxonterm_id NOT IN (
		SELECT vt.id FROM vocabularyterm vt JOIN vocabulary v ON v.id = vt.vocabulary_id WHERE v.vocabularylabel = 'antibody_taxon'
	));
	IF unmapped > 0 THEN
		RAISE EXCEPTION 'v0.53.0.13: % antibody rows have a host/antigen taxon term not present in the unified antibody_taxon vocabulary', unmapped;
	END IF;
END $$;

DELETE FROM vocabularyterm WHERE vocabulary_id IN (
	SELECT id FROM vocabulary WHERE vocabularylabel IN ('antibody_host_taxon', 'antibody_antigen_taxon')
);
DELETE FROM vocabulary WHERE vocabularylabel IN ('antibody_host_taxon', 'antibody_antigen_taxon');
