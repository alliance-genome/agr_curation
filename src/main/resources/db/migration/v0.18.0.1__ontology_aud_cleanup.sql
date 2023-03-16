-- Add missing PK on atpterm table
ALTER TABLE ONLY atpterm ADD CONSTRAINT atpterm_pkey PRIMARY KEY (curie);
ALTER TABLE ONLY atpterm_aud ADD CONSTRAINT atpterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE mpterm_aud DROP CONSTRAINT fkg4sqxe4ofrkn9vaenvdrrffwt; -- Old FK to ontology term
ALTER TABLE mpterm DROP CONSTRAINT fkta9f30vmw7h1smmv68to1ipyq; -- Old FK to ontology term
