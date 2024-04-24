INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'GENE', 'SARS-CoV-2 Gene Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Direct (LinkML) DQM Gene Loads';
INSERT INTO bulkmanualload (id, dataprovider)
	SELECT id, 'SARSCoV2' FROM bulkload WHERE name = 'SARS-CoV-2 Gene Load';