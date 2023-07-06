CREATE TABLE mmoterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY mmoterm
    ADD CONSTRAINT mmoterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY mmoterm
    ADD CONSTRAINT mmoterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);


CREATE TABLE mmoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE mmoterm_aud
    ADD CONSTRAINT mmoterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY mmoterm_aud
    ADD CONSTRAINT mmoterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
