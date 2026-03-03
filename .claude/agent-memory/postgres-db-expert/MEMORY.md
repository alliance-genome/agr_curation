# PostgreSQL DB Expert - Agent Memory

## Database Configuration (alpha)
- PostgreSQL 15, shared_buffers=2GB, effective_cache_size=10GB, work_mem=64MB, random_page_cost=4
- Binaries: /Applications/Postgres.app/Contents/Versions/15/bin/

## AGM (AffectedGenomicModel) Schema
- Inheritance: biologicalentity -> genomicentity -> affectedgenomicmodel (JOINED strategy)
- Total AGMs: ~791K, Active (non-obsolete, non-internal): ~557K
- Key collections on AGM entity (see [agm-schema.md](agm-schema.md))

## Key Table Sizes
| Table | Rows | Notes |
|-------|------|-------|
| biologicalentity | 27.7M | Root of inheritance hierarchy |
| genomicentity | 27.7M | Intermediate inheritance table |
| affectedgenomicmodel | 791K | AGM leaf table |
| agmalleleassociation | 2.04M | ~2.6 per AGM avg |
| agmphenotypeannotation | 624K | ~0.8 per AGM avg |
| agmdiseaseannotation | 32K | ~0.04 per AGM avg, small enough for seq scan |
| agmsequencetargetingreagentassociation | 24K | ~0.03 per AGM avg |
| slotannotation | 18.4M total, 972K with AGM refs | ~1.2 per AGM avg |
| genomicentity_crossreference | 6.6M | Only ~11 for 10K AGMs (negligible) |
| allelegeneassociation | 2.5M | Indexed on alleleassociationsubject_id |
| sqtrgeneassociation | 152K | Indexed on sequencetargetingreagentassociationsubject_id |
| 253K distinct alleles referenced by AGMs, 10K distinct STRs |

## Key Indexes
- `idx_biologicalentity_covering` - Most heavily used (1.7B scans!), partial on obsolete=false AND internal=false
- `idx_biologicalentity_active` - Also heavily used (16M scans)
- agmalleleassociation_agmassociationsubject_index - Used for batch-fetch
- agmphenotypeannotation_phenotypeannotationsubject_index - Used for batch-fetch
- agmdiseaseannotation_diseaseannotationsubject_index - EXISTS but planner chooses seq scan (32K rows too small)
- agmstrassociation_agmassociationsubject_index - Heavily used (121K scans)
- slotannotation_singleagm_index - Available for batch-fetch

## Performance Notes
- agmdiseaseannotation (32K rows) seq-scans even with IN(100 IDs) - table too small for index to help
- Hibernate batch-fetch-size=100 works well with existing indexes
- The getAllIds query uses parallel workers (4) and idx_biologicalentity_active, runs in ~715ms
- findByIds with 1500 IDs runs in ~197ms using nested loop + index scans
