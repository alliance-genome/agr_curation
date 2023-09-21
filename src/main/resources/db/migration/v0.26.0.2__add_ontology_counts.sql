ALTER TABLE ontologyterm
   ADD COLUMN childcount int,
   ADD COLUMN descendentcount int;

ALTER TABLE ontologyterm_aud
   ADD COLUMN childcount int,
   ADD COLUMN descendentcount int;
