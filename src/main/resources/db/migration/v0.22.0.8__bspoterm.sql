-- BSPOTerm tables
CREATE TABLE bspoterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY bspoterm
    ADD CONSTRAINT bspoterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY bspoterm
    ADD CONSTRAINT bspoterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);

CREATE TABLE bspoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE bspoterm_aud
    ADD CONSTRAINT bspoterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY bspoterm_aud
    ADD CONSTRAINT bspoterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);