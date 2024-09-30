CREATE INDEX diseaseannotation_annotationtype_index ON diseaseannotation USING btree (annotationtype_id);
CREATE INDEX agmdiseaseannotation_assertedallele_index ON agmdiseaseannotation USING btree (assertedallele_id);
CREATE INDEX diseaseannotation_createdby_index ON diseaseannotation USING btree (createdby_id);
CREATE INDEX diseaseannotation_curie_index ON diseaseannotation USING btree (curie);
CREATE INDEX diseaseannotation_dataprovider_index ON diseaseannotation USING btree (dataprovider_id);
CREATE INDEX diseaseannotation_diseaseannotationobject_index ON diseaseannotation USING btree (diseaseannotationobject_id);
CREATE INDEX agmdiseaseannotation_diseaseannotationsubject_index ON agmdiseaseannotation USING btree (diseaseannotationsubject_id);
CREATE INDEX diseaseannotation_diseasegeneticmodifierrelation_index ON diseaseannotation USING btree (diseasegeneticmodifierrelation_id);
CREATE INDEX diseaseannotation_geneticsex_index ON diseaseannotation USING btree (geneticsex_id);
CREATE INDEX agmdiseaseannotation_inferredallele_index ON agmdiseaseannotation USING btree (inferredallele_id);
CREATE INDEX agmdiseaseannotation_inferredgene_index ON agmdiseaseannotation USING btree (inferredgene_id);
CREATE INDEX diseaseannotation_internal_index ON diseaseannotation USING btree (internal);
CREATE INDEX diseaseannotation_modentityid_index ON diseaseannotation USING btree (modentityid);
CREATE INDEX diseaseannotation_modinternalid_index ON diseaseannotation USING btree (modinternalid);
CREATE INDEX diseaseannotation_negated_index ON diseaseannotation USING btree (negated);
CREATE INDEX diseaseannotation_obsolete_index ON diseaseannotation USING btree (obsolete);
CREATE INDEX diseaseannotation_relation_index ON diseaseannotation USING btree (relation_id);
CREATE INDEX diseaseannotation_secondarydataprovider_index ON diseaseannotation USING btree (secondarydataprovider_id);
CREATE INDEX diseaseannotation_singlereference_index ON diseaseannotation USING btree (singlereference_id);
CREATE INDEX diseaseannotation_uniqueid_index ON diseaseannotation USING btree (uniqueid);
CREATE INDEX diseaseannotation_updatedby_index ON diseaseannotation USING btree (updatedby_id);

-- CREATE INDEX idxfm9hjt2cp5opepuhro41rko11 ON diseaseannotation_biologicalentity USING btree (annotation_id);
-- CREATE INDEX idx2q76899x3i9nmfrpk1s4gqaa5 ON diseaseannotation_biologicalentity USING btree (diseasegeneticmodifiers_id);

-- CREATE INDEX idx7ckisymg9glquxs8ckv58o4wa ON diseaseannotation_conditionrelation USING btree (annotation_id);
-- CREATE INDEX idx6ie2uykt18gbauf5t26c9gw4p ON diseaseannotation_conditionrelation USING btree (conditionrelations_id);

CREATE INDEX agmdiseaseannotation_gene_assertedgenes_index  ON agmdiseaseannotation_gene USING btree (assertedgenes_id);
CREATE INDEX agmdiseaseannotation_gene_agmda_index ON agmdiseaseannotation_gene USING btree (agmdiseaseannotation_id);
CREATE INDEX diseaseannotation_gene_da_index ON diseaseannotation_gene USING btree (diseaseannotation_id);
CREATE INDEX diseaseannotation_gene_with_index ON diseaseannotation_gene USING btree (with_id);

-- CREATE INDEX idxn2ac81velbbenm2oce4xqkyy0 ON diseaseannotation_note USING btree (annotation_id);
-- CREATE INDEX idx6kkpigf83ojt26fiqg2w1dy1p ON diseaseannotation_note USING btree (relatednotes_id);

CREATE INDEX diseaseannotation_ontologyterm_da_index ON diseaseannotation_ontologyterm USING btree (diseaseannotation_id);
CREATE INDEX diseaseannotation_ontologyterm_evidencecodes_index ON diseaseannotation_ontologyterm USING btree (evidencecodes_id);

CREATE INDEX diseaseannotation_vocabularyterm_da_index ON diseaseannotation_vocabularyterm USING btree (diseaseannotation_id);
CREATE INDEX diseaseannotation_vocabularyterm_dq_index ON diseaseannotation_vocabularyterm USING btree (diseasequalifiers_id);

CREATE INDEX agmphenotypeannotation_assertedallele_index ON agmphenotypeannotation USING btree (assertedallele_id);
CREATE INDEX phenotypeannotation_createdby_index ON phenotypeannotation USING btree (createdby_id);
CREATE INDEX phenotypeannotation_crossreference_index ON phenotypeannotation USING btree (crossreference_id);
CREATE INDEX phenotypeannotation_curie_index ON phenotypeannotation USING btree (curie);
CREATE INDEX phenotypeannotation_dataprovider_index ON phenotypeannotation USING btree (dataprovider_id);
CREATE INDEX agmphenotypeannotation_inferredallele_index ON agmphenotypeannotation USING btree (inferredallele_id);
CREATE INDEX agmphenotypeannotation_inferredgene_index ON agmphenotypeannotation USING btree (inferredgene_id);
CREATE INDEX phenotypeannotation_internal_index ON phenotypeannotation USING btree (internal);
CREATE INDEX phenotypeannotation_modentityid_index ON phenotypeannotation USING btree (modentityid);
CREATE INDEX phenotypeannotation_modinternalid_index ON phenotypeannotation USING btree (modinternalid);
CREATE INDEX phenotypeannotation_obsolete_index ON phenotypeannotation USING btree (obsolete);
CREATE INDEX agmphenotypeannotation_phenotypeannotationsubject_index ON agmphenotypeannotation USING btree (phenotypeannotationsubject_id);
CREATE INDEX phenotypeannotation_relation_index ON phenotypeannotation USING btree (relation_id);
CREATE INDEX phenotypeannotation_singlereference_index ON phenotypeannotation USING btree (singlereference_id);
CREATE INDEX phenotypeannotation_uniqueid_index ON phenotypeannotation USING btree (uniqueid);
CREATE INDEX phenotypeannotation_updatedby_index ON phenotypeannotation USING btree (updatedby_id);

-- CREATE INDEX idx4p03xofye2drhcxfv4wh5btvj ON phenotypeannotation_conditionrelation USING btree (annotation_id);
-- CREATE INDEX idx84beulwvg0hkmudmow0dlsxv7 ON phenotypeannotation_conditionrelation USING btree (conditionrelations_id);

CREATE INDEX agmphenotypeannotation_gene_assertedgenes_index ON agmphenotypeannotation_gene USING btree (assertedgenes_id);
CREATE INDEX agmphenotypeannotation_gene_agmpa_index ON agmphenotypeannotation_gene USING btree (agmphenotypeannotation_id);

-- CREATE INDEX idxrgqdq9ngi6fi44kqb6l6gqn1s ON phenotypeannotation_note USING btree (annotation_id);
-- CREATE INDEX idxit2xg2ed815njy85kl3hgsjwu ON phenotypeannotation_note USING btree (relatednotes_id);

CREATE INDEX phenotypeannotation_ontologyterm_phenotypeterms_index ON phenotypeannotation_ontologyterm USING btree (phenotypeterms_id);
CREATE INDEX phenotypeannotation_ontologyterm_pa_index ON phenotypeannotation_ontologyterm USING btree (phenotypeannotation_id);

CREATE INDEX allelediseaseannotation_diseaseannotationsubject_index ON allelediseaseannotation USING btree (diseaseannotationsubject_id);
CREATE INDEX allelediseaseannotation_inferredgene_index ON allelediseaseannotation USING btree (inferredgene_id);

CREATE INDEX allelediseaseannotation_gene_assertedgenes_index ON allelediseaseannotation_gene USING btree (assertedgenes_id);
CREATE INDEX allelediseaseannotation_gene_alleleda_index ON allelediseaseannotation_gene USING btree (allelediseaseannotation_id);

CREATE INDEX allelegeneassociation_alleleassociationsubject_index ON allelegeneassociation USING btree (alleleassociationsubject_id);
CREATE INDEX allelegeneassociation_allelegeneassociationobject_index ON allelegeneassociation USING btree (allelegeneassociationobject_id);
CREATE INDEX allelegeneassociation_createdby_index ON allelegeneassociation USING btree (createdby_id);
CREATE INDEX allelegeneassociation_evidencecode_index ON allelegeneassociation USING btree (evidencecode_id);
CREATE INDEX allelegeneassociation_internal_index ON allelegeneassociation USING btree (internal);
CREATE INDEX allelegeneassociation_obsolete_index ON allelegeneassociation USING btree (obsolete);
CREATE INDEX allelegeneassociation_relatednote_index ON allelegeneassociation USING btree (relatednote_id);
CREATE INDEX allelegeneassociation_relation_index ON allelegeneassociation USING btree (relation_id);
CREATE INDEX allelegeneassociation_updatedby_index ON allelegeneassociation USING btree (updatedby_id);

-- CREATE INDEX idx9u4cqfbtajokovj1quacsvld2 ON allelegeneassociation_informationcontententity USING btree (association_id);
-- CREATE INDEX idxp3na9i2xw0ea9igcwx1jdpc9g ON allelegeneassociation_informationcontententity USING btree (evidence_id);

CREATE INDEX allelephenotypeannotation_inferredgene_index ON allelephenotypeannotation USING btree (inferredgene_id);
CREATE INDEX allelephenotypeannotation_phenotypeannotationsubject_index ON allelephenotypeannotation USING btree (phenotypeannotationsubject_id);

CREATE INDEX allelephenotypeannotation_gene_assertedgenes_index ON allelephenotypeannotation_gene USING btree (assertedgenes_id);
CREATE INDEX allelephenotypeannotation_gene_allelepa_index ON allelephenotypeannotation_gene USING btree (allelephenotypeannotation_id);

-- CREATE INDEX idxebg770w4q4fayk1vpfucdu1xy ON codingsequencegenomiclocationassociation USING btree (codingsequenceassociationsubject_id);
-- CREATE INDEX idxbmljoyqmk4cdd3os0dumc9bgp ON codingsequencegenomiclocationassociation USING btree (codingsequencegenomiclocationassociationobject_id);
-- CREATE INDEX idx6khftnwxcrifhid51ffnd054g ON codingsequencegenomiclocationassociation USING btree (createdby_id);
-- CREATE INDEX idxhfv6me8h9mleiytvoytio0nsi ON codingsequencegenomiclocationassociation USING btree (internal);
-- CREATE INDEX idxgrky8raqexv45mq83suvekrdo ON codingsequencegenomiclocationassociation USING btree (obsolete);
-- CREATE INDEX idxa5wjxifv7vi0y5ltgdnp5ep9t ON codingsequencegenomiclocationassociation USING btree (phase);
-- CREATE INDEX idxkr58i2vv5784vl6osva8shi9e ON codingsequencegenomiclocationassociation USING btree (relation_id);
-- CREATE INDEX idxr0bkeewpnaajqi1xds5c7j1oc ON codingsequencegenomiclocationassociation USING btree (strand);
-- CREATE INDEX idxsb0mnawmi55r20kcipnykm41q ON codingsequencegenomiclocationassociation USING btree (updatedby_id);

-- CREATE INDEX idxp4b569h2wevn6yxgv6veonxfr ON CodingSequenceGenomicLocationAssociation_InformationContentEntity USING btree (association_id);
-- CREATE INDEX idxp0ldc7a577pft5v9959muhdh5 ON CodingSequenceGenomicLocationAssociation_InformationContentEntity USING btree (evidence_id);

-- CREATE INDEX idxvsibphiuleew24wjckpfxeae  ON constructgenomicentityassociation USING btree (internal);
-- CREATE INDEX idx8myuo0a6hx59vxfhlbrcn8ujk ON constructgenomicentityassociation USING btree (constructassociationsubject_id);
-- CREATE INDEX idxevduykusoc08mycbrmn4o7dnq ON constructgenomicentityassociation USING btree (constructgenomicentityassociationobject_id);
-- CREATE INDEX idxch64a8ot2vv6i0rue8khyjb3m ON constructgenomicentityassociation USING btree (createdby_id);
-- CREATE INDEX idxi6hc4krcrxpsrrwj728vd2wpx ON constructgenomicentityassociation USING btree (obsolete);
-- CREATE INDEX idxsoyddowolab2mrp5eaxj57yar ON constructgenomicentityassociation USING btree (relation_id);
-- CREATE INDEX idxevh4c07xjfxyylj7ck2dtn6jh ON constructgenomicentityassociation USING btree (updatedby_id);

-- CREATE INDEX idxg5fxj3nedebt3cw93uxptf7om ON constructgenomicentityassociation_informationcontententity USING btree (association_id);
-- CREATE INDEX idxb9tloa39yyvirjvacah10hfd5 ON constructgenomicentityassociation_informationcontententity USING btree (evidence_id);

CREATE INDEX constructgeassociation_note_cgea_index ON constructgenomicentityassociation_note USING btree (constructgenomicentityassociation_id);
CREATE INDEX constructgeassociation_note_relatednotes_index ON constructgenomicentityassociation_note USING btree (relatednotes_id);

-- CREATE INDEX idxhgjuh3b5r08q54fwecp64k3a4 ON exongenomiclocationassociation USING btree (createdby_id);
-- CREATE INDEX idx82dedqro7ri1f8g558icrjxc6 ON exongenomiclocationassociation USING btree (exonassociationsubject_id);
-- CREATE INDEX idx53jvdw533s5e082xlj0sx7p7x ON exongenomiclocationassociation USING btree (exongenomiclocationassociationobject_id);
-- CREATE INDEX idx2knwxko11qkxvr98w7l76wlgi ON exongenomiclocationassociation USING btree (internal);
-- CREATE INDEX idxo923wv3e7dg9e2flm08ghwiig ON exongenomiclocationassociation USING btree (obsolete);
-- CREATE INDEX idx5ufu3a6guayhxy2cg6crxde2i ON exongenomiclocationassociation USING btree (relation_id);
-- CREATE INDEX idxdwfj56lqtx86brdvwafjqo8nn ON exongenomiclocationassociation USING btree (strand);
-- CREATE INDEX idxd12130inhcxcp1dbks190955j ON exongenomiclocationassociation USING btree (updatedby_id); 

-- CREATE INDEX idxtaunnvner3uu6vfarcb9kv0o1 ON exongenomiclocationassociation_informationcontententity USING btree (association_id);
-- CREATE INDEX idxbugjixb2a6vb0xuho653me8as ON exongenomiclocationassociation_informationcontententity USING btree (evidence_id);

CREATE INDEX genediseaseannotation_diseaseannotationsubject_index ON genediseaseannotation USING btree (diseaseannotationsubject_id);
CREATE INDEX genediseaseannotation_sgdstrainbackground_index ON genediseaseannotation USING btree (sgdstrainbackground_id);

CREATE INDEX geneexpressionannotation_createdby_index ON geneexpressionannotation USING btree (createdby_id);
CREATE INDEX geneexpressionannotation_curie_index ON geneexpressionannotation USING btree (curie);
CREATE INDEX geneexpressionannotation_dataprovider_index ON geneexpressionannotation USING btree (dataprovider_id);
CREATE INDEX geneexpressionannotation_expressionannotationsubject_index ON geneexpressionannotation USING btree (expressionannotationsubject_id);
CREATE INDEX geneexpressionannotation_expressionassayused_index ON geneexpressionannotation USING btree (expressionassayused_id);
CREATE INDEX geneexpressionannotation_expressionpattern_index ON geneexpressionannotation USING btree (expressionpattern_id);
CREATE INDEX geneexpressionannotation_internal_index ON geneexpressionannotation USING btree (internal);
CREATE INDEX geneexpressionannotation_modentityid_index ON geneexpressionannotation USING btree (modentityid);
CREATE INDEX geneexpressionannotation_modinternalid_index ON geneexpressionannotation USING btree (modinternalid);
CREATE INDEX geneexpressionannotation_obsolete_index ON geneexpressionannotation USING btree (obsolete);
CREATE INDEX geneexpressionannotation_relation_index ON geneexpressionannotation USING btree (relation_id);
CREATE INDEX geneexpressionannotation_singlereference_index ON geneexpressionannotation USING btree (singlereference_id);
CREATE INDEX geneexpressionannotation_uniqueid_index ON geneexpressionannotation USING btree (uniqueid);
CREATE INDEX geneexpressionannotation_updatedby_index ON geneexpressionannotation USING btree (updatedby_id);

-- CREATE INDEX idxa1uthowkvl1bch1namofq466f ON geneexpressionannotation_conditionrelation USING btree (annotation_id);
-- CREATE INDEX idxrcptfqy2xuw35xeer5ilrqtc1 ON geneexpressionannotation_conditionrelation USING btree (conditionrelations_id);

-- CREATE INDEX idx87c6fqrsecyifcktu92y7jde4 ON geneexpressionannotation_note USING btree (annotation_id);
-- CREATE INDEX idxm4mig223w5vb6bpjubkd5xuac ON geneexpressionannotation_note USING btree (relatednotes_id);

CREATE INDEX genegeneticinteraction_createdby_index ON genegeneticinteraction USING btree (createdby_id);
CREATE INDEX genegeneticinteraction_geneassociationsubject_index ON genegeneticinteraction USING btree (geneassociationsubject_id);
CREATE INDEX genegeneticinteraction_genegeneassociationobject_index ON genegeneticinteraction USING btree (genegeneassociationobject_id);
CREATE INDEX genegeneticinteraction_interactionid_index ON genegeneticinteraction USING btree (interactionid);
CREATE INDEX genegeneticinteraction_interactionsource_index ON genegeneticinteraction USING btree (interactionsource_id);
CREATE INDEX genegeneticinteraction_interactiontype_index ON genegeneticinteraction USING btree (interactiontype_id);
CREATE INDEX genegeneticinteraction_interactorageneticperturbation_index ON genegeneticinteraction USING btree (interactorageneticperturbation_id);
CREATE INDEX genegeneticinteraction_interactorarole_index ON genegeneticinteraction USING btree (interactorarole_id);
CREATE INDEX genegeneticinteraction_interactoratype_index ON genegeneticinteraction USING btree (interactoratype_id);
CREATE INDEX genegeneticinteraction_interactorbgeneticperturbation_index ON genegeneticinteraction USING btree (interactorbgeneticperturbation_id);
CREATE INDEX genegeneticinteraction_interactorbrole_index ON genegeneticinteraction USING btree (interactorbrole_id);
CREATE INDEX genegeneticinteraction_interactorbtype_index ON genegeneticinteraction USING btree (interactorbtype_id);
CREATE INDEX genegeneticinteraction_internal_index ON genegeneticinteraction USING btree (internal);
CREATE INDEX genegeneticinteraction_obsolete_index ON genegeneticinteraction USING btree (obsolete);
CREATE INDEX genegeneticinteraction_relation_index ON genegeneticinteraction USING btree (relation_id);
CREATE INDEX genegeneticinteraction_uniqueid_index ON genegeneticinteraction USING btree (uniqueid);
CREATE INDEX genegeneticinteraction_updatedby_index ON genegeneticinteraction USING btree (updatedby_id);

-- CREATE INDEX idxlyqi995di8uvyll25qqmvvsnq ON genegeneticinteraction_crossreference USING btree (geneinteraction_id);
-- CREATE INDEX idxolfx2gsnlx0vyvpv85ba3s1my ON genegeneticinteraction_crossreference USING btree (crossreferences_id);

-- CREATE INDEX idxiwxeu6vppndg2hl252knhgcm2 ON genegeneticinteraction_informationcontententity USING btree (association_id);
-- CREATE INDEX idx3l5ew8bcu7dqbqu2gux877rqp ON genegeneticinteraction_informationcontententity USING btree (evidence_id);

CREATE INDEX genegeneticinteraction_phenotypesortraits_interaction_index ON genegeneticinteraction_phenotypesortraits USING btree (genegeneticinteraction_id);
CREATE INDEX genegeneticinteraction_phenotypesortraits_pt_index ON genegeneticinteraction_phenotypesortraits USING btree (phenotypesortraits);

CREATE INDEX genemolecularinteraction_aggregationdatabase_index ON genemolecularinteraction USING btree (aggregationdatabase_id);
CREATE INDEX genemolecularinteraction_createdby_index ON genemolecularinteraction USING btree (createdby_id);
CREATE INDEX genemolecularinteraction_detectionmethod_index ON genemolecularinteraction USING btree (detectionmethod_id);
CREATE INDEX genemolecularinteraction_geneassociationsubject_index ON genemolecularinteraction USING btree (geneassociationsubject_id);
CREATE INDEX genemolecularinteraction_genegeneassociationobject_index ON genemolecularinteraction USING btree (genegeneassociationobject_id);
CREATE INDEX genemolecularinteraction_interactionid_index ON genemolecularinteraction USING btree (interactionid);
CREATE INDEX genemolecularinteraction_interactionsource_index ON genemolecularinteraction USING btree (interactionsource_id);
CREATE INDEX genemolecularinteraction_interactiontype_index ON genemolecularinteraction USING btree (interactiontype_id);
CREATE INDEX genemolecularinteraction_interactorarole_index ON genemolecularinteraction USING btree (interactorarole_id);
CREATE INDEX genemolecularinteraction_interactoratype_index ON genemolecularinteraction USING btree (interactoratype_id);
CREATE INDEX genemolecularinteraction_interactorbrole_index ON genemolecularinteraction USING btree (interactorbrole_id);
CREATE INDEX genemolecularinteraction_interactorbtype_index ON genemolecularinteraction USING btree (interactorbtype_id);
CREATE INDEX genemolecularinteraction_internal_index ON genemolecularinteraction USING btree (internal);
CREATE INDEX genemolecularinteraction_obsolete_index ON genemolecularinteraction USING btree (obsolete);
CREATE INDEX genemolecularinteraction_relation_index ON genemolecularinteraction USING btree (relation_id);
CREATE INDEX genemolecularinteraction_uniqueid_index ON genemolecularinteraction USING btree (uniqueid);
CREATE INDEX genemolecularinteraction_updatedby_index ON genemolecularinteraction USING btree (updatedby_id);

-- CREATE INDEX idx7lx7isr6eb5w0w5in5vsncutg ON genemolecularinteraction_crossreference USING btree (crossreferences_id);
-- CREATE INDEX idx7wsd79wrsj6s8e33ij89a3eg8 ON genemolecularinteraction_crossreference USING btree (geneinteraction_id);

-- CREATE INDEX idxcuc2oxltucskw9yld02sck6kk ON genemolecularinteraction_informationcontententity USING btree (association_id);
-- CREATE INDEX idxqdpatuorfp3xeoa2ogmg20x1q ON genemolecularinteraction_informationcontententity USING btree (evidence_id);

CREATE INDEX genephenotypeannotation_phenotypeannotationsubject_index ON genephenotypeannotation USING btree (phenotypeannotationsubject_id);
CREATE INDEX genephenotypeannotation_sgdstrainbackground_index ON genephenotypeannotation USING btree (sgdstrainbackground_id);

CREATE INDEX sqtrgeneassociation_createdby_index ON sequencetargetingreagentgeneassociation USING btree (createdby_id);
CREATE INDEX sqtrgeneassociation_internal_index ON sequencetargetingreagentgeneassociation USING btree (internal);
CREATE INDEX sqtrgeneassociation_obsolete_index ON sequencetargetingreagentgeneassociation USING btree (obsolete);
CREATE INDEX sqtrgeneassociation_relation_index ON sequencetargetingreagentgeneassociation USING btree (relation_id);
CREATE INDEX sqtrgeneassociation_sqtrassociationsubject_index ON sequencetargetingreagentgeneassociation USING btree (sequencetargetingreagentassociationsubject_id);
CREATE INDEX sqtrgeneassociation_sqtrgeneassociationobject_index ON sequencetargetingreagentgeneassociation USING btree (sequencetargetingreagentgeneassociationobject_id);
CREATE INDEX sqtrgeneassociation_updatedby_index ON sequencetargetingreagentgeneassociation USING btree (updatedby_id);

-- CREATE INDEX idx3qy7to4cavfvoj89fkux971yb ON sequencetargetingreagentgeneassociation_informationcontententit USING btree (association_id);
-- CREATE INDEX idxeo9v38oy64ff8lbhb656yl532 ON sequencetargetingreagentgeneassociation_informationcontententit USING btree (evidence_id);

-- CREATE INDEX idxon9k4nqnwlav7ammge7obyshm ON transcriptcodingsequenceassociation USING btree (createdby_id);
-- CREATE INDEX idxf95tn5xv2nugt594ch7kggtex ON transcriptcodingsequenceassociation USING btree (internal);
-- CREATE INDEX idxd7d7oeub6eclirxmjpmc7ibot ON transcriptcodingsequenceassociation USING btree (obsolete);
-- CREATE INDEX idxio6g2jejebbjqugso2ge8cncf ON transcriptcodingsequenceassociation USING btree (relation_id);
-- CREATE INDEX idxptss0twuhfg2ibqnb8vai4v2v ON transcriptcodingsequenceassociation USING btree (transcriptassociationsubject_id);
-- CREATE INDEX idx8jsnbfbebppm2memg9yywpea6 ON transcriptcodingsequenceassociation USING btree (transcriptcodingsequenceassociationobject_id);
-- CREATE INDEX idxe31s0f5w54vnjhxeysi3g1oy0 ON transcriptcodingsequenceassociation USING btree (updatedby_id); 

-- CREATE INDEX idxl4jg4t2dlkjivgea1lta3l9xm ON transcriptcodingsequenceassociation_informationcontententity USING btree (association_id);
-- CREATE INDEX idxmbtwtvy0731b13p3dhoni3ywd ON transcriptcodingsequenceassociation_informationcontententity USING btree (evidence_id);

-- CREATE INDEX idxfdeljm7108w0j2nbrrme945ur ON transcriptexonassociation USING btree (createdby_id);
-- CREATE INDEX idxrig5300vc2ppesjckdkpv219m ON transcriptexonassociation USING btree (internal);
-- CREATE INDEX idxfmwe55e0r89q00xqlvi4v7sm0 ON transcriptexonassociation USING btree (obsolete);
-- CREATE INDEX idxm76icxt2r2qs52o4wpiexpb39 ON transcriptexonassociation USING btree (relation_id);
-- CREATE INDEX idxdct5af7efyqufibj1rjrvieam ON transcriptexonassociation USING btree (transcriptassociationsubject_id);
-- CREATE INDEX idxhpo9wefgxyoasohcctnr0qev3 ON transcriptexonassociation USING btree (transcriptexonassociationobject_id);
-- CREATE INDEX idx29575n6x9yueygvw1mj9exc2g ON transcriptexonassociation USING btree (updatedby_id);

-- CREATE INDEX idxf124air9olggpyrs5i3rgkbkj ON transcriptexonassociation_informationcontententity USING btree (association_id);
-- CREATE INDEX idxgg4iqmx96raypy761sn4h99ns ON transcriptexonassociation_informationcontententity USING btree (evidence_id);

CREATE INDEX transcriptgeneassociation_createdby_index ON transcriptgeneassociation USING btree (createdby_id);
CREATE INDEX transcriptgeneassociation_internal_index ON transcriptgeneassociation USING btree (internal);
CREATE INDEX transcriptgeneassociation_obsolete_index ON transcriptgeneassociation USING btree (obsolete);
CREATE INDEX transcriptgeneassociation_relation_index ON transcriptgeneassociation USING btree (relation_id);
CREATE INDEX transcriptgeneassociation_transcriptassociationsubject_index ON transcriptgeneassociation USING btree (transcriptassociationsubject_id);
CREATE INDEX transcriptgeneassociation_transcriptgeneassociationobject_index ON transcriptgeneassociation USING btree (transcriptgeneassociationobject_id);
CREATE INDEX transcriptgeneassociation_updatedby_index ON transcriptgeneassociation USING btree (updatedby_id);

-- CREATE INDEX idxnu079dovpg7bfrb6uawgtscqs ON transcriptgeneassociation_informationcontententity USING btree (association_id);
-- CREATE INDEX idxis18nw3pj2ru6wlejbwotqsv9 ON transcriptgeneassociation_informationcontententity USING btree (evidence_id);

-- CREATE INDEX idxaicr23temspg10v3f22k52ssd ON transcriptgenomiclocationassociation USING btree (createdby_id);
-- CREATE INDEX idxd06sc5p3gc11brcoxwp7ppbmr ON transcriptgenomiclocationassociation USING btree (internal);
-- CREATE INDEX idxgrqw9lxw4l1whho97aose6qh5 ON transcriptgenomiclocationassociation USING btree (obsolete);
-- CREATE INDEX idxii7jvungodeeudebo6c4kebny ON transcriptgenomiclocationassociation USING btree (phase);
-- CREATE INDEX idxtqvg5149lyo8q1oof4hhd7mmw ON transcriptgenomiclocationassociation USING btree (relation_id);
-- CREATE INDEX idx9yipaclxcmb82h69o4k5q3utt ON transcriptgenomiclocationassociation USING btree (strand);
-- CREATE INDEX idx4yukrngwb6ipj72wujbxa87bb ON transcriptgenomiclocationassociation USING btree (transcriptassociationsubject_id);
-- CREATE INDEX idxgo4p2kepo9x83moktih7gurb0 ON transcriptgenomiclocationassociation USING btree (transcriptgenomiclocationassociationobject_id);
-- CREATE INDEX idx1fpq38gqgxatnm69ge6purjbc ON transcriptgenomiclocationassociation USING btree (updatedby_id);

-- CREATE INDEX idx5bqn8a8osk6oy4514gpf8xs3  ON transcriptgenomiclocationassociation_informationcontententity USING btree (evidence_id);
-- CREATE INDEX idxghedmb4mmore7js1d3ni9thpn ON transcriptgenomiclocationassociation_informationcontententity USING btree (association_id);

-- Missing Index

CREATE INDEX cellularcomponentqualifiers_cellularcomponentqualifiers_index ON public.anatomicalsite_cellularcomponentqualifiers USING btree (cellularcomponentqualifiers_id);

