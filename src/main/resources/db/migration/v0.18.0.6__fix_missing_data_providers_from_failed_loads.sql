ALTER TABLE dataprovider
	ADD COLUMN tmp_curie varchar(255);
	
INSERT INTO dataprovider (id, tmp_curie)
	SELECT nextval('hibernate_sequence'), curie FROM biologicalentity WHERE dataprovider_id IS NULL;
	
UPDATE biologicalentity 
	SET dataprovider_id = id 
	FROM dataprovider 
	WHERE biologicalentity.dataprovider_id IS NULL AND biologicalentity.curie = dataprovider.tmp_curie;
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_curie LIKE 'FB:%' AND organization.abbreviation = 'FB';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_curie LIKE 'WB:%' AND organization.abbreviation = 'WB';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_curie LIKE 'MGI:%' AND organization.abbreviation = 'MGI';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_curie LIKE 'RGD:%' AND organization.abbreviation = 'RGD';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_curie LIKE 'SGD:%' AND organization.abbreviation = 'SGD';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_curie LIKE 'ZFIN:%' AND organization.abbreviation = 'ZFIN';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_curie LIKE 'HGNC:%' AND organization.abbreviation = 'OMIM';
	
ALTER TABLE dataprovider
	DROP COLUMN tmp_curie;
	
ALTER TABLE dataprovider
	ADD COLUMN tmp_id varchar(255);
	
INSERT INTO dataprovider (id, tmp_id)
	SELECT nextval('hibernate_sequence'), uniqueid FROM diseaseannotation WHERE dataprovider_id IS NULL;
	
UPDATE diseaseannotation
	SET dataprovider_id = id 
	FROM dataprovider 
	WHERE biologicalentity.dataprovider_id IS NULL AND diseaseannotation.uniqueid = dataprovider.tmp_id;
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_id LIKE 'FB:%' AND organization.abbreviation = 'FB';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_id LIKE 'WB:%' AND organization.abbreviation = 'WB';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_id LIKE 'MGI:%' AND organization.abbreviation = 'MGI';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_id LIKE 'RGD:%' AND organization.abbreviation = 'RGD';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_id LIKE 'SGD:%' AND organization.abbreviation = 'SGD';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_id LIKE 'ZFIN:%' AND organization.abbreviation = 'ZFIN';
	
UPDATE dataprovider
	SET sourceorganization_id = organization.id
	FROM organization
	WHERE dataprovider.tmp_id LIKE 'HGNC:%' AND organization.abbreviation = 'OMIM';
	
ALTER TABLE dataprovider
	DROP COLUMN tmp_id;