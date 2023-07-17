CREATE TABLE dpoterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY dpoterm
    ADD CONSTRAINT dpoterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY dpoterm
    ADD CONSTRAINT dpoterm_curie_fk FOREIGN KEY (curie) REFERENCES phenotypeterm(curie);


CREATE TABLE dpoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE dpoterm_aud
    ADD CONSTRAINT dpoterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY dpoterm_aud
    ADD CONSTRAINT dpoterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES phenotypeterm_aud(curie, rev);
