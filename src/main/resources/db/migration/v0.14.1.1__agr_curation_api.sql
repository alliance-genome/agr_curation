CREATE TABLE public.obiterm (
    curie character varying(255) NOT NULL
);

CREATE TABLE public.obiterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE ONLY public.obiterm
    ADD CONSTRAINT obiterm_pkey PRIMARY KEY (curie);
ALTER TABLE ONLY public.obiterm
    ADD CONSTRAINT obiterm_curie_fk FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);

ALTER TABLE ONLY public.obiterm_aud
    ADD CONSTRAINT obiterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.obiterm_aud
    ADD CONSTRAINT obiterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
