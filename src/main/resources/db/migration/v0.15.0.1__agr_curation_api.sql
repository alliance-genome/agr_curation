-- Select IDs to clean up
CREATE TEMPORARY TABLE canonical_taxons AS
	SELECT n.curie
	FROM ncbitaxonterm n
	INNER JOIN ontologyterm o ON n.curie = o.curie
	WHERE o.name LIKE 'Danio rerio%'
		OR o.name LIKE 'Caenorhabditis elegans%'
		OR o.name LIKE 'Homo sapiens%'
		OR o.name LIKE 'Saccharomyces cerevisiae%'
		OR o.name LIKE 'Drosophila melanogaster%'
		OR o.name LIKE 'Rattus norvegicus%'
		OR o.name LIKE 'Mus musculus%';

CREATE TEMPORARY TABLE non_canonical_bio_entities AS
	SELECT curie FROM biologicalentity
	WHERE taxon_curie NOT IN (SELECT * from canonical_taxons);

CREATE TEMPORARY TABLE non_canonical_genes AS
	SELECT g.curie FROM non_canonical_bio_entities nc
	JOIN gene g ON g.curie = nc.curie;

CREATE TEMPORARY TABLE non_canonical_alleles AS
	SELECT a.curie FROM non_canonical_bio_entities nc
	JOIN allele a ON a.curie = nc.curie;

CREATE TEMPORARY TABLE non_canonical_agms AS
	SELECT agm.curie FROM non_canonical_bio_entities nc
	JOIN affectedgenomicmodel agm ON agm.curie = nc.curie;

CREATE TEMPORARY TABLE non_canonical_alleledas AS
	SELECT id FROM allelediseaseannotation da
	JOIN non_canonical_alleles nc ON nc.curie = da.subject_curie
	UNION
	SELECT id FROM allelediseaseannotation da
	JOIN non_canonical_genes nc ON nc.curie = inferredgene_curie;

CREATE TEMPORARY TABLE non_canonical_genedas AS
	SELECT da.id FROM genediseaseannotation da
	JOIN non_canonical_genes nc ON nc.curie = da.subject_curie
	UNION
	SELECT da.id FROM genediseaseannotation da
	JOIN non_canonical_agms nc ON nc.curie = da.sgdstrainbackground_curie;

CREATE TEMPORARY TABLE non_canonical_agmdas AS
	SELECT da.id FROM agmdiseaseannotation da
	JOIN non_canonical_genes nc ON nc.curie = da.inferredgene_curie
	UNION
	SELECT da.id FROM agmdiseaseannotation da
	JOIN non_canonical_alleles nc ON nc.curie = da.inferredallele_curie
	UNION
	SELECT da.id FROM agmdiseaseannotation da
	JOIN non_canonical_alleles nc ON nc.curie = da.assertedallele_curie;

-- Cleanup queries

--  * For Disease annotations
DELETE FROM allelediseaseannotation_gene
WHERE allelediseaseannotation_id IN (SELECT id FROM non_canonical_alleledas);

DELETE FROM allelediseaseannotation
WHERE id IN (SELECT id FROM non_canonical_alleledas);

DELETE FROM genediseaseannotation
WHERE id IN (SELECT id FROM non_canonical_genedas);

DELETE FROM agmdiseaseannotation_gene
WHERE agmdiseaseannotation_id IN (SELECT id FROM non_canonical_agmdas);

DELETE FROM agmdiseaseannotation
WHERE id IN (SELECT id FROM non_canonical_agmdas);

DELETE FROM diseaseannotation_conditionrelation
WHERE diseaseannotation_id IN (SELECT id FROM non_canonical_alleledas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_agmdas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_genedas);

DELETE FROM diseaseannotation_ecoterm
WHERE diseaseannotation_id IN (SELECT id FROM non_canonical_alleledas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_agmdas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_genedas);

DELETE FROM diseaseannotation_gene
WHERE diseaseannotation_id IN (SELECT id FROM non_canonical_alleledas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_agmdas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_genedas);

DELETE FROM diseaseannotation_note
WHERE diseaseannotation_id IN (SELECT id FROM non_canonical_alleledas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_agmdas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_genedas);

DELETE FROM diseaseannotation_vocabularyterm
WHERE diseaseannotation_id IN (SELECT id FROM non_canonical_alleledas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_agmdas)
 OR diseaseannotation_id IN (SELECT id FROM non_canonical_genedas);

DELETE FROM diseaseannotation
WHERE id IN (SELECT id FROM non_canonical_alleledas)
 OR id IN (SELECT id FROM non_canonical_agmdas)
 OR id IN (SELECT id FROM non_canonical_genedas);

--  * For slot annotations
DELETE FROM genefullnameslotannotation
WHERE singlegene_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genesymbolslotannotation WHERE singlegene_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genesynonymslotannotation WHERE singlegene_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genesystematicnameslotannotation WHERE singlegene_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelefullnameslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelemutationtypeslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelesecondaryidslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelesymbolslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelesynonymslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);

--  * For biological entities
DELETE FROM genomicentity_crossreference WHERE genomicentity_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genomicentity_secondaryidentifiers WHERE genomicentity_curie IN (SELECT curie from non_canonical_bio_entities);

DELETE FROM gene WHERE curie IN (SELECT curie from non_canonical_bio_entities);

DELETE FROM allele_reference WHERE allele_curie IN (SELECT curie from non_canonical_bio_entities);

DELETE FROM allele WHERE curie IN (SELECT curie from non_canonical_bio_entities);

DELETE FROM affectedgenomicmodel WHERE curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genomicentity WHERE curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM biologicalentity
	WHERE curie IN (SELECT curie from non_canonical_bio_entities);
