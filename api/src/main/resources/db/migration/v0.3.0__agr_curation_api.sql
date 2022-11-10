create sequence hibernate_sequence start 1 increment 1;

    create table AffectedGenomicModel (
       parental_population varchar(255),
        subtype varchar(255),
        curie varchar(255) not null,
        primary key (curie)
    );

    create table AGMDiseaseAnnotation (
       predicate varchar(255),
        id int8 not null,
        inferredAllele_curie varchar(255),
        inferredGene_curie varchar(255),
        subject_curie varchar(255),
        primary key (id)
    );

    create table Allele (
       description TEXT,
        feature_type varchar(255),
        symbol varchar(255),
        curie varchar(255) not null,
        primary key (curie)
    );

    create table Allele_GeneGenomicLocation (
       Allele_curie varchar(255) not null,
        genomicLocations_id int8 not null
    );

    create table AlleleDiseaseAnnotation (
       predicate varchar(255),
        id int8 not null,
        inferredGene_curie varchar(255),
        subject_curie varchar(255),
        primary key (id)
    );

    create table AnatomicalTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table Association (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        uniqueId varchar(2000),
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)

    );

    create table BiologicalEntity (
       curie varchar(255) not null,
        created timestamp,
        lastUpdated timestamp,
        taxon_curie varchar(255),
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (curie)
    );

    create table BulkFMSLoad (
       dataSubType varchar(255),
        dataType varchar(255),
        id int8 not null,
        primary key (id)
    );

    create table BulkLoad (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        backendBulkLoadType varchar(255),
        errorMessage TEXT,
        fileExtension varchar(255),
        name varchar(255),
        ontologyType varchar(255),
        status varchar(255),
        group_id int8,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table BulkLoadFile (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        errorMessage TEXT,
        fileSize int8,
        localFilePath varchar(255),
        md5Sum varchar(255),
        recordCount int4,
        s3Path varchar(255),
        status varchar(255),
        bulkLoad_id int8,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table BulkLoadFileException (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        exception jsonb,
        bulkLoadFileHistory_id int8,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table BulkLoadFileHistory (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        completedRecords int8,
        failedRecords int8,
        loadFinished timestamp,
        loadStarted timestamp,
        totalRecords int8,
        bulkLoadFile_id int8,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table BulkLoadGroup (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        name varchar(255),
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table BulkManualLoad (
       dataType varchar(255),
        id int8 not null,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table BulkScheduledLoad (
       cronSchedule varchar(255),
        scheduleActive boolean,
        schedulingErrorMessage TEXT,
        id int8 not null,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table BulkURLLoad (
       url varchar(255),
        id int8 not null,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table CHEBITerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table ChemicalTerm (
       formula varchar(255),
        inchi varchar(750),
        inchiKey varchar(255),
        iupac varchar(500),
        smiles varchar(500),
        curie varchar(255) not null,
        primary key (curie)
    );

    create table ConditionRelation (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        uniqueId varchar(2000),
        conditionRelationType varchar(255),
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    handle character varying(255),
    createdby_id bigint,
    modifiedby_id bigint,
    singlereference_curie character varying(255),
        primary key (id)
    );

    create table ConditionRelation_ExperimentalCondition (
       ConditionRelation_id int8 not null,
        conditions_id int8 not null
    );

    create table CrossReference (
       curie varchar(255) not null,
        created timestamp,
        lastUpdated timestamp,
        displayName varchar(255),
        prefix varchar(255),
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (curie)
    );

    create table CrossReference_pageAreas (
       CrossReference_curie varchar(255) not null,
        pageAreas varchar(255)
    );

    create table DAOTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table DiseaseAnnotation (
       diseaseRelation varchar(255),
        modId varchar(255),
        negated boolean default false not null,
        id int8 not null,
        object_curie varchar(255),
        singleReference_curie varchar(255),
    dataprovider character varying(255),
    secondarydataprovider character varying(255),
    diseasegeneticmodifier_curie character varying(255),
        primary key (id)
    );

    create table DiseaseAnnotation_ConditionRelation (
       DiseaseAnnotation_id int8 not null,
        conditionRelations_id int8 not null
    );

    create table DiseaseAnnotation_EcoTerm (
       DiseaseAnnotation_id int8 not null,
        evidenceCodes_curie varchar(255) not null
    );

    create table DiseaseAnnotation_Gene (
       DiseaseAnnotation_id int8 not null,
        with_curie varchar(255) not null
    );

    create table DOTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table EcoTerm (
       abbreviation varchar(255),
        curie varchar(255) not null,
        primary key (curie)
    );

    create table EMAPATerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table ExperimentalCondition (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        uniqueId varchar(2000),
        conditionQuantity varchar(255),
        conditionStatement varchar(255),
        conditionAnatomy_curie varchar(255),
        conditionChemical_curie varchar(255),
        conditionClass_curie varchar(255),
        conditionGeneOntology_curie varchar(255),
        conditionId_curie varchar(255),
        conditionTaxon_curie varchar(255),
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    conditionfreetext text,
    conditionsummary character varying(255),
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table ExperimentalConditionOntologyTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table Gene (
       automatedGeneDescription TEXT,
        geneSynopsis TEXT,
        geneSynopsisURL varchar(255),
        symbol varchar(255),
        curie varchar(255) not null,
        geneType_curie varchar(255),
        primary key (curie)
    );

    create table Gene_GeneGenomicLocation (
       Gene_curie varchar(255) not null,
        genomicLocations_id int8 not null
    );

    create table GeneDiseaseAnnotation (
       predicate varchar(255),
        id int8 not null,
        sgdStrainBackground_curie varchar(255),
        subject_curie varchar(255),
        primary key (id)
    );

    create table GeneGenomicLocation (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        assembly varchar(255),
        endPos int4,
        startPos int4,
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table GenomicEntity (
       name TEXT,
        curie varchar(255) not null,
        primary key (curie)
    );

    create table GenomicEntity_CrossReference (
       GenomicEntity_curie varchar(255) not null,
        crossReferences_curie varchar(255) not null
    );

    create table GenomicEntity_secondaryIdentifiers (
       GenomicEntity_curie varchar(255) not null,
        secondaryIdentifiers varchar(255)
    );

    create table GenomicEntity_Synonym (
       genomicEntities_curie varchar(255) not null,
        synonyms_id int8 not null
    );

    create table GOTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table MATerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table Molecule (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table MPTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table NCBITaxonTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table OntologyTerm (
       curie varchar(255) not null,
        created timestamp,
        lastUpdated timestamp,
        definition TEXT,
        name varchar(2000),
        namespace varchar(255),
        obsolete boolean,
        type varchar(255),
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (curie)
    );

    create table OntologyTerm_CrossReference (
       OntologyTerm_curie varchar(255) not null,
        crossReferences_curie varchar(255) not null
    );

    create table OntologyTerm_definitionUrls (
       OntologyTerm_curie varchar(255) not null,
        definitionUrls TEXT
    );

    create table OntologyTerm_secondaryIdentifiers (
       OntologyTerm_curie varchar(255) not null,
        secondaryIdentifiers varchar(255)
    );

    create table OntologyTerm_subsets (
       OntologyTerm_curie varchar(255) not null,
        subsets varchar(255)
    );

    create table OntologyTerm_synonyms (
       OntologyTerm_curie varchar(255) not null,
        synonyms TEXT
    );

    create table Person (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        apiToken varchar(255),
        email varchar(255),
        firstName varchar(255),
        lastName varchar(255),
        modId varchar(255),
        uniqueId varchar(255),
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    middlename character varying(255),
    orcid character varying(255),
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table Reference (
       curie varchar(255) not null,
        created timestamp,
        lastUpdated timestamp,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (curie)
    );

    create table REVINFO (
       REV int4 not null,
        REVTSTMP int8,
        primary key (REV)
    );

    create table SOTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table Synonym (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        name varchar(255),
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table Vocabulary (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        isObsolete boolean default false not null,
        name varchar(255),
        vocabularyDescription varchar(255),
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
         primary key (id)
    );

    create table VocabularyTerm (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        abbreviation varchar(255),
        definition varchar(255),
        isObsolete boolean default false not null,
        name varchar(255),
        vocabulary_id int8,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    modifiedby_id bigint,
        primary key (id)
    );

    create table VocabularyTerm_CrossReference (
       VocabularyTerm_id int8 not null,
        crossReferences_curie varchar(255) not null
    );

    create table VocabularyTerm_textSynonyms (
       VocabularyTerm_id int8 not null,
        textSynonyms TEXT
    );

    create table WBbtTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table XcoTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table ZecoTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table ZfaTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    alter table if exists Association 
       add constraint UK_t6dcflvn2bytt4xegpy2cnl1x unique (uniqueId);

    alter table if exists BulkLoadFile 
       add constraint UK_7nic6jxn8vx9mykmsjhmjck9k unique (md5Sum);

    alter table if exists ConditionRelation 
       add constraint UK_ett3ft5dxnonwnjd6lnjgme1o unique (uniqueId);
create index IDXcc1trjvkm2h0x2d99imkm4c11 on CrossReference_pageAreas (CrossReference_curie);

    alter table if exists DiseaseAnnotation 
       add constraint UK_hlsp8ic6sxwpd99k8gc90eq4a unique (modId);
create index IDXj6eavg6eannqn6uhvja6p4enf on DiseaseAnnotation_Gene (DiseaseAnnotation_id);

    alter table if exists ExperimentalCondition 
       add constraint UK_yb8nvuqbvlpy6e41sx37wtvr unique (uniqueId);
create index IDXcmlmyaq41oab54whjt5cglo8v on GenomicEntity_CrossReference (GenomicEntity_curie);
create index IDXkyy0nwnxxbnoba74d1pmxwk2h on GenomicEntity_secondaryIdentifiers (GenomicEntity_curie);
create index IDX8veukg3tw21aorory1ei1c8on on GenomicEntity_Synonym (genomicEntities_curie);
create index IDX41w2gn9m2s5mjdydwbsqjhfox on OntologyTerm_CrossReference (OntologyTerm_curie);
create index IDX1c3xuyhjua7fn0s7tscxluby8 on OntologyTerm_CrossReference (crossReferences_curie);
create index IDX171k63a40d8huvbhohveql7so on OntologyTerm_definitionUrls (OntologyTerm_curie);
create index IDXsvjjbf5eugfrbue5yo4jgarpn on OntologyTerm_secondaryIdentifiers (OntologyTerm_curie);
create index IDXips7lcqafkikxweue2p0h13t9 on OntologyTerm_subsets (OntologyTerm_curie);
create index IDXpydgr8unpmiig9jnsm89f55br on OntologyTerm_synonyms (OntologyTerm_curie);

    alter table if exists Person 
       add constraint UK_585qcyc8qh7bg1fwgm1pj4fus unique (email);

    alter table if exists Vocabulary 
       add constraint UK_7a3owq9kyfv5eirj0bjkmifyf unique (name);
create index IDX4snyoxumi6hnl3mugq9x4ep4p on VocabularyTerm_CrossReference (VocabularyTerm_id);
create index IDXsdesyork9yoruo27pe2cetjog on VocabularyTerm_CrossReference (crossReferences_curie);
create index IDXknjhcn64qms05eq8c8s2hhmxc on VocabularyTerm_textSynonyms (VocabularyTerm_id);

    alter table if exists AffectedGenomicModel 
       add constraint FKke1qw7ijaa33fqv1bifsiwiv9 
       foreign key (curie) 
       references GenomicEntity;

    alter table if exists AGMDiseaseAnnotation 
       add constraint FKo9dilcfxv6tw0oaeds0yss8op 
       foreign key (inferredAllele_curie) 
       references Allele;

    alter table if exists AGMDiseaseAnnotation 
       add constraint FKtj1uj3to13fi4q32bc2p65lah 
       foreign key (inferredGene_curie) 
       references Gene;

    alter table if exists AGMDiseaseAnnotation 
       add constraint FKlvr4o1waqclvbktjmyg6x25ls 
       foreign key (subject_curie) 
       references AffectedGenomicModel;

ALTER TABLE ONLY public.agmdiseaseannotation
    ADD CONSTRAINT fkp1rktcpoyvnr2f756ncdb8k24 FOREIGN KEY (id) REFERENCES public.diseaseannotation(id) ON DELETE CASCADE;

    alter table if exists Allele 
       add constraint FK42r7586hi59wcwakfyr30l6l3 
       foreign key (curie) 
       references GenomicEntity;

    alter table if exists Allele_GeneGenomicLocation 
       add constraint FKk3m8sj0e342pbhl2q56awc16a 
       foreign key (genomicLocations_id) 
       references GeneGenomicLocation;

    alter table if exists Allele_GeneGenomicLocation 
       add constraint FKomfoc4gujrmdg18e3xe3kisdp 
       foreign key (Allele_curie) 
       references Allele;

    alter table if exists AlleleDiseaseAnnotation 
       add constraint FKnecrivvmqgg2ifhppubrjy5ey 
       foreign key (inferredGene_curie) 
       references Gene;

    alter table if exists AlleleDiseaseAnnotation 
       add constraint FKerk9wpvk1ka4pkm0t1dqyyeyl 
       foreign key (subject_curie) 
       references Allele;

ALTER TABLE ONLY public.allelediseaseannotation
    ADD CONSTRAINT fk3unb0kaxocbodllqe35hu4w0c FOREIGN KEY (id) REFERENCES public.diseaseannotation(id) ON DELETE CASCADE;

    alter table if exists AnatomicalTerm 
       add constraint FKfepti479fro1b09ybaltkofqu 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists BiologicalEntity 
       add constraint FK5c19vicptarinu2wgj7xyhhum 
       foreign key (taxon_curie) 
       references NCBITaxonTerm;

    alter table if exists BulkFMSLoad 
       add constraint FKj3lf8vdpiflwx2gisua4kurs1 
       foreign key (id) 
       references BulkScheduledLoad;

    alter table if exists BulkLoad 
       add constraint FKo25bugche1vp384eme3exv04d 
       foreign key (group_id) 
       references BulkLoadGroup;

    alter table if exists BulkLoadFile 
       add constraint FKkakppk407vfefvttp4a5p6npg 
       foreign key (bulkLoad_id) 
       references BulkLoad;

ALTER TABLE ONLY public.bulkloadfileexception
    ADD CONSTRAINT fkgt2k1ohdyuodwu71mofkyplhy FOREIGN KEY (bulkloadfilehistory_id) REFERENCES public.bulkloadfilehistory(id) ON DELETE CASCADE;

ALTER TABLE ONLY public.bulkloadfilehistory
    ADD CONSTRAINT fkk9bvfu4248kgyyrupeii7t6m0 FOREIGN KEY (bulkloadfile_id) REFERENCES public.bulkloadfile(id) ON DELETE CASCADE;

    alter table if exists BulkManualLoad 
       add constraint FKd3pm3o2v9xb1uy15b241o56j 
       foreign key (id) 
       references BulkLoad;

    alter table if exists BulkScheduledLoad 
       add constraint FK8br24n4em8tr58k9spdhep28f 
       foreign key (id) 
       references BulkLoad;

    alter table if exists BulkURLLoad 
       add constraint FKp6lnq5x8g34hc2i3b3focu4vg 
       foreign key (id) 
       references BulkScheduledLoad;

    alter table if exists CHEBITerm 
       add constraint FK7enwyeblw2xt5yo5co0keko5f 
       foreign key (curie) 
       references ChemicalTerm;

    alter table if exists ChemicalTerm 
       add constraint FK2fegif3wy9egh5r2yy8wplrwu 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists ConditionRelation_ExperimentalCondition 
       add constraint FKp2129xf9bt872p0rycff6iy93 
       foreign key (conditions_id) 
       references ExperimentalCondition;

    alter table if exists ConditionRelation_ExperimentalCondition 
       add constraint FK69kutljbycmp1vimotmlxp36j 
       foreign key (ConditionRelation_id) 
       references ConditionRelation;

    alter table if exists CrossReference_pageAreas 
       add constraint FKmd0dqivib8hjjtqiqsiyq0hgf 
       foreign key (CrossReference_curie) 
       references CrossReference;

    alter table if exists DAOTerm 
       add constraint FK3xjbjyyuqqyvspspeael1m7fe 
       foreign key (curie) 
       references AnatomicalTerm;

    alter table if exists DiseaseAnnotation 
       add constraint FKp8u0w7kiirnjfcmdl4oyjhcs3 
       foreign key (object_curie) 
       references DOTerm;

    alter table if exists DiseaseAnnotation 
       add constraint FKk6hg8sfqhqhlsdjmyex63bvo7 
       foreign key (singleReference_curie) 
       references Reference;

    alter table if exists DiseaseAnnotation 
       add constraint FK5a3i0leqdmstsdfpq1j1b15el 
       foreign key (id) 
       references Association;

    alter table if exists DiseaseAnnotation_ConditionRelation 
       add constraint FKlvpswihwk7vf7ijnyustt47na 
       foreign key (conditionRelations_id) 
       references ConditionRelation;

    alter table if exists DiseaseAnnotation_ConditionRelation 
       add constraint FK2wrhxll4ol0fxdpynbmue5stm 
       foreign key (DiseaseAnnotation_id) 
       references DiseaseAnnotation;


    alter table if exists DiseaseAnnotation_EcoTerm 
       add constraint FKp79bf46xsyojpvjjguoe3vuuu 
       foreign key (evidenceCodes_curie) 
       references EcoTerm;

    alter table if exists DiseaseAnnotation_EcoTerm 
       add constraint FK43rw1jai2kqggx518nsu8c4me 
       foreign key (DiseaseAnnotation_id) 
       references DiseaseAnnotation;

    alter table if exists DiseaseAnnotation_Gene 
       add constraint FK6akpr16qusnfom0fdhjjevkb2 
       foreign key (with_curie) 
       references Gene;

    alter table if exists DiseaseAnnotation_Gene 
       add constraint FKy4jlhsgseecd3gkxjtwp28ba 
       foreign key (DiseaseAnnotation_id) 
       references DiseaseAnnotation;

    alter table if exists DOTerm 
       add constraint FKp8el2duba9ym3l6gd5dy43swk 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists EcoTerm 
       add constraint FKskvp24kfp723htxmk0m9ev4ns 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists EMAPATerm 
       add constraint FKcm3tpjo7lxsx61pj7gs5y9f9u 
       foreign key (curie) 
       references AnatomicalTerm;

    alter table if exists ExperimentalCondition 
       add constraint FKsso9a3875a8t0ver6u6qciuap 
       foreign key (conditionAnatomy_curie) 
       references AnatomicalTerm;

    alter table if exists ExperimentalCondition 
       add constraint FK2rmhalgeg6rghpat78b2cpcoc 
       foreign key (conditionChemical_curie) 
       references ChemicalTerm;

    alter table if exists ExperimentalCondition 
       add constraint FKp0oqdnt9bmx68i84neufkcb3a 
       foreign key (conditionClass_curie) 
       references ZecoTerm;

    alter table if exists ExperimentalCondition 
       add constraint FKhi2109btsx06x2u9kdg7y7xp0 
       foreign key (conditionGeneOntology_curie) 
       references GOTerm;

    alter table if exists ExperimentalCondition 
       add constraint FKagp6m2xqeu7bapu5hyh2pmha9 
       foreign key (conditionId_curie) 
       references ExperimentalConditionOntologyTerm;

    alter table if exists ExperimentalCondition 
       add constraint FKcl89ywjgllce228a0uo8fd0ee 
       foreign key (conditionTaxon_curie) 
       references NCBITaxonTerm;

    alter table if exists ExperimentalConditionOntologyTerm 
       add constraint FK5jlaea2evnqnrlf72jglhqq6p 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists Gene 
       add constraint FKiaxg0dhug3stym3gjovw598w1 
       foreign key (geneType_curie) 
       references SOTerm;

    alter table if exists Gene 
       add constraint FK9v4jtwy759c3cfub0uxye5rue 
       foreign key (curie) 
       references GenomicEntity;

    alter table if exists Gene_GeneGenomicLocation 
       add constraint FKf0ean58ivdvmq1is9shvywe3 
       foreign key (genomicLocations_id) 
       references GeneGenomicLocation;

    alter table if exists Gene_GeneGenomicLocation 
       add constraint FKnh14ykhb6xm1hnfak0lbifaex 
       foreign key (Gene_curie) 
       references Gene;

    alter table if exists GeneDiseaseAnnotation 
       add constraint FK51h0w9jsd45qw5f3v2v0o28mu 
       foreign key (sgdStrainBackground_curie) 
       references AffectedGenomicModel;

    alter table if exists GeneDiseaseAnnotation 
       add constraint FK8xs26m9hfc38nmy7gvu3cec3t 
       foreign key (subject_curie) 
       references Gene;

ALTER TABLE ONLY public.genediseaseannotation
    ADD CONSTRAINT fk3j5deigrhrwln0srh51vtw3m8 FOREIGN KEY (id) REFERENCES public.diseaseannotation(id) ON DELETE CASCADE;

    alter table if exists GenomicEntity 
       add constraint FKhi54si7gksfs3f6jrbytaddbi 
       foreign key (curie) 
       references BiologicalEntity;

    alter table if exists GenomicEntity_CrossReference 
       add constraint FK3fiksr8hbcttuwaiorcgeh5ip 
       foreign key (crossReferences_curie) 
       references CrossReference;

    alter table if exists GenomicEntity_CrossReference 
       add constraint FK9b9qofiu2sump8fnfxgux1lvl 
       foreign key (GenomicEntity_curie) 
       references GenomicEntity;

    alter table if exists GenomicEntity_secondaryIdentifiers 
       add constraint FKf36bpep9iuxsfpq7bdqqp2oay 
       foreign key (GenomicEntity_curie) 
       references GenomicEntity;

    alter table if exists GenomicEntity_Synonym 
       add constraint FKes64s4i1gon9839dojaq6airf 
       foreign key (synonyms_id) 
       references Synonym;

    alter table if exists GenomicEntity_Synonym 
       add constraint FKrfh6t1dmhd1jbsklymyuvqk22 
       foreign key (genomicEntities_curie) 
       references GenomicEntity;

    alter table if exists GOTerm 
       add constraint FK4gf262ba8btx03wi3vl5vhfao 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists MATerm 
       add constraint FKtlgqvrv4vuh8gqihevh6adya4 
       foreign key (curie) 
       references AnatomicalTerm;

    alter table if exists Molecule 
       add constraint FKnnf79fdaivbnqu0p9kes1jtd1 
       foreign key (curie) 
       references ChemicalTerm;

    alter table if exists MPTerm 
       add constraint FKta9f30vmw7h1smmv68to1ipyq 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists NCBITaxonTerm 
       add constraint FK47k37g37jc1e4wdt76ajmn0xk 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_CrossReference 
       add constraint FK9508vlhm2u3xpuf5041d9ye8y 
       foreign key (crossReferences_curie) 
       references CrossReference;

    alter table if exists OntologyTerm_CrossReference 
       add constraint FK3e1a40poh1ehjk91h42bx7i45 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_definitionUrls 
       add constraint FKnhkhso5kmei3t37mkhodkkfgt 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_secondaryIdentifiers 
       add constraint FKpkg5jfw6wypf4v43bpb4ergu7 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_subsets 
       add constraint FKchq4ex53obwegdhgxrovd5r53 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_synonyms 
       add constraint FKjf8xunyry3dy9njpqb01tvjsr 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists SOTerm 
       add constraint FKri7tkc9slvpex9v83peovegyt 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists VocabularyTerm 
       add constraint FKpua589tn1kypabuee8ytlmfpf 
       foreign key (vocabulary_id) 
       references Vocabulary;

    alter table if exists VocabularyTerm_CrossReference 
       add constraint FK9uin0anwbl5mibr82471byv8r 
       foreign key (crossReferences_curie) 
       references CrossReference;

    alter table if exists VocabularyTerm_CrossReference 
       add constraint FK6qrhqmglrld3g7jny4n7ls4v1 
       foreign key (VocabularyTerm_id) 
       references VocabularyTerm;

    alter table if exists VocabularyTerm_textSynonyms 
       add constraint FKnb1g5wap721lwp2rn7gse6geq 
       foreign key (VocabularyTerm_id) 
       references VocabularyTerm;

    alter table if exists WBbtTerm 
       add constraint FKqnxqrnadcxojeti2ienobdqh0 
       foreign key (curie) 
       references AnatomicalTerm;

    alter table if exists XcoTerm 
       add constraint FK35ywtb8qiadqbwsb706ebu81c 
       foreign key (curie) 
       references ExperimentalConditionOntologyTerm;

    alter table if exists ZecoTerm 
       add constraint FKqd3f6hcopl67fwai6viq07t88 
       foreign key (curie) 
       references ExperimentalConditionOntologyTerm;

    alter table if exists ZfaTerm 
       add constraint FK572s3xiqi0y4gjblq8xjyk3f7 
       foreign key (curie) 
       references AnatomicalTerm;

CREATE TABLE public.curationreport (
    id bigint NOT NULL,
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    birtreportfilepath character varying(255),
    cronschedule character varying(255),
    curationreportstatus character varying(255),
    errormessage text,
    name character varying(255),
    scheduleactive boolean,
    schedulingerrormessage text,
    createdby_id bigint,
    modifiedby_id bigint,
    curationreportgroup_id bigint,
    primary key (id)
);

CREATE TABLE public.curationreportgroup (
    id bigint NOT NULL,
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    name character varying(255),
    createdby_id bigint,
    modifiedby_id bigint,
    primary key (id)
);

CREATE TABLE public.curationreporthistory (
    id bigint NOT NULL,
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    curationreportstatus character varying(255),
    curationreporttimestamp timestamp without time zone,
    htmlfilepath character varying(255),
    pdffilepath character varying(255),
    xlsfilepath character varying(255),
    createdby_id bigint,
    modifiedby_id bigint,
    curationreport_id bigint,
    primary key (id)
);

CREATE TABLE public.diseaseannotation_note (
    diseaseannotation_id bigint NOT NULL,
    relatednotes_id bigint NOT NULL
);

CREATE TABLE public.diseaseannotation_vocabularyterm (
    diseaseannotation_id bigint NOT NULL,
    diseasequalifiers_id bigint NOT NULL
);

CREATE TABLE public.fbdvterm (
    curie character varying(255) NOT NULL
);

CREATE TABLE public.mmusdvterm (
    curie character varying(255) NOT NULL
);

CREATE TABLE public.note (
    id bigint NOT NULL,
    datecreated timestamp without time zone,
    dateupdated timestamp without time zone,
    dbdatecreated timestamp without time zone,
    dbdateupdated timestamp without time zone,
    internal boolean DEFAULT true NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    freetext text,
    createdby_id bigint,
    modifiedby_id bigint,
    notetype_id bigint,
    primary key (id)
);

CREATE TABLE public.note_reference (
    note_id bigint NOT NULL,
    references_curie character varying(255) NOT NULL
);

CREATE TABLE public.person_emails (
    person_id bigint NOT NULL,
    emails character varying(255)
);

CREATE TABLE public.person_oldemails (
    person_id bigint NOT NULL,
    oldemails character varying(255)
);

CREATE TABLE public.phenotypeterm (
    curie character varying(255) NOT NULL,
    primary key (curie)
);

CREATE TABLE public.stageterm (
    curie character varying(255) NOT NULL,
    primary key (curie)
);

CREATE TABLE public.wblsterm (
    curie character varying(255) NOT NULL,
    primary key (curie)
);

CREATE TABLE public.xbaterm (
    curie character varying(255) NOT NULL,
    primary key (curie)
);

CREATE TABLE public.xbedterm (
    curie character varying(255) NOT NULL,
    primary key (curie)
);

CREATE TABLE public.xbsterm (
    curie character varying(255) NOT NULL,
    primary key (curie)
);

CREATE TABLE public.xpoterm (
    curie character varying(255) NOT NULL,
    primary key (curie)
);

CREATE TABLE public.xsmoterm (
    curie character varying(255) NOT NULL,
    primary key (curie)
);

CREATE TABLE public.zfsterm (
    curie character varying(255) NOT NULL,
    primary key (curie)
);

-- AUD tables

CREATE TABLE public.affectedgenomicmodel_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    parental_population character varying(255),
    subtype character varying(255)
);

CREATE TABLE public.agmdiseaseannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    inferredallele_curie character varying(255),
    inferredgene_curie character varying(255),
    subject_curie character varying(255)
);

CREATE TABLE public.allele_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    description text,
    feature_type character varying(255),
    symbol character varying(255)
);

CREATE TABLE public.allele_genegenomiclocation_aud (
    rev integer NOT NULL,
    allele_curie character varying(255) NOT NULL,
    genomiclocations_id bigint NOT NULL,
    revtype smallint
);

CREATE TABLE public.allelediseaseannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    inferredgene_curie character varying(255),
    subject_curie character varying(255)
);

CREATE TABLE public.anatomicalterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.association_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint
);

CREATE TABLE public.biologicalentity_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    taxon_curie character varying(255)
);

CREATE TABLE public.bulkfmsload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    datasubtype character varying(255),
    datatype character varying(255)
);

CREATE TABLE public.bulkload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    backendbulkloadtype character varying(255),
    errormessage text,
    fileextension character varying(255),
    name character varying(255),
    ontologytype character varying(255),
    status character varying(255),
    group_id bigint
);

CREATE TABLE public.bulkloadfile_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    errormessage text,
    filesize bigint,
    localfilepath character varying(255),
    md5sum character varying(255),
    recordcount integer,
    s3path character varying(255),
    status character varying(255),
    bulkload_id bigint
);

CREATE TABLE public.bulkloadfileexception_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    exception jsonb,
    bulkloadfilehistory_id bigint
);

CREATE TABLE public.bulkloadfilehistory_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    completedrecords bigint,
    failedrecords bigint,
    loadfinished timestamp without time zone,
    loadstarted timestamp without time zone,
    totalrecords bigint,
    bulkloadfile_id bigint
);

CREATE TABLE public.bulkloadgroup_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    name character varying(255)
);

CREATE TABLE public.bulkmanualload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    datatype character varying(255)
);

CREATE TABLE public.bulkscheduledload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    cronschedule character varying(255),
    scheduleactive boolean,
    schedulingerrormessage text
);

CREATE TABLE public.bulkurlload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    url character varying(255)
);

CREATE TABLE public.chebiterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.chemicalterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    formula character varying(255),
    inchi character varying(750),
    inchikey character varying(255),
    iupac character varying(500),
    smiles character varying(500)
);

CREATE TABLE public.conditionrelation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    handle character varying(255),
    conditionrelationtype_id bigint,
    singlereference_curie character varying(255)
);

CREATE TABLE public.conditionrelation_experimentalcondition_aud (
    rev integer NOT NULL,
    conditionrelation_id bigint NOT NULL,
    conditions_id bigint NOT NULL,
    revtype smallint
);

CREATE TABLE public.crossreference_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    displayname character varying(255),
    prefix character varying(255)
);

CREATE TABLE public.crossreference_pageareas_aud (
    rev integer NOT NULL,
    crossreference_curie character varying(255) NOT NULL,
    pageareas character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.curationreport_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    birtreportfilepath character varying(255),
    cronschedule character varying(255),
    curationreportstatus character varying(255),
    errormessage text,
    name character varying(255),
    scheduleactive boolean,
    schedulingerrormessage text,
    curationreportgroup_id bigint
);

CREATE TABLE public.curationreportgroup_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    name character varying(255)
);

CREATE TABLE public.curationreporthistory_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    curationreportstatus character varying(255),
    curationreporttimestamp timestamp without time zone,
    htmlfilepath character varying(255),
    pdffilepath character varying(255),
    xlsfilepath character varying(255),
    curationreport_id bigint
);

CREATE TABLE public.daoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.diseaseannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    dataprovider character varying(255),
    modentityid character varying(255),
    negated boolean DEFAULT false,
    secondarydataprovider character varying(255),
    annotationtype_id bigint,
    diseasegeneticmodifier_curie character varying(255),
    diseasegeneticmodifierrelation_id bigint,
    diseaserelation_id bigint,
    geneticsex_id bigint,
    object_curie character varying(255),
    singlereference_curie character varying(255)
);

CREATE TABLE public.diseaseannotation_conditionrelation_aud (
    rev integer NOT NULL,
    diseaseannotation_id bigint NOT NULL,
    conditionrelations_id bigint NOT NULL,
    revtype smallint
);

CREATE TABLE public.diseaseannotation_ecoterm_aud (
    rev integer NOT NULL,
    diseaseannotation_id bigint NOT NULL,
    evidencecodes_curie character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.diseaseannotation_gene_aud (
    rev integer NOT NULL,
    diseaseannotation_id bigint NOT NULL,
    with_curie character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.diseaseannotation_note_aud (
    rev integer NOT NULL,
    diseaseannotation_id bigint NOT NULL,
    relatednotes_id bigint NOT NULL,
    revtype smallint
);

CREATE TABLE public.diseaseannotation_vocabularyterm_aud (
    rev integer NOT NULL,
    diseaseannotation_id bigint NOT NULL,
    diseasequalifiers_id bigint NOT NULL,
    revtype smallint
);

CREATE TABLE public.doterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.ecoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    abbreviation character varying(255)
);

CREATE TABLE public.emapaterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.experimentalcondition_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    conditionfreetext text,
    conditionquantity character varying(255),
    conditionstatement character varying(255),
    conditionsummary character varying(255),
    conditionanatomy_curie character varying(255),
    conditionchemical_curie character varying(255),
    conditionclass_curie character varying(255),
    conditiongeneontology_curie character varying(255),
    conditionid_curie character varying(255),
    conditiontaxon_curie character varying(255)
);

CREATE TABLE public.experimentalconditionontologyterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.fbdvterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.gene_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    automatedgenedescription text,
    genesynopsis text,
    genesynopsisurl character varying(255),
    symbol character varying(255),
    genetype_curie character varying(255)
);

CREATE TABLE public.gene_genegenomiclocation_aud (
    rev integer NOT NULL,
    gene_curie character varying(255) NOT NULL,
    genomiclocations_id bigint NOT NULL,
    revtype smallint
);

CREATE TABLE public.genediseaseannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    sgdstrainbackground_curie character varying(255),
    subject_curie character varying(255)
);

CREATE TABLE public.genegenomiclocation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    assembly character varying(255),
    endpos integer,
    startpos integer
);

CREATE TABLE public.genomicentity_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    name text
);

CREATE TABLE public.genomicentity_crossreference_aud (
    rev integer NOT NULL,
    genomicentity_curie character varying(255) NOT NULL,
    crossreferences_curie character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.genomicentity_secondaryidentifiers_aud (
    rev integer NOT NULL,
    genomicentity_curie character varying(255) NOT NULL,
    secondaryidentifiers character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.genomicentity_synonym_aud (
    rev integer NOT NULL,
    genomicentities_curie character varying(255) NOT NULL,
    synonyms_id bigint NOT NULL,
    revtype smallint
);

CREATE TABLE public.goterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.loggedinperson_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    apitoken character varying(255),
    oktaemail character varying(255),
    oktaid character varying(255),
    usersettings jsonb
);

CREATE TABLE public.materm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.mmusdvterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.molecule_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.mpterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.ncbitaxonterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.note_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    freetext text,
    notetype_id bigint
);

CREATE TABLE public.note_reference_aud (
    rev integer NOT NULL,
    note_id bigint NOT NULL,
    references_curie character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.ontologyterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    definition text,
    name character varying(2000),
    namespace character varying(255),
    type character varying(255)
);

CREATE TABLE public.ontologyterm_crossreference_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    crossreferences_curie character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.ontologyterm_definitionurls_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    definitionurls text NOT NULL,
    revtype smallint
);

CREATE TABLE public.ontologyterm_secondaryidentifiers_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    secondaryidentifiers character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.ontologyterm_subsets_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    subsets character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.ontologyterm_synonyms_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    synonyms text NOT NULL,
    revtype smallint
);

CREATE TABLE public.person_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    firstname character varying(255),
    lastname character varying(255),
    middlename character varying(255),
    modentityid character varying(255),
    orcid character varying(255)
);

CREATE TABLE public.person_emails_aud (
    rev integer NOT NULL,
    person_id bigint NOT NULL,
    emails character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.person_oldemails_aud (
    rev integer NOT NULL,
    person_id bigint NOT NULL,
    oldemails character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.phenotypeterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.reference_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint
);

CREATE TABLE public.soterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.stageterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.synonym_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    name character varying(255)
);

CREATE TABLE public.vocabulary_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    name character varying(255),
    vocabularydescription character varying(255)
);

CREATE TABLE public.vocabularyterm_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    abbreviation character varying(255),
    definition character varying(255),
    name character varying(255),
    vocabulary_id bigint
);

CREATE TABLE public.vocabularyterm_crossreference_aud (
    rev integer NOT NULL,
    vocabularyterm_id bigint NOT NULL,
    crossreferences_curie character varying(255) NOT NULL,
    revtype smallint
);

CREATE TABLE public.vocabularyterm_textsynonyms_aud (
    rev integer NOT NULL,
    vocabularyterm_id bigint NOT NULL,
    textsynonyms text NOT NULL,
    revtype smallint
);

CREATE TABLE public.wbbtterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.wblsterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.xbaterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.xbedterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.xbsterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.xcoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.xpoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.xsmoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.zecoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.zfaterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

CREATE TABLE public.zfsterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);

ALTER TABLE ONLY public.affectedgenomicmodel_aud
    ADD CONSTRAINT affectedgenomicmodel_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.agmdiseaseannotation_aud
    ADD CONSTRAINT agmdiseaseannotation_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.allele_aud
    ADD CONSTRAINT allele_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.allele_genegenomiclocation_aud
    ADD CONSTRAINT allele_genegenomiclocation_aud_pkey PRIMARY KEY (rev, allele_curie, genomiclocations_id);
ALTER TABLE ONLY public.allelediseaseannotation_aud
    ADD CONSTRAINT allelediseaseannotation_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.anatomicalterm_aud
    ADD CONSTRAINT anatomicalterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.association_aud
    ADD CONSTRAINT association_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.biologicalentity_aud
    ADD CONSTRAINT biologicalentity_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.bulkfmsload_aud
    ADD CONSTRAINT bulkfmsload_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.bulkload_aud
    ADD CONSTRAINT bulkload_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.bulkloadfile_aud
    ADD CONSTRAINT bulkloadfile_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.bulkloadfileexception_aud
    ADD CONSTRAINT bulkloadfileexception_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.bulkloadfilehistory_aud
    ADD CONSTRAINT bulkloadfilehistory_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.bulkloadgroup_aud
    ADD CONSTRAINT bulkloadgroup_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.bulkmanualload_aud
    ADD CONSTRAINT bulkmanualload_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.bulkscheduledload_aud
    ADD CONSTRAINT bulkscheduledload_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.bulkurlload_aud
    ADD CONSTRAINT bulkurlload_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.chebiterm_aud
    ADD CONSTRAINT chebiterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.chemicalterm_aud
    ADD CONSTRAINT chemicalterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.conditionrelation_aud
    ADD CONSTRAINT conditionrelation_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.conditionrelation_experimentalcondition_aud
    ADD CONSTRAINT conditionrelation_experimentalcondition_aud_pkey PRIMARY KEY (rev, conditionrelation_id, conditions_id);
ALTER TABLE ONLY public.crossreference_aud
    ADD CONSTRAINT crossreference_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.crossreference_pageareas_aud
    ADD CONSTRAINT crossreference_pageareas_aud_pkey PRIMARY KEY (rev, crossreference_curie, pageareas);
ALTER TABLE ONLY public.curationreport_aud
    ADD CONSTRAINT curationreport_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.curationreportgroup_aud
    ADD CONSTRAINT curationreportgroup_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.curationreporthistory_aud
    ADD CONSTRAINT curationreporthistory_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.daoterm_aud
    ADD CONSTRAINT daoterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.diseaseannotation_aud
    ADD CONSTRAINT diseaseannotation_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.diseaseannotation_conditionrelation_aud
    ADD CONSTRAINT diseaseannotation_conditionrelation_aud_pkey PRIMARY KEY (rev, diseaseannotation_id, conditionrelations_id);
ALTER TABLE ONLY public.diseaseannotation_ecoterm_aud
    ADD CONSTRAINT diseaseannotation_ecoterm_aud_pkey PRIMARY KEY (rev, diseaseannotation_id, evidencecodes_curie);
ALTER TABLE ONLY public.diseaseannotation_gene_aud
    ADD CONSTRAINT diseaseannotation_gene_aud_pkey PRIMARY KEY (rev, diseaseannotation_id, with_curie);
ALTER TABLE ONLY public.diseaseannotation_note_aud
    ADD CONSTRAINT diseaseannotation_note_aud_pkey PRIMARY KEY (rev, diseaseannotation_id, relatednotes_id);
ALTER TABLE ONLY public.diseaseannotation_vocabularyterm_aud
    ADD CONSTRAINT diseaseannotation_vocabularyterm_aud_pkey PRIMARY KEY (rev, diseaseannotation_id, diseasequalifiers_id);
ALTER TABLE ONLY public.doterm_aud
    ADD CONSTRAINT doterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.ecoterm_aud
    ADD CONSTRAINT ecoterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.emapaterm_aud
    ADD CONSTRAINT emapaterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.experimentalcondition_aud
    ADD CONSTRAINT experimentalcondition_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.experimentalconditionontologyterm_aud
    ADD CONSTRAINT experimentalconditionontologyterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.fbdvterm_aud
    ADD CONSTRAINT fbdvterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.gene_aud
    ADD CONSTRAINT gene_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.gene_genegenomiclocation_aud
    ADD CONSTRAINT gene_genegenomiclocation_aud_pkey PRIMARY KEY (rev, gene_curie, genomiclocations_id);
ALTER TABLE ONLY public.genediseaseannotation_aud
    ADD CONSTRAINT genediseaseannotation_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.genegenomiclocation_aud
    ADD CONSTRAINT genegenomiclocation_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.genomicentity_aud
    ADD CONSTRAINT genomicentity_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.genomicentity_crossreference_aud
    ADD CONSTRAINT genomicentity_crossreference_aud_pkey PRIMARY KEY (rev, genomicentity_curie, crossreferences_curie);
ALTER TABLE ONLY public.genomicentity_secondaryidentifiers_aud
    ADD CONSTRAINT genomicentity_secondaryidentifiers_aud_pkey PRIMARY KEY (rev, genomicentity_curie, secondaryidentifiers);
ALTER TABLE ONLY public.genomicentity_synonym_aud
    ADD CONSTRAINT genomicentity_synonym_aud_pkey PRIMARY KEY (rev, genomicentities_curie, synonyms_id);
ALTER TABLE ONLY public.goterm_aud
    ADD CONSTRAINT goterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.loggedinperson_aud
    ADD CONSTRAINT loggedinperson_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.materm_aud
    ADD CONSTRAINT materm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.mmusdvterm_aud
    ADD CONSTRAINT mmusdvterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.molecule_aud
    ADD CONSTRAINT molecule_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.mpterm_aud
    ADD CONSTRAINT mpterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.ncbitaxonterm_aud
    ADD CONSTRAINT ncbitaxonterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.note_aud
    ADD CONSTRAINT note_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.note_reference_aud
    ADD CONSTRAINT note_reference_aud_pkey PRIMARY KEY (rev, note_id, references_curie);
ALTER TABLE ONLY public.ontologyterm_aud
    ADD CONSTRAINT ontologyterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.ontologyterm_crossreference_aud
    ADD CONSTRAINT ontologyterm_crossreference_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, crossreferences_curie);
ALTER TABLE ONLY public.ontologyterm_definitionurls_aud
    ADD CONSTRAINT ontologyterm_definitionurls_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, definitionurls);
ALTER TABLE ONLY public.ontologyterm_secondaryidentifiers_aud
    ADD CONSTRAINT ontologyterm_secondaryidentifiers_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, secondaryidentifiers);
ALTER TABLE ONLY public.ontologyterm_subsets_aud
    ADD CONSTRAINT ontologyterm_subsets_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, subsets);
ALTER TABLE ONLY public.ontologyterm_synonyms_aud
    ADD CONSTRAINT ontologyterm_synonyms_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, synonyms);
ALTER TABLE ONLY public.person_aud
    ADD CONSTRAINT person_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.person_emails_aud
    ADD CONSTRAINT person_emails_aud_pkey PRIMARY KEY (rev, person_id, emails);
ALTER TABLE ONLY public.person_oldemails_aud
    ADD CONSTRAINT person_oldemails_aud_pkey PRIMARY KEY (rev, person_id, oldemails);
ALTER TABLE ONLY public.phenotypeterm_aud
    ADD CONSTRAINT phenotypeterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.reference_aud
    ADD CONSTRAINT reference_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.soterm_aud
    ADD CONSTRAINT soterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.stageterm_aud
    ADD CONSTRAINT stageterm_aud_pkey PRIMARY KEY (curie, rev);
ALTER TABLE ONLY public.synonym_aud
    ADD CONSTRAINT synonym_aud_pkey PRIMARY KEY (id, rev);
ALTER TABLE ONLY public.ontologyterm_crossreference_aud
    ADD CONSTRAINT fk15bpj1c3nu4aubev76oy5oth0 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.mmusdvterm_aud
    ADD CONSTRAINT fk19prhk8fikp11bpxxm2tqxh6u FOREIGN KEY (curie, rev) REFERENCES public.stageterm_aud(curie, rev);
ALTER TABLE ONLY public.note
    ADD CONSTRAINT fk1hmiukouswyk4lqkdyqhsh9n2 FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.note_aud
    ADD CONSTRAINT fk1r4uoh4rg9vyahvb8bpd6dfn7 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.zfsterm_aud
    ADD CONSTRAINT fk27dwwh5ekpa60ug4ystuwwn6w FOREIGN KEY (curie, rev) REFERENCES public.stageterm_aud(curie, rev);
ALTER TABLE ONLY public.association_aud
    ADD CONSTRAINT fk2cnuv5m2xs6vaupmjsnce0sq6 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.diseaseannotation_conditionrelation_aud
    ADD CONSTRAINT fk2i4y7kdvdurhtxhq4qb8ijy0c FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.vocabulary_aud
    ADD CONSTRAINT fk2pe60ji8kxipj6qf3cd6hjda5 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.biologicalentity
    ADD CONSTRAINT fk36gq0macvyrb6objapp3o8bwp FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.xsmoterm
    ADD CONSTRAINT fk36v85u2ghsukqljkkh1jxmtlx FOREIGN KEY (curie) REFERENCES public.chemicalterm(curie);
ALTER TABLE ONLY public.note
    ADD CONSTRAINT fk3ru1c5ymn3vcqfb32p93ywimk FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.note_reference_aud
    ADD CONSTRAINT fk4220odf5ydvxanscty4b77gxu FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.association
    ADD CONSTRAINT fk4hugd4syivmg65wvbnlajtbqp FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkloadgroup
    ADD CONSTRAINT fk4j3eii20chv4644ww8abl3rai FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.wblsterm
    ADD CONSTRAINT fk4k9uvngmwim574bm5mu5q4203 FOREIGN KEY (curie) REFERENCES public.stageterm(curie);
ALTER TABLE ONLY public.goterm_aud
    ADD CONSTRAINT fk4kjm9hm06yutma1ilq04h967s FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.gene_aud
    ADD CONSTRAINT fk4n82maba8vniaxet7w2w1sfg4 FOREIGN KEY (curie, rev) REFERENCES public.genomicentity_aud(curie, rev);
ALTER TABLE ONLY public.phenotypeterm
    ADD CONSTRAINT fk4ymq8h2kdhq6ix6sfb4q4fn7a FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);
ALTER TABLE ONLY public.diseaseannotation_note
    ADD CONSTRAINT fk52oyqivgshum07elq4opyfr5o FOREIGN KEY (relatednotes_id) REFERENCES public.note(id);
ALTER TABLE ONLY public.biologicalentity_aud
    ADD CONSTRAINT fk5hkwd2k49xql5qy0aby85qtad FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.soterm_aud
    ADD CONSTRAINT fk5i3iqfnxf9hxjq6jmay2gm68g FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.curationreporthistory_aud
    ADD CONSTRAINT fk5mffk8y6qajllde8m24dwjn9v FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.crossreference
    ADD CONSTRAINT fk5x1tcwy014uoda88nbro7h443 FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.person_oldemails
    ADD CONSTRAINT fk654l0pd6eiho72xjxl7gkk6nh FOREIGN KEY (person_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.person
    ADD CONSTRAINT fk6ujtrho00dn63yrdkw87wq8e2 FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkloadgroup_aud
    ADD CONSTRAINT fk722g0iotb8v01pq0cej3w7gke FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.curationreporthistory
    ADD CONSTRAINT fk73td89gv1cchrqjdgu4fjq7ri FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.conditionrelation
    ADD CONSTRAINT fk76h2a6c78nwngxavbl9jhgk83 FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.vocabularyterm
    ADD CONSTRAINT fk7al3e88sx9urmj8exf5cfqvuk FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.chebiterm_aud
    ADD CONSTRAINT fk7grscrrhdcw9ek6agi78j4ca1 FOREIGN KEY (curie, rev) REFERENCES public.chemicalterm_aud(curie, rev);
ALTER TABLE ONLY public.diseaseannotation_vocabularyterm
    ADD CONSTRAINT fk7jhlm01yyrnyd26c5extqi9iv FOREIGN KEY (diseasequalifiers_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.materm_aud
    ADD CONSTRAINT fk7lfprbh8k8mnw9yf8ywp7xieg FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);
ALTER TABLE ONLY public.curationreportgroup_aud
    ADD CONSTRAINT fk7pl1pmjstb9eqi80e353a06v8 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.bulkloadfile_aud
    ADD CONSTRAINT fk7sl5m81qa11sr40chch9vg6uj FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.genegenomiclocation
    ADD CONSTRAINT fk7ub2juqlwvc30f4ptun1hf7oe FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.synonym
    ADD CONSTRAINT fk7vnrtg56eqmveqgq57srqx145 FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.loggedinperson_aud
    ADD CONSTRAINT fk85a9lkhkdxh4n4nmrhyoms7m0 FOREIGN KEY (id, rev) REFERENCES public.person_aud(id, rev);
ALTER TABLE ONLY public.curationreportgroup
    ADD CONSTRAINT fk88u5vv0al4vnoh9jf3jtpcemn FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.reference_aud
    ADD CONSTRAINT fk897g2lxdu1btxkcikigm6j4wo FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.person_emails
    ADD CONSTRAINT fk8avuc0e64q9m8is24yofnegru FOREIGN KEY (person_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkmanualload_aud
    ADD CONSTRAINT fk8md78mwbobme56gipe40vj2qj FOREIGN KEY (id, rev) REFERENCES public.bulkload_aud(id, rev);
ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT fk8wx5lbnkk1026m46lpa67bl5s FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkloadfile
    ADD CONSTRAINT fk8x22rg3nrv53h5cx2cr1ktpik FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.synonym_aud
    ADD CONSTRAINT fk8y4re95uhgku1km6nsauced0b FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.crossreference_pageareas_aud
    ADD CONSTRAINT fk98pht4vedx64hgab6drv9mkv0 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.gene_genegenomiclocation_aud
    ADD CONSTRAINT fk9r6hcuks8roeulgycv5fs22t FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.allelediseaseannotation
    ADD CONSTRAINT fk_alleledasubject FOREIGN KEY (subject_curie) REFERENCES public.allele(curie) ON DELETE CASCADE;
ALTER TABLE ONLY public.genediseaseannotation
    ADD CONSTRAINT fk_genedasubject FOREIGN KEY (subject_curie) REFERENCES public.gene(curie) ON DELETE CASCADE;
ALTER TABLE ONLY public.agmdiseaseannotation
    ADD CONSTRAINT fk_agmdasubject FOREIGN KEY (subject_curie) REFERENCES public.affectedgenomicmodel(curie) ON DELETE CASCADE;
ALTER TABLE ONLY public.diseaseannotation_note_aud
    ADD CONSTRAINT fka8x8613e1vmoaxljdrvhftj7a FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.emapaterm_aud
    ADD CONSTRAINT fkaipxoy4lm50q9mphk2yp7whyh FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);
ALTER TABLE ONLY public.person_oldemails_aud
    ADD CONSTRAINT fkakd9rx5hbx6rtrrir7s5vnbdw FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.crossreference
    ADD CONSTRAINT fkakjy736v2tfb6amt6bgvub0j3 FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.anatomicalterm_aud
    ADD CONSTRAINT fkan2c886jcsep01s7rqibfghfh FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.ontologyterm
    ADD CONSTRAINT fkanjjxyhfysspj1s5v8sbsut7l FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.ncbitaxonterm_aud
    ADD CONSTRAINT fkap27v3trsn5u9q93qb8ikabrf FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.stageterm
    ADD CONSTRAINT fkas58x03rc132q00y838dv1gsb FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);
ALTER TABLE ONLY public.diseaseannotation_vocabularyterm
    ADD CONSTRAINT fkb7dg8qvpicnh87s0162sn62gl FOREIGN KEY (diseaseannotation_id) REFERENCES public.diseaseannotation(id);
ALTER TABLE ONLY public.stageterm_aud
    ADD CONSTRAINT fkbe4dl5s3i7ga7hqryddog2g0f FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.xpoterm_aud
    ADD CONSTRAINT fkbqybirrf1obv6esgeaj8os211 FOREIGN KEY (curie, rev) REFERENCES public.phenotypeterm_aud(curie, rev);
ALTER TABLE ONLY public.wblsterm_aud
    ADD CONSTRAINT fkbug8ndvvjf8e7e3rpcih06j63 FOREIGN KEY (curie, rev) REFERENCES public.stageterm_aud(curie, rev);
ALTER TABLE ONLY public.xbedterm
    ADD CONSTRAINT fkc0dct519510hodhd9d7phfq6g FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);
ALTER TABLE ONLY public.person_emails_aud
    ADD CONSTRAINT fkc0wx6sg207a31fbnb6rr46kkg FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.allele_aud
    ADD CONSTRAINT fkc4cub43jynmwqke9rpwpglhkt FOREIGN KEY (curie, rev) REFERENCES public.genomicentity_aud(curie, rev);
ALTER TABLE ONLY public.molecule_aud
    ADD CONSTRAINT fkcbo1onn61w7v5ivh1e1h2tcd7 FOREIGN KEY (curie, rev) REFERENCES public.chemicalterm_aud(curie, rev);
ALTER TABLE ONLY public.vocabularyterm_textsynonyms_aud
    ADD CONSTRAINT fkcf8u1gm2oabsasujo5y26gruv FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.xbaterm
    ADD CONSTRAINT fkcnuymf0pvlgwcfd918o4pgs5n FOREIGN KEY (curie) REFERENCES public.anatomicalterm(curie);
ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT fkco6nij8nirwvpnf43vhcecs0y FOREIGN KEY (diseasegeneticmodifier_curie) REFERENCES public.biologicalentity(curie) ON DELETE CASCADE;
ALTER TABLE ONLY public.bulkscheduledload_aud
    ADD CONSTRAINT fkcy45x0q8y5riyg80olf0mtwyh FOREIGN KEY (id, rev) REFERENCES public.bulkload_aud(id, rev);
ALTER TABLE ONLY public.person
    ADD CONSTRAINT fkd51g73pvihqnndvly2f44lqrn FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.affectedgenomicmodel_aud
    ADD CONSTRAINT fkd6m9in16kh1tqvln37a13r3hx FOREIGN KEY (curie, rev) REFERENCES public.genomicentity_aud(curie, rev);
ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT fkddi5xpbarp8yfp46o1f5k30xi FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.ontologyterm_aud
    ADD CONSTRAINT fkdxjp2u3w3xoi7p9j7huceg2ts FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.reference
    ADD CONSTRAINT fke2knql9fqrwglgxvov9sb833e FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkloadgroup
    ADD CONSTRAINT fke3w2c0d5038mua8j9hhsfmqre FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.zecoterm_aud
    ADD CONSTRAINT fke5wuchgyjhb2orgvht50q2dah FOREIGN KEY (curie, rev) REFERENCES public.experimentalconditionontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.biologicalentity
    ADD CONSTRAINT fkeb2gg0lnhwlyonbovjnv07eu6 FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.diseaseannotation_note
    ADD CONSTRAINT fkeb3hi0d63vs83vg7lbqavs6pu FOREIGN KEY (diseaseannotation_id) REFERENCES public.diseaseannotation(id);
ALTER TABLE ONLY public.conditionrelation
    ADD CONSTRAINT fkepks7ittosxkwdcvwn06ash19 FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.vocabulary
    ADD CONSTRAINT fkf3bctkbi3fg4cy8nqpakt5ujl FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.synonym
    ADD CONSTRAINT fkf6mf8g3b7cy1our37ief0dgr6 FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.note
    ADD CONSTRAINT fkffhfxvyfw1pxf2km0o82k5jp7 FOREIGN KEY (notetype_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.vocabularyterm_aud
    ADD CONSTRAINT fkfg8df1h10nehkt3pw4cebbde8 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.bulkurlload_aud
    ADD CONSTRAINT fkfvaendvsykh6kly11h1207bdo FOREIGN KEY (id, rev) REFERENCES public.bulkscheduledload_aud(id, rev);
ALTER TABLE ONLY public.mpterm_aud
    ADD CONSTRAINT fkg4sqxe4ofrkn9vaenvdrrffwt FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.genediseaseannotation_aud
    ADD CONSTRAINT fkgdfxkba52f3wpivygqajpgveq FOREIGN KEY (id, rev) REFERENCES public.diseaseannotation_aud(id, rev);
ALTER TABLE ONLY public.vocabularyterm
    ADD CONSTRAINT fkgefxm2laoldkaafmjjvoesag4 FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.daoterm_aud
    ADD CONSTRAINT fkgif1cep78abowfodb5rxvoq1x FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);
ALTER TABLE ONLY public.curationreporthistory
    ADD CONSTRAINT fkgjgh4i3snk2uapi0nksmdbnpv FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkloadfileexception
    ADD CONSTRAINT fkh0omhd3t3hnpmcopia04rmm0v FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkloadfilehistory
    ADD CONSTRAINT fkh69f048adbnwqj0w32wme943l FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.xbaterm_aud
    ADD CONSTRAINT fkha6obinkag86qlcpemxmgv4ly FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);
ALTER TABLE ONLY public.ontologyterm_definitionurls_aud
    ADD CONSTRAINT fkhddvwlroknt64hmthirfpo7no FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.reference
    ADD CONSTRAINT fkhgidt2wmi7xvxv7hpom0vq6gt FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkload_aud
    ADD CONSTRAINT fkhhh8994753467hwa33blp22dc FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.wbbtterm_aud
    ADD CONSTRAINT fkhu85m34h8hf95s453u3m4ed8y FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);
ALTER TABLE ONLY public.vocabulary
    ADD CONSTRAINT fki380voms7lg1dub0t0ty2bwwk FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.chemicalterm_aud
    ADD CONSTRAINT fkieeg5x1a11dqom8dw4valm169 FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.xbsterm_aud
    ADD CONSTRAINT fkij9o8qar117chfev5ghkupl3o FOREIGN KEY (curie, rev) REFERENCES public.stageterm_aud(curie, rev);
ALTER TABLE ONLY public.diseaseannotation_ecoterm_aud
    ADD CONSTRAINT fkinnydpai0athl1jn4vpjqulnq FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.genomicentity_synonym_aud
    ADD CONSTRAINT fkirf7xq12pkuqpna31dbdiylo4 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.xsmoterm_aud
    ADD CONSTRAINT fkjdlhxyvw79i14932dqdb1bx8f FOREIGN KEY (curie, rev) REFERENCES public.chemicalterm_aud(curie, rev);
ALTER TABLE ONLY public.ontologyterm_secondaryidentifiers_aud
    ADD CONSTRAINT fkjvsj7p0oj0plbpxtu04cskfhs FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.diseaseannotation_aud
    ADD CONSTRAINT fkkco190qeeoqsitv4p2ecvamtu FOREIGN KEY (id, rev) REFERENCES public.association_aud(id, rev);
ALTER TABLE ONLY public.conditionrelation_aud
    ADD CONSTRAINT fkkcw0iu1vmw6ttm7g645947yyy FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.genegenomiclocation
    ADD CONSTRAINT fkkdwe6pqs0h91n7bxm3rwbbnh0 FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.doterm_aud
    ADD CONSTRAINT fkkgu80ih0f55tskr386gucsqh2 FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.bulkloadfileexception
    ADD CONSTRAINT fkkixcrsm0avf27cqtxdj9hhm9v FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.experimentalconditionontologyterm_aud
    ADD CONSTRAINT fkkr4o08hq0jboq6g4ou5gmn8xd FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.ontologyterm_synonyms_aud
    ADD CONSTRAINT fkl2ra6s3aosf68bgss49loflot FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.bulkfmsload_aud
    ADD CONSTRAINT fkl33k8qcbmbx4yssfnvlgicyri FOREIGN KEY (id, rev) REFERENCES public.bulkscheduledload_aud(id, rev);
ALTER TABLE ONLY public.agmdiseaseannotation_aud
    ADD CONSTRAINT fkl6x226295n9ms1kugrsi88efp FOREIGN KEY (id, rev) REFERENCES public.diseaseannotation_aud(id, rev);
ALTER TABLE ONLY public.diseaseannotation_vocabularyterm_aud
    ADD CONSTRAINT fkl7nljf1i3ulbkxmtr1m174m86 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.genegenomiclocation_aud
    ADD CONSTRAINT fklln6of6vsrfvcbnku3x9na2k0 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.curationreportgroup
    ADD CONSTRAINT fklt1jeksri20mben7ku0qin4si FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.curationreport
    ADD CONSTRAINT fklxm7gwmtxl1qlxbc0x00nk9xq FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkloadfileexception_aud
    ADD CONSTRAINT fkm7op2ir0vi9pwcctl39kqbo70 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.mmusdvterm
    ADD CONSTRAINT fkmkv8r93hlnf06oa8xy2usdq9g FOREIGN KEY (curie) REFERENCES public.stageterm(curie);
ALTER TABLE ONLY public.allelediseaseannotation_aud
    ADD CONSTRAINT fkn5epg1m6f6l6cqh59ai23mws3 FOREIGN KEY (id, rev) REFERENCES public.diseaseannotation_aud(id, rev);
ALTER TABLE ONLY public.fbdvterm
    ADD CONSTRAINT fkn7q3y19l70sef1h4f9ippnjoa FOREIGN KEY (curie) REFERENCES public.stageterm(curie);
ALTER TABLE ONLY public.xbsterm
    ADD CONSTRAINT fkn8tvxj5qdpjssxjl9jneqst92 FOREIGN KEY (curie) REFERENCES public.stageterm(curie);
ALTER TABLE ONLY public.genomicentity_aud
    ADD CONSTRAINT fknd0sic0qo3ko71w4d9k5urg48 FOREIGN KEY (curie, rev) REFERENCES public.biologicalentity_aud(curie, rev);
ALTER TABLE ONLY public.zfsterm
    ADD CONSTRAINT fknkrcnh3l23ol126v9w61nk079 FOREIGN KEY (curie) REFERENCES public.stageterm(curie);
ALTER TABLE ONLY public.xcoterm_aud
    ADD CONSTRAINT fknlbuiyo3i6daerkmpim317bd3 FOREIGN KEY (curie, rev) REFERENCES public.experimentalconditionontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.note_reference
    ADD CONSTRAINT fknr8td9rfl6vd6cstukci0e0qq FOREIGN KEY (note_id) REFERENCES public.note(id);
ALTER TABLE ONLY public.curationreport
    ADD CONSTRAINT fkns8e08gsl14gra1obg63jmm3m FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.vocabularyterm_crossreference_aud
    ADD CONSTRAINT fko00dyud2urf5787jggfx57nyf FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.bulkloadfile
    ADD CONSTRAINT fko6rgciyt4bsh1i48wcb0ar51u FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.conditionrelation_experimentalcondition_aud
    ADD CONSTRAINT fkogkeeb66oxunetml17wbxkogv FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.experimentalcondition_aud
    ADD CONSTRAINT fkos799amubpywlufc5ttysjp5h FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.ontologyterm
    ADD CONSTRAINT fkovkusgncyac2o27pb4vp7ni0n FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkload
    ADD CONSTRAINT fkoyli2i7necktxvvqeamcisk8d FOREIGN KEY (modifiedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.bulkloadfilehistory
    ADD CONSTRAINT fkp57143k8kjks65susryxvbewg FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.note_reference
    ADD CONSTRAINT fkpjpycg6lduif89o5ahp4d8u8 FOREIGN KEY (references_curie) REFERENCES public.reference(curie);
ALTER TABLE ONLY public.bulkloadfilehistory_aud
    ADD CONSTRAINT fkppa5tcqtwv560svqkq6b958hc FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.association
    ADD CONSTRAINT fkpsajgrrbs4x9panhka0cq9ihy FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.genomicentity_secondaryidentifiers_aud
    ADD CONSTRAINT fkpt86y0ets51hu48asq8x2g98s FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.conditionrelation
    ADD CONSTRAINT fkq7oftj89x5jfekjhhc0lah3j3 FOREIGN KEY (singlereference_curie) REFERENCES public.reference(curie);
ALTER TABLE ONLY public.person_aud
    ADD CONSTRAINT fkqbm2y5o4elhanxeq26reu73yd FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.xpoterm
    ADD CONSTRAINT fkqeoygtsa6sf94gw089ugocoph FOREIGN KEY (curie) REFERENCES public.phenotypeterm(curie);
ALTER TABLE ONLY public.bulkload
    ADD CONSTRAINT fkr6h3wcsynq5hh156kglkji08i FOREIGN KEY (createdby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.allele_genegenomiclocation_aud
    ADD CONSTRAINT fkr7xwt9rj6bxnmrlyd4h9d9oe7 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.ecoterm_aud
    ADD CONSTRAINT fkrdtwy8r0gnnh6numgdbgi9e6s FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.crossreference_aud
    ADD CONSTRAINT fkricj7nn0u0fec2l2r2fo7al55 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.curationreporthistory
    ADD CONSTRAINT fks207a6ublnuxngm7r98p8yf6d FOREIGN KEY (curationreport_id) REFERENCES public.curationreport(id);
ALTER TABLE ONLY public.curationreport_aud
    ADD CONSTRAINT fks2fh7j9mb60yfxyd83gklhgmo FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.genomicentity_crossreference_aud
    ADD CONSTRAINT fks5gsr2myfi6i0mq3n9rix7lpd FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.zfaterm_aud
    ADD CONSTRAINT fks66s1k4fon0to2kk7qfsm1xon FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);
ALTER TABLE ONLY public.diseaseannotation_gene_aud
    ADD CONSTRAINT fks6e9od45c5fv6nd4olb819vsd FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.phenotypeterm_aud
    ADD CONSTRAINT fksap791c8unrey4xnqcydm8kv1 FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.xbedterm_aud
    ADD CONSTRAINT fkshxj981p427yuk4qgtvcirp5k FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);
ALTER TABLE ONLY public.ontologyterm_subsets_aud
    ADD CONSTRAINT fksn5i9xgd2avp7co3c70lv37 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);
ALTER TABLE ONLY public.curationreport
    ADD CONSTRAINT fkt78gdn62wmw2ods3coj8rqyoe FOREIGN KEY (curationreportgroup_id) REFERENCES public.curationreportgroup(id);
ALTER TABLE ONLY public.fbdvterm_aud
    ADD CONSTRAINT fkvtaradvq4e6fdjecf2m4ujap FOREIGN KEY (curie, rev) REFERENCES public.stageterm_aud(curie, rev);






































