ALTER TABLE diseaseannotation
	DROP COLUMN dateupdated,
	DROP COLUMN datecreated;
	
ALTER TABLE diseaseannotation_aud
	DROP COLUMN datelastmodified,
	DROP COLUMN creationdate,
	DROP COLUMN modifiedby,
	DROP COLUMN createdby,
	DROP COLUMN modid;