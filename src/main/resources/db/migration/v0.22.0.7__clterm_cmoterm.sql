-- CLTerm tables
CREATE TABLE clterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY clterm
    ADD CONSTRAINT clterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY clterm
    ADD CONSTRAINT clterm_curie_fk FOREIGN KEY (curie) REFERENCES anatomicalterm(curie);


CREATE TABLE clterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE clterm_aud
    ADD CONSTRAINT clterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY clterm_aud
    ADD CONSTRAINT clterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES anatomicalterm_aud(curie, rev);

-- CMOTerm tables
CREATE TABLE cmoterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY cmoterm
    ADD CONSTRAINT cmoterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY cmoterm
    ADD CONSTRAINT cmoterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);

CREATE TABLE cmoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE cmoterm_aud
    ADD CONSTRAINT cmoterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY cmoterm_aud
    ADD CONSTRAINT cmoterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);