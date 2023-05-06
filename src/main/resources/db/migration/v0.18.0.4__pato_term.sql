CREATE TABLE public.patoterm (
    curie character varying(255) NOT NULL
);

CREATE TABLE public.patoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE ONLY public.patoterm
    ADD CONSTRAINT patoterm_pkey PRIMARY KEY (curie);
ALTER TABLE ONLY public.patoterm
    ADD CONSTRAINT patoterm_curie_fk FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);

ALTER TABLE ONLY public.patoterm_aud
    ADD CONSTRAINT patoterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.patoterm_aud
    ADD CONSTRAINT patoterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
