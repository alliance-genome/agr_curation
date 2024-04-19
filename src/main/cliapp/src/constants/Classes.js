
export const CLASSES = Object.freeze({
  Gene: { name: "Genes", link: "/#/genes", type: 'entity', },
  Allele: { name: "Alleles", link: "/#/alleles", type: 'entity', },
  AffectedGenomicModel: { name: "Affected Genomic Models", link: "/#/agms", type: 'entity', },
  Variant: { name: "Variants", link: "/#/variants", type: "entity", },
  DiseaseAnnotation: { name: "Disease Annotations", link: "/#/diseaseAnnotations", type: 'entity', },
  AGMDiseaseAnnotation: { name: "AGM Disease Annotations", link: "/#/diseaseAnnotations", type: 'entity', },
  AlleleDiseaseAnnotation: { name: "Allele Disease Annotations", link: "/#/diseaseAnnotations", type: 'entity', },
  GeneDiseaseAnnotation: { name: "Gene Disease Annotations", link: "/#/diseaseAnnotations", type: 'entity', },
  PhenotypeAnnotation: { name: "Phenotype Annotations", link: "/#/phenotypeAnnotations", type: 'entity', },
  AGMPhenotypeAnnotation: { name: "AGM Phenotype Annotations", link: "/#/phenotypeAnnotations", type: 'entity', },
  AllelePhenotypeAnnotation: { name: "Allele Phenotype Annotations", link: "/#/phenotypeAnnotations", type: 'entity', },
  GeneGeneticInteraction: { name: "Gene Genetic Interactions", link: "/#/geneGeneticInteractions", type: 'entity', },
  GeneMolecularInteraction: { name: "Gene Molecular Interactions", link: "/#/geneMolecularInteractions", type: 'entity', },
  GenePhenotypeAnnotation: { name: "Gene Phenotype Annotations", link: "/#/phenotypeAnnotations", type: 'entity', },
  ExperimentalCondition: { name: "Experimental Conditions", link: "/#/experimentalConditions", type: 'entity', },
  ConditionRelation: { name: "Condition Relations", link: "/#/conditionRelations", type: 'entity', },
  Construct: { name: "Constructs", link: "/#/constructs", type: 'entity' },
  Molecule: { name: "Molecules", link: "/#/molecules", type: 'entity', },
  Reference: { name: "Literature References", link: "/#/references", type: 'entity', },
  Specie: { name: "Species", link: "/#/species", type: 'entity'},

  DOTerm: { name: "DO", link: "/#/ontology/do", type: 'ontology', },
  CHEBITerm: { name: "CHEBI", link: "/#/ontology/chebi", type: 'ontology', },
  XSMOTerm: { name: "XSMO", link: "/#/ontology/xsmo", type: 'ontology', },
  ECOTerm: { name: "ECO", link: "/#/ontology/eco", type: 'ontology', },
  SOTerm: { name: "SO", link: "/#/ontology/so", type: 'ontology', },
  GOTerm: { name: "GO", link: "/#/ontology/go", type: 'ontology', },
  MATerm: { name: "MA", link: "/#/ontology/ma", type: 'ontology', },
  ZFATerm: { name: "ZFA", link: "/#/ontology/zfa", type: 'ontology', },
  MPTerm: { name: "MP", link: "/#/ontology/mp", type: 'ontology', },
  DAOTerm: { name: "DAO", link: "/#/ontology/dao", type: 'ontology', },
  EMAPATerm: { name: "EMAPA", link: "/#/ontology/emapa", type: 'ontology', },
  WBBTTerm: { name: "WBBT", link: "/#/ontology/wbbt", type: 'ontology', },
  XBATerm: { name: "XBA", link: "/#/ontology/xba", type: 'ontology', },
  XBSTerm: { name: "XBS", link: "/#/ontology/xbs", type: 'ontology', },
  XCOTerm: { name: "XCO", link: "/#/ontology/xco", type: 'ontology', },
  ROTerm: { name: "RO", link: "/#/ontology/ro", type: 'ontology', },
  ZECOTerm: { name: "ZECO", link: "/#/ontology/zeco", type: 'ontology', },
  NCBITaxonTerm: { name: "NCBITaxon", link: "/#/ontology/ncbitaxon", type: 'ontology', },
  WBLSTerm: { name: "WBLS", link: "/#/ontology/wbls", type: 'ontology', },
  FBDVTerm: { name: "FBDV", link: "/#/ontology/fbdv", type: 'ontology', },
  MMUSDVTerm: { name: "MMUSDV", link: "/#/ontology/mmusdv", type: 'ontology', },
  ZFSTerm: { name: "ZFS", link: "/#/ontology/zfs", type: 'ontology', },
  XPOTerm: { name: "XPO", link: "/#/ontology/xpo", type: 'ontology', },
  ATPTerm: { name: "ATP", link: "/#/ontology/atp", type: 'ontology', },
  VTTerm: { name: "VT", link: "/#/ontology/vt", type: 'ontology', },
  XBEDTerm: { name: "XBED", link: "/#/ontology/xbed", type: 'ontology', },
  OBITerm: { name: "OBI", link: "/#/ontology/obi", type: 'ontology', },
  WBPhenotypeTerm: { name: "WBPheno", link: "/#/ontology/wbpheno", type: 'ontology', },
  PATOTerm: { name: "PATO", link: "/#/ontology/pato", type: 'ontology', },
  HPTerm: { name: "HP", link: "/#/ontology/hp", type: 'ontology', },
  DPOTerm: { name: "DPO", link: "/#/ontology/dpo", type: 'ontology', },
  MMOTerm: { name: "MMO", link: "/#/ontology/mmo", type: 'ontology' },
  APOTerm: { name: "APO", link: "/#/ontology/apo", type: 'ontology', },
  MITerm: { name: "MI", link: "/#/ontology/mi", type: 'ontology', },
  MPATHTerm: { name: "MPATH", link: "/#/ontology/mpath", type: 'ontology', },
  MODTerm: { name: "MOD", link: "/#/ontology/mod", type: 'ontology', },
  UBERONTerm: { name: "UBERON", link: "/#/ontology/uberon", type: 'ontology', },
  RSTerm: { name: "RS", link: "/#/ontology/rs", type: 'ontology', },
  PWTerm: { name: "PW", link: "/#/ontology/pw", type: 'ontology', },
  CLTerm: { name: "CL", link: "/#/ontology/cl", type: 'ontology', },
  CMOTerm: { name: "CMO", link: "/#/ontology/cmo", type: 'ontology', },
  BSPOTerm: { name: "BSPO", link: "/#/ontology/bspo", type: 'ontology', },
  GENOTerm: { name: "GENO", link: "/#/ontology/geno", type: 'ontology', },

  CurationReport: { name: "Curation Reports", link: "/#/reports", type: 'system', },
  BulkLoad: { name: "Bulk Loads / Failed Loads", link: "/#/dataloads", type: 'system', },
});

