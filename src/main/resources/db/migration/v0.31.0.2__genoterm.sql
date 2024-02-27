-- GENOTerm tables
CREATE TABLE genoterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY genoterm
    ADD CONSTRAINT genoterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY genoterm
    ADD CONSTRAINT genoterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);