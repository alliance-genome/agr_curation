-- ALTER TABLE ONLY agmdiseaseannotation ADD CONSTRAINT fk2kkuby8cha4mi979rfrufh8ms FOREIGN KEY (assertedallele_id) REFERENCES allele(id);
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fk2umyg0v10hgtljwjbpond8jrc FOREIGN KEY (singlereference_id) REFERENCES reference(id);
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fk37dsmk0slmfxeeibjqkqkixda FOREIGN KEY (geneticsex_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY agmdiseaseannotation ADD CONSTRAINT fk60is9ul76u2gwwt3kri70nw8 FOREIGN KEY (diseaseannotationsubject_id) REFERENCES affectedgenomicmodel(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fk7ahiuo64x1a5hxracbvlagdt FOREIGN KEY (diseasegeneticmodifierrelation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fk8gxsa86awg2oqv0vs8s7jb7fw FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fk8jli70oa2ilrdlttpla0ds4di FOREIGN KEY (annotationtype_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY agmdiseaseannotation ADD CONSTRAINT fkcovck6ip7rxrud0twr4kid8f3 FOREIGN KEY (inferredallele_id) REFERENCES allele(id);
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fkg7mtotyaq0cs501cll3bk45hr FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fkhh9as28mwsysbjgx6hwt622wr FOREIGN KEY (secondarydataprovider_id) REFERENCES dataprovider(id);
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fkjl650wl0c24o9tq3hkbggxhwy FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY agmdiseaseannotation ADD CONSTRAINT fkjpqd4ps8pjklagy0wq6u6ljk1 FOREIGN KEY (inferredgene_id) REFERENCES gene(id);
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fkk64jxju84kxfc90ok6uor1wv2 FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);
-- ALTER TABLE ONLY diseaseannotation ADD CONSTRAINT fkmipqq6lp7u6rr7gif932q4vca FOREIGN KEY (diseaseannotationobject_id) REFERENCES ontologyterm(id);

-- ALTER TABLE ONLY diseaseannotation_biologicalentity ADD CONSTRAINT fkg694iaaw2ydb0cdslugifj8l5 FOREIGN KEY (diseasegeneticmodifiers_id) REFERENCES biologicalentity(id);
-- ALTER TABLE ONLY diseaseannotation_biologicalentity ADD CONSTRAINT fkhqcbm1d8ou0qqre8snp6cg5u7 FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation(id);

-- ALTER TABLE ONLY diseaseannotation_conditionrelation ADD CONSTRAINT fkabtdvl6g81qdhi5cw77tnhlvl FOREIGN KEY (annotation_id) REFERENCES diseaseannotation(id);
-- ALTER TABLE ONLY diseaseannotation_conditionrelation ADD CONSTRAINT fkrgr1hx0oqqa2966t1730c8a0l FOREIGN KEY (conditionrelations_id) REFERENCES conditionrelation(id);

-- ALTER TABLE ONLY diseaseannotation_gene ADD CONSTRAINT fkbif35stv5wbi5uh9n0kdmou0q FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation(id);
-- ALTER TABLE ONLY diseaseannotation_gene ADD CONSTRAINT fkcch8h6dw4rft9d68a0yn6se6e FOREIGN KEY (with_id) REFERENCES gene(id);
-- ALTER TABLE ONLY agmdiseaseannotation_gene ADD CONSTRAINT fkbif35stv5wbi5uh9n0kdmou0q FOREIGN KEY (agmdiseaseannotation_id) REFERENCES agmdiseaseannotation(id);
-- ALTER TABLE ONLY agmdiseaseannotation_gene ADD CONSTRAINT fkeyw19j24y6nwrxkqk9gad9eli FOREIGN KEY (assertedgenes_id) REFERENCES gene(id);

-- ALTER TABLE ONLY diseaseannotation_note ADD CONSTRAINT fk7vskf61fs1ti0kk05bm4ywcg6 FOREIGN KEY (annotation_id) REFERENCES diseaseannotation(id);
-- ALTER TABLE ONLY diseaseannotation_note ADD CONSTRAINT fkr55esx6lfo1f4u9eem1iwfkgx FOREIGN KEY (relatednotes_id) REFERENCES note(id);

-- ALTER TABLE ONLY diseaseannotation_ontologyterm ADD CONSTRAINT fkhldp7usikdqpabhs8k6e7tqm6 FOREIGN KEY (evidencecodes_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY diseaseannotation_ontologyterm ADD CONSTRAINT fktbnis09ieoxxxn0uac17y4tqj FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation(id);

-- ALTER TABLE ONLY diseaseannotation_vocabularyterm ADD CONSTRAINT fk7tkcolewth9efu38upga0s8fr FOREIGN KEY (diseaseannotation_id) REFERENCES diseaseannotation(id);
-- ALTER TABLE ONLY diseaseannotation_vocabularyterm ADD CONSTRAINT fkrj9u5k77j4lljg6bw7qo1y20w FOREIGN KEY (diseasequalifiers_id) REFERENCES vocabularyterm(id);

-- ALTER TABLE ONLY agmphenotypeannotation ADD CONSTRAINT fk22g2vuw5nph0qq41xtij396e3 FOREIGN KEY (inferredallele_id) REFERENCES allele(id);
-- ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT fk7plbgonr9g4yoefd1k5t0b1ap FOREIGN KEY (crossreference_id) REFERENCES crossreference(id);
-- ALTER TABLE ONLY agmphenotypeannotation ADD CONSTRAINT fkf0v97kcpydfiqiyvl3uh3rc90 FOREIGN KEY (inferredgene_id) REFERENCES gene(id);
-- ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT fkk86dqfob7lotkw7yuxd5dkaln FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT fkkblpivg8a4flg0t34evh25w8v FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT fkl1478mporoblxqjhqwma373ge FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);
-- ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT fkn1pb5j9be6lbvv8cwykecqjr9 FOREIGN KEY (singlereference_id) REFERENCES reference(id);
-- ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT fkn9jqg4yu2l1fq57f9k7egtawo FOREIGN KEY (assertedallele_id) REFERENCES allele(id);
-- ALTER TABLE ONLY agmphenotypeannotation ADD CONSTRAINT fkry48ahelheguy1pbu5dccxo2c FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES affectedgenomicmodel(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY phenotypeannotation ADD CONSTRAINT fksf5ja292y4tqljav1jrqeg60b FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);

-- ALTER TABLE ONLY phenotypeannotation_conditionrelation ADD CONSTRAINT fk707208iqski5px893183pk8f3 FOREIGN KEY (conditionrelations_id) REFERENCES conditionrelation(id);
-- ALTER TABLE ONLY phenotypeannotation_conditionrelation ADD CONSTRAINT fk7g3pjr9xu3425uylbk5v7ijje FOREIGN KEY (annotation_id) REFERENCES phenotypeannotation(id);

-- ALTER TABLE ONLY agmphenotypeannotation_gene ADD CONSTRAINT fk3bh3w7dbey6cmvnjveqrcmw3p FOREIGN KEY (agmphenotypeannotation_id) REFERENCES agmphenotypeannotation(id);
-- ALTER TABLE ONLY agmphenotypeannotation_gene ADD CONSTRAINT fks0f8p72jth9vtbquhyir7wpbh FOREIGN KEY (assertedgenes_id) REFERENCES gene(id);

-- ALTER TABLE ONLY phenotypeannotation_note ADD CONSTRAINT fkm35y1lsuyeoa4wsvw0s7fa8ge FOREIGN KEY (annotation_id) REFERENCES phenotypeannotation(id);
-- ALTER TABLE ONLY phenotypeannotation_note ADD CONSTRAINT fkswu81d1c6sq3b654gow81kpdc FOREIGN KEY (relatednotes_id) REFERENCES note(id);

-- ALTER TABLE ONLY phenotypeannotation_ontologyterm ADD CONSTRAINT fkkcbaa7jit2qom15cggnbiw45b FOREIGN KEY (phenotypeannotation_id) REFERENCES phenotypeannotation(id);
-- ALTER TABLE ONLY phenotypeannotation_ontologyterm ADD CONSTRAINT fkjxjka3d3dapimmtx0dy0okela FOREIGN KEY (phenotypeterms_id) REFERENCES ontologyterm(id);

-- ALTER TABLE ONLY allelediseaseannotation ADD CONSTRAINT fkqeqofi8sivg0j670gwpg5yvpp FOREIGN KEY (diseaseannotationsubject_id) REFERENCES allele(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY allelediseaseannotation ADD CONSTRAINT fktp2an4byav09yi71drrvtyjum FOREIGN KEY (inferredgene_id) REFERENCES gene(id);

-- ALTER TABLE ONLY allelediseaseannotation_gene ADD CONSTRAINT fk6ogbd2pywnhgiry1udwepkmtb FOREIGN KEY (assertedgenes_id) REFERENCES gene(id);
-- ALTER TABLE ONLY allelediseaseannotation_gene ADD CONSTRAINT fkkqvw9ea43v28gre268o3si6ay FOREIGN KEY (allelediseaseannotation_id) REFERENCES allelediseaseannotation(id);

-- ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT fk6p0nk2n01ab2ksjrdlyq25h4y FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT fkao3pmku9d1lh67ptuiq2t08wr FOREIGN KEY (allelegeneassociationobject_id) REFERENCES gene(id);
-- ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT fki6gdp1pjjtirs1v3k47oluqc3 FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT fkjhasohfaw7s26ogq5eg772rf0 FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT fko082oqtvkx9k55nm67vas3n1k FOREIGN KEY (evidencecode_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT fkqn4egk0drms9hxcekff8gam6q FOREIGN KEY (relatednote_id) REFERENCES note(id);
-- ALTER TABLE ONLY allelegeneassociation ADD CONSTRAINT fktho8ivna95vaox70mtry5cwwj FOREIGN KEY (alleleassociationsubject_id) REFERENCES allele(id);

-- ALTER TABLE ONLY allelegeneassociation_informationcontententity ADD CONSTRAINT fkemd3tife09j5ur86mf0elsjr9 FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
-- ALTER TABLE ONLY allelegeneassociation_informationcontententity ADD CONSTRAINT fkj6e0pl03cm27tahbt289e5h3f FOREIGN KEY (association_id) REFERENCES allelegeneassociation(id);

-- ALTER TABLE ONLY allelephenotypeannotation ADD CONSTRAINT fk1nd47lkg6wge7vtkdf61m9dqj FOREIGN KEY (inferredgene_id) REFERENCES gene(id);
-- ALTER TABLE ONLY allelephenotypeannotation ADD CONSTRAINT fkmurlgsm5dsodxtj3rg2dccrcs FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES allele(id) ON DELETE CASCADE;

-- ALTER TABLE ONLY allelephenotypeannotation_gene ADD CONSTRAINT fkdqeeeahsm84hjksl8593fbo6v FOREIGN KEY (association_id) REFERENCES allelephenotypeannotation(id);
-- ALTER TABLE ONLY allelephenotypeannotation_gene ADD CONSTRAINT fkp6gsx4q1q2d5ct5kpumhbd94y FOREIGN KEY (assertedgenes_id) REFERENCES gene(id);

-- ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT fk4efucyh5rpl0g0fpdti9y0qda FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT fk6q7qkw2peq0erl2n4cni6kbyb FOREIGN KEY (codingsequencegenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);
-- ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT fkbdlaouhol3v1law37h2q9ifaw FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT fkipqt0lqrx61hgek3pmt679xoj FOREIGN KEY (codingsequenceassociationsubject_id) REFERENCES codingsequence(id);
-- ALTER TABLE ONLY codingsequencegenomiclocationassociation ADD CONSTRAINT fklkt1u6mvqb5im061x5437amin FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);

-- ALTER TABLE ONLY CodingSequenceGenomicLocationAssociation_InformationContentEntity ADD CONSTRAINT fkm4es4h50h4511egeuoyfyxk0v FOREIGN KEY (association_id) REFERENCES codingsequencegenomiclocationassociation(id);
-- ALTER TABLE ONLY CodingSequenceGenomicLocationAssociation_InformationContentEntity ADD CONSTRAINT fksugal77n5hdh385dgpdx9tdmc FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

-- ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT fk5me06it1f8io7h4mktk6431b2 FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT fk9i7kbfdcy5hn6ws7xxf5kh0h2 FOREIGN KEY (constructgenomicentityassociationobject_id) REFERENCES genomicentity(id);
-- ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT fkei5227y3hjt2t6vsduwaqnnk7 FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT fkjg9e0tx4lynyaolbllcc3vg02 FOREIGN KEY (constructassociationsubject_id) REFERENCES construct(id);
-- ALTER TABLE ONLY constructgenomicentityassociation ADD CONSTRAINT fkpa6wqqg7gmm5d2nqdcml13aj3 FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);

-- ALTER TABLE ONLY constructgenomicentityassociation_informationcontententity ADD CONSTRAINT fka63t5u6e2c4dtcuwkwrhc8u9q FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
-- ALTER TABLE ONLY constructgenomicentityassociation_informationcontententity ADD CONSTRAINT fkf95vn8pjy50619o0y7iefyxo9 FOREIGN KEY (association_id) REFERENCES constructgenomicentityassociation(id);

-- ALTER TABLE ONLY constructgenomicentityassociation_note ADD CONSTRAINT fk9whd0ahcjprqnj131koir30wf FOREIGN KEY (association_id) REFERENCES constructgenomicentityassociation(id);
-- ALTER TABLE ONLY constructgenomicentityassociation_note ADD CONSTRAINT fkg64353f5upaidhljoj6m1yuc9 FOREIGN KEY (relatednotes_id) REFERENCES note(id);

-- ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT fk3c0ysdo5k8nmm0u6evjr1hdjo FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT fk5gbibcffgcyxc4ecwwl6jqks6 FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT fka0ne6k6iyq1rp73ij2s5upbfw FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT fkj36y7gfpojdgw0cei8p09uv5j FOREIGN KEY (exonassociationsubject_id) REFERENCES exon(id);
-- ALTER TABLE ONLY exongenomiclocationassociation ADD CONSTRAINT fkrhqru9jlmpeb9hllrbyonmndh FOREIGN KEY (exongenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);

-- ALTER TABLE ONLY exongenomiclocationassociation_informationcontententity ADD CONSTRAINT fkr5x4yhyie8e2cxetmuls112a3 FOREIGN KEY (association_id) REFERENCES exongenomiclocationassociation(id);
-- ALTER TABLE ONLY exongenomiclocationassociation_informationcontententity ADD CONSTRAINT fkti351r4r2wp3cm7e5f3qn894g FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

-- ALTER TABLE ONLY genediseaseannotation ADD CONSTRAINT fkrb70ercqcvrw29rfkivta704o FOREIGN KEY (diseaseannotationsubject_id) REFERENCES gene(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY genediseaseannotation ADD CONSTRAINT fktpww72cuubetd08y0ansac6gw FOREIGN KEY (sgdstrainbackground_id) REFERENCES affectedgenomicmodel(id);

-- ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT fk17ng0qvlftfcfwrvr1yhrnh9h FOREIGN KEY (singlereference_id) REFERENCES reference(id);
-- ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT fk2xe5vtnm2sba1a8legviqpytj FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT fk5gdi65gt84d6ieu34ncu33ftf FOREIGN KEY (expressionpattern_id) REFERENCES expressionpattern(id);
-- ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT fkgx41eka7b0e065kwo23n5fs5q FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT fklaj7vmfvdk90xkn6qadgfxwdr FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT fknrlc59ffsxn8bc4h8kwj7mgu4 FOREIGN KEY (expressionassayused_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT fkny440x2v8wufob980onb8vexv FOREIGN KEY (dataprovider_id) REFERENCES dataprovider(id);
-- ALTER TABLE ONLY geneexpressionannotation ADD CONSTRAINT fkpmfpi2pglpegd8c5ssqh2uv1w FOREIGN KEY (expressionannotationsubject_id) REFERENCES gene(id);

-- ALTER TABLE ONLY geneexpressionannotation_conditionrelation ADD CONSTRAINT fk967wi9o53ibvsoww5pxr7y3y2 FOREIGN KEY (annotation_id) REFERENCES geneexpressionannotation(id);
-- ALTER TABLE ONLY geneexpressionannotation_conditionrelation ADD CONSTRAINT fkqlyp0tmp9kowr3fasiq4lfgp FOREIGN KEY (conditionrelations_id) REFERENCES conditionrelation(id);

-- ALTER TABLE ONLY geneexpressionannotation_note ADD CONSTRAINT fk555k6362tmne0w4km2e2khn9a FOREIGN KEY (annotation_id) REFERENCES geneexpressionannotation(id);
-- ALTER TABLE ONLY geneexpressionannotation_note ADD CONSTRAINT fko3gm56s7kyef1fp07fwq69lw4 FOREIGN KEY (relatednotes_id) REFERENCES note(id);

-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fk223kijupg28b6mm86feo17r87 FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fk71xgxcnjkufiafwbavyr6m2ka FOREIGN KEY (interactiontype_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkb68mw5p0eril3ff6nfa47vqh8 FOREIGN KEY (interactorbtype_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkcnm6fb1eihhe5nuj2wiaksd8f FOREIGN KEY (interactorbgeneticperturbation_id) REFERENCES allele(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkcsuqi3l8tradsactj8t52khud FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkd70w7xi4ml95h5banj5vy2lqc FOREIGN KEY (interactorbrole_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkfxq8sixxwkb1jv7rh18yvwi53 FOREIGN KEY (interactorageneticperturbation_id) REFERENCES allele(id) ON DELETE CASCADE;
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkh2fp3yothl234yu9kitgspfpd FOREIGN KEY (interactorarole_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkhj6l8tygpxpab2moqx4h4pef7 FOREIGN KEY (interactoratype_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkj4fphalc7tmgy8sp7e0qsm5sx FOREIGN KEY (interactionsource_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkkr3yyhywq1px02gnli02eynvs FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fklov5kqhbymgrnmifgy1sdf09l FOREIGN KEY (geneassociationsubject_id) REFERENCES gene(id);
-- ALTER TABLE ONLY genegeneticinteraction ADD CONSTRAINT fkpgava8ud39i9a1bbioqm8s6hy FOREIGN KEY (genegeneassociationobject_id) REFERENCES gene(id);

-- ALTER TABLE ONLY genegeneticinteraction_crossreference ADD CONSTRAINT fkgkf3r1nb1c4nk32xex79psi42 FOREIGN KEY (geneinteraction_id) REFERENCES genegeneticinteraction(id);
-- ALTER TABLE ONLY genegeneticinteraction_crossreference ADD CONSTRAINT fksog88v596kgbud929oeonf92s FOREIGN KEY (crossreferences_id) REFERENCES crossreference(id);

-- ALTER TABLE ONLY genegeneticinteraction_informationcontententity ADD CONSTRAINT fkgpv0m4gscumstbs8y3ek9sp2k FOREIGN KEY (association_id) REFERENCES genegeneticinteraction(id);
-- ALTER TABLE ONLY genegeneticinteraction_informationcontententity ADD CONSTRAINT fkgutrng68vtdhwr1qlx92ml715 FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

-- ALTER TABLE ONLY genegeneticinteraction_phenotypesortraits ADD CONSTRAINT fk428xqcrj9euixb8jryhqqjc4g FOREIGN KEY (genegeneticinteraction_id) REFERENCES genegeneticinteraction(id);

-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fk1jgic74kl1c0g8b00li2x9uta FOREIGN KEY (interactorarole_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fkafjwyp0cf3pqof2ri7yirj07a FOREIGN KEY (interactorbtype_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fkc2ts1psdlxv1q5pt4bykdgx2x FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fkgmt12207c9ll4jj2ev9tb7ltr FOREIGN KEY (interactiontype_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fkhfosavsr0c588aoyt243idi6s FOREIGN KEY (geneassociationsubject_id) REFERENCES gene(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fkk9kl5bdcfebb9nn8ux6hv84vn FOREIGN KEY (detectionmethod_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fkl11owt2vsvjj6gfctjg0wm5ee FOREIGN KEY (interactorbrole_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fkob42y8ggn4tqr5dj8wp5jarvc FOREIGN KEY (interactionsource_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fkpf8prdftq2o1ayen1c8w3kbrj FOREIGN KEY (interactoratype_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fkr8u0gn70cc7va4q0yt8o0wh6p FOREIGN KEY (aggregationdatabase_id) REFERENCES ontologyterm(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fks6m85gh445ieytj9n665rr9eg FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fksjxeqrdby7udc1v71xcawbehr FOREIGN KEY (genegeneassociationobject_id) REFERENCES gene(id);
-- ALTER TABLE ONLY genemolecularinteraction ADD CONSTRAINT fksqnn5xdoniaory4upnkkk2iry FOREIGN KEY (updatedby_id) REFERENCES person(id);

-- ALTER TABLE ONLY genemolecularinteraction_crossreference ADD CONSTRAINT fkmnlia9wqktsqf6n0nce2q2l9 FOREIGN KEY (geneinteraction_id) REFERENCES genemolecularinteraction(id);
-- ALTER TABLE ONLY genemolecularinteraction_crossreference ADD CONSTRAINT fk6rdt433qfoib1q26sbxbpt489 FOREIGN KEY (crossreferences_id) REFERENCES crossreference(id);

-- ALTER TABLE ONLY genemolecularinteraction_informationcontententity ADD CONSTRAINT fk7u6wpv1juxshjv1noyshkrtla FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
-- ALTER TABLE ONLY genemolecularinteraction_informationcontententity ADD CONSTRAINT fkooo9u7pkb7yar3qlbdb9k1l87 FOREIGN KEY (association_id) REFERENCES genemolecularinteraction(id);

-- ALTER TABLE ONLY genephenotypeannotation ADD CONSTRAINT fk8e6wa7h1d64pvueh9ng7gpksc FOREIGN KEY (sgdstrainbackground_id) REFERENCES affectedgenomicmodel(id);
-- ALTER TABLE ONLY genephenotypeannotation ADD CONSTRAINT fkca9jnh4snsv4whcxfhiuv3o5f FOREIGN KEY (phenotypeannotationsubject_id) REFERENCES gene(id) ON DELETE CASCADE;

-- ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT fk4oywb4vxq460mph6qqfucrklc FOREIGN KEY (sequencetargetingreagentassociationsubject_id) REFERENCES sequencetargetingreagent(id);
-- ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT fk7h8rchplqiu7rji3t751d16fb FOREIGN KEY (sequencetargetingreagentgeneassociationobject_id) REFERENCES gene(id);
-- ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT fkaxs3aj4tdrywqpf6sfrk824rn FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT fkf34iyy9myqed615lvm21exrp FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT fkp1m5va6abm32twxddmcxl50xb FOREIGN KEY (createdby_id) REFERENCES person(id);

-- ALTER TABLE ONLY sequencetargetingreagentgeneassociation_informationcontententit ADD CONSTRAINT fke9vquysmfh3c4lkcfmajh5hj8 FOREIGN KEY (association_id) REFERENCES sequencetargetingreagentgeneassociation(id);
-- ALTER TABLE ONLY sequencetargetingreagentgeneassociation_informationcontententit ADD CONSTRAINT fkfib30x859rb0p83hlojly8ghc FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

-- ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT fk13y1vlql0xt9moj0ulf4nsdj7 FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT fk4n6xdc24lo3rymynnjmjg98yb FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT fk4wsw77bry9naicli7b6jxqqq8 FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
-- ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT fkjlfma6v1oit4atwbwelo6b3uw FOREIGN KEY (transcriptcodingsequenceassociationobject_id) REFERENCES codingsequence(id);
-- ALTER TABLE ONLY transcriptcodingsequenceassociation ADD CONSTRAINT fkpos4jj7u82aojox7pssksgkjx FOREIGN KEY (createdby_id) REFERENCES person(id);

-- ALTER TABLE ONLY transcriptcodingsequenceassociation_informationcontententity ADD CONSTRAINT fk7i7m32cuet11t71r7arbao3yy FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
-- ALTER TABLE ONLY transcriptcodingsequenceassociation_informationcontententity ADD CONSTRAINT fkk6mgowf6uq7nw8cxa2huj8h67 FOREIGN KEY (association_id) REFERENCES transcriptcodingsequenceassociation(id);

-- ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT fka00ut46dje2rc4qtbryl8as7b FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT fkdmwvnm7xnfh3qo8k0f3933opr FOREIGN KEY (transcriptexonassociationobject_id) REFERENCES exon(id);
-- ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT fkfbxqumoil00tmd67mawmscdt7 FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT fkoug4l2bpj2ineapkwnopxcld4 FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
-- ALTER TABLE ONLY transcriptexonassociation ADD CONSTRAINT fkruqduw01ixripbq0jsy6bvj8d FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);

-- ALTER TABLE ONLY transcriptexonassociation_informationcontententity ADD CONSTRAINT fkgdasgw5u791fn92fdkskn51w3 FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
-- ALTER TABLE ONLY transcriptexonassociation_informationcontententity ADD CONSTRAINT fkiii0hpbot3mksbdyesqj38ust FOREIGN KEY (association_id) REFERENCES transcriptexonassociation(id);

-- ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT fk3dbarf3ebsa6kjvu3j8wv5s99 FOREIGN KEY (transcriptgeneassociationobject_id) REFERENCES gene(id);
-- ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT fk3j11pvym3scjrw7889oos21rd FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
-- ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT fkdbaufd0xuh8vql7po8vityjnd FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT fkii8alynjje87mims76rjvhubp FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY transcriptgeneassociation ADD CONSTRAINT fkk1vrpkcc7j2edjvfbidq5fil6 FOREIGN KEY (createdby_id) REFERENCES person(id);

-- ALTER TABLE ONLY transcriptgeneassociation_informationcontententity ADD CONSTRAINT fk4j4tyh9wjrr4xcaau05thvmrl FOREIGN KEY (association_id) REFERENCES transcriptgeneassociation(id);
-- ALTER TABLE ONLY transcriptgeneassociation_informationcontententity ADD CONSTRAINT fkrfpiqj508x62wbmf8hn6c2iq6 FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

-- ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT fk2xsh4isfa05148kxx4b44sx22 FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
-- ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT fk68ctcfo18ki8aecl8nwoe5iy7 FOREIGN KEY (updatedby_id) REFERENCES person(id);
-- ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT fk7x6dki5ye5bo7q8l7q4spliwn FOREIGN KEY (transcriptassociationsubject_id) REFERENCES transcript(id);
-- ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT fko9x0yg7fso9wrakgkjgik5jf8 FOREIGN KEY (createdby_id) REFERENCES person(id);
-- ALTER TABLE ONLY transcriptgenomiclocationassociation ADD CONSTRAINT fksrry8iu0efl589u9e36028k00 FOREIGN KEY (transcriptgenomiclocationassociationobject_id) REFERENCES assemblycomponent(id);

-- ALTER TABLE ONLY transcriptgenomiclocationassociation_informationcontententity ADD CONSTRAINT fk5byb2ffc3nvu6iroggtof0p96 FOREIGN KEY (association_id) REFERENCES transcriptgenomiclocationassociation(id);
-- ALTER TABLE ONLY transcriptgenomiclocationassociation_informationcontententity ADD CONSTRAINT fkf72x535nil1ng129s1sg13lko FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);
