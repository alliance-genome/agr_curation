DELETE FROM agmphenotypeannotation where assertedallele_id IS NOT NULL;
DELETE FROM agmphenotypeannotation where inferredallele_id IS NOT NULL;
DELETE FROM agmphenotypeannotation where inferredgene_id IS NOT NULL;
DELETE FROM agmphenotypeannotation_gene;

DELETE FROM allelephenotypeannotation where inferredgene_id IS NOT NULL;
DELETE FROM allelephenotypeannotation_gene;