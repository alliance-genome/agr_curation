ALTER TABLE biologicalentity
	ADD COLUMN dataprovider_id bigint;
	
ALTER TABLE biologicalentity
	ADD CONSTRAINT biologicalentity_dataprovider_id_fk
		FOREIGN KEY (dataprovider_id) REFERENCES dataprovider (id);