
SELECT setval( 'agmdiseaseannotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'AGMPhenotypeAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'AlleleDiseaseAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'AlleleGeneAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'AllelePhenotypeAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'CodingSequenceGenomicLocationAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'ConstructGenomicEntityAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'ExonGenomicLocationAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) ); 
SELECT setval( 'GeneDiseaseAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'GeneGeneticInteraction_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'GeneMolecularInteraction_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'GenePhenotypeAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'SequenceTargetingReagentGeneAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'TranscriptCodingSequenceAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'TranscriptExonAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'TranscriptGeneAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
SELECT setval( 'TranscriptGenomicLocationAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );
