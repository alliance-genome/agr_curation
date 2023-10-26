CREATE SEQUENCE public.association_seq            START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkload_seq               START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkloadfile_seq           START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkloadfileexception_seq  START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkloadfilehistory_seq    START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkloadgroup_seq          START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.conditionrelation_seq      START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.crossreference_seq         START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.curationreport_seq         START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.curationreportgroup_seq    START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.curationreporthistory_seq  START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.dataprovider_seq           START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.experimentalcondition_seq  START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.genetogeneorthology_seq    START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.note_seq                   START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.organization_seq           START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.person_seq                 START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.personsetting_seq          START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.reagent_seq                START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.resourcedescriptor_seq     START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.resourcedescriptorpage_seq START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.revinfo_seq                START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.slotannotation_seq         START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.synonym_seq                START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.vocabulary_seq             START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.vocabularyterm_seq         START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.vocabularytermset_seq      START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

ALTER TABLE ONLY public.annotation                               ADD CONSTRAINT uk_5yecel87ntgvx62kmhgmlxw1y UNIQUE (modinternalid);
ALTER TABLE ONLY public.variant_note                             ADD CONSTRAINT uk_65p1uti6gxdgu4mg68qsxa0jq UNIQUE (relatednotes_id);
ALTER TABLE ONLY public.dataprovider                             ADD CONSTRAINT uk_67b8y080x4r2uyenvtuavp9ff UNIQUE (crossreference_id);
ALTER TABLE ONLY public.annotation_note                          ADD CONSTRAINT uk_6l9gna26fvcuvo5tqxr3w9cn6 UNIQUE (relatednotes_id);
ALTER TABLE ONLY public.vocabulary                               ADD CONSTRAINT uk_avxkpwi6f4x6fhb7pvsi86pog UNIQUE (vocabularylabel);
ALTER TABLE ONLY public.allelesymbolslotannotation               ADD CONSTRAINT uk_cqs9fgy7x7g3qr1pdgau5c5tn UNIQUE (singleallele_curie);
ALTER TABLE ONLY public.reagent                                  ADD CONSTRAINT uk_eakmwvrtkqtnv0j6p5sacjith UNIQUE (modentityid);
ALTER TABLE ONLY public.reagent                                  ADD CONSTRAINT uk_f9mv5mtgjw7yhjj8c4985i649 UNIQUE (modinternalid);
ALTER TABLE ONLY public.alleledatabasestatusslotannotation       ADD CONSTRAINT uk_ft8hjypm29ccs6y2om9njy5xn UNIQUE (singleallele_curie);
ALTER TABLE ONLY public.constructsymbolslotannotation            ADD CONSTRAINT uk_hr39jgto2d1rk5tilrliw5lpa UNIQUE (singleconstruct_id);
ALTER TABLE ONLY public.constructgenomicentityassociation_note   ADD CONSTRAINT uk_j3o4mwvqgbj57m6k22cm49j4r UNIQUE (relatednotes_id);
ALTER TABLE ONLY public.genesystematicnameslotannotation         ADD CONSTRAINT uk_juyeoj8vpa12nu08y2fyicsru UNIQUE (singlegene_curie);
ALTER TABLE ONLY public.vocabularytermset                        ADD CONSTRAINT uk_kovuuc5lh1hwyccs4iesimxst UNIQUE (vocabularylabel);
ALTER TABLE ONLY public.constructcomponentslotannotation_note    ADD CONSTRAINT uk_mumhfyxsu10xe2rpm4q48va38 UNIQUE (relatednotes_id);
ALTER TABLE ONLY public.constructfullnameslotannotation          ADD CONSTRAINT uk_p9e9f3q4cq6pwyiblkblyxaqo UNIQUE (singleconstruct_id);
ALTER TABLE ONLY public.annotation                               ADD CONSTRAINT uk_q3qsfxfry01magx331btt1dg3 UNIQUE (modentityid);
ALTER TABLE ONLY public.genefullnameslotannotation               ADD CONSTRAINT uk_qfksipprsdas2w1wvo6wu0p60 UNIQUE (singlegene_curie);
ALTER TABLE ONLY public.allelegenomicentityassociation           ADD CONSTRAINT uk_rasoamnjibx68mo5i23alqapb UNIQUE (relatednote_id);
ALTER TABLE ONLY public.genesymbolslotannotation                 ADD CONSTRAINT uk_sc43chptpm0vf0sm8r37ja6xw UNIQUE (singlegene_curie);
ALTER TABLE ONLY public.organization                             ADD CONSTRAINT uk_se6qdn024tn2t1wxh6cwc4mlm UNIQUE (homepageresourcedescriptorpage_id);
ALTER TABLE ONLY public.allelefullnameslotannotation             ADD CONSTRAINT uk_t4xoa43k9g4i4kh9gmei20gsw UNIQUE (singleallele_curie);
ALTER TABLE ONLY public.allelegeneassociation                    ADD CONSTRAINT uk_t67772oacc91c2ye7tvu0bwax UNIQUE (object_curie);

CREATE INDEX allelegeneassociation_object_curie_index            ON public.allelegeneassociation USING btree (object_curie);
CREATE INDEX singlereferenceassociation_singlereference_index    ON public.singlereferenceassociation USING btree (singlereference_curie);
CREATE INDEX vocabulary_vocabularylabel_index                    ON public.vocabulary USING btree (vocabularylabel);
CREATE INDEX vocabularytermset_vocabularylabel_index             ON public.vocabularytermset USING btree (vocabularylabel);

ALTER TABLE ONLY public.allelegenomicentityassociation           ADD CONSTRAINT fk1qks8xk2i7ml0qnhgx8q6ieex FOREIGN KEY (id) REFERENCES public.evidenceassociation(id);
ALTER TABLE ONLY public.constructgenomicentityassociation        ADD CONSTRAINT fkgrhw9gxslaub14x4b0mc7v9mk FOREIGN KEY (id) REFERENCES public.evidenceassociation(id);
ALTER TABLE ONLY public.annotation_note                          ADD CONSTRAINT fks4im5g992bpgi6wa1rp9y8vil FOREIGN KEY (annotation_id) REFERENCES public.annotation(id);
