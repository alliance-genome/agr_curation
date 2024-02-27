-- GENOTerm tables
CREATE TABLE genoterm (
    id bigint NOT NULL
);

ALTER TABLE ONLY genoterm
    ADD CONSTRAINT genoterm_pkey PRIMARY KEY (id);

ALTER TABLE ONLY genoterm
    ADD CONSTRAINT genoterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm(id);