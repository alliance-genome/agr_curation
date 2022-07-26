CREATE TABLE public.ontologyterm_ancestor_descendant (
    descendants_curie character varying(255) NOT NULL,
    ancestors_curie character varying(255) NOT NULL
);
ALTER TABLE public.ontologyterm_ancestor_descendant OWNER TO postgres;
ALTER TABLE ONLY public.ontologyterm_ancestor_descendant
    ADD CONSTRAINT ontologyterm_ancestor_descendant_pkey PRIMARY KEY (descendants_curie, ancestors_curie);
ALTER TABLE ONLY public.ontologyterm_ancestor_descendant
    ADD CONSTRAINT fk62tk8kyfxk80w7n06w0d4o5yf FOREIGN KEY (ancestors_curie) REFERENCES public.ontologyterm(curie);
ALTER TABLE ONLY public.ontologyterm_ancestor_descendant
    ADD CONSTRAINT fkh6pn8ibta2l7jnov2ds2dqyyt FOREIGN KEY (descendants_curie) REFERENCES public.ontologyterm(curie);

CREATE INDEX idxkuk9hn0on6uehy4y1eb3m9iwe ON public.ontologyterm_ancestor_descendant USING btree (descendants_curie);
CREATE INDEX idx9b18f3mepsct62sj2yts1rwvc ON public.ontologyterm_ancestor_descendant USING btree (ancestors_curie);

CREATE TABLE public.ontologyterm_ancestor_descendant_aud (
    rev integer NOT NULL,
    descendants_curie character varying(255) NOT NULL,
    ancestors_curie character varying(255) NOT NULL,
    revtype smallint
);
ALTER TABLE public.ontologyterm_ancestor_descendant_aud OWNER TO postgres;
ALTER TABLE ONLY public.ontologyterm_ancestor_descendant_aud
    ADD CONSTRAINT ontologyterm_ancestor_descendant_aud_pkey PRIMARY KEY (rev, descendants_curie, ancestors_curie);
ALTER TABLE ONLY public.ontologyterm_ancestor_descendant_aud
    ADD CONSTRAINT fk635s0r88br22cxm4ilchrlug8 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);

CREATE TABLE public.ontologyterm_parent_children (
    children_curie character varying(255) NOT NULL,
    parents_curie character varying(255) NOT NULL
);
ALTER TABLE public.ontologyterm_parent_children OWNER TO postgres;
ALTER TABLE ONLY public.ontologyterm_parent_children
    ADD CONSTRAINT ontologyterm_parent_children_pkey PRIMARY KEY (children_curie, parents_curie);
ALTER TABLE ONLY public.ontologyterm_parent_children
    ADD CONSTRAINT fkhjjhjxsp6gacmykm0bwijv0tj FOREIGN KEY (parents_curie) REFERENCES public.ontologyterm(curie);
ALTER TABLE ONLY public.ontologyterm_parent_children
    ADD CONSTRAINT fkqrefoml52l7b5nr5w3diqr5er FOREIGN KEY (children_curie) REFERENCES public.ontologyterm(curie);
CREATE INDEX idxid46tt7msr03mhg9ko1iyihmd ON public.ontologyterm_parent_children USING btree (parents_curie);
CREATE INDEX idxisejd0svxv8bgxovq4tu8iku5 ON public.ontologyterm_parent_children USING btree (children_curie);

CREATE TABLE public.ontologyterm_parent_children_aud (
    rev integer NOT NULL,
    children_curie character varying(255) NOT NULL,
    parents_curie character varying(255) NOT NULL,
    revtype smallint
);
ALTER TABLE public.ontologyterm_parent_children_aud OWNER TO postgres;
ALTER TABLE ONLY public.ontologyterm_parent_children_aud
    ADD CONSTRAINT ontologyterm_parent_children_aud_pkey PRIMARY KEY (rev, children_curie, parents_curie);
ALTER TABLE ONLY public.ontologyterm_parent_children_aud
    ADD CONSTRAINT fk6n3ftltlg3djbjximdbwtpaqd FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
