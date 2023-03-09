CREATE TABLE public.wbphenotypeterm (
    curie character varying(255) NOT NULL
);
ALTER TABLE public.wbphenotypeterm OWNER TO postgres;

CREATE TABLE public.wbphenotypeterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);
ALTER TABLE public.wbphenotypeterm_aud OWNER TO postgres;

ALTER TABLE ONLY public.wbphenotypeterm_aud
    ADD CONSTRAINT wbphenotypeterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.wbphenotypeterm
    ADD CONSTRAINT wbphenotypeterm_pkey PRIMARY KEY (curie);

ALTER TABLE ONLY public.wbphenotypeterm_aud
    ADD CONSTRAINT fk48fs5fxgn3sfvyyiqpgoquuma FOREIGN KEY (curie, rev) REFERENCES public.phenotypeterm_aud(curie, rev);
ALTER TABLE ONLY public.wbphenotypeterm
    ADD CONSTRAINT fkhonuwqm6g91le1xigmik3yrwl FOREIGN KEY (curie) REFERENCES public.phenotypeterm(curie);
