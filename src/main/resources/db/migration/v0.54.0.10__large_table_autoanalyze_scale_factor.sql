-- SCRUM-6535: analyse the large shared tables after 1% of their rows change instead of the 10% default.
--
-- slotannotation, note and crossreference hold rows for every entity type, so a new type (cassettes,
-- transgenic tools) can add several hundred thousand rows without reaching the default trigger of
-- 10% of the table, about 1.9M rows on slotannotation. Until autoanalyze runs, the planner has no
-- statistics for the new type and misestimates every lookup on it.
--
-- Seen on the FB cassette reload: with statistics taken while slotannotation held no cassette rows,
-- the planner estimated ~1 CassetteSynonymSlotAnnotation, used the slotannotationtype index and
-- filtered all 78k synonyms per cassette, 15-26 ms per query against 0.7 ms on the
-- singlecassette_id index. Four such lookups per record took the load from ~83 to ~13 records/s.
--
-- At 1% the trigger is about 194k changes on slotannotation, 107k on note and 222k on
-- crossreference. Only analyse is changed; vacuum thresholds keep their defaults. SET on a storage
-- parameter takes a SHARE UPDATE EXCLUSIVE lock, which does not block reads or writes.

ALTER TABLE slotannotation SET (autovacuum_analyze_scale_factor = 0.01);
ALTER TABLE note SET (autovacuum_analyze_scale_factor = 0.01);
ALTER TABLE crossreference SET (autovacuum_analyze_scale_factor = 0.01);
