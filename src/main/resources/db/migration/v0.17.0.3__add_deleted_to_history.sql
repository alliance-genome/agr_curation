
ALTER TABLE BulkLoadFileHistory
	ADD COLUMN totalDeleteRecords int8,
	ADD COLUMN deletedRecords int8,
	ADD COLUMN deleteFailedRecords int8;

ALTER TABLE BulkLoadFileHistory_aud
	ADD COLUMN totalDeleteRecords int8,
	ADD COLUMN deletedRecords int8,
	ADD COLUMN deleteFailedRecords int8;
