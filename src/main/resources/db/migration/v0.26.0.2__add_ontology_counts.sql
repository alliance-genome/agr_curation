ALTER TABLE ontologyterm
   ADD COLUMN childcount bigint,
   ADD COLUMN descendentcount int;

ALTER TABLE ontologyterm_aud
   ADD COLUMN childcount bigint,
   ADD COLUMN descendentcount int;
