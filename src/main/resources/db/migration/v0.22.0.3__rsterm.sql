-- RSOTerm tables
CREATE TABLE rsterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY rsterm
    ADD CONSTRAINT rsterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY rsterm
    ADD CONSTRAINT rsterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);

CREATE TABLE rsterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE rsterm_aud
    ADD CONSTRAINT rsterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY rsterm_aud
    ADD CONSTRAINT rsterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);