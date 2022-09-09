UPDATE bulkloadfile
	SET dbdatecreated = datecreated
	WHERE dbdatecreated IS null;

UPDATE bulkloadfile
	SET dbdateupdated = dateupdated
	WHERE dbdateupdated IS null;

UPDATE bulkloadfilehistory
	SET dbdatecreated = datecreated
	WHERE dbdatecreated IS null;

UPDATE bulkloadfilehistory
	SET dbdateupdated = dateupdated
	WHERE dbdateupdated IS null;	
