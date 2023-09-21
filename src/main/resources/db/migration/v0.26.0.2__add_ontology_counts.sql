ALTER TABLE ontologyterm
   ADD COLUMN childcount bigint,
   ADD COLUMN descendentcount bigint;

ALTER TABLE ontologyterm_aud
   ADD COLUMN childcount bigint,
   ADD COLUMN descendentcount bigint;
