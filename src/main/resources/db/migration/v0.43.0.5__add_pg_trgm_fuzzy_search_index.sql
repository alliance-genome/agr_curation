-- Flyway directive: disable transactions for CREATE INDEX CONCURRENTLY
-- See: https://flywaydb.org/documentation/configuration/parameters/executeInTransaction
-- flyway:noTransaction

-- Prerequisites: pg_trgm extension must be enabled (see v0.43.0.4)

-- Drop existing B-tree index on displaytext
-- Using CONCURRENTLY to make this compatible with non-transactional mode
DROP INDEX CONCURRENTLY IF EXISTS public.slotannotation_displaytext_index;

-- Create GIN index with pg_trgm operator class for fuzzy search performance
-- This improves fuzzy search queries from ~2s to ~20-50ms (50-100x improvement)
-- Using CONCURRENTLY to avoid blocking table operations during index creation
CREATE INDEX CONCURRENTLY slotannotation_displaytext_trgm_idx
ON public.slotannotation USING gin (upper(displaytext) gin_trgm_ops);
