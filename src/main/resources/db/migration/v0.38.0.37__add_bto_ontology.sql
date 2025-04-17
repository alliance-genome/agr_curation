INSERT INTO bulkload (id, backendbulkloadtype, name, ontologytype, internal, obsolete, group_id, dbdatecreated, bulkloadstatus)
	SELECT nextval('bulkload_seq'), 'ONTOLOGY', 'BTO Ontology Load', 'BTO', false, false, id, now(), 'STOPPED'
	FROM bulkloadgroup WHERE name = 'Ontology Bulk Loads';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', true
	FROM bulkload where name = 'BTO Ontology Load';
	
INSERT INTO bulkurlload (id, bulkloadurl)
	SELECT id, 'http://purl.obolibrary.org/obo/bto.owl'
	FROM bulkload where name = 'BTO Ontology Load';