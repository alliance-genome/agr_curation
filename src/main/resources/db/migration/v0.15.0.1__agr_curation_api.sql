DELETE FROM genefullnameslotannotation WHERE singlegene_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM genesymbolslotannotation WHERE singlegene_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM genesynonymslotannotation WHERE singlegene_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM genesystematicnameslotannotation WHERE singlegene_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM gene WHERE curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM allelefullnameslotannotation WHERE singleallele_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM allelemutationtypeslotannotation WHERE singleallele_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM allelesecondaryidslotannotation WHERE singleallele_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM allelesymbolslotannotation WHERE singleallele_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM allelesynonymslotannotation WHERE singleallele_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM allele_reference WHERE allele_curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM allele WHERE curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM affectedgenomicmodel WHERE curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM genomicentity WHERE curie IN (
	SELECT curie FROM biologicalentity
		WHERE taxon_curie NOT IN (
			SELECT n.curie FROM ncbitaxonterm n
				INNER JOIN ontologyterm o ON n.curie = o.curie
				WHERE o.name LIKE 'Danio rerio%'
					OR o.name LIKE 'Caenorhabditis elegans%'
					OR o.name LIKE 'Homo sapiens%'
					OR o.name LIKE 'Saccharomyces cerevisiae%'
					OR o.name LIKE 'Drosophila melanogaster%'
					OR o.name LIKE 'Rattus norvegicus%'
					OR o.name LIKE 'Mus musculus%'
		)
	);
	
DELETE FROM biologicalentity
	WHERE taxon_curie NOT IN (
		SELECT n.curie FROM ncbitaxonterm n
			INNER JOIN ontologyterm o ON n.curie = o.curie
			WHERE o.name LIKE 'Danio rerio%'
				OR o.name LIKE 'Caenorhabditis elegans%'
				OR o.name LIKE 'Homo sapiens%'
				OR o.name LIKE 'Saccharomyces cerevisiae%'
				OR o.name LIKE 'Drosophila melanogaster%'
				OR o.name LIKE 'Rattus norvegicus%'
				OR o.name LIKE 'Mus musculus%'
	);