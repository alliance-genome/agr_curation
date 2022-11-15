CREATE TABLE public.roterm (
    curie character varying(255) NOT NULL
);

CREATE TABLE public.roterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE ONLY public.roterm
    ADD CONSTRAINT roterm_pkey PRIMARY KEY (curie);
ALTER TABLE ONLY public.roterm
    ADD CONSTRAINT fkyqhfu00n2xcrfg2w4iv9wwx8 FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);

ALTER TABLE ONLY public.roterm_aud
    ADD CONSTRAINT roterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.roterm_aud
    ADD CONSTRAINT fk8wmlph21s6vviddt2tx63fhqn FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
