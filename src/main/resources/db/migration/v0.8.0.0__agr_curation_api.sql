CREATE TABLE public.ontologyterm_isa_ancestor_descendant (
    isadescendants_curie character varying(255) NOT NULL,
    isaancestors_curie character varying(255) NOT NULL
);
ALTER TABLE public.ontologyterm_isa_ancestor_descendant OWNER TO postgres;
ALTER TABLE ONLY public.ontologyterm_isa_ancestor_descendant
    ADD CONSTRAINT ontologyterm_ancestor_descendant_pkey PRIMARY KEY (isadescendants_curie, isaancestors_curie);
ALTER TABLE ONLY public.ontologyterm_isa_ancestor_descendant
    ADD CONSTRAINT fk62tk8kyfxk80w7n06w0d4o5yf FOREIGN KEY (isaancestors_curie) REFERENCES public.ontologyterm(curie);
ALTER TABLE ONLY public.ontologyterm_isa_ancestor_descendant
    ADD CONSTRAINT fkh6pn8ibta2l7jnov2ds2dqyyt FOREIGN KEY (isadescendants_curie) REFERENCES public.ontologyterm(curie);

CREATE INDEX idxll2agbrj7gqreke3x7hr8wvi8 ON public.ontologyterm_isa_ancestor_descendant USING btree (isadescendants_curie);
CREATE INDEX idxss79m7jisaqcm3kfq5r7gro16 ON public.ontologyterm_isa_ancestor_descendant USING btree (isaancestors_curie);

CREATE TABLE public.ontologyterm_isa_ancestor_descendant_aud (
    rev integer NOT NULL,
    isadescendants_curie character varying(255) NOT NULL,
    isaancestors_curie character varying(255) NOT NULL,
    revtype smallint
);
ALTER TABLE public.ontologyterm_isa_ancestor_descendant_aud OWNER TO postgres;
ALTER TABLE ONLY public.ontologyterm_isa_ancestor_descendant_aud
    ADD CONSTRAINT ontologyterm_ancestor_descendant_aud_pkey PRIMARY KEY (rev, isadescendants_curie, isaancestors_curie);
ALTER TABLE ONLY public.ontologyterm_isa_ancestor_descendant_aud
    ADD CONSTRAINT fk635s0r88br22cxm4ilchrlug8 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);

CREATE TABLE public.ontologyterm_isa_parent_children (
    isachildren_curie character varying(255) NOT NULL,
    isaparents_curie character varying(255) NOT NULL
);
ALTER TABLE public.ontologyterm_isa_parent_children OWNER TO postgres;
ALTER TABLE ONLY public.ontologyterm_isa_parent_children
    ADD CONSTRAINT ontologyterm_parent_children_pkey PRIMARY KEY (isachildren_curie, isaparents_curie);
ALTER TABLE ONLY public.ontologyterm_isa_parent_children
    ADD CONSTRAINT fkhjjhjxsp6gacmykm0bwijv0tj FOREIGN KEY (isaparents_curie) REFERENCES public.ontologyterm(curie);
ALTER TABLE ONLY public.ontologyterm_isa_parent_children
    ADD CONSTRAINT fkqrefoml52l7b5nr5w3diqr5er FOREIGN KEY (isachildren_curie) REFERENCES public.ontologyterm(curie);

CREATE INDEX idx1wx6c7akkhro1m34rawo283t0 ON public.ontologyterm_isa_parent_children USING btree (isaparents_curie);
CREATE INDEX idx91kybf28ecbonyxlh4s46c756 ON public.ontologyterm_isa_parent_children USING btree (isachildren_curie);

CREATE TABLE public.ontologyterm_isa_parent_children_aud (
    rev integer NOT NULL,
    isachildren_curie character varying(255) NOT NULL,
    isaparents_curie character varying(255) NOT NULL,
    revtype smallint
);
ALTER TABLE public.ontologyterm_isa_parent_children_aud OWNER TO postgres;
ALTER TABLE ONLY public.ontologyterm_isa_parent_children_aud
    ADD CONSTRAINT ontologyterm_parent_children_aud_pkey PRIMARY KEY (rev, isachildren_curie, isaparents_curie);
ALTER TABLE ONLY public.ontologyterm_isa_parent_children_aud
    ADD CONSTRAINT fk6n3ftltlg3djbjximdbwtpaqd FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
