# Variant Index Document Structure

## Index: variant_site_index_<timestamp>

### Category: sequence_summary (238,314 docs on local)
One document per variant-consequence pair.

```
{
  "category": "sequence_summary",
  "searchable": false,
  "allele": {
    "type": "Allele",
    "taxon": { "curie": "NCBITaxon:...", "name": "..." }
  },
  "variant": {
    "start": <int>,
    "end": <int>,
    "hgvs": "NC_...:g.<pos><ref>><alt>",
    "referenceSequence": "<ref>",
    "variantSequence": "<alt>",
    "nucleotideChange": "<ref>><alt>",
    "variantAssociationSubject": {
      "type": "Variant",
      "taxon": {...},
      "variantType": { "curie": "SO:SNP", "name": "SNP" }
    },
    "variantGenomicLocationAssociationObject": {
      "type": "AssemblyComponent",
      "name": "chr...",
      "genomeAssembly": { "type": "GenomeAssembly", "primaryExternalId": "..." }
    },
    "predictedVariantConsequences": [
      {
        "variantTranscript": {
          "type": "Transcript",
          "curie": "...",
          "transcriptType": { "name": "protein_coding" },
          "transcriptGeneAssociations": [...]
        },
        "vepImpact": { "name": "MODIFIER|LOW|MODERATE|HIGH" },
        "vepConsequences": [ { "name": "intergenic_variant|..." } ],
        "geneLevelConsequence": false
      }
    ],
    "overlapGenes": [ { gene objects with curie, taxon, geneSymbol, genomic locations } ],
    "mostSevereConsequence": { same structure as single consequence },
    "hgvsC": [""],
    "hgvsP": [""]
  },
  "consequence": {  // <-- THIS IS THE KEY DIFFERENCE: single consequence for this row
    "variantTranscript": {...},
    "vepImpact": {...},
    "vepConsequences": [...],
    "geneLevelConsequence": false
  }
}
```

### Category: variant_summary (151,105 docs on local)
One document per variant. Same structure as sequence_summary but:
- NO top-level `consequence` field
- HAS `subCategory` field (observed value: "HTP_variant")
- Some variants may lack `overlapGenes` or have empty `predictedVariantConsequences`

### Species Observed
- Saccharomyces cerevisiae (yeast) - NCBITaxon:559292, assembly R64-2-1

### Notes
- All docs have `searchable: false` -- variant index is not used for site search, only for variant table display
- Ratio of ~1.58 sequence_summary per variant_summary suggests avg ~1.6 consequences per variant
- Both categories carry the full `predictedVariantConsequences` array inside `variant`
