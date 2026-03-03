# AGM Indexer Performance Analysis - Feb 2026

## Current State After Recent Fixes
- Converted from page/limit to ID-batch pattern (correct)
- Added `quarkus.hibernate-orm.fetch.batch-size=100` (good global fix)
- IndexerConfig: 4 threads, bufferSize=1500, bulkActions=1500, concurrentRequests=8, runInParallel=true

## Remaining Issues (Ranked by Severity)

### HIGH: Deep entity graph causes cascading lazy loads in builder
- ModelDocumentBuilder.getAssociatedGenes() traverses 3-4 levels deep
- AGM -> agmSequenceTargetingReagentAssociations -> STR -> sequenceTargetingReagentGeneAssociations -> Gene
- AGM -> components (AgmAlleleAssociation) -> Allele -> alleleGeneAssociations -> Gene
- Each level is a separate lazy collection load, batch-fetch-size=100 helps but doesn't eliminate

### HIGH: DiseaseAnnotation/PhenotypeAnnotation conditionRelations are lazy ManyToMany
- Each annotation's conditionRelations -> conditions -> ExperimentalCondition chain
- conditionRelationType is a ManyToOne (loaded individually per relation)
- getUniqueExperimentConditionId() forces load of conditions + conditionRelationType per relation

### MEDIUM: findByIds JPQL query returns bare entities with no JOIN FETCH
- All collections are LAZY by default on OneToMany/ManyToMany
- The global batch-fetch-size=100 batches lazy loads but still issues N/100 queries per collection type

### LOW: 4 concurrent threads hitting the same curation API instance
- Each thread sends 1500 IDs per batch - reasonable
- But all 4 threads in parallel could mean 4 * complex-query simultaneously
