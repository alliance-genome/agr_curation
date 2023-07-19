-- RSOTerm tables
CREATE TABLE pwterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY pwterm
    ADD CONSTRAINT pwterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY pwterm
    ADD CONSTRAINT pwterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);

CREATE TABLE pwterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE pwterm_aud
    ADD CONSTRAINT pwterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY pwterm_aud
    ADD CONSTRAINT pwterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);