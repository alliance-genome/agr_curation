ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT uk_olja8aemibpg4owaw7k3ixo9b UNIQUE (diseaseannotationcurie);

CREATE INDEX crossreference_createdby_index ON public.crossreference USING btree (createdby_id);
CREATE INDEX crossreference_updatedby_index ON public.crossreference USING btree (updatedby_id);
CREATE INDEX idx2hm799bn7xal2pqoq6d4v8ik9 ON public.ontologyterm_synonym USING btree (ontologyterm_curie);
CREATE INDEX idx8o0l1xsm13k7qe0btnlr0x32j ON public.reference_crossreference USING btree (reference_curie);
CREATE INDEX idx9ej6fab264th2fst1yq26s5vg ON public.reference_crossreference USING btree (crossreferences_curie);
CREATE INDEX idxbcpc5ib23w0ssq0wskm99vxmq ON public.agmdiseaseannotation_gene USING btree (agmdiseaseannotation_id);
CREATE INDEX idxbqh4lumthmqa1jdjlq4dhamjp ON public.person_oldemails USING btree (person_id);
CREATE INDEX idxce99mr9ponwii377kkwpess1l ON public.allele_reference USING btree (allele_curie);
CREATE INDEX idxgb71atjgxqcgqnronvuprq8g4 ON public.allelediseaseannotation_gene USING btree (allelediseaseannotation_id);
CREATE INDEX idxq77tmijmj2fsow0sklmdrixmb ON public.person_emails USING btree (person_id);
CREATE INDEX idxsfk08sqo0k364ixvsd8iui53i ON public.allele_reference USING btree (references_curie);
CREATE INDEX person_uniqueid_index ON public.person USING btree (uniqueid);
CREATE INDEX personsetting_createdby_index ON public.personsetting USING btree (createdby_id);
CREATE INDEX personsetting_person_index ON public.personsetting USING btree (person_id);
CREATE INDEX personsetting_updatedby_index ON public.personsetting USING btree (updatedby_id);
CREATE INDEX reference_createdby_index ON public.reference USING btree (createdby_id);
CREATE INDEX reference_updatedby_index ON public.reference USING btree (updatedby_id);

ALTER TABLE ONLY public.vocabularytermset
    ADD CONSTRAINT fk2r8ma9a3j52tdas1y22yk9pso FOREIGN KEY (updatedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.ontologyterm_synonym
    ADD CONSTRAINT fk4uyg8s1tkgg3vp1cb8dn3vyvr FOREIGN KEY (synonyms_id) REFERENCES public.synonym(id);
ALTER TABLE ONLY public.allelediseaseannotation_gene_aud
    ADD CONSTRAINT fk7d31xk262qph3wn2g2le6esdx FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.vocabularytermset
    ADD CONSTRAINT fkbl3ym2v86924bcfwpxoe55itm FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.allele_reference_aud
    ADD CONSTRAINT fkhuya942qqdhsi6m0v37njxh30 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.atpterm_aud
    ADD CONSTRAINT fki65mb9a7b4el107fk4ox2f0kd FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.mpterm_aud
    ADD CONSTRAINT fkjw611qjy95wa8gjjthb1uptjy FOREIGN KEY (curie, rev) REFERENCES public.phenotypeterm_aud(curie, rev);
ALTER TABLE ONLY public.vocabularytermset_aud
    ADD CONSTRAINT fkkqfblby0u8l1h2i3fm7h4ynlh FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.mpterm
    ADD CONSTRAINT fkorn5mvrebk70b70o3sepp2fwe FOREIGN KEY (curie) REFERENCES public.phenotypeterm(curie);
ALTER TABLE ONLY public.vocabularytermset_vocabularyterm_aud
    ADD CONSTRAINT fkq706vs5jruui4bidehfeh5xqi FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.agmdiseaseannotation_gene_aud
    ADD CONSTRAINT fkrbw9608l4haci5t3w3ll9xmcu FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.atpterm
    ADD CONSTRAINT fksnxpka3rhxycguxrcyfyobtjf FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);
