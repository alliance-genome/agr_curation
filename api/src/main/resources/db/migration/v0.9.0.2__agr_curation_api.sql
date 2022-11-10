ALTER TABLE bulkloadfile
	ADD datelastloaded timestamp without time zone;
		
ALTER TABLE bulkloadfile_aud
	ADD datelastloaded timestamp without time zone;
	
UPDATE bulkloadfile
	SET datelastloaded=subquery.dateloaded
	FROM (
		SELECT max(loadfinished) as dateloaded, bulkloadfile_id
			FROM bulkloadfilehistory
			GROUP BY bulkloadfile_id
		) AS subquery
	WHERE bulkloadfile.id = subquery.bulkloadfile_id;

UPDATE bulkloadfile
	SET datelastloaded = dbdateupdated
	WHERE datelastloaded is null;
	
UPDATE bulkloadfile
	SET datelastloaded = dateupdated
	WHERE datelastloaded is null;