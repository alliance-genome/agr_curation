
ALTER TABLE BulkLoadFileHistory
	ADD COLUMN deletedRecords int8,
	ADD COLUMN deleteFailureRecords int8;

ALTER TABLE BulkLoadFileHistory_aud
	ADD COLUMN deletedRecords int8,
	ADD COLUMN deleteFailureRecords int8;
