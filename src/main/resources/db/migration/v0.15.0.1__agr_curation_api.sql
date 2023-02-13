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

-- Cleanup queries
DELETE FROM genefullnameslotannotation
WHERE singlegene_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genesymbolslotannotation WHERE singlegene_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genesynonymslotannotation WHERE singlegene_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genesystematicnameslotannotation WHERE singlegene_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM gene WHERE curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelefullnameslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelemutationtypeslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelesecondaryidslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelesymbolslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allelesynonymslotannotation WHERE singleallele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allele_reference WHERE allele_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM allele WHERE curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM affectedgenomicmodel WHERE curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genomicentity_crossreference WHERE genomicentity_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genomicentity_secondaryidentifiers WHERE genomicentity_curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM genomicentity WHERE curie IN (SELECT curie from non_canonical_bio_entities);
	
DELETE FROM biologicalentity
	WHERE curie IN (SELECT curie from non_canonical_bio_entities);
