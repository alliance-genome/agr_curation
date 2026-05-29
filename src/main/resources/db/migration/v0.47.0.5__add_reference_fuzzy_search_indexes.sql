-- Add indexes for reference fuzzy search performance.
-- Prerequisites: pg_trgm extension must be enabled (see v0.43.0.4).
--
-- These indexes support validator/API-client lookup of references from
-- paper-derived strings such as partial titles, short citations, PMIDs, DOIs,
-- MOD reference IDs, and AGRKB reference curies.

CREATE INDEX IF NOT EXISTS reference_shortcitation_trgm_idx
ON public.reference USING gin (UPPER(shortcitation) gin_trgm_ops)
WHERE shortcitation IS NOT NULL;

CREATE INDEX IF NOT EXISTS informationcontententity_curie_trgm_idx
ON public.informationcontententity USING gin (UPPER(curie) gin_trgm_ops)
WHERE curie IS NOT NULL
	AND internal = false
	AND obsolete = false;

CREATE INDEX IF NOT EXISTS crossreference_referencedcurie_trgm_idx
ON public.crossreference USING gin (UPPER(referencedcurie) gin_trgm_ops)
WHERE referencedcurie IS NOT NULL
	AND internal = false
	AND obsolete = false;

CREATE INDEX IF NOT EXISTS crossreference_displayname_trgm_idx
ON public.crossreference USING gin (UPPER(displayname) gin_trgm_ops)
WHERE displayname IS NOT NULL
	AND internal = false
	AND obsolete = false;
