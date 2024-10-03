
SELECT setval( 'GeneExpressionAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM GeneExpressionAnnotation) );

