CREATE TABLE allelefunctionalimpactslotannotation (
	id bigint PRIMARY KEY,
	singleallele_curie varchar(255),
	phenotypeterm_curie varchar(255),
	phenotypestatement text
);

ALTER TABLE allelefunctionalimpactslotannotation ADD CONSTRAINT allelefunctionalimpactslotannotation_singleallele_curie_fk FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);
ALTER TABLE allelefunctionalimpactslotannotation ADD CONSTRAINT allelefunctionalimpactslotannotation_phenotypeterm_curie_fk FOREIGN KEY (phenotypeterm_curie) REFERENCES phenotypeterm (curie);

CREATE INDEX allelefunctionalimpact_singleallele_curie_index ON allelefunctionalimpactslotannotation USING btree (singleallele_curie);
CREATE INDEX allelefunctionalimpact_phenotypeterm_curie_index ON allelefunctionalimpactslotannotation USING btree (phenotypeterm_curie);

CREATE TABLE allelefunctionalimpactslotannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
	singleallele_curie varchar(255),
	phenotypeterm_curie varchar(255),
	phenotypestatement text
);

ALTER TABLE allelefunctionalimpactslotannotation_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE allelefunctionalimpactslotannotation_aud ADD CONSTRAINT allelefunctionalimpactslotannotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES slotannotation_aud (id, rev);

CREATE TABLE allelefunctionalimpactslotannotation_vocabularyterm (
	allelefunctionalimpactslotannotation_id bigint,
	functionalimpacts_id bigint
);
	
ALTER TABLE allelefunctionalimpactslotannotation_vocabularyterm ADD CONSTRAINT allelefunctionalimpactsa_vocabterm_afisa_curie_fk FOREIGN KEY (allelefunctionalimpactslotannotation_id) REFERENCES allelefunctionalimpactslotannotation (id);
ALTER TABLE allelefunctionalimpactslotannotation_vocabularyterm ADD CONSTRAINT allelefunctionalimpactsa_vocabterm_functionimpacts_id_fk FOREIGN KEY (functionalimpacts_id) REFERENCES vocabularyterm (id);

CREATE INDEX allelefunctionalimpactslotannotation_id_index ON allelefunctionalimpactslotannotation_vocabularyterm USING btree (allelefunctionalimpactslotannotation_id);
CREATE INDEX allelefunctionalimpactslotannotation_functionalimpacts_id_index ON allelefunctionalimpactslotannotation_vocabularyterm USING btree (functionalimpacts_id);

CREATE TABLE allelefunctionalimpactslotannotation_vocabularyterm_aud (
	allelefunctionalimpactslotannotation_id bigint,
	functionalimpacts_id bigint,
	rev integer NOT NULL,
	revtype smallint
);	

ALTER TABLE allelefunctionalimpactslotannotation_vocabularyterm_aud ADD PRIMARY KEY (allelefunctionalimpactslotannotation_id, functionalimpacts_id, rev);

ALTER TABLE allelefunctionalimpactslotannotation_vocabularyterm_aud ADD CONSTRAINT allelefunctionalimpactslotannotation_vocabularyterm_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

INSERT INTO vocabulary (id, name) VALUES (nextval('hibernate_sequence'), 'Allele Functional Impact');
	
ALTER TABLE vocabularyterm ALTER COLUMN definition TYPE text;
ALTER TABLE vocabularyterm_aud ALTER COLUMN definition TYPE text;
	
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'function_uncertain', id, 'A sequence variant in which the function of a gene product is unknown with respect to a reference. (SO:0002220)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'transcript_function', id, 'A sequence variant which alters the functioning of a transcript with respect to a reference sequence. (SO:0001538)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'translational_product_function', id, 'A sequence variant that affects the functioning of a translational product with respect to a reference sequence. (SO:0001539)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'decreased_transcript_level', id, 'A sequence variant that increases the level of mature, spliced and processed RNA with respect to a reference sequence. (SO:0001541)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'increased_transcript_level', id, 'A sequence variant that increases the level of mature, spliced and processed RNA with respect to a reference sequence. (SO:0001542)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'decreased_transcript_stability', id, 'A sequence variant that decreases transcript stability with respect to a reference sequence. (SO:0001547)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'gain_of_function', id, 'A sequence variant whereby new or enhanced function is conferred on the gene product. (SO:0002053)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'dominant_negative_(antimorphic)', id, 'A variant where the mutated gene product adversely affects the other (wild type) gene product. (SO:0002052)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'dominant_negative_(antimorphic)_genetic_evidence', id, 'An allele that is inferred to make a gene product that is antagonistic to the wild-type gene product from the observations that extra copies of the wild-type allele reduce the expressivity and/or penetrance of the phenotype. So, the phenotype over a deficiency is stronger than that over a wild-type allele, which in turn is stronger than that in the presence of a duplication of the wild-type locus. This test is similar to that used for hypomorphic alleles. An antimorph can be distinguished from a dominant hypermorph as only the former can be reverted to wild-type by deletion of the mutant allele. [FBcv:0000292]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'dominant_negative_(antimorphic)_molecular_evidence', id, 'An allele that has been shown by molecular evidence to make a gene product that is antagonistic to the function of the wild-type gene product. [FBcv:0000694]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'hypermorphic', id, 'An allele that makes either increased amounts of a normal gene product or a gene product with normal function but increased activity compared to wild-type. [FBcv:0000696]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'hypermorphic_genetic_evidence', id, 'An allele inferred to make either increased amounts of a normal gene product or a gene product with normal function but increased activity compared to wild-type from the observation that extra copies in the genome increase the expressivity and/or penetrance of the phenotype. Most commonly this evidence takes the form of experiments showing that homozygotes (2 copies) have a stronger phenotype than transheterozygotes to a deletion of the gene (1 copy). [FBcv:0000293]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'hypermorphic_molecular_evidence', id, 'Allele shown by molecular evidence to make either a functionally wild-type gene product at increased levels or a gene product with the same function as wild-type but with increased activity. [FBcv:0000693]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'activation', id FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'constitutively_active', id, 'A constitutively active allele produces an enzyme or protein product whose activity level is constant over time and independent from normal regulatory controls. (MGI definition)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'overexpression', id FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'neomorphic', id, 'An allele that makes a gene product with a novel function or expression pattern compared to wild-type. [FBcv:0000697]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'neomorphic_genetic_evidence', id, 'An allele that is inferred to make a gene product with a novel function or expression pattern compared to wild-type based on the evidence that the phenotype is unaffected by extra or fewer doses of the wild-type gene. [FBcv:0000291]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'neomorphic_molecular_evidence', id, 'An allele shown by molecular evidence to produce a gene product with a novel function or expression pattern compared to wild-type. [FBcv:0000692]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'conditional_activity', id, 'The activity of the gene product is normal under some conditions and altered under others.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'temperature_conditional_gain_of_function', id, 'An allele that at some temperatures but not others, either makes a normally functioning gene product but at higher levels or in a different spatial or temporal pattern to wild-type, or a product with increased or novel activity compared to wild-type. [FBcv:0000748]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'heat_sensitive_antimorphic', id, 'An allele that makes a gene product that is antagonistic to the function of the wild-type gene product at high temperatures, but not at lower temperatures. [FBcv:0000761]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'heat_sensitive_hypermorphic', id, 'An allele that, at high temperatures but not lower temperatures, makes a gene product with normal function but at higher levels or with higher activity than in wild-type. [FBcv:0000779]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'heat_sensitive_neomorphic', id, 'An allele that, at some high temperatures but not lower temperatures, makes a gene product with a novel function or expression pattern compared to wild-type. [FBcv:0000770]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'cold_sensitive_antimorphic', id, 'An allele that makes a gene product that is antagonistic to the function of the wild-type gene product at cold temperatures, but not at higher temperatures. [ FBcv:0000762]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'cold_sensitive_hypermorphic', id, 'An allele that, at low temperatures but not higher temperatures, makes a gene product with normal function but at higher levels or with higher activity than in wild-type. [FBcv:0000780]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'cold_sensitive_neomorphic', id, 'An allele that, at some low temperatures but not higher temperatures, makes a gene product at with a novel function or expression pattern compared to wild-type. [FBcv:0000771]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'loss_of_function', id, 'A sequence variant whereby the gene product has diminished or abolished function. (SO:0002054)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'temperature_conditional_loss_of_function', id, 'An allele that at some temperatures but not others either makes no functional gene product or makes reduced levels of a normally functioning gene product or makes a gene product with reduced activity compared to wild-type. [FBcv:0000745]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'heat_sensitive_hypomorphic', id, 'An allele that makes a gene product that is functionally equivalent to wild-type but which at high temperatures is present in a lesser amount or with lowered activity, and that retains normal levels of expression or activity at low temperatures. [FBcv:0000752]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'heat_sensitive_amorphic', id, 'An allele that completely lacks function (i.e. whose gene productive is completely inactive) at high temperatures, but that retains at least some function at lower temperatures.[FBcv:0000736]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'cold_sensitive_amorphic', id, 'An allele that completely lacks function (i.e. whose gene productive is completely inactive) at low temperatures, but that retains at least some function at higher temperatures. [FBcv:0001003]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'cold_sensitive_hypomorphic', id, 'An allele that makes a gene product that is functionally equivalent to wild-type but which at low temperatures is present in a lesser amount or with lowered activity, and that retains normal function at higher temperatures. [FBcv:0000753]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'hypomorphic_(reduction_of_function)', id, 'Allele that makes a gene product that is functionally equivalent to wild-type but in a lesser amount or with lowered activity. [FBcv:0000690]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'hypomorphic_genetic_evidence', id, 'Allele inferred to make a gene product that is functionally equivalent to wild-type but in a lesser amount or with lowered activity from the observation that extra copies in the genome decrease the expressivity and/or penetrance of the phenotype. Most commonly this evidence takes the form of experiments showing that the homozygote (2 copies) has a weaker phenotype than the allele in trans to a deletion of the gene (1 copy)) [FBcv:0000289]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'hypomorphic_molecular_evidence', id, 'An allele shown by molecular evidence to make a gene product that is functionally equivalent to wild-type but in a lesser amount or with lowered activity. [FBcv:0000691]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'knockdown', id, 'A knockdown is a specific class of hypomorph in which the affected gene''s DNA sequence is not directly modified (e.g. RNAi).' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'amorphic_(null/knockout)', id, 'A variant whereby the gene product is not functional or the gene product is not produced. (SO:0002055)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'amorphic_(null/knockout)_genetic_evidence', id, 'An allele inferred to completely lack function from the observation that addition of extra copies in the genome has no effect on the phenotype. Most commonly, evidence takes the form of the observation that the phenotype of homozygotes (2 copies) is identical to that seen when the allele is in trans to a deletion of the gene (1 copy) [FBcv:0000288]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'amorphic_(null/knockout)_molecular_evidence', id, 'An allele shown by molecular evidence to completely lack function, producing either a completely inactive gene product or none at all. [FBcv:0000689]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'wild-type', id, 'Allele that corresponds to the wild-type one. [FBcv:0000294]' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'repressible', id, 'The mutation leads to reduction of levels of the gene product, often through the use of a repressible promoter. (SGD definition)' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'misexpressed', id, 'The mutation results in expression of the gene product at a developmental stage, in a cell type, or at a subcellular location different from that at which the wild-type gene is expressed.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'inducible', id, 'A mutation is inducible if its activation or expression occurs with addition or removal of an external stimulus.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'inserted_expressed_sequence', id, 'Inserted expressed sequences are experimentally introduced and expressed in at least one cell type. This categorical set includes only those mutations that are not included in other sets of introduced sequences (i.e. Reporter, Transposase, Recombinase).' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'modified_isoform(s)', id, 'Alleles that produce modified isoform(s) (e.g. different gene products encoded by the same gene sequence as a result of alternative splicing) are those in which at least one wild-type isoform is produced, while others are mutant or missing.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'no_functional_change', id, 'No functional change describes a mutation in which no discernible effect on the expression or function of the gene has been demonstrated. An example is the introduction of loxP sites to flank a critical exon, which usually has no effect on gene expression or function until cre recombinase is applied. This set also includes inserted expressed sequences, such as reporter alleles, when the endogenous gene product activity is not affected.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'recombinase', id, 'A recombinase (e.g. cre, flp) is an experimentally introduced expressed gene whose product has the activity to remove or invert a region of DNA that is flanked with recombinase recognition sites sites (e.g. loxP, frt).' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'reporter', id, 'A reporter is an experimentally introduced expressed gene whose product is easily detected and not ordinarily present in the organism or cell type under study. Bacterial beta-galactosidase (LacZ), whose activity can be detected using a staining reaction, is a commonly used reporter gene; as is green fluorescent protein (GFP), which is detected by immunofluorescence.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'RMCE-ready', id, 'Recombinase Mediated Cassette Exchange (RMCE)-Ready mutations contain acceptor recombination sites for targeting expression constructs to a defined genomic region for targeted cassette exchange., thus avoiding possible positional effects of transgene expression.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'transactivator', id, 'An experimentally introduced intermediate protein (e.g. tTA) used to stimulate or repress expression of another gene through the use of an external stimulus.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'transposase', id, 'Transposases are enzymes that bind to the ends of a transposon and catalyze the transposition of a transposon from one part of the genome to another.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'transposon_concatemer', id, 'Transposon concatemers are multiple copies of transposon sequences inserted at a single site. Transposon concatamers serve as donor sites in transposase mutagenesis.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'epitope_tag', id, 'An epitope tag is experimentally introduced expressed antigen (e.g. FLAG, HA) that is fused to the endogenous gene product' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'endonuclease', id, 'An endonuclease (e.g. cas9) is an experimentally introduced expressed gene whose product has the activity to cut DNA at specific sites' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'modified_regulatory_region', id, 'The allele has changes to one or more regulatory sequences that change when, where, or how much the gene is expressed' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'conditional_ready', id, 'Conditional ready alleles contain at least one region of DNA flanked by recombinase recognition sites (e.g. loxP, frt). Subsequent interaction with a recombinase (e.g. cre, FRT) will remove or invert the flanked region, resulting in a deletion or alteration in expression of the target gene or transgene.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'humanized_sequence', id, 'Humanized sequences are alleles or transgenes that have been engineered to make a non-human sequence closer to that of human.' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'hypomorphic_predicted_from_sequence', id, 'An allele that is predicted to be hypomorphic based on the sequence of the allele' FROM vocabulary WHERE name = 'Allele Functional Impact';
INSERT INTO vocabularyterm (id, name, vocabulary_id, definition) SELECT nextval('hibernate_sequence'), 'amorphic_(null)_predicted_from_sequence', id, 'An allele that is predicted to be null/amorphic based on the sequence of the allele' FROM vocabulary WHERE name = 'Allele Functional Impact';