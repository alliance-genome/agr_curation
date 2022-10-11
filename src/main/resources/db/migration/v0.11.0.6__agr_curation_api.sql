CREATE TABLE public.personsetting (
		id bigint NOT NULL,
		datecreated timestamp without time zone,
		dateupdated timestamp without time zone,
		dbdatecreated timestamp without time zone,
		dbdateupdated timestamp without time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		settingskey character varying(255),
		settingsmap jsonb,
		createdby_id bigint,
		updatedby_id bigint,
		person_id bigint
);

CREATE TABLE public.personsetting_aud (
		id bigint NOT NULL,
		rev integer NOT NULL,
		revtype smallint,
		settingskey character varying(255),
		settingsmap jsonb,
		person_id bigint
);

ALTER TABLE ONLY public.personsetting_aud
		ADD CONSTRAINT personsetting_aud_pkey PRIMARY KEY (id, rev);

ALTER TABLE ONLY public.personsetting
		ADD CONSTRAINT personsetting_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.personsetting
		ADD CONSTRAINT fke1vgebqeld0wd3sf6cunn80m9 FOREIGN KEY (createdby_id) REFERENCES public.person(id);

ALTER TABLE ONLY public.personsetting
		ADD CONSTRAINT fkgguiepii4pd00a8wefwds75wf FOREIGN KEY (updatedby_id) REFERENCES public.person(id);

ALTER TABLE ONLY public.personsetting
		ADD CONSTRAINT fkhi5m73qq4jmabfhrhmhlhxr4h FOREIGN KEY (person_id) REFERENCES public.person(id);

ALTER TABLE ONLY public.personsetting_aud
		ADD CONSTRAINT fkq0ul02mp3963koe1p6sntqc8e FOREIGN KEY (rev) REFERENCES public.revinfo(rev);

