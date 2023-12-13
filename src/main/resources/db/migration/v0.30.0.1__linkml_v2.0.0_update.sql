-- Revert temporary changes to association table columns

ALTER TABLE constructgenomicentityassociation RENAME subjectconstruct_id TO subject_id;
ALTER TABLE constructgenomicentityassociation_aud RENAME subjectconstruct_id TO subject_id;

ALTER TABLE constructgenomicentityassociation RENAME objectgenomicentity_curie TO object_curie;
ALTER TABLE constructgenomicentityassociation_aud RENAME objectgenomicentity_curie TO object_curie;

ALTER TABLE allelegeneassociation RENAME objectgene_curie TO object_curie;
ALTER TABLE allelegeneassociation_aud RENAME objectgene_curie TO object_curie;

-- Random cleanup

ALTER INDEX gene_taxon_index RENAME TO gene_genetype_index;

ALTER TABLE organization DROP COLUMN IF EXISTS uniqueid;
ALTER TABLE organization_aud DROP COLUMN IF EXISTS uniqueid;

DROP TABLE paperhandle;
DROP TABLE paperhandle_aud;

-- Drop indexes and constraints

ALTER TABLE affectedgenomicmodel DROP CONSTRAINT affectedgenomicmodel_pkey;
ALTER TABLE affectedgenomicmodel DROP CONSTRAINT fkke1qw7ijaa33fqv1bifsiwiv9;

ALTER TABLE affectedgenomicmodel_aud DROP CONSTRAINT affectedgenomicmodel_aud_pkey;
ALTER TABLE affectedgenomicmodel_aud DROP CONSTRAINT fkd6m9in16kh1tqvln37a13r3hx;

ALTER TABLE agmdiseaseannotation DROP CONSTRAINT agmdiseaseannotation_assertedallele_curie_fk;
ALTER TABLE agmdiseaseannotation DROP CONSTRAINT fk_agmdasubject
ALTER TABLE agmdiseaseannotation DROP CONSTRAINT fko9dilcfxv6tw0oaeds0yss8op;
ALTER TABLE agmdiseaseannotation RENAME CONSTRAINT fkp1rktcpoyvnr2f756ncdb8k24 TO agmdiseaseannotation_id_fk;
ALTER TABLE agmdiseaseannotation DROP CONSTRAINT fktj1uj3to13fi4q32bc2p65lah;

ALTER TABLE agmdiseaseannotation_aud RENAME CONSTRAINT fkl6x226295n9ms1kugrsi88efp TO agmdiseaseannotation_aud_id_rev_fk;

ALTER TABLE agmdiseaseannotation_gene DROP CONSTRAINT agmdiseaseannotation_gene_assertedgenes_curie_fk;
ALTER INDEX idxbcpc5ib23w0ssq0wskm99vxmq RENAME TO agmdiseaseannotation_gene_agmdiseaseannotation_index;

ALTER TABLE agmdiseaseannotation_gene_aud RENAME CONSTRAINT fkrbw9608l4haci5t3w3ll9xmcu TO agmdiseaseannotation_gene_aud_rev_fk;

ALTER TABLE allele DROP CONSTRAINT allele_pkey;
ALTER TABLE allele DROP CONSTRAINT fk42r7586hi59wcwakfyr30l6l3;

ALTER TABLE allele_aud DROP CONSTRAINT allele_aud_pkey;
ALTER TABLE allele_aud DROP CONSTRAINT fkc4cub43jynmwqke9rpwpglhkt;

ALTER TABLE allele_note DROP CONSTRAINT allele_note_allele_curie_fk;
ALTER TABLE allele_note DROP CONSTRAINT uk_3ja9wii4jxp0krcfpturumjhb;
ALTER TABLE allele_note RENAME CONSTRAINT allele_note_relatednotes_id_key TO allele_note_relatednotes_id_uk;
ALTER INDEX idx3ja9wii4jxp0krcfpturumjhb RENAME TO allele_note_relatednotes_index;
DROP INDEX idx50j986kkb5ymfepkamokl1q9w;

ALTER TABLE allele_note_aud DROP CONSTRAINT allele_note_aud_pkey;

ALTER TABLE allele_reference DROP CONSTRAINT allele_reference_allele_curie_fk;
ALTER TABLE allele_reference DROP CONSTRAINT allele_reference_references_curie_fk;
DROP INDEX idx4jhjumoyh806jhpi2c0cf3t7w;
DROP INDEX idxo6e5hexti6nfdj1v6ytnlwjvd;
DROP INDEX idxsfk08sqo0k364ixvsd8iui53i;

ALTER TABLE allele_reference_aud DROP CONSTRAINT allele_reference_aud_pkey;
ALTER TABLE allele_reference_aud RENAME CONSTRAINT fkhuya942qqdhsi6m0v37njxh30 TO allele_reference_aud_rev_fk;

ALTER TABLE alleledatabasestatusslotannotation DROP CONSTRAINT alleledatabasestatus_singleallele_curie_fk;
ALTER INDEX alleledatabasestatus_databasestatus_id_index RENAME TO alleledatabasestatus_databasestatus_index;
DROP INDEX alleledatabasestatus_singleallele_curie_index;

ALTER TABLE allelediseaseannotation DROP CONSTRAINT fk_alleledasubject;
ALTER TABLE allelediseaseannotation RENAME CONSTRAINT fk3unb0kaxocbodllqe35hu4w0c TO allelediseaseannotation_id_fk;
ALTER TABLE allelediseaseannotation DROP CONSTRAINT fknecrivvmqgg2ifhppubrjy5ey;
DROP INDEX allelediseaseannotation_inferredgene_index;
DROP INDEX allelediseaseannotation_subject_index;

ALTER TABLE allelediseaseannotation_gene DROP CONSTRAINT allelediseaseannotation_gene_assertedgenes_curie_fk;
ALTER INDEX idxgb71atjgxqcgqnronvuprq8g4 RENAME TO allelediseaseannotationgene_diseaseannotation_index;
DROP INDEX idxf6sdkewbxohussr4fs7440vj5;

ALTER TABLE allelefullnameslotannotation DROP CONSTRAINT allelefullname_singleallele_curie_fk;
DROP INDEX allelefullname_singleallele_curie_index;

ALTER TABLE allelefunctionalimpactslotannotation DROP CONSTRAINT allelefunctionalimpactslotannotation_singleallele_curie_fk;
ALTER TABLE allelefunctionalimpactslotannotation RENAME CONSTRAINT fkng1x5duqyvks0enxdtr7andrl TO allelefunctionalimpactslotannotation_id_fk;
ALTER TABLE allelefunctionalimpactslotannotation DROP CONSTRAINT allelefunctionalimpactslotannotation_phenotypeterm_curie_fk;

ALTER TABLE allelefunctionalimpactslotannotation_vocabularyterm RENAME CONSTRAINT allelefunctionalimpactsa_vocabterm_afisa_curie_fk TO allelefunctionalimpactsa_vocabterm_afisa_id_fk;
ALTER TABLE allelefunctionalimpactslotannotation_vocabularyterm RENAME CONSTRAINT allelefunctionalimpactsa_vocabterm_functionimpacts_id_fk TO allelefunctionalimpactsa_vocabterm_functionalimpacts_id_fk;
ALTER INDEX allelefunctionalimpactslotannotation_functionalimpacts_id_index RENAME TO allelefunctionalimpactsa_vocabterm_functionalimpacts_index;
ALTER INDEX allelefunctionalimpactslotannotation_id_index TO allelefunctionalimpactsa_vocabterm_afisa_index;

ALTER TABLE allelegeneassociation DROP CONSTRAINT allelegeneassociation_object_curie_fk;
ALTER TABLE allelegeneassociation DROP CONSTRAINT allelegeneassociation_subject_curie_fk;
DROP INDEX allelegeneassociation_object_index;
DROP INDEX allelegeneassociation_subject_index;

ALTER TABLE allelegenomicentityassociation DROP CONSTRAINT allelegenomicentityassociation_evidencecode_curie_fk;
ALTER TABLE allelegenomicentityassociation RENAME CONSTRAINT fk1qks8xk2i7ml0qnhgx8q6ieex TO allelegenomicentityassociation_id_fk;

ALTER TABLE allelegermlinetransmissionstatusslotannotation DROP CONSTRAINT allelegermlinetransmissionstatus_singleallele_curie_fk;
ALTER TABLE allelegermlinetransmissionstatusslotannotation RENAME CONSTRAINT fkimjfrsvtapxj0gl4ocsfc68iy TO allelegermlinetransmissionstatus_id_fk;
ALTER INDEX allelegermlinetransmissionstatus_status_id_index RENAME TO allelegermlinetransmissionstatus_status_index;

ALTER TABLE alleleinheritancemodeslotannotation DROP CONSTRAINT alleleinheritancemodeslotannotation_phenotypeterm_curie_fk;
ALTER TABLE alleleinheritancemodeslotannotation DROP CONSTRAINT fkcgj3a3skh2a666q8wvehcs1pw;
DROP INDEX alleleinheritancemode_phenotypeterm_curie_index;
DROP INDEX alleleinheritancemode_singleallele_curie_index;
ALTER INDEX alleleinheritancemode_inheritancemode_id_index RENAME TO alleleinheritancemode_inheritancemode_index;

ALTER TABLE allelemutationtypeslotannotation DROP CONSTRAINT allelemutationtypeslotannotation_singleallele_curie_fk;
DROP INDEX allelemutationtype_singleallele_curie_index;

ALTER TABLE allelemutationtypeslotannotation_soterm RENAME CONSTRAINT allelemutationtypeslotannotation_aud_amsa_id_fk TO allelemutationtypesa_soterm_amsa_id_fk;
ALTER TABLE allelemutationtypeslotannotation_soterm DROP CONSTRAINT allelemutationtypeslotannotation_aud_mutationtypes_curie_fk;
ALTER INDEX allelemutationtypeslotannotation_id_index RENAME TO allelemutationtypesa_soterm_amsa_index;

ALTER TABLE allelenomenclatureeventslotannotation DROP CONSTRAINT allelenomenclatureeventslotannotation_singleallele_curie_fk;
DROP INDEX allelenomenclatureevent_singleallele_curie_index;
ALTER INDEX allelenomenclatureevent_nomenclatureevent_id_index RENAME TO allelenomenclatureevent_nomenclatureevent_index;

ALTER TABLE allelesecondaryidslotannotation DROP CONSTRAINT allelesecondaryidslotannotation_singleallele_curie_fk;
ALTER TABLE allelesecondaryidslotannotation RENAME CONSTRAINT fko6369ool2dxdy8odi2i9brj4r TO allelesecondaryidslotannotation_id_fk;
DROP INDEX allelesecondaryid_singleallele_curie_index;

ALTER TABLE allelesymbolslotannotation DROP CONSTRAINT allelesymbolslotannotation_singleallele_curie_fk;
DROP INDEX allelesymbol_singleallele_curie_index;

ALTER TABLE allelesynonymslotannotation DROP CONSTRAINT allelesynonymslotannotation_singleallele_curie_fk;
DROP INDEX allelesynonym_singleallele_curie_index;

ALTER TABLE anatomicalterm DROP CONSTRAINT anatomicalterm_pkey;
ALTER TABLE anatomicalterm DROP CONSTRAINT fkfepti479fro1b09ybaltkofqu;

ALTER TABLE anatomicalterm_aud DROP CONSTRAINT antomicalterm_aud_pkey;
ALTER TABLE anatomicalterm_aud DROP CONSTRAINT fkan2c886jcsep01s7rqibfghfh;

ALTER INDEX annotation_conditionrelation_annotation_id_index RENAME TO annotation_conditionrelation_annotation_index;
ALTER INDEX annotation_conditionrelation_conditionrelations_id_index RENAME TO annotation_conditionrelation_conditionrelations_index;

ALTER TABLE annotation_note RENAME CONSTRAINT fks4im5g992bpgi6wa1rp9y8vil TO annotation_note_annotation_id_fk;
ALTER INDEX annotation_note_annotation_id_index RENAME TO annotation_note_annotation_index;
ALTER INDEX annotation_note_relatednotes_id_index RENAME TO annotation_note_relatednotes_index;

ALTER TABLE apoterm DROP CONSTRAINT apoterm_pkey;
ALTER TABLE apoterm DROP CONSTRAINT apoterm_curie_fk;

ALTER TABLE apoterm_aud DROP CONSTRAINT apoterm_aud_pkey;
ALTER TABLE apoterm_aud DROP CONSTRAINT apoterm_aud_curie_rev_fk;

ALTER TABLE atpterm DROP CONSTRAINT atpterm_pkey;
ALTER TABLE atpterm DROP CONSTRAINT fksnxpka3rhxycguxrcyfyobtjf;

ALTER TABLE atpterm_aud DROP CONSTRAINT atpterm_aud_pkey;
ALTER TABLE atpterm_aud DROP CONSTRAINT fki65mb9a7b4el107fk4ox2f0kd;

ALTER TABLE biologicalentity DROP CONSTRAINT biologicalentity_pkey;
ALTER TABLE biologicalentity DROP CONSTRAINT fk5c19vicptarinu2wgj7xyhhum;
DROP INDEX biologicalentity_taxon_index;

ALTER TABLE biologicalentity_aud DROP CONSTRAINT biologicalentity_aud_pkey;
ALTER TABLE biologicalentity_aud DROP CONSTRAINT fk5hkwd2k49xql5qy0aby85qtad;

ALTER TABLE bspoterm_aud DROP CONSTRAINT bspoterm_aud_pkey;
ALTER TABLE bspoterm_aud DROP CONSTRAINT bspoterm_aud_curie_rev_fk;

ALTER TABLE bulkload_aud DROP CONSTRAINT fkhhh8994753467hwa33blp22dc;

ALTER TABLE bulkloadfile_aud DROP CONSTRAINT fk7sl5m81qa11sr40chch9vg6uj;

ALTER TABLE bulkloadfileexception_aud DROP CONSTRAINT fkm7op2ir0vi9pwcctl39kqbo70;

ALTER TABLE bulkloadfilehistory_aud DROP CONSTRAINT fkppa5tcqtwv560svqkq6b958hc;

ALTER TABLE bulkloadgroup_aud DROP CONSTRAINT fk722g0iotb8v01pq0cej3w7gke;

ALTER TABLE chebiterm DROP CONSTRAINT chebiterm_pkey;
ALTER TABLE chebiterm DROP CONSTRAINT fk7enwyeblw2xt5yo5co0keko5f;

ALTER TABLE chebiterm_aud DROP CONSTRAINT chebiterm_aud_pkey;
ALTER TABLE chebiterm_aud DROP CONSTRAINT fk7grscrrhdcw9ek6agi78j4ca1;

ALTER TABLE chemicalterm DROP CONSTRAINT chemicalterm_pkey;
ALTER TABLE chemicalterm DROP CONSTRAINT fk2fegif3wy9egh5r2yy8wplrwu;

ALTER TABLE chemicalterm_aud DROP CONSTRAINT chemicalterm_aud_pkey;
ALTER TABLE chemicalterm_aud DROP CONSTRAINT fkieeg5x1a11dqom8dw4valm169;

ALTER TABLE clterm DROP CONSTRAINT clterm_pkey;
ALTER TABLE clterm DROP CONSTRAINT clterm_curie_fk;

ALTER TABLE clterm_aud DROP CONSTRAINT clterm_aud_pkey;
ALTER TABLE clterm_aud DROP CONSTRAINT clterm_aud_curie_rev_fk;

ALTER TABLE cmoterm DROP CONSTRAINT clterm_pkey;
ALTER TABLE cmoterm DROP CONSTRAINT clterm_curie_fk;

ALTER TABLE cmoterm_aud DROP CONSTRAINT cmoterm_aud_pkey;
ALTER TABLE cmoterm_aud DROP CONSTRAINT cmoterm_aud_curie_rev_fk;

ALTER TABLE conditionrelation RENAME CONSTRAINT fkn8t4joy3iheftpbxt0omvxl52 TO conditionrelation_conditionrelationtype_id_fk;
ALTER TABLE conditionrelation DROP CONSTRAINT conditionrelation_singlereference_curie_fk;

ALTER TABLE conditionrelation_aud DROP CONSTRAINT fkkcw0iu1vmw6ttm7g645947yyy;

ALTER TABLE construct_reference DROP CONSTRAINT construct_reference_references_curie_fk;
ALTER INDEX construct_reference_construct_id_index TO construct_reference_construct_index;
DROP INDEX construct_reference_references_curie_index;

ALTER TABLE constructcomponentslotannotation DROP CONSTRAINT constructcomponentslotannotation_taxon_curie_fk;
DROP INDEX constructcomponentslotannotation_taxon_index;

ALTER INDEX constructcomponentsa_note_ccsa_id_index RENAME TO constructcomponentsa_note_ccsa_index;
ALTER INDEX constructcomponentsa_note_relatednotes_id_index RENAME TO constructcomponentsa_note_relatednotes_index

ALTER INDEX constructfullname_singleconstruct_id_index RENAME TO constructfullname_singleconstruct_index;

ALTER TABLE constructgenomicentityassociation DROP CONSTRAINT constructgenomicentityassociation_object_curie_fk;
ALTER TABLE constructgenomicentityassociation RENAME CONSTRAINT fkgrhw9gxslaub14x4b0mc7v9mk TO constructgenomicentityassociation_id_fk;
DROP INDEX constructgenomicentityassociation_object_index;

ALTER INDEX cgeassociation_note_cgeassociation_id_index RENAME TO cgeassociation_note_cgeassociation_index;
ALTER INDEX cgeassociation_note_relatednotes_id_index RENAME TO cgeassociation_note_relatednotes_index;

ALTER INDEX constructsymbol_singleconstruct_id_index RENAME TO constructsymbol_singleconstruct_index;

ALTER INDEX constructsynonym_singleconstruct_id_index RENAME TO constructsynonym_singleconstruct_index;

ALTER TABLE crossreference_aud DROP CONSTRAINT fkricj7nn0u0fec2l2r2fo7al55;

ALTER TABLE curationreport_aud DROP CONSTRAINT fks2fh7j9mb60yfxyd83gklhgmo;

ALTER TABLE curationreportgroup_aud DROP CONSTRAINT fk7pl1pmjstb9eqi80e353a06v8;

ALTER TABLE curationreporthistory_aud DROP CONSTRAINT fk5mffk8y6qajllde8m24dwjn9v;

ALTER TABLE daoterm DROP CONSTRAINT daoterm_pkey;
ALTER TABLE daoterm DROP CONSTRAINT fk3xjbjyyuqqyvspspeael1m7fe;

ALTER TABLE daoterm_aud DROP CONSTRAINT daoterm_aud_pkey;
ALTER TABLE daoterm_aud DROP CONSTRAINT fkgif1cep78abowfodb5rxvoq1x;

ALTER TABLE dataprovider_aud DROP CONSTRAINT fk66rd9ltf5l9paxmvyr63tcqwf;

ALTER TABLE diseaseannotation DROP CONSTRAINT diseaseannotation_object_curie_fk;
DROP INDEX diseaseannotation_object_index;

ALTER TABLE diseaseannotation_biologicalentity DROP CONSTRAINT diseaseannotation_biologicalentity_dgm_curie_fk;
ALTER INDEX idxa408leg4b6rrhx0e0e35yno7t RENAME TO diseaseannotation_biologicalentity_diseaseannotation_index;
DROP INDEX idxon9a94gwp0jl6t2sfjqv0iwa5;

ALTER TABLE diseaseannotation_ecoterm RENAME CONSTRAINT idxon9a94gwp0jl6t2sfjqv0iwa5 TO diseaseannotation_ecoterm_diseaseannotation_id_fk;
ALTER TABLE diseaseannotation_ecoterm DROP CONSTRAINT fkp79bf46xsyojpvjjguoe3vuuu;
DROP INDEX idx3u0swr7xss7wjjve7ocd08u27;
ALTER INDEX idxntbiegjxs3ooqy894m4y5vgwb RENAME TO diseaseannotation_ecoterm_diseaseannotation_index;

ALTER TABLE diseaseannotation_gene DROP CONSTRAINT fk6akpr16qusnfom0fdhjjevkb2;
ALTER TABLE diseaseannotation_gene RENAME CONSTRAINT fky4jlhsgseecd3gkxjtwp28ba TO diseaseannotation_gene_diseaseannotation_id_fk;
DROP INDEX idxc8bxs3ggb4rt2kmnqja0js5mq;
ALTER INDEX idxj6eavg6eannqn6uhvja6p4enf RENAME TO diseaseannotation_gene_diseaseannotation_index;

ALTER TABLE diseaseannotation_vocabularyterm RENAME CONSTRAINT fk7jhlm01yyrnyd26c5extqi9iv TO diseaseannotation_vocabularyterm_diseasequalifiers_id_fk;
ALTER TABLE diseaseannotation_vocabularyterm RENAME CONSTRAINT fkb7dg8qvpicnh87s0162sn62gl TO diseaseannotation_vocabularyterm_diseaseannotation_id_fk;
ALTER INDEX idxbnb14fsatd291upd9af38fnyp RENAME TO diseaseannotation_vocabularyterm_diseasequalifiers_index;
ALTER INDEX idxc8oiw5qoippfjl0b6s9oegiss RENAME TO diseaseannotation_vocabularyterm_diseaseannotation_index;

ALTER TABLE doterm DROP CONSTRAINT doterm_pkey;
ALTER TABLE doterm DROP CONSTRAINT fkp8el2duba9ym3l6gd5dy43swk;

ALTER TABLE doterm_aud DROP CONSTRAINT doterm_aud_pkey;
ALTER TABLE doterm_aud DROP CONSTRAINT fkkgu80ih0f55tskr386gucsqh2;

ALTER TABLE dpoterm DROP CONSTRAINT dpoterm_pkey;
ALTER TABLE dpoterm DROP CONSTRAINT dpoterm_curie_fk;

ALTER TABLE dpoterm_aud DROP CONSTRAINT dpoterm_aud_pkey;
ALTER TABLE dpoterm_aud DROP CONSTRAINT dpoterm_aud_curie_rev_fk;

ALTER TABLE ecoterm DROP CONSTRAINT doterm_pkey;
ALTER TABLE ecoterm DROP CONSTRAINT fkskvp24kfp723htxmk0m9ev4ns;

ALTER TABLE ecoterm_aud DROP CONSTRAINT doterm_aud_pkey;
ALTER TABLE ecoterm_aud DROP CONSTRAINT fkrdtwy8r0gnnh6numgdbgi9e6s;

ALTER TABLE emapaterm DROP CONSTRAINT emapaterm_pkey;
ALTER TABLE emapaterm DROP CONSTRAINT fkcm3tpjo7lxsx61pj7gs5y9f9u;

ALTER TABLE emapaterm_aud DROP CONSTRAINT emapaterm_aud_pkey;
ALTER TABLE emapaterm_aud DROP CONSTRAINT fkaipxoy4lm50q9mphk2yp7whyh;

ALTER TABLE evidenceassociation_informationcontententity DROP CONSTRAINT evidenceassociation_infocontent_evidence_curie_fk;
ALTER INDEX evidenceassociation_infocontent_evidenceassociation_id_index RENAME TO evidenceassociation_infocontent_evidenceassociation_index;
DROP INDEX evidenceassociation_infocontent_evidence_curie_index;

ALTER TABLE experimentalcondition DROP CONSTRAINT fk2rmhalgeg6rghpat78b2cpcoc;
ALTER TABLE experimentalcondition DROP CONSTRAINT fkagp6m2xqeu7bapu5hyh2pmha9;
ALTER TABLE experimentalcondition DROP CONSTRAINT fkcl89ywjgllce228a0uo8fd0ee;
ALTER TABLE experimentalcondition DROP CONSTRAINT fkhi2109btsx06x2u9kdg7y7xp0;
ALTER TABLE experimentalcondition DROP CONSTRAINT fkp0oqdnt9bmx68i84neufkcb3a;
ALTER TABLE experimentalcondition DROP CONSTRAINT fksso9a3875a8t0ver6u6qciuap;

ALTER TABLE experimentalconditionontologyterm DROP CONSTRAINT experimentalconditionontologyterm_pkey;
ALTER TABLE experimentalconditionontologyterm DROP CONSTRAINT fk5jlaea2evnqnrlf72jglhqq6p;

ALTER TABLE experimentalconditionontologyterm_aud DROP CONSTRAINT experimentalconditionontologyterm_aud_pkey;
ALTER TABLE experimentalconditionontologyterm_aud DROP CONSTRAINT fkkr4o08hq0jboq6g4ou5gmn8xd;

ALTER TABLE fbdvterm DROP CONSTRAINT fbdvterm_pkey;
ALTER TABLE fbdvterm DROP CONSTRAINT fkn7q3y19l70sef1h4f9ippnjoa;

ALTER TABLE fbdvterm_aud DROP CONSTRAINT fbdvterm_aud_pkey;
ALTER TABLE fbdvterm_aud DROP CONSTRAINT fkvtaradvq4e6fdjecf2m4ujap;

ALTER TABLE gene DROP CONSTRAINT gene_pkey;
ALTER TABLE gene DROP CONSTRAINT fk9v4jtwy759c3cfub0uxye5rue;
ALTER TABLE gene DROP CONSTRAINT fkiaxg0dhug3stym3gjovw598w1;
DROP INDEX gene_taxon_index;

ALTER TABLE gene_aud DROP CONSTRAINT gene_aud_pkey;
ALTER TABLE gene_aud DROP CONSTRAINT fk4n82maba8vniaxet7w2w1sfg4;

ALTER TABLE genediseaseannotation RENAME CONSTRAINT fk3j5deigrhrwln0srh51vtw3m8 TO genediseaseannotation_id_fk;
ALTER TABLE genediseaseannotation DROP CONSTRAINT fk51h0w9jsd45qw5f3v2v0o28mu;
ALTER TABLE genediseaseannotation DROP CONSTRAINT fk_genedasubject;

ALTER TABLE genefullnameslotannotation DROP CONSTRAINT genefullnameslotannotation_singlegene_curie_fk;
DROP INDEX genefullname_singlegene_curie_index;

ALTER TABLE genesecondaryidslotannotation DROP CONSTRAINT fkhtdt6peje2s446u44ax3knp14;
ALTER TABLE genesecondaryidslotannotation RENAME CONSTRAINT fkq0oks812epecjvubiasibhw2s TO genesecondaryidslotannotation_id_fk;
DROP INDEX genesecondaryid_singlegene_curie_index;

ALTER TABLE genesymbolslotannotation DROP CONSTRAINT genesymbolslotannotation_singlegene_curie_fk;
DROP INDEX genesymbol_singlegene_curie_index;

ALTER TABLE genesynonymslotannotation DROP CONSTRAINT genesynonymslotannotation_singlegene_curie_fk;
DROP INDEX genesynonym_singlegene_curie_index;

ALTER TABLE genesystematicnameslotannotation DROP CONSTRAINT genesystematicnameslotannotation_singlegene_curie_fk;
DROP INDEX genesystematicname_singlegene_curie_index;

ALTER TABLE genetogeneorthology DROP CONSTRAINT genetogeneorthology_objectgene_curie_fk;
ALTER TABLE genetogeneorthology DROP CONSTRAINT genetogeneorthology_subjectgene_curie_fk;
DROP INDEX genetogeneorthology_objectgene_index;
DROP INDEX genetogeneorthology_subjectgene_index;

ALTER TABLE genetogeneorthologycurated DROP CONSTRAINT genetogeneorthologycurated_evidencecode_curie_fk;
ALTER TABLE genetogeneorthologycurated DROP CONSTRAINT genetogeneorthologycurated_singlereference_curie_fk;
DROP INDEX genetogeneorthologycurated_evidencecode_index;
DROP INDEX genetogeneorthologycurated_singlereference_index;

ALTER TABLE genomicentity DROP CONSTRAINT genomicentity_pkey;
ALTER TABLE genomicentity DROP CONSTRAINT fkhi54si7gksfs3f6jrbytaddbi;

ALTER TABLE genomicentity_aud DROP CONSTRAINT genomicentity_aud_pkey;
ALTER TABLE genomicentity_aud DROP CONSTRAINT fknd0sic0qo3ko71w4d9k5urg48;

ALTER TABLE genomicentity_crossreference DROP CONSTRAINT fk9b9qofiu2sump8fnfxgux1lvl;
ALTER INDEX genomicentity_crossreference_crossreferences_id_index RENAME TO genomicentity_crossreference_crossreferences_index;
DROP INDEX genomicentity_crossreference_ge_curie_xref_id_index;
DROP INDEX genomicentity_crossreference_genomicentity_curie_index;

ALTER TABLE goterm DROP CONSTRAINT goterm_pkey;
ALTER TABLE goterm DROP CONSTRAINT fk4gf262ba8btx03wi3vl5vhfao;

ALTER TABLE goterm_aud DROP CONSTRAINT goterm_aud_pkey;
ALTER TABLE goterm_aud DROP CONSTRAINT fk4kjm9hm06yutma1ilq04h967s;

ALTER TABLE hpterm DROP CONSTRAINT hpterm_pkey;
ALTER TABLE hpterm DROP CONSTRAINT hpterm_curie_fk;

ALTER TABLE hpterm_aud DROP CONSTRAINT hpterm_aud_pkey;
ALTER TABLE hpterm_aud DROP CONSTRAINT hpterm_aud_curie_rev_fk;

ALTER TABLE materm DROP CONSTRAINT materm_pkey;
ALTER TABLE materm DROP CONSTRAINT fktlgqvrv4vuh8gqihevh6adya4;

ALTER TABLE materm_aud DROP CONSTRAINT materm_aud_pkey;
ALTER TABLE materm_aud DROP CONSTRAINT fk7lfprbh8k8mnw9yf8ywp7xieg;

ALTER TABLE miterm DROP CONSTRAINT miterm_pkey;
ALTER TABLE miterm DROP CONSTRAINT miterm_curie_fk;

ALTER TABLE miterm_aud DROP CONSTRAINT miterm_aud_pkey;
ALTER TABLE miterm_aud DROP CONSTRAINT miterm_aud_curie_rev_fk;

ALTER TABLE mmoterm DROP CONSTRAINT mmoterm_pkey;
ALTER TABLE mmoterm DROP CONSTRAINT mmoterm_curie_fk;

ALTER TABLE mmoterm_aud DROP CONSTRAINT mmoterm_aud_pkey;
ALTER TABLE mmoterm_aud DROP CONSTRAINT mmoterm_aud_curie_rev_fk;

ALTER TABLE mmusdvterm DROP CONSTRAINT mmusdvterm_pkey;
ALTER TABLE mmusdvterm DROP CONSTRAINT fkmkv8r93hlnf06oa8xy2usdq9g;

ALTER TABLE mmusdvterm_aud DROP CONSTRAINT mmusdvterm_aud_pkey;
ALTER TABLE mmusdvterm_aud DROP CONSTRAINT fk19prhk8fikp11bpxxm2tqxh6u;

ALTER TABLE modterm DROP CONSTRAINT modterm_pkey;
ALTER TABLE modterm DROP CONSTRAINT modterm_curie_fk;

ALTER TABLE modterm_aud DROP CONSTRAINT modterm_aud_pkey;
ALTER TABLE modterm_aud DROP CONSTRAINT modterm_aud_curie_rev_fk;

ALTER TABLE molecule DROP CONSTRAINT molecule_pkey;
ALTER TABLE molecule DROP CONSTRAINT fknnf79fdaivbnqu0p9kes1jtd1;

ALTER TABLE molecule_aud DROP CONSTRAINT molecule_aud_pkey;
ALTER TABLE molecule_aud DROP CONSTRAINT fkcbo1onn61w7v5ivh1e1h2tcd7;

ALTER TABLE mpathterm DROP CONSTRAINT mpathterm_pkey;
ALTER TABLE mpathterm DROP CONSTRAINT mpathterm_curie_fk;

ALTER TABLE mpathterm_aud DROP CONSTRAINT mpathterm_aud_pkey;
ALTER TABLE mpathterm_aud DROP CONSTRAINT mpathterm_aud_curie_rev_fk;

ALTER TABLE mpterm DROP CONSTRAINT mpterm_pkey;
ALTER TABLE mpterm DROP CONSTRAINT fkorn5mvrebk70b70o3sepp2fwe;

ALTER TABLE mpterm_aud DROP CONSTRAINT mpterm_aud_pkey;
ALTER TABLE mpterm_aud DROP CONSTRAINT fkjw611qjy95wa8gjjthb1uptjy;

ALTER TABLE ncbitaxonterm DROP CONSTRAINT ncbitaxonterm_pkey;
ALTER TABLE ncbitaxonterm DROP CONSTRAINT fk47k37g37jc1e4wdt76ajmn0xk;

ALTER TABLE ncbitaxonterm_aud DROP CONSTRAINT ncbitaxonterm_aud_pkey;
ALTER TABLE ncbitaxonterm_aud DROP CONSTRAINT fkap27v3trsn5u9q93qb8ikabrf;

ALTER TABLE note_aud DROP CONSTRAINT fk1r4uoh4rg9vyahvb8bpd6dfn7;

ALTER TABLE note_reference RENAME CONSTRAINT fknr8td9rfl6vd6cstukci0e0qq TO note_reference_note_id_fk;
ALTER TABLE note_reference DROP CONSTRAINT note_reference_references_curie_fk;
ALTER INDEX idxk4kbcn96bs4gafx883i9sj7my RENAME TO note_reference_note_index;

ALTER TABLE obiterm DROP CONSTRAINT obiterm_pkey;
ALTER TABLE obiterm DROP CONSTRAINT obiterm_curie_fk;

ALTER TABLE obiterm_aud DROP CONSTRAINT obiterm_aud_pkey;
ALTER TABLE obiterm_aud DROP CONSTRAINT obiterm_aud_curie_rev_fk;

ALTER TABLE ontologyterm DROP CONSTRAINT ontologyterm_pkey;

ALTER TABLE ontologyterm_aud DROP CONSTRAINT ontologyterm_aud_pkey;
ALTER TABLE ontologyterm_aud DROP CONSTRAINT fkdxjp2u3w3xoi7p9j7huceg2ts;

ALTER TABLE ontologyterm_crossreference DROP CONSTRAINT fk3e1a40poh1ehjk91h42bx7i45;
DROP INDEX ontologyterm_crossreference_ontologyterm_curie_index;
ALTER INDEX ontologyterm_crossreference_crossreferences_id_index RENAME TO ontologyterm_crossreference_crossreferences_index;

ALTER TABLE ontologyterm_definitionurls DROP CONSTRAINT fknhkhso5kmei3t37mkhodkkfgt;
DROP INDEX idx171k63a40d8huvbhohveql7so;

ALTER TABLE ontologyterm_isa_ancestor_descendant DROP CONSTRAINT ontologyterm_ancestor_descendant_pkey;
ALTER TABLE ontologyterm_isa_ancestor_descendant DROP CONSTRAINT fkh6pn8ibta2l7jnov2ds2dqyyt;
ALTER TABLE ontologyterm_isa_ancestor_descendant DROP CONSTRAINT fk62tk8kyfxk80w7n06w0d4o5yf;
DROP INDEX idxll2agbrj7gqreke3x7hr8wvi8;
DROP INDEX idxss79m7jisaqcm3kfq5r7gro16;

ALTER TABLE ontologyterm_isa_parent_children DROP CONSTRAINT ontologyterm_parent_children_pkey;
ALTER TABLE ontologyterm_isa_parent_children DROP CONSTRAINT fkhjjhjxsp6gacmykm0bwijv0tj;
ALTER TABLE ontologyterm_isa_parent_children DROP CONSTRAINT fkqrefoml52l7b5nr5w3diqr5er;
DROP INDEX idx1wx6c7akkhro1m34rawo283t0;
DROP INDEX idx91kybf28ecbonyxlh4s46c756;

ALTER TABLE ontologyterm_secondaryidentifiers DROP CONSTRAINT fkpkg5jfw6wypf4v43bpb4ergu7;
DROP INDEX idxsvjjbf5eugfrbue5yo4jgarpn;

ALTER TABLE ontologyterm_subsets DROP CONSTRAINT fkchq4ex53obwegdhgxrovd5r53;
DROP INDEX idxips7lcqafkikxweue2p0h13t9;

ALTER TABLE ontologyterm_synonym RENAME CONSTRAINT fk4uyg8s1tkgg3vp1cb8dn3vyvr TO ontologyterm_synonym_synonyms_id_fk;
ALTER TABLE ontologyterm_synonym DROP CONSTRAINT fkjf8xunyry3dy9njpqb01tvjsr;
DROP INDEX ontologyterm_synonym_ontologyterm_curie_index;

ALTER TABLE organization_aud DROP CONSTRAINT organization_aud_rev_fk;

ALTER TABLE patoterm DROP CONSTRAINT patoterm_pkey;
ALTER TABLE patoterm DROP CONSTRAINT patoterm_curie_fk;

ALTER TABLE patoterm_aud DROP CONSTRAINT patoterm_aud_pkey;
ALTER TABLE patoterm_aud DROP CONSTRAINT patoterm_aud_curie_rev_fk;

ALTER TABLE person_aud DROP CONSTRAINT fkqbm2y5o4elhanxeq26reu73yd;

ALTER TABLE phenotypeterm DROP CONSTRAINT phenotypeterm_pkey;
ALTER TABLE phenotypeterm DROP CONSTRAINT fk4ymq8h2kdhq6ix6sfb4q4fn7a;

ALTER TABLE phenotypeterm_aud DROP CONSTRAINT phenotypeterm_aud_pkey;
ALTER TABLE phenotypeterm_aud DROP CONSTRAINT fksap791c8unrey4xnqcydm8kv1;

ALTER TABLE pwterm DROP CONSTRAINT pwterm_pkey;
ALTER TABLE pwterm DROP CONSTRAINT pwterm_curie_fk;

ALTER TABLE pwterm_aud DROP CONSTRAINT pwterm_aud_pkey;
ALTER TABLE pwterm_aud DROP CONSTRAINT pwterm_aud_curie_rev_fk;

ALTER TABLE reagent_aud DROP CONSTRAINT reagent_aud_rev_fk;

ALTER INDEX reagent_secondaryidentifiers_reagent_id_index RENAME TO reagent_secondaryidentifiers_reagent_index;

ALTER reference DROP CONSTRAINT reference_pkey;
ALTER reference DROP CONSTRAINT fk17o77er2650ydtr1dhtd0y5kn;

ALTER reference_aud DROP CONSTRAINT reference_aud_pkey;
ALTER reference_aud DROP CONSTRAINT fk897g2lxdu1btxkcikigm6j4wo;
ALTER reference_aud DROP CONSTRAINT reference_aud_curie_rev_fk;

ALTER TABLE reference_crossreference DROP CONSTRAINT reference_crossreference_reference_curie_fk;
DROP INDEX idx5f73olsmf7f70k9nimewmv2ov;
ALTER INDEX reference_crossreference_crossreferences_id_index RENAME TO reference_crossreference_crossreferences_index;
DROP INDEX idx8o0l1xsm13k7qe0btnlr0x32j;

ALTER TABLE resourcedescriptor_aud DROP CONSTRAINT resourcedescriptor_aud_rev_fk;

ALTER TABLE resourcedescriptorpage_aud DROP CONSTRAINT resourcedescriptorpage_aud_rev_fk;
-- Add columns

-- Add constraints and indexes

ALTER TABLE affectedgenomicmodel ADD PRIMARY KEY (id);
ALTER TABLE affectedgenomicmodel ADD CONSTRAINT affectedgenomicmodel_id_fk FOREIGN KEY (id) REFERENCES genomicentity (id);

ALTER TABLE affectedgenomicmodel_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE affectedgenomicmodel_aud ADD CONSTRAINT affectedgenomicmodel_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES genomicentity_aud (id, rev);

ALTER TABLE agmdiseaseannotation ADD CONSTRAINT agmdiseaseannotation_subject_id_fk (subject_id) REFERENCES affectedgenomicmodel (id);
ALTER TABLE agmdiseaseannotation ADD CONSTRAINT agmdiseaseannotation_assertedallele_id_fk (assertedallele_id) REFERENCES allele (id);
ALTER TABLE agmdiseaseannotation ADD CONSTRAINT agmdiseaseannotation_inferredallele_id_fk (inferredallele_id) REFERENCES allele (id);
ALTER TABLE agmdiseaseannotation ADD CONSTRAINT agmdiseaseannotation_inferredgene_id_fk (inferredgene_id) REFERENCES gene (id);

ALTER TABLE agmdiseaseannotation_gene ADD CONSTRAINT agmdiseaseannotation_gene_assertedgenes_id_fk (assertedgenes_id) REFERENCES gene (id);

ALTER TABLE allele ADD PRIMARY KEY (id);
ALTER TABLE allele ADD CONSTRAINT allele_id_fk FOREIGN KEY (id) REFERENCES genomicentity (id);

ALTER TABLE allele_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE allele_aud ADD CONSTRAINT allele_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES genomicentity_aud (id, rev);

ALTER TABLE allele_note ADD CONSTRAINT allele_note_allele_id_fk FOREIGN KEY (allele_id) REFERENCES allele (id);
CREATE INDEX allele_note_allele_index ON allele_note USING btree (allele_id);

ALTER TABLE allele_note_aud ADD PRIMARY KEY (allele_id, relatednotes_id, rev);

ALTER TABLE allele_reference ADD CONSTRAINT allele_reference_allele_id_fk FOREIGN KEY (allele_id) REFERENCES allele (id);
ALTER TABLE allele_reference ADD CONSTRAINT allele_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES reference (id);
CREATE INDEX allele_reference_allele_index ON allele_reference USING btree (allele_id);
CREATE INDEX allele_reference_references_index ON allele_reference USING btree (references_id);
CREATE INDEX allele_reference_allele_references_index ON allele_reference USING btree (allele_id, references_id);

ALTER TABLE allele_reference_aud ADD PRIMARY KEY (allele_id, references_id, rev);

ALTER TABLE alleledatabasestatusslotannotation ADD CONSTRAINT alleledatabasestatus_singleallele_id_fk FOREIGN KEY (allele_id) REFERENCES allele (id);
CREATE INDEX alleledatabasestatus_singleallele_index ON alleledatabasestatusslotannotation USING btree (allele_id);

ALTER TABLE allelediseaseannotation ADD CONSTRAINT allelediseaseannoation_subject_id_fk FOREIGN KEY (subject_id) REFERENCES allele (id);
ALTER TABLE allelediseaseannotation ADD CONSTRAINT allelediseaseannotation_inferredgene_id_fk FOREIGN KEY (inferredgene_id) REFERENCES gene (id);
CREATE INDEX allelediseaseannotation_inferredgene_index ON allelediseaseannotation USING btree (inferredgene_id);
CREATE INDEX allelediseaseannotation_subject_index ON allelediseaseannotation USING btree (subject_id);

ALTER TABLE allelediseaseannotation_gene ADD CONSTRAINT allelediseaseannotation_gene_assertedgenes_id_fk;
CREATE INDEX allelediseaseannotationgene_assertedgenes_index ON allelediseaseannotation_gene USING btree (assertedgenes_id);

CREATE INDEX allelefullname_singleallele_index ON allelefullnameslotannotation USING btree (allele_id);

ALTER TABLE allelefunctionalimpactslotannotation ADD CONSTRAINT allelefunctionalimpactslotannotation_singleallele_id_fk FOREIGN KEY (singleallele_id) REFERENCES allele (id);
ALTER TABLE allelefunctionalimpactslotannotation ADD CONSTRAINT allelefunctionalimpactslotannotation_phenotypeterm_id_fk FOREIGN KEY (phenotypeterm_id) REFERENCES phenotypeterm (id);
CREATE INDEX allelefunctionalimpact_singleallele_index ON allelefunctionalimpactslotannotation USING btree (singleallele_id);
CREATE INDEX allelefunctionalimpact_phenotypeterm_index ON allelefunctionalimpactslotannotation USING btree (phenotypeterm_id);

ALTER TABLE allelegeneassociation ADD CONSTRAINT allelegeneassociation_object_id_fk FOREIGN KEY (object_id) REFERENCES gene (id);
ALTER TABLE allelegeneassociation ADD CONSTRAINT allelegeneassociation_subject_id_fk FOREIGN KEY (subject_id) REFERENCES allele (id);
CREATE INDEX allelegeneassociation_object_index ON allelegeneassociation USING btree (object_id);
CREATE INDEX allelegeneassociation_subject_index ON allelegeneassociation USING btree (subject_id);

ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_evidencecode_id_fk FOREIGN KEY (evidencecode_id) REFERENCES ecoterm (id);

ALTER TABLE allelegermlinetransmissionstatusslotannotation ADD CONSTRAINT allelegermlinetransmissionstatus_singleallele_id_fk FOREIGN KEY (singleallele_id) REFERENCES allele (id);
CREATE INDEX allelegermlinetransmissionstatus_singleallele_index ON allelegermlinetransmissionstatusslotannotation USING btree (singleallele_id);

ALTER TABLE alleleinheritancemodeslotannotation ADD CONSTRAINT alleleinheritancemode_phenotypeterm_id_fk FOREIGN KEY (phenotypeterm_id) REFERENCES phenotypeterm (id);
ALTER TABLE alleleinheritancemodeslotannotation ADD CONSTRAINT alleleinheritancemode_singleallele_id_fk FOREIGN KEY (singleallele_id) REFERENCES allele (id);
CREATE INDEX alleleinheritancemode_phenotypeterm_index ON alleleinheritancemodeslotannotation USING btree (phenotypeterm_id);
CREATE INDEX alleleinheritancemode_singleallele_index ON alleleinheritancemodeslotannotation USING btree (singleallele_id);

ALTER TABLE allelemutationtypeslotannotation ADD CONSTRAINT allelemutationtype_singleallele_id_fk FOREIGN KEY (singleallele_id) REFERENCES allele (id);
CREATE INDEX allelemutationtype_singleallele_index ON allelemutationtypeslotannotation USING btree (singleallele_id);

ALTER TABLE allelemutationtypeslotannotation_soterm ADD CONSTRAINT allelemutationtypesa_soterm_mutationtypes_id_fk FOREIGN KEY (mutationtypes_id) REFERENCES soterm (id);
CREATE INDEX allelemutationtypesa_soterm_mutationtypes_index ON allelemutationtypeslotannotation_soterm USING btree (mutationtypes_id);

ALTER TABLE allelenomenclatureeventslotannotation ADD CONSTRAINT allelenomenclatureeventslotannotation_singleallele_id_fk FOREIGN KEY (singleallele_id) REFERENCES allele (id);
CREATE INDEX allelenomenclatureevent_singleallele_index ON allelenomenclatureeventslotannotation USING btree (singleallele_id);

ALTER TABLE allelesecondaryidslotannotation ADD CONSTRAINT allelesecondaryidslotannotation_singleallele_id_fk FOREIGN KEY (singleallele_id) REFERENCES allele (id);
CREATE INDEX allelesecondaryid_singleallele_index ON allelesecondaryidslotannotation USING btree (singleallele_id);

ALTER TABLE allelesymbolslotannotation ADD CONSTRAINT allelesymbolslotannotation_singleallele_id_fk FOREIGN KEY (singleallele_id) REFERENCES allele (id);
CREATE INDEX allelesymbol_singleallele_index ON allelesymbolslotannotation USING btree (singleallele_id);

ALTER TABLE allelesynonymslotannotation ADD CONSTRAINT allelesynonymslotannotation_singleallele_id_fk FOREIGN KEY (singleallele_id) REFERENCES allele (id);
CREATE INDEX allelesynonym_singleallele_index ON allelesynonymslotannotation USING btree (singleallele_id);

ALTER TABLE anatomicalterm ADD PRIMARY KEY (id);
ALTER TABLE anatomicalterm ADD CONSTRAINT anatomicalterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE anatomicalterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE anatomicalterm_aud ADD CONSTRAINT anatomicalterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE apoterm ADD PRIMARY KEY (id);
ALTER TABLE apoterm ADD CONSTRAINT apoterm_id_fk FOREIGN KEY (id) REFERENCES phenotypeterm (id);

ALTER TABLE apoterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE apoterm_aud ADD CONSTRAINT apoterm_aud_id_rev_fk (id, rev) REFERENCES phenotypeterm_aud (id, rev);

ALTER TABLE atpterm ADD PRIMARY KEY (id);
ALTER TABLE atpterm ADD CONSTRAINT atpterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE atpterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE atpterm_aud ADD CONSTRAINT atpterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE biologicalentity ADD PRIMARY KEY (id);
ALTER TABLE biologicalentity ADD CONSTRAINT biologicalentity_id_fk FOREIGN KEY (id) REFERENCES submittedobject (id);
ALTER TABLE biologicalentity ADD CONSTRAINT biologicalentity_taxon_id_fk FOREIGN KEY (taxon_id) REFERENCES ncbitaxonterm (id);
CREATE INDEX biologicalentity_taxon_index ON biologicalentity USING btree (taxon_id);

ALTER TABLE biologicalentity_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE biologicalentity_aud ADD CONSTRAINT biologicalentity_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES submittedobject_aud (id, rev);

ALTER TABLE bspoterm ADD PRIMARY KEY (id);
ALTER TABLE bspoterm ADD CONSTRAINT bspoterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE bspoterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE bspoterm_aud ADD CONSTRAINT bspoterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE bulkload ADD CONSTRAINT bulkload_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE bulkload_aud ADD CONSTRAINT bulkload_aud_id_rev_fk (id, rev) REFRENCES auditedobject_aud (id, rev);

ALTER TABLE bulkloadfile ADD CONSTRAINT bulkloadfile_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE bulkloadfile_aud ADD CONSTRAINT bulkloadfile_aud_id_rev_fk (id, rev) REFRENCES auditedobject_aud (id, rev);

ALTER TABLE bulkloadfileexception ADD CONSTRAINT bulkloadfileexception_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE bulkloadfileexception_aud ADD CONSTRAINT bulkloadfileexception_aud_id_rev_fk (id, rev) REFRENCES auditedobject_aud (id, rev);

ALTER TABLE bulkloadfilehistory ADD CONSTRAINT bulkloadfilehistory_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE bulkloadfilehistory_aud ADD CONSTRAINT bulkloadfilehistory_aud_id_rev_fk (id, rev) REFRENCES auditedobject_aud (id, rev);

ALTER TABLE bulkloadgroup ADD CONSTRAINT bulkloadgroup_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE bulkloadgroup_aud ADD CONSTRAINT bulkloadgroup_aud_id_rev_fk (id, rev) REFRENCES auditedobject_aud (id, rev);

ALTER TABLE chebiterm ADD PRIMARY KEY (id);
ALTER TABLE chebiterm ADD CONSTRAINT chebiterm_id_fk FOREIGN KEY (id) REFERENCES chemicalterm (id);

ALTER TABLE chebiterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE chebiterm_aud ADD CONSTRAINT chebiterm_aud_id_rev_fk (id, rev) REFERENCES chemicalterm_aud (id, rev);

ALTER TABLE chemicalterm ADD PRIMARY KEY (id);
ALTER TABLE chemicalterm ADD CONSTRAINT chemicalterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE chemicalterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE chemicalterm_aud ADD CONSTRAINT chemicalterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE clterm ADD PRIMARY KEY (id);
ALTER TABLE clterm ADD CONSTRAINT clterm_id_fk FOREIGN KEY (id) REFERENCES anatomicalterm (id);

ALTER TABLE clterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE clterm_aud ADD CONSTRAINT clterm_aud_id_rev_fk (id, rev) REFERENCES anatomicalterm_aud (id, rev);

ALTER TABLE cmoterm ADD PRIMARY KEY (id);
ALTER TABLE cmoterm ADD CONSTRAINT cmoterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE cmoterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE cmoterm_aud ADD CONSTRAINT cmoterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE conditionrelation ADD CONSTRAINT conditionrelation_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);
ALTER TABLE conditionrelation ADD CONSTRAINT conditionrelation_singlereference_id_fk FOREIGN KEY (singlereference_id) REFERENCES reference (id);

ALTER TABLE conditionrelation_aud ADD CONSTRAINT conditionrelation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES auditedobject_aud (id, rev);

ALTER TABLE construct_reference ADD CONSTRAINT construct_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES reference (id);
CREATE INDEX construct_reference_references_index ON construct_reference USING btree (references_id);

ALTER TABLE constructcomponentslotannotation ADD CONSTRAINT constructcomponentslotannotation_taxon_id_fk FOREIGN KEY (taxon_id) REFERENCES ncbitaxonterm (id);
CREATE INDEX constructcomponentslotannotation_taxon_index ON constructcomponentslotannotation USING btree (taxon_id);

ALTER TABLE constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_object_id_fk FOREIGN KEY (genomicentity_id) REFERENCES genomicentity (id);
CREATE INDEX constructgenomicentityassociation_object_index ON constructgenomicentityassociation USING btree (object_id);

ALTER TABLE crossreference ADD CONSTRAINT crossreference_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE crossreference_aud ADD CONSTRAINT crossreference_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES auditedobject_aud (id, rev);

ALTER TABLE curationreport ADD CONSTRAINT curationreport_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE curationreport_aud ADD CONSTRAINT curationreport_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES auditedobject_aud (id, rev);

ALTER TABLE curationreportgroup ADD CONSTRAINT curationreportgroup_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE curationreportgroup_aud ADD CONSTRAINT curationreportgroup_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES auditedobject_aud (id, rev);

ALTER TABLE curationreporthistory ADD CONSTRAINT curationreporthistory_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE curationreportgroup_aud ADD CONSTRAINT curationreporthistory_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES auditedobject_aud (id, rev);

ALTER TABLE daoterm ADD PRIMARY KEY (id);
ALTER TABLE daoterm ADD CONSTRAINT daoterm_id_fk FOREIGN KEY (id) REFERENCES anatomicalterm (id);

ALTER TABLE daoterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE daoterm_aud ADD CONSTRAINT daoterm_aud_id_rev_fk (id, rev) REFERENCES anatomicalterm_aud (id, rev);

ALTER TABLE dataprovider ADD CONSTRAINT dataprovider_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE dataprovider_aud ADD CONSTRAINT dataprovider_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES auditedobject_aud (id, rev);

ALTER TABLE diseaseannotation ADD CONSTRINAT diseaseannotation_object_id_fk FOREIGN KEY (object_id) REFERENCES doterm (id);
CREATE INDEX diseaseannotation_object_index ON diseaseannotation USING btree (object_id);

ALTER TABLE diseaseannotation_biologicalentity ADD CONSTRAINT diseaseannotation_biologicalentity_dgm_id_fk FOREIGN KEY (diseasegeneticmodifiers_id) REFERENCES biologicalentity (id);
CREATE INDEX diseaseannotation_biologicalentity_dgms_index ON diseaseannotation_biologicalentity USING btree (diseasegeneticmodifiers_id);

ALTER TABLE diseaseannotation_ecoterm ADD CONSTRAINT diseaseannotation_ecoterm_evidencecodes_id_fk FOREIGN KEY (evidencecodes_id) REFERENCES ecoterm (id);
CREATE INDEX diseaseannotation_ecoterm_evidencecodes_index ON diseaseannotation_ecoterm USING btree (evidencecodes_id);

ALTER TABLE diseaseannotation_gene ADD CONSTRAINT diseaseannotation_gene_with_id_fk FOREIGN KEY (with_id) REFERENCES gene (id);
CREATE INDEX diseaseannotation_gene_with_index ON diseaseannotation_gene USING btree (with_id);

ALTER TABLE doterm ADD PRIMARY KEY (id);
ALTER TABLE doterm ADD CONSTRAINT doterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE doterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE doterm_aud ADD CONSTRAINT doterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE dpoterm ADD PRIMARY KEY (id);
ALTER TABLE dpoterm ADD CONSTRAINT dpoterm_id_fk FOREIGN KEY (id) REFERENCES phenotypeterm (id);

ALTER TABLE dpoterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE dpoterm_aud ADD CONSTRAINT dpoterm_aud_id_rev_fk (id, rev) REFERENCES phenotypeterm_aud (id, rev);

ALTER TABLE ecoterm ADD PRIMARY KEY (id);
ALTER TABLE ecoterm ADD CONSTRAINT ecoterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE ecoterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE ecoterm_aud ADD CONSTRAINT ecoterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE emapaterm ADD PRIMARY KEY (id);
ALTER TABLE emapaterm ADD CONSTRAINT emapaterm_id_fk FOREIGN KEY (id) REFERENCES anatomicalterm (id);

ALTER TABLE emapaterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE emapaterm_aud ADD CONSTRAINT emapaterm_aud_id_rev_fk (id, rev) REFERENCES anatomicalterm_aud (id, rev);

ALTER TABLE evidenceassociation_informationcontententity ADD CONSTRAINT evidenceassociation_infocontent_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity (id);
CREATE INDEX evidenceassociation_infocontent_evidence_index ON evidenceassociation_informationcontententity USING btree (evidence_id);

ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditionchemical_id_fk FOREIGN KEY (conditionchemical_id) REFERENCES chemicalterm (id);
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditionid_id_fk FOREIGN KEY (conditionid_id) REFERENCES experimentalconditionontologyterm (id);
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditiontaxon_id_fk FOREIGN KEY (conditiontaxon_id) REFERENCES ncbitaxontermterm (id);
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditiongeneontology_id_fk FOREIGN KEY (conditiongeneontology_id) REFERENCES goterm (id);
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditionclass_id_fk FOREIGN KEY (conditionclass_id) REFERENCES zecoterm (id);
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditionanatomy_id_fk FOREIGN KEY (conditionanatomy_id) REFERENCES anatomicalterm (id);

ALTER TABLE experimentalconditionontologyterm ADD PRIMARY KEY (id);
ALTER TABLE experimentalconditionontologyterm ADD CONSTRAINT experimentalconditionontologyterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE experimentalconditionontologyterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE experimentalconditionontologyterm_aud ADD CONSTRAINT experimentalconditionontologyterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE fbdvterm ADD PRIMARY KEY (id);
ALTER TABLE fbdvterm ADD CONSTRAINT fbdvterm_id_fk FOREIGN KEY (id) REFERENCES stageterm (id);

ALTER TABLE fbdvterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE fbdvterm_aud ADD CONSTRAINT fbdvterm_aud_id_rev_fk (id, rev) REFERENCES stageterm_aud (id, rev);

ALTER TABLE gene ADD PRIMARY KEY (id);
ALTER TABLE gene ADD CONSTRAINT gene_id_fk FOREIGN KEY (id) REFERENCES genomicentity (id);
ALTER TABLE gene ADD CONSTRAINT gene_genetype_id_fk FOREIGN KEY (genetype_id) REFERENCES soterm (id);
CREATE INDEX gene_genetype_index ON gene USING btree (genetype_id);

ALTER TABLE gene_aud ADD PRIMARY_KEY (id, rev);
ALTER TABLE gene_aud ADD CONSTRAINT gene_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES genomicentity_aud (id, rev);

ALTER TABLE genediseaseannotation ADD CONSTRAINT genediseaseannotation_subject_id_fk FOREIGN KEY (subject_id) REFERENCES gene (id);
ALTER TABLE genediseaseannotation ADD CONSTRAINT genediseaseannotation_sgdstrainbackground_id_fk (sgdstrainbackground_id) REFERENCES affectedgenomicmodel (id);

ALTER TABLE genefullnameslotannotation ADD CONSTRAINT genefullnameslotannotation_singlegene_id_fk (singlegene_id) REFERENCES gene (id);
CREATE INDEX genefullname_singlegene_index ON genefullnameslotannotation USING btree (singlegene_id);

ALTER TABLE genesecondaryidslotannotation ADD CONSTRAINT genesecondaryidslotannotation_singlegene_id_fk (singlegene_id) REFERENCES gene (id);
CREATE INDEX genesecondaryid_singlegene_index ON genesecondaryidslotannotation USING btree (singlegene_id);

ALTER TABLE genesymbolslotannotation ADD CONSTRAINT genesymbolslotannotation_singlegene_id_fk (singlegene_id) REFERENCES gene (id);
CREATE INDEX genesymbol_singlegene_index ON genesymbolslotannotation USING btree (singlegene_id);

ALTER TABLE genesynonymslotannotation ADD CONSTRAINT genesynonymslotannotation_singlegene_id_fk (singlegene_id) REFERENCES gene (id);
CREATE INDEX genesynonym_singlegene_index ON genesynonymslotannotation USING btree (singlegene_id);

ALTER TABLE genesystematicnameslotannotation ADD CONSTRAINT genesystematicnameslotannotation_singlegene_id_fk (singlegene_id) REFERENCES gene (id);
CREATE INDEX genesystematicname_singlegene_index ON genesystematicnameslotannotation USING btree (singlegene_id);

ALTER TABLE genetogeneorthology ADD CONSTRAINT genetogeneorthology_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);
ALTER TABLE genetogeneorthology ADD CONSTRAINT genetogeneorthology_object_id_fk FOREIGN KEY (object_id) REFERENCES gene (id);
ALTER TABLE genetogeneorthology ADD CONSTRAINT genetogeneorthology_subject_id_fk FOREIGN KEY (subject_id) REFERENCES gene (id);
CREATE INDEX genetogeneorthology_object_index ON genetogeneorthology USING btree (object_id);
CREATE INDEX genetogeneorthology_subject_index ON genetogeneorthology USING btree (subject_id);

ALTER TABLE genetogeneorthologycurated ADD CONSTRAINT genetogeneorthologycurated_evidencecode_id_fk FOREIGN KEY (evidencecode_id) REFERENCES ecoterm (id);
ALTER TABLE genetogeneorthologycurated ADD CONSTRAINT genetogeneorthologycurated_singlereference_id_fk FOREIGN KEY (singlereference_id) REFERENCES reference (id);
CREATE INDEX genetogeneorthologycurated_evidencecode_index ON genetogeneorthologycurated USING btree (evidencecode_id);
CREATE INDEX genetogeneorthologycurated_singlereference_index ON genetogeneorthologycurated USING btree (singlereference_id);

ALTER TABLE genomicentity ADD PRIMARY KEY (id);
ALTER TABLE genomicentity ADD CONSTRAINT genomicentity_id_fk FOREIGN KEY (id) REFERENCES biologicalentity (id);

ALTER TABLE genomicentity_aud ADD PRIMARY_KEY (id, rev);
ALTER TABLE genomicentity_aud ADD CONSTRAINT genomicentity_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES biologicalentity_aud (id, rev);

ALTER TABLE genomicentity_crossreference ADD CONSTRAINT genomicentitycrossreference_genomicentity_id_fk FOREIGN KEY (genomicentity_id) REFERENCES genomicentity (id);
CREATE INDEX genomicentity_crossreference_ge_xref_index ON genomicentity_crossreference USING btree (genomicentity_id, crossreferences_id);
CREATE INDEX genomicentity_crossreference_genomicentity_index ON genomicentity_crossreference USING btree (genomicentity_id);

ALTER TABLE goterm ADD PRIMARY KEY (id);
ALTER TABLE goterm ADD CONSTRAINT goterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE goterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE goterm_aud ADD CONSTRAINT goterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE hpterm ADD PRIMARY KEY (id);
ALTER TABLE hpterm ADD CONSTRAINT hpterm_id_fk FOREIGN KEY (id) REFERENCES phenotypeterm (id);

ALTER TABLE hpterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE hpterm_aud ADD CONSTRAINT hpterm_aud_id_rev_fk (id, rev) REFERENCES phenotypeterm_aud (id, rev);

ALTER TABLE materm ADD PRIMARY KEY (id);
ALTER TABLE materm ADD CONSTRAINT materm_id_fk FOREIGN KEY (id) REFERENCES anatomicalterm (id);

ALTER TABLE materm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE materm_aud ADD CONSTRAINT materm_aud_id_rev_fk (id, rev) REFERENCES anatomicalterm_aud (id, rev);

ALTER TABLE miterm ADD PRIMARY KEY (id);
ALTER TABLE miterm ADD CONSTRAINT miterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE miterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE miterm_aud ADD CONSTRAINT miterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE mmoterm ADD PRIMARY KEY (id);
ALTER TABLE mmoterm ADD CONSTRAINT mmoterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE mmoterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE mmoterm_aud ADD CONSTRAINT mmoterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE mmusdvterm ADD PRIMARY KEY (id);
ALTER TABLE mmusdvterm ADD CONSTRAINT mmusdvterm_id_fk FOREIGN KEY (id) REFERENCES stageterm (id);

ALTER TABLE mmusdvterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE mmusdvterm_aud ADD CONSTRAINT mmusdvterm_aud_id_rev_fk (id, rev) REFERENCES stageterm_aud (id, rev);

ALTER TABLE modterm ADD PRIMARY KEY (id);
ALTER TABLE modterm ADD CONSTRAINT modterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE modterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE modterm_aud ADD CONSTRAINT modterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE molecule ADD PRIMARY KEY (id);
ALTER TABLE molecule ADD CONSTRAINT molecule_id_fk FOREIGN KEY (id) REFERENCES chemicalterm (id);

ALTER TABLE molecule_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE molecule_aud ADD CONSTRAINT molecule_aud_id_rev_fk (id, rev) REFERENCES chemicalterm_aud (id, rev);

ALTER TABLE mpathterm ADD PRIMARY KEY (id);
ALTER TABLE mpathterm ADD CONSTRAINT mpathterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE mpathterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE mpathterm_aud ADD CONSTRAINT mpathterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE mpterm ADD PRIMARY KEY (id);
ALTER TABLE mpterm ADD CONSTRAINT mpterm_id_fk FOREIGN KEY (id) REFERENCES phenotypeterm (id);

ALTER TABLE mpterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE mpterm_aud ADD CONSTRAINT mpterm_aud_id_rev_fk (id, rev) REFERENCES phenotypeterm_aud (id, rev);

ALTER TABLE ncbitaxonterm ADD PRIMARY KEY (id);
ALTER TABLE ncbitaxonterm ADD CONSTRAINT ncbitaxonterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE ncbitaxonterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE ncbitaxonterm_aud ADD CONSTRAINT ncbitaxonterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE note ADD CONSTRAINT note_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE note_aud ADD CONSTRAINT note_aud_id_rev_fk (id, rev) REFRENCES auditedobject_aud (id, rev);

ALTER TABLE note_reference ADD CONSTRAINT note_reference_references_id_fk FOREIGN KEY (references_id) REFERENCES reference (id);
CREATE INDEX note_reference_references_index ON note_reference USING btree (references_id);

ALTER TABLE obiterm ADD PRIMARY KEY (id);
ALTER TABLE obiterm ADD CONSTRAINT obiterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE obiterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE obiterm_aud ADD CONSTRAINT obiterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE ontologyterm ADD PRIMARY KEY (id);
ALTER TABLE ontologyterm ADD CONSTRAINT ontologyterm_id_fk FOREIGN KEY (id) REFERENCES curieobject (id);

ALTER TABLE ontologyterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE ontologyterm_aud ADD CONSTRAINT ontologyterm_aud_id_rev_fk (id, rev) REFERENCES curieobject_aud (id, rev);

ALTER TABLE ontologyterm_crossreference ADD CONSTRAINT ontologyterm_crossreference_ontologyterm_id_fk FOREIGN KEY (ontologyterm_id) REFERENCES ontologyterm (id);
CREATE INDEX ontologyterm_crossreference_ontologyterm_index ON ontologyterm_crossreference USING btree (ontologyterm_id);

ALTER TABLE ontologyterm_definitionurls ADD CONSTRAINT ontologyterm_definitionurls_ontologyterm_id_fk FOREIGN KEY (ontologyterm_id) REFERENCES ontologyterm (id);
CREATE INDEX ontologyterm_definitionurls_ontologyterm_index ON ontologyterm_definitionurls USING btree (ontologyterm_id);

ALTER TABLE ontologyterm_isa_ancestor_descendant ADD PRIMARY KEY (isadescendants_id, isaancestors_id);
ALTER TABLE ontologyterm_isa_ancestor_descendant ADD CONSTRAINT ontologyterm_isa_ancestor_descendant_isaancestors_id_fk FOREIGN KEY (isaancestors_id) REFERENCES ontologyterm(id);
ALTER TABLE ontologyterm_isa_ancestor_descendant ADD CONSTRAINT ontologyterm_isa_ancestor_descendant_isadescendants_id_fk FOREIGN KEY (isadescendants_id) REFERENCES ontologyterm(id);
CREATE INDEX ontologyterm_isa_ancestor_descendant_isaancestors_index ON ontologyterm_isa_ancestor_descendant USING btree (isaancestors_id);
CREATE INDEX ontologyterm_isa_ancestor_descendant_isadescendants_index ON ontologyterm_isa_ancestor_descendant USING btree (isadescendants_id);

ALTER TABLE ontologyterm_isa_parent_children ADD PRIMARY KEY (isachildren_id, isaparents_id);
ALTER TABLE ontologyterm_isa_parent_children ADD CONSTRAINT ontologyterm_isa_parent_children_isachildren_id_fk FOREIGN KEY (isachildren_id) REFERENCES ontologyterm (id);
ALTER TABLE ontologyterm_isa_parent_children ADD CONSTRAINT ontologyterm_isa_parent_children_isaparents_id_fk FOREIGN KEY (isaparents_id) REFERENCES ontologyterm (id);
CREATE INDEX ontologyterm_isa_parent_children_isachildren_index ON ontologyterm_isa_parent_children USING btree (isachildren_id);
CREATE INDEX ontologyterm_isa_parent_children_isaparents_index ON ontologyterm_isa_parent_children USING btree (isaparents_id);

ALTER TABLE ontologyterm_secondaryidentifiers ADD CONSTRAINT ontologyterm_secondaryidentifiers_ontologyterm_id_fk FOREIGN KEY (ontologyterm_id) REFERENCES ontologyterm (id);
CREATE INDEX ontologyterm_secondaryidentifiers_ontologyterm_index ON ontologyterm_secondaryidentifiers USING btree (ontologyterm_id);

ALTER TABLE ontologyterm_subsets ADD CONSTRAINT ontologyterm_subsets_ontologyterm_id_fk FOREIGN KEY (ontologyterm_id) REFERENCES ontologyterm (id);
CREATE INDEX ontologyterm_subsets_ontologyterm_index ON ontologyterm_subsets USING btree (ontologyterm_id);

ALTER TABLE ontologyterm_synonym ADD CONSTRAINT ontologyterm_synonym_ontologyterm_id_fk FOREIGN KEY (ontologyterm_id) REFERENCES ontologyterm (id);
CREATE INDEX ontologyterm_synonym_ontologyterm_index ON ontologyterm_synonym USING btree (ontologyterm_id);

ALTER TABLE organization ADD CONSTRAINT organization_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE organization_aud ADD CONSTRAINT organization_aud_id_rev_fk (id, rev) REFERENCES auditedobject_aud (id, rev);

ALTER TABLE patoterm ADD PRIMARY KEY (id);
ALTER TABLE patoterm ADD CONSTRAINT patoterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE patoterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE patoterm_aud ADD CONSTRAINT patoterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE person ADD CONSTRAINT person_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE person_aud ADD CONSTRAINT person_aud_id_rev_fk (id, rev) REFERENCES auditedobject_aud (id, rev);

ALTER TABLE phenotypeterm ADD PRIMARY KEY (id);
ALTER TABLE phenotypeterm ADD CONSTRAINT phenotypeterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE phenotypeterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE phenotypeterm_aud ADD CONSTRAINT phenotypeterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE pwterm ADD PRIMARY KEY (id);
ALTER TABLE pwterm ADD CONSTRAINT pwterm_id_fk FOREIGN KEY (id) REFERENCES ontologyterm (id);

ALTER TABLE pwterm_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE pwterm_aud ADD CONSTRAINT pwterm_aud_id_rev_fk (id, rev) REFERENCES ontologyterm_aud (id, rev);

ALTER TABLE reagent ADD CONSTRAINT reagent_id_fk FOREIGN KEY (id) REFERENCES submittedobject (id);

ALTER TABLE reagent_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE reagent_aud ADD CONSTRAINT reagent_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES submittedobject_aud (id, rev);

ALTER TABLE reference ADD PRIMARY KEY (id);
ALTER TABLE reference ADD CONSTRAINT reference_id_fk FOREIGN KEY (id) REFERENCES informationcontententity (id);

ALTER TABLE reference_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE reference_aud ADD CONSTRAINT reference_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES informationcontententity_aud (id, rev);

ALTER TABLE reference_crossreference ADD CONSTRAINT reference_crossreference_reference_id_fk FOREIGN KEY (reference_id) REFERENCES reference (id);
CREATE INDEX reference_crossreference_reference_index ON reference_crossreference USING btree (reference_id);

ALTER TABLE resourcedescriptor ADD CONSTRAINT resourcedescriptor_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE resourcedescriptor_aud ADD CONSTRAINT resourcedescriptor_aud_id_rev_fk (id, rev) REFERENCES auditedobject_aud (id, rev);

ALTER TABLE resourcedescriptorpage ADD CONSTRAINT resourcedescriptorpage_id_fk FOREIGN KEY (id) REFERENCES auditedobject (id);

ALTER TABLE resourcedescriptorpage_aud ADD CONSTRAINT resourcedescriptorpage_aud_id_rev_fk (id, rev) REFERENCES auditedobject_aud (id, rev);