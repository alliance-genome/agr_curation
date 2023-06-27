CREATE TABLE vtterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY vtterm
    ADD CONSTRAINT vtterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY vtterm
    ADD CONSTRAINT vtterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);


CREATE TABLE vtterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE vtterm_aud
    ADD CONSTRAINT vtterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY vtterm_aud
    ADD CONSTRAINT vtterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
