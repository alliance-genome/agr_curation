-- MITerm tables
CREATE TABLE miterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY miterm
    ADD CONSTRAINT miterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY miterm
    ADD CONSTRAINT miterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);

CREATE TABLE miterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE miterm_aud
    ADD CONSTRAINT miterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY miterm_aud
    ADD CONSTRAINT miterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);

CREATE TABLE mpathterm (
    curie character varying(255) NOT NULL
);

-- MPATHTerm tables
ALTER TABLE ONLY mpathterm
    ADD CONSTRAINT mpathterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY mpathterm
    ADD CONSTRAINT mpathterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);

CREATE TABLE mpathterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE mpathterm_aud
    ADD CONSTRAINT mpathterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY mpathterm_aud
    ADD CONSTRAINT mpathterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);

-- MODTerm tables
CREATE TABLE modterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY modterm
    ADD CONSTRAINT modterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY modterm
    ADD CONSTRAINT modterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);

CREATE TABLE modterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE modterm_aud
    ADD CONSTRAINT modterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY modterm_aud
    ADD CONSTRAINT modterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);

-- UBERONTerm tables
CREATE TABLE uberonterm (
    curie character varying(255) NOT NULL
);

ALTER TABLE ONLY uberonterm
    ADD CONSTRAINT uberonterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY uberonterm
    ADD CONSTRAINT uberonterm_curie_fk FOREIGN KEY (curie) REFERENCES ontologyterm(curie);

CREATE TABLE uberonterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE uberonterm_aud
    ADD CONSTRAINT uberonterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE ONLY uberonterm_aud
    ADD CONSTRAINT uberonterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);