# Elasticsearch Expert Agent Memory

## Index Inventory (Local - localhost:9200)
- **variant_site_index_1771534073904**: 389,419 docs, 124.8 MB, 4 pri / 0 rep, green
  - `sequence_summary`: 238,314 docs (one per variant-consequence pair)
  - `variant_summary`: 151,105 docs (one per variant, has `subCategory` field)
- Index naming pattern: `variant_site_index_<timestamp>` (numeric suffix is a timestamp-based unique ID)

## Variant Index Document Structure
- See [variant-index-structure.md](variant-index-structure.md) for detailed field mapping notes

### Key Differences Between Categories
| Field | sequence_summary | variant_summary |
|---|---|---|
| Top-level `consequence` | Yes (single) | No |
| `subCategory` | No | Yes ("HTP_variant") |
| Granularity | per variant-consequence | per variant |
| `searchable` | false | false |

### Common Fields (Both Categories)
- `category`, `searchable`, `allele` (with taxon), `variant` (main payload)
- `variant` contains: `start`, `end`, `hgvs`, `referenceSequence`, `variantSequence`, `nucleotideChange`, `variantType`, genomic location, `predictedVariantConsequences[]`, `overlapGenes[]`, `mostSevereConsequence`, `hgvsC[]`, `hgvsP[]`

## Detailed Notes
- [variant-index-structure.md](variant-index-structure.md) - Full variant index document structure
