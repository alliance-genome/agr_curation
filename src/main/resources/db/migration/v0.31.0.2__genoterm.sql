-- GENOTerm tables
CREATE TABLE genoterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY genoterm
    ADD CONSTRAINT genoterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY genoterm
    ADD CONSTRAINT genoterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);

CREATE TABLE genoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE genoterm_aud
    ADD CONSTRAINT genoterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY genoterm_aud
    ADD CONSTRAINT genoterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);