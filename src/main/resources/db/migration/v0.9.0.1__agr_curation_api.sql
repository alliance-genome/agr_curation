DROP TABLE allele_genegenomiclocation;

DROP TABLE allele_genegenomiclocation_aud;

DROP TABLE gene_genegenomiclocation;

DROP TABLE gene_genegenomiclocation_aud;

DROP TABLE genegenomiclocation;

DROP TABLE genegenomiclocation_aud;

ALTER TABLE bulkload
	RENAME COLUMN status TO bulkloadstatus;

ALTER TABLE bulkload_aud
	RENAME COLUMN status TO bulkloadstatus;

ALTER TABLE bulkloadfile
	RENAME COLUMN status TO bulkloadstatus;

ALTER TABLE bulkloadfile_aud
	RENAME COLUMN status TO bulkloadstatus;
	
ALTER TABLE bulkfmsload
	RENAME COLUMN datatype TO fmsdatatype;

ALTER TABLE bulkfmsload_aud
	RENAME COLUMN datatype TO fmsdatatype;
	
ALTER TABLE bulkfmsload
	RENAME COLUMN datasubtype TO fmsdatasubtype;

ALTER TABLE bulkfmsload_aud
	RENAME COLUMN datasubtype TO fmsdatasubtype;
	
ALTER TABLE bulkurlload
	RENAME COLUMN url to bulkloadurl;
	
ALTER TABLE bulkurlload_aud
	RENAME COLUMN url to bulkloadurl;
	
ALTER TABLE gene
	DROP COLUMN automatedgenedescription,
	DROP COLUMN genesynopsis,
	DROP COLUMN genesynopsisurl;
	
INSERT INTO phenotypeterm
	SELECT curie FROM mpterm;

ALTER TABLE synonym ALTER COLUMN name TYPE varchar(2000);

ALTER TABLE synonym_aud ALTER COLUMN name TYPE varchar(2000);
	
INSERT INTO synonym (name)
	SELECT synonyms FROM ontologyterm_synonyms;
	
INSERT INTO synonym (id, name)
	SELECT nextval('hibernate_sequence'), synonyms FROM ontologyterm_synonyms;
	
ALTER TABLE ontologyterm_synonyms
	ADD synonyms_id bigint;
	
ALTER TABLE ontologyterm_synonyms_aud
	ADD synonyms_id bigint;

UPDATE ontologyterm_synonyms
	SET synonyms_id = synonym.id
	FROM synonym
	WHERE synonym.name = ontologyterm_synonyms.synonyms;
	
UPDATE ontologyterm_synonyms_aud
	SET synonyms_id = synonym.id
	FROM synonym
	WHERE synonym.name = ontologyterm_synonyms_aud.synonyms;

ALTER TABLE ontologyterm_synonyms
	DROP synonyms;

ALTER TABLE ontologyterm_synonyms_aud
	DROP synonyms;
		
ALTER TABLE ontologyterm_synonyms
	RENAME TO ontologyterm_synonym;
		
ALTER TABLE ontologyterm_synonyms_aud
	RENAME TO ontologyterm_synonym_aud;
	