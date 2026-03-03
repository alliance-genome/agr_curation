# AGM Performance Analysis (Feb 2026)

## Optimizations Applied
1. `quarkus.hibernate-orm.fetch.batch-size=100` (application.properties line 57)
2. ID-batch pattern: GET /model/ids + POST /model/byids
3. @JsonView(CurationView.ModelDocument.class) on endpoint

## Estimated Query Count for 1500 AGMs
- Pre-optimization: ~10,000-50,000+ queries (N+1 everywhere)
- Post-optimization: ~75-170 queries (batched at 100)
- Biggest contributors: 4 top-level OneToMany collections x 15 batches each = 60 queries

## Remaining Optimization Opportunities (ranked)
1. (MEDIUM) @Fetch(FetchMode.SUBSELECT) on top-level collections could reduce 60 -> 4 queries
2. (MEDIUM) Condition relations cascade (annotations -> conditionRelations -> conditions) adds 20-40 queries
3. (LOW) No @Transactional on controller/service findByIds - relies on Quarkus open-session-in-view
4. (INFORMATIONAL) @JsonView only controls serialization, not loading; Gene.geneSymbol lazy load at serialization adds 1-3 queries

## Key Insight
- default_batch_fetch_size applies to ALL lazy associations and proxies globally
- Does NOT help with: (a) collections already fetched eagerly, (b) @Fetch(JOIN) overrides
- JOINED inheritance makes each batch query join 3 tables for Gene/Allele/AGM proxies
- The builder's sequential iteration pattern works well with batch fetching (first access triggers pre-fetch for batch)
