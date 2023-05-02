drop table if exists public.hpterm;
drop table if exists public.hpterm_aud;

CREATE TABLE public.hpterm (
    curie character varying(255) NOT NULL
);

CREATE TABLE public.hpterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE ONLY public.hpterm
    ADD CONSTRAINT hpterm_pkey PRIMARY KEY (curie);
ALTER TABLE ONLY public.hpterm
    ADD CONSTRAINT hpterm_curie_fk FOREIGN KEY (curie) REFERENCES public.phenotypeterm(curie);

ALTER TABLE ONLY public.hpterm_aud
    ADD CONSTRAINT hpterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.hpterm_aud
    ADD CONSTRAINT hpterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES public.phenotypeterm_aud(curie, rev);
