ALTER TABLE person
   ADD COLUMN alliancemember_id bigint;

ALTER TABLE person_aud
   ADD COLUMN alliancemember_id bigint;

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT fk17o77er2650ydtr1dhtd0y5kn FOREIGN KEY (curie) REFERENCES public.informationcontententity(curie);

ALTER TABLE ONLY public.person
    ADD CONSTRAINT fkn46f7oo60gxpfqhonks0upqqr FOREIGN KEY (alliancemember_id) REFERENCES public.alliancemember(id);

ALTER TABLE ONLY public.alliancemember_aud
    ADD CONSTRAINT fkrm1beae40jicjirbiwsga0m8k FOREIGN KEY (id, rev) REFERENCES public.organization_aud(id, rev);

-- Other hibernate changes not previously captured

ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT uk_7hierauo01uy17h3g1okxfbhd UNIQUE (uniqueid);

ALTER TABLE ONLY public.organization
    ADD CONSTRAINT uk_7kj9rrdnqg4olayctqlcv270t UNIQUE (abbreviation);

ALTER TABLE ONLY public.organization
    ADD CONSTRAINT uk_e136w69kdo8hecafsl8r770gm UNIQUE (uniqueid);

CREATE INDEX informationcontent_createdby_index ON public.informationcontententity USING btree (createdby_id);

CREATE INDEX informationcontent_updatedby_index ON public.informationcontententity USING btree (updatedby_id);

CREATE INDEX slotannotation_informationcontententity_evidence_curie ON public.slotannotation_informationcontententity USING btree (evidence_curie);
