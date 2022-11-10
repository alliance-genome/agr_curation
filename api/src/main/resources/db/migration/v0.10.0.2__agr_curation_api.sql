ALTER TABLE diseaseannotation
	DROP COLUMN dateupdated,
	DROP COLUMN datecreated;
	
ALTER TABLE diseaseannotation_aud
	DROP COLUMN IF EXISTS datelastmodified,
	DROP COLUMN IF EXISTS creationdate,
	DROP COLUMN IF EXISTS modifiedby,
	DROP COLUMN IF EXISTS createdby,
	DROP COLUMN IF EXISTS modid;