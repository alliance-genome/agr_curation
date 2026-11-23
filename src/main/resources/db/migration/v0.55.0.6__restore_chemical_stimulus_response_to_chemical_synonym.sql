-- SCRUM-6714: "response to chemical" is a Data Set Category Tags synonym of the "chemical stimulus"
-- term (originally added in v0.35.0.9__add_htp_tags.sql, re-added in v0.37.0.58__htp_tags_fix.sql).
-- It was since removed from alpha outside of a migration (a separate, already-tracked bug), which
-- broke WB's direct-submission HTP dataset loads - WB's files use this synonym, not the canonical
-- name. Restoring it here so those loads can proceed while that separate bug is tracked/fixed.
--
-- Guarded on NOT EXISTS so it is a no-op if the synonym is still present.
INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms)
SELECT id, 'response to chemical'
FROM vocabularyterm
WHERE name = 'chemical stimulus'
AND NOT EXISTS (
	SELECT 1 FROM vocabularyterm_synonyms
	WHERE vocabularyterm_id = vocabularyterm.id
	AND synonyms = 'response to chemical'
);
