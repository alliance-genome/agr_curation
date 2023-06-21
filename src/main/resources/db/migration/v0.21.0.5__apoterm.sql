CREATE TABLE apoterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY apoterm
    ADD CONSTRAINT apoterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY apoterm
    ADD CONSTRAINT apoterm_curie_fk FOREIGN KEY (curie) REFERENCES phenotypeterm(curie);


CREATE TABLE apoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE apoterm_aud
    ADD CONSTRAINT apoterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY apoterm_aud
    ADD CONSTRAINT apoterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES phenotypeterm_aud(curie, rev);
