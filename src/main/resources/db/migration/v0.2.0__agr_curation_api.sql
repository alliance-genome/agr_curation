create sequence hibernate_sequence start 1 increment 1;

    create table AffectedGenomicModel (
       parental_population varchar(255),
        subtype varchar(255),
        curie varchar(255) not null,
        primary key (curie)
    );

    create table AffectedGenomicModel_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        parental_population varchar(255),
        subtype varchar(255),
        primary key (curie, REV)
    );

    create table AGMDiseaseAnnotation (
       predicate varchar(255),
        id int8 not null,
        inferredAllele_curie varchar(255),
        inferredGene_curie varchar(255),
        subject_curie varchar(255),
        primary key (id)
    );

    create table AGMDiseaseAnnotation_AUD (
       id int8 not null,
        REV int4 not null,
        predicate varchar(255),
        inferredAllele_curie varchar(255),
        inferredGene_curie varchar(255),
        subject_curie varchar(255),
        primary key (id, REV)
    );

    create table Allele (
       description TEXT,
        feature_type varchar(255),
        symbol varchar(255),
        curie varchar(255) not null,
        primary key (curie)
    );

    create table Allele_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        description TEXT,
        feature_type varchar(255),
        symbol varchar(255),
        primary key (curie, REV)
    );

    create table Allele_GeneGenomicLocation (
       Allele_curie varchar(255) not null,
        genomicLocations_id int8 not null
    );

    create table Allele_GeneGenomicLocation_AUD (
       REV int4 not null,
        Allele_curie varchar(255) not null,
        genomicLocations_id int8 not null,
        REVTYPE int2,
        primary key (REV, Allele_curie, genomicLocations_id)
    );

    create table AlleleDiseaseAnnotation (
       predicate varchar(255),
        id int8 not null,
        inferredGene_curie varchar(255),
        subject_curie varchar(255),
        primary key (id)
    );

    create table AlleleDiseaseAnnotation_AUD (
       id int8 not null,
        REV int4 not null,
        predicate varchar(255),
        inferredGene_curie varchar(255),
        subject_curie varchar(255),
        primary key (id, REV)
    );

    create table AnatomicalTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table AnatomicalTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table Association (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        uniqueId varchar(2000),
        primary key (id)
    );

    create table Association_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        primary key (id, REV)
    );

    create table BiologicalEntity (
       curie varchar(255) not null,
        created timestamp,
        lastUpdated timestamp,
        taxon_curie varchar(255),
        primary key (curie)
    );

    create table BiologicalEntity_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        REVTYPE int2,
        taxon_curie varchar(255),
        primary key (curie, REV)
    );

    create table BulkFMSLoad (
       dataSubType varchar(255),
        dataType varchar(255),
        id int8 not null,
        primary key (id)
    );

    create table BulkFMSLoad_AUD (
       id int8 not null,
        REV int4 not null,
        dataSubType varchar(255),
        dataType varchar(255),
        primary key (id, REV)
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
        primary key (id)
    );

    create table BulkLoad_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        backendBulkLoadType varchar(255),
        errorMessage TEXT,
        fileExtension varchar(255),
        name varchar(255),
        ontologyType varchar(255),
        status varchar(255),
        group_id int8,
        primary key (id, REV)
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
        primary key (id)
    );

    create table BulkLoadFile_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        errorMessage TEXT,
        fileSize int8,
        localFilePath varchar(255),
        md5Sum varchar(255),
        recordCount int4,
        s3Path varchar(255),
        status varchar(255),
        bulkLoad_id int8,
        primary key (id, REV)
    );

    create table BulkLoadGroup (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        name varchar(255),
        primary key (id)
    );

    create table BulkLoadGroup_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        name varchar(255),
        primary key (id, REV)
    );

    create table BulkManualLoad (
       dataType varchar(255),
        id int8 not null,
        primary key (id)
    );

    create table BulkManualLoad_AUD (
       id int8 not null,
        REV int4 not null,
        dataType varchar(255),
        primary key (id, REV)
    );

    create table BulkScheduledLoad (
       cronSchedule varchar(255),
        scheduleActive boolean,
        schedulingErrorMessage TEXT,
        id int8 not null,
        primary key (id)
    );

    create table BulkScheduledLoad_AUD (
       id int8 not null,
        REV int4 not null,
        cronSchedule varchar(255),
        scheduleActive boolean,
        schedulingErrorMessage TEXT,
        primary key (id, REV)
    );

    create table BulkURLLoad (
       url varchar(255),
        id int8 not null,
        primary key (id)
    );

    create table BulkURLLoad_AUD (
       id int8 not null,
        REV int4 not null,
        url varchar(255),
        primary key (id, REV)
    );

    create table CHEBITerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table CHEBITerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
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

    create table ChemicalTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        formula varchar(255),
        inchi varchar(750),
        inchiKey varchar(255),
        iupac varchar(500),
        smiles varchar(500),
        primary key (curie, REV)
    );

    create table ConditionRelation (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        uniqueId varchar(2000),
        conditionRelationType varchar(255),
        primary key (id)
    );

    create table ConditionRelation_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        conditionRelationType varchar(255),
        primary key (id, REV)
    );

    create table ConditionRelation_ExperimentalCondition (
       ConditionRelation_id int8 not null,
        conditions_id int8 not null
    );

    create table ConditionRelation_ExperimentalCondition_AUD (
       REV int4 not null,
        ConditionRelation_id int8 not null,
        conditions_id int8 not null,
        REVTYPE int2,
        primary key (REV, ConditionRelation_id, conditions_id)
    );

    create table CrossReference (
       curie varchar(255) not null,
        created timestamp,
        lastUpdated timestamp,
        displayName varchar(255),
        prefix varchar(255),
        primary key (curie)
    );

    create table CrossReference_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        REVTYPE int2,
        displayName varchar(255),
        prefix varchar(255),
        primary key (curie, REV)
    );

    create table CrossReference_pageAreas (
       CrossReference_curie varchar(255) not null,
        pageAreas varchar(255)
    );

    create table CrossReference_pageAreas_AUD (
       REV int4 not null,
        CrossReference_curie varchar(255) not null,
        pageAreas varchar(255) not null,
        REVTYPE int2,
        primary key (REV, CrossReference_curie, pageAreas)
    );

    create table DAOTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table DAOTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table DiseaseAnnotation (
       diseaseRelation varchar(255),
        modId varchar(255),
        negated boolean default false not null,
        id int8 not null,
        object_curie varchar(255),
        reference_curie varchar(255),
        primary key (id)
    );

    create table DiseaseAnnotation_AUD (
       id int8 not null,
        REV int4 not null,
        diseaseRelation varchar(255),
        modId varchar(255),
        negated boolean default false,
        object_curie varchar(255),
        reference_curie varchar(255),
        primary key (id, REV)
    );

    create table DiseaseAnnotation_ConditionRelation (
       DiseaseAnnotation_id int8 not null,
        conditionRelations_id int8 not null
    );

    create table DiseaseAnnotation_ConditionRelation_AUD (
       REV int4 not null,
        DiseaseAnnotation_id int8 not null,
        conditionRelations_id int8 not null,
        REVTYPE int2,
        primary key (REV, DiseaseAnnotation_id, conditionRelations_id)
    );

    create table DiseaseAnnotation_EcoTerm (
       DiseaseAnnotation_id int8 not null,
        evidenceCodes_curie varchar(255) not null
    );

    create table DiseaseAnnotation_EcoTerm_AUD (
       REV int4 not null,
        DiseaseAnnotation_id int8 not null,
        evidenceCodes_curie varchar(255) not null,
        REVTYPE int2,
        primary key (REV, DiseaseAnnotation_id, evidenceCodes_curie)
    );

    create table DiseaseAnnotation_Gene (
       DiseaseAnnotation_id int8 not null,
        with_curie varchar(255) not null
    );

    create table DiseaseAnnotation_Gene_AUD (
       REV int4 not null,
        DiseaseAnnotation_id int8 not null,
        with_curie varchar(255) not null,
        REVTYPE int2,
        primary key (REV, DiseaseAnnotation_id, with_curie)
    );

    create table DOTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table DOTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table EcoTerm (
       abbreviation varchar(255),
        curie varchar(255) not null,
        primary key (curie)
    );

    create table EcoTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        abbreviation varchar(255),
        primary key (curie, REV)
    );

    create table EMAPATerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table EMAPATerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
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
        primary key (id)
    );

    create table ExperimentalCondition_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        conditionQuantity varchar(255),
        conditionStatement varchar(255),
        conditionAnatomy_curie varchar(255),
        conditionChemical_curie varchar(255),
        conditionClass_curie varchar(255),
        conditionGeneOntology_curie varchar(255),
        conditionId_curie varchar(255),
        conditionTaxon_curie varchar(255),
        primary key (id, REV)
    );

    create table ExperimentalCondition_PaperHandle (
       ExperimentalCondition_id int8 not null,
        paperHandles_handle varchar(255) not null
    );

    create table ExperimentalCondition_PaperHandle_AUD (
       REV int4 not null,
        ExperimentalCondition_id int8 not null,
        paperHandles_handle varchar(255) not null,
        REVTYPE int2,
        primary key (REV, ExperimentalCondition_id, paperHandles_handle)
    );

    create table ExperimentalConditionOntologyTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table ExperimentalConditionOntologyTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
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

    create table Gene_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        automatedGeneDescription TEXT,
        geneSynopsis TEXT,
        geneSynopsisURL varchar(255),
        symbol varchar(255),
        geneType_curie varchar(255),
        primary key (curie, REV)
    );

    create table Gene_GeneGenomicLocation (
       Gene_curie varchar(255) not null,
        genomicLocations_id int8 not null
    );

    create table Gene_GeneGenomicLocation_AUD (
       REV int4 not null,
        Gene_curie varchar(255) not null,
        genomicLocations_id int8 not null,
        REVTYPE int2,
        primary key (REV, Gene_curie, genomicLocations_id)
    );

    create table GeneDiseaseAnnotation (
       predicate varchar(255),
        id int8 not null,
        sgdStrainBackground_curie varchar(255),
        subject_curie varchar(255),
        primary key (id)
    );

    create table GeneDiseaseAnnotation_AUD (
       id int8 not null,
        REV int4 not null,
        predicate varchar(255),
        sgdStrainBackground_curie varchar(255),
        subject_curie varchar(255),
        primary key (id, REV)
    );

    create table GeneGenomicLocation (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        assembly varchar(255),
        endPos int4,
        startPos int4,
        primary key (id)
    );

    create table GeneGenomicLocation_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        assembly varchar(255),
        endPos int4,
        startPos int4,
        primary key (id, REV)
    );

    create table GenomicEntity (
       name TEXT,
        curie varchar(255) not null,
        primary key (curie)
    );

    create table GenomicEntity_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        name TEXT,
        primary key (curie, REV)
    );

    create table GenomicEntity_CrossReference (
       GenomicEntity_curie varchar(255) not null,
        crossReferences_curie varchar(255) not null
    );

    create table GenomicEntity_CrossReference_AUD (
       REV int4 not null,
        GenomicEntity_curie varchar(255) not null,
        crossReferences_curie varchar(255) not null,
        REVTYPE int2,
        primary key (REV, GenomicEntity_curie, crossReferences_curie)
    );

    create table GenomicEntity_secondaryIdentifiers (
       GenomicEntity_curie varchar(255) not null,
        secondaryIdentifiers varchar(255)
    );

    create table GenomicEntity_secondaryIdentifiers_AUD (
       REV int4 not null,
        GenomicEntity_curie varchar(255) not null,
        secondaryIdentifiers varchar(255) not null,
        REVTYPE int2,
        primary key (REV, GenomicEntity_curie, secondaryIdentifiers)
    );

    create table GenomicEntity_Synonym (
       genomicEntities_curie varchar(255) not null,
        synonyms_id int8 not null
    );

    create table GenomicEntity_Synonym_AUD (
       REV int4 not null,
        genomicEntities_curie varchar(255) not null,
        synonyms_id int8 not null,
        REVTYPE int2,
        primary key (REV, genomicEntities_curie, synonyms_id)
    );

    create table GOTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table GOTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table MATerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table MATerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table Molecule (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table Molecule_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table MPTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table MPTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table NCBITaxonTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table NCBITaxonTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
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
        primary key (curie)
    );

    create table OntologyTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        REVTYPE int2,
        definition TEXT,
        name varchar(2000),
        namespace varchar(255),
        obsolete boolean,
        type varchar(255),
        primary key (curie, REV)
    );

    create table OntologyTerm_CrossReference (
       OntologyTerm_curie varchar(255) not null,
        crossReferences_curie varchar(255) not null
    );

    create table OntologyTerm_CrossReference_AUD (
       REV int4 not null,
        OntologyTerm_curie varchar(255) not null,
        crossReferences_curie varchar(255) not null,
        REVTYPE int2,
        primary key (REV, OntologyTerm_curie, crossReferences_curie)
    );

    create table OntologyTerm_definitionUrls (
       OntologyTerm_curie varchar(255) not null,
        definitionUrls TEXT
    );

    create table OntologyTerm_definitionUrls_AUD (
       REV int4 not null,
        OntologyTerm_curie varchar(255) not null,
        definitionUrls TEXT not null,
        REVTYPE int2,
        primary key (REV, OntologyTerm_curie, definitionUrls)
    );

    create table OntologyTerm_secondaryIdentifiers (
       OntologyTerm_curie varchar(255) not null,
        secondaryIdentifiers varchar(255)
    );

    create table OntologyTerm_secondaryIdentifiers_AUD (
       REV int4 not null,
        OntologyTerm_curie varchar(255) not null,
        secondaryIdentifiers varchar(255) not null,
        REVTYPE int2,
        primary key (REV, OntologyTerm_curie, secondaryIdentifiers)
    );

    create table OntologyTerm_subsets (
       OntologyTerm_curie varchar(255) not null,
        subsets varchar(255)
    );

    create table OntologyTerm_subsets_AUD (
       REV int4 not null,
        OntologyTerm_curie varchar(255) not null,
        subsets varchar(255) not null,
        REVTYPE int2,
        primary key (REV, OntologyTerm_curie, subsets)
    );

    create table OntologyTerm_synonyms (
       OntologyTerm_curie varchar(255) not null,
        synonyms TEXT
    );

    create table OntologyTerm_synonyms_AUD (
       REV int4 not null,
        OntologyTerm_curie varchar(255) not null,
        synonyms TEXT not null,
        REVTYPE int2,
        primary key (REV, OntologyTerm_curie, synonyms)
    );

    create table PaperHandle (
       handle varchar(255) not null,
        reference_curie varchar(255),
        primary key (handle)
    );

    create table PaperHandle_AUD (
       handle varchar(255) not null,
        REV int4 not null,
        REVTYPE int2,
        reference_curie varchar(255),
        primary key (handle, REV)
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
        primary key (id)
    );

    create table Reference (
       curie varchar(255) not null,
        created timestamp,
        lastUpdated timestamp,
        primary key (curie)
    );

    create table Reference_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        REVTYPE int2,
        primary key (curie, REV)
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

    create table SOTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table Synonym (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        name varchar(255),
        primary key (id)
    );

    create table Synonym_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        name varchar(255),
        primary key (id, REV)
    );

    create table Vocabulary (
       id int8 not null,
        created timestamp,
        lastUpdated timestamp,
        isObsolete boolean default false not null,
        name varchar(255),
        vocabularyDescription varchar(255),
        primary key (id)
    );

    create table Vocabulary_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        isObsolete boolean default false,
        name varchar(255),
        vocabularyDescription varchar(255),
        primary key (id, REV)
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
        primary key (id)
    );

    create table VocabularyTerm_AUD (
       id int8 not null,
        REV int4 not null,
        REVTYPE int2,
        abbreviation varchar(255),
        definition varchar(255),
        isObsolete boolean default false,
        name varchar(255),
        vocabulary_id int8,
        primary key (id, REV)
    );

    create table VocabularyTerm_CrossReference (
       VocabularyTerm_id int8 not null,
        crossReferences_curie varchar(255) not null
    );

    create table VocabularyTerm_CrossReference_AUD (
       REV int4 not null,
        VocabularyTerm_id int8 not null,
        crossReferences_curie varchar(255) not null,
        REVTYPE int2,
        primary key (REV, VocabularyTerm_id, crossReferences_curie)
    );

    create table VocabularyTerm_textSynonyms (
       VocabularyTerm_id int8 not null,
        textSynonyms TEXT
    );

    create table VocabularyTerm_textSynonyms_AUD (
       REV int4 not null,
        VocabularyTerm_id int8 not null,
        textSynonyms TEXT not null,
        REVTYPE int2,
        primary key (REV, VocabularyTerm_id, textSynonyms)
    );

    create table WBbtTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table WBbtTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table XcoTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table XcoTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table ZecoTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table ZecoTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
    );

    create table ZfaTerm (
       curie varchar(255) not null,
        primary key (curie)
    );

    create table ZfaTerm_AUD (
       curie varchar(255) not null,
        REV int4 not null,
        primary key (curie, REV)
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

    alter table if exists AffectedGenomicModel_AUD 
       add constraint FKd6m9in16kh1tqvln37a13r3hx 
       foreign key (curie, REV) 
       references GenomicEntity_AUD;

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

    alter table if exists AGMDiseaseAnnotation 
       add constraint FKp1rktcpoyvnr2f756ncdb8k24 
       foreign key (id) 
       references DiseaseAnnotation;

    alter table if exists AGMDiseaseAnnotation_AUD 
       add constraint FKl6x226295n9ms1kugrsi88efp 
       foreign key (id, REV) 
       references DiseaseAnnotation_AUD;

    alter table if exists Allele 
       add constraint FK42r7586hi59wcwakfyr30l6l3 
       foreign key (curie) 
       references GenomicEntity;

    alter table if exists Allele_AUD 
       add constraint FKc4cub43jynmwqke9rpwpglhkt 
       foreign key (curie, REV) 
       references GenomicEntity_AUD;

    alter table if exists Allele_GeneGenomicLocation 
       add constraint FKk3m8sj0e342pbhl2q56awc16a 
       foreign key (genomicLocations_id) 
       references GeneGenomicLocation;

    alter table if exists Allele_GeneGenomicLocation 
       add constraint FKomfoc4gujrmdg18e3xe3kisdp 
       foreign key (Allele_curie) 
       references Allele;

    alter table if exists Allele_GeneGenomicLocation_AUD 
       add constraint FKr7xwt9rj6bxnmrlyd4h9d9oe7 
       foreign key (REV) 
       references REVINFO;

    alter table if exists AlleleDiseaseAnnotation 
       add constraint FKnecrivvmqgg2ifhppubrjy5ey 
       foreign key (inferredGene_curie) 
       references Gene;

    alter table if exists AlleleDiseaseAnnotation 
       add constraint FKerk9wpvk1ka4pkm0t1dqyyeyl 
       foreign key (subject_curie) 
       references Allele;

    alter table if exists AlleleDiseaseAnnotation 
       add constraint FK3unb0kaxocbodllqe35hu4w0c 
       foreign key (id) 
       references DiseaseAnnotation;

    alter table if exists AlleleDiseaseAnnotation_AUD 
       add constraint FKn5epg1m6f6l6cqh59ai23mws3 
       foreign key (id, REV) 
       references DiseaseAnnotation_AUD;

    alter table if exists AnatomicalTerm 
       add constraint FKfepti479fro1b09ybaltkofqu 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists AnatomicalTerm_AUD 
       add constraint FKan2c886jcsep01s7rqibfghfh 
       foreign key (curie, REV) 
       references OntologyTerm_AUD;

    alter table if exists Association_AUD 
       add constraint FK2cnuv5m2xs6vaupmjsnce0sq6 
       foreign key (REV) 
       references REVINFO;

    alter table if exists BiologicalEntity 
       add constraint FK5c19vicptarinu2wgj7xyhhum 
       foreign key (taxon_curie) 
       references NCBITaxonTerm;

    alter table if exists BiologicalEntity_AUD 
       add constraint FK5hkwd2k49xql5qy0aby85qtad 
       foreign key (REV) 
       references REVINFO;

    alter table if exists BulkFMSLoad 
       add constraint FKj3lf8vdpiflwx2gisua4kurs1 
       foreign key (id) 
       references BulkScheduledLoad;

    alter table if exists BulkFMSLoad_AUD 
       add constraint FKl33k8qcbmbx4yssfnvlgicyri 
       foreign key (id, REV) 
       references BulkScheduledLoad_AUD;

    alter table if exists BulkLoad 
       add constraint FKo25bugche1vp384eme3exv04d 
       foreign key (group_id) 
       references BulkLoadGroup;

    alter table if exists BulkLoad_AUD 
       add constraint FKhhh8994753467hwa33blp22dc 
       foreign key (REV) 
       references REVINFO;

    alter table if exists BulkLoadFile 
       add constraint FKkakppk407vfefvttp4a5p6npg 
       foreign key (bulkLoad_id) 
       references BulkLoad;

    alter table if exists BulkLoadFile_AUD 
       add constraint FK7sl5m81qa11sr40chch9vg6uj 
       foreign key (REV) 
       references REVINFO;

    alter table if exists BulkLoadGroup_AUD 
       add constraint FK722g0iotb8v01pq0cej3w7gke 
       foreign key (REV) 
       references REVINFO;

    alter table if exists BulkManualLoad 
       add constraint FKd3pm3o2v9xb1uy15b241o56j 
       foreign key (id) 
       references BulkLoad;

    alter table if exists BulkManualLoad_AUD 
       add constraint FK8md78mwbobme56gipe40vj2qj 
       foreign key (id, REV) 
       references BulkLoad_AUD;

    alter table if exists BulkScheduledLoad 
       add constraint FK8br24n4em8tr58k9spdhep28f 
       foreign key (id) 
       references BulkLoad;

    alter table if exists BulkScheduledLoad_AUD 
       add constraint FKcy45x0q8y5riyg80olf0mtwyh 
       foreign key (id, REV) 
       references BulkLoad_AUD;

    alter table if exists BulkURLLoad 
       add constraint FKp6lnq5x8g34hc2i3b3focu4vg 
       foreign key (id) 
       references BulkScheduledLoad;

    alter table if exists BulkURLLoad_AUD 
       add constraint FKfvaendvsykh6kly11h1207bdo 
       foreign key (id, REV) 
       references BulkScheduledLoad_AUD;

    alter table if exists CHEBITerm 
       add constraint FK7enwyeblw2xt5yo5co0keko5f 
       foreign key (curie) 
       references ChemicalTerm;

    alter table if exists CHEBITerm_AUD 
       add constraint FK7grscrrhdcw9ek6agi78j4ca1 
       foreign key (curie, REV) 
       references ChemicalTerm_AUD;

    alter table if exists ChemicalTerm 
       add constraint FK2fegif3wy9egh5r2yy8wplrwu 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists ChemicalTerm_AUD 
       add constraint FKieeg5x1a11dqom8dw4valm169 
       foreign key (curie, REV) 
       references OntologyTerm_AUD;

    alter table if exists ConditionRelation_AUD 
       add constraint FKkcw0iu1vmw6ttm7g645947yyy 
       foreign key (REV) 
       references REVINFO;

    alter table if exists ConditionRelation_ExperimentalCondition 
       add constraint FKp2129xf9bt872p0rycff6iy93 
       foreign key (conditions_id) 
       references ExperimentalCondition;

    alter table if exists ConditionRelation_ExperimentalCondition 
       add constraint FK69kutljbycmp1vimotmlxp36j 
       foreign key (ConditionRelation_id) 
       references ConditionRelation;

    alter table if exists ConditionRelation_ExperimentalCondition_AUD 
       add constraint FKogkeeb66oxunetml17wbxkogv 
       foreign key (REV) 
       references REVINFO;

    alter table if exists CrossReference_AUD 
       add constraint FKricj7nn0u0fec2l2r2fo7al55 
       foreign key (REV) 
       references REVINFO;

    alter table if exists CrossReference_pageAreas 
       add constraint FKmd0dqivib8hjjtqiqsiyq0hgf 
       foreign key (CrossReference_curie) 
       references CrossReference;

    alter table if exists CrossReference_pageAreas_AUD 
       add constraint FK98pht4vedx64hgab6drv9mkv0 
       foreign key (REV) 
       references REVINFO;

    alter table if exists DAOTerm 
       add constraint FK3xjbjyyuqqyvspspeael1m7fe 
       foreign key (curie) 
       references AnatomicalTerm;

    alter table if exists DAOTerm_AUD 
       add constraint FKgif1cep78abowfodb5rxvoq1x 
       foreign key (curie, REV) 
       references AnatomicalTerm_AUD;

    alter table if exists DiseaseAnnotation 
       add constraint FKp8u0w7kiirnjfcmdl4oyjhcs3 
       foreign key (object_curie) 
       references DOTerm;

    alter table if exists DiseaseAnnotation 
       add constraint FK77fmab327prjh1sb7gk6na6ak 
       foreign key (reference_curie) 
       references Reference;

    alter table if exists DiseaseAnnotation 
       add constraint FK5a3i0leqdmstsdfpq1j1b15el 
       foreign key (id) 
       references Association;

    alter table if exists DiseaseAnnotation_AUD 
       add constraint FKkco190qeeoqsitv4p2ecvamtu 
       foreign key (id, REV) 
       references Association_AUD;

    alter table if exists DiseaseAnnotation_ConditionRelation 
       add constraint FKlvpswihwk7vf7ijnyustt47na 
       foreign key (conditionRelations_id) 
       references ConditionRelation;

    alter table if exists DiseaseAnnotation_ConditionRelation 
       add constraint FK2wrhxll4ol0fxdpynbmue5stm 
       foreign key (DiseaseAnnotation_id) 
       references DiseaseAnnotation;

    alter table if exists DiseaseAnnotation_ConditionRelation_AUD 
       add constraint FK2i4y7kdvdurhtxhq4qb8ijy0c 
       foreign key (REV) 
       references REVINFO;

    alter table if exists DiseaseAnnotation_EcoTerm 
       add constraint FKp79bf46xsyojpvjjguoe3vuuu 
       foreign key (evidenceCodes_curie) 
       references EcoTerm;

    alter table if exists DiseaseAnnotation_EcoTerm 
       add constraint FK43rw1jai2kqggx518nsu8c4me 
       foreign key (DiseaseAnnotation_id) 
       references DiseaseAnnotation;

    alter table if exists DiseaseAnnotation_EcoTerm_AUD 
       add constraint FKinnydpai0athl1jn4vpjqulnq 
       foreign key (REV) 
       references REVINFO;

    alter table if exists DiseaseAnnotation_Gene 
       add constraint FK6akpr16qusnfom0fdhjjevkb2 
       foreign key (with_curie) 
       references Gene;

    alter table if exists DiseaseAnnotation_Gene 
       add constraint FKy4jlhsgseecd3gkxjtwp28ba 
       foreign key (DiseaseAnnotation_id) 
       references DiseaseAnnotation;

    alter table if exists DiseaseAnnotation_Gene_AUD 
       add constraint FKs6e9od45c5fv6nd4olb819vsd 
       foreign key (REV) 
       references REVINFO;

    alter table if exists DOTerm 
       add constraint FKp8el2duba9ym3l6gd5dy43swk 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists DOTerm_AUD 
       add constraint FKkgu80ih0f55tskr386gucsqh2 
       foreign key (curie, REV) 
       references OntologyTerm_AUD;

    alter table if exists EcoTerm 
       add constraint FKskvp24kfp723htxmk0m9ev4ns 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists EcoTerm_AUD 
       add constraint FKrdtwy8r0gnnh6numgdbgi9e6s 
       foreign key (curie, REV) 
       references OntologyTerm_AUD;

    alter table if exists EMAPATerm 
       add constraint FKcm3tpjo7lxsx61pj7gs5y9f9u 
       foreign key (curie) 
       references AnatomicalTerm;

    alter table if exists EMAPATerm_AUD 
       add constraint FKaipxoy4lm50q9mphk2yp7whyh 
       foreign key (curie, REV) 
       references AnatomicalTerm_AUD;

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

    alter table if exists ExperimentalCondition_AUD 
       add constraint FKos799amubpywlufc5ttysjp5h 
       foreign key (REV) 
       references REVINFO;

    alter table if exists ExperimentalCondition_PaperHandle 
       add constraint FKr26iemfq6lymbmntj55n3ln33 
       foreign key (paperHandles_handle) 
       references PaperHandle;

    alter table if exists ExperimentalCondition_PaperHandle 
       add constraint FKgyta8a57cq00n4trlt5rtgick 
       foreign key (ExperimentalCondition_id) 
       references ExperimentalCondition;

    alter table if exists ExperimentalCondition_PaperHandle_AUD 
       add constraint FK8lrpj619ga1aq4w6e28cxol4p 
       foreign key (REV) 
       references REVINFO;

    alter table if exists ExperimentalConditionOntologyTerm 
       add constraint FK5jlaea2evnqnrlf72jglhqq6p 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists ExperimentalConditionOntologyTerm_AUD 
       add constraint FKkr4o08hq0jboq6g4ou5gmn8xd 
       foreign key (curie, REV) 
       references OntologyTerm_AUD;

    alter table if exists Gene 
       add constraint FKiaxg0dhug3stym3gjovw598w1 
       foreign key (geneType_curie) 
       references SOTerm;

    alter table if exists Gene 
       add constraint FK9v4jtwy759c3cfub0uxye5rue 
       foreign key (curie) 
       references GenomicEntity;

    alter table if exists Gene_AUD 
       add constraint FK4n82maba8vniaxet7w2w1sfg4 
       foreign key (curie, REV) 
       references GenomicEntity_AUD;

    alter table if exists Gene_GeneGenomicLocation 
       add constraint FKf0ean58ivdvmq1is9shvywe3 
       foreign key (genomicLocations_id) 
       references GeneGenomicLocation;

    alter table if exists Gene_GeneGenomicLocation 
       add constraint FKnh14ykhb6xm1hnfak0lbifaex 
       foreign key (Gene_curie) 
       references Gene;

    alter table if exists Gene_GeneGenomicLocation_AUD 
       add constraint FK9r6hcuks8roeulgycv5fs22t 
       foreign key (REV) 
       references REVINFO;

    alter table if exists GeneDiseaseAnnotation 
       add constraint FK51h0w9jsd45qw5f3v2v0o28mu 
       foreign key (sgdStrainBackground_curie) 
       references AffectedGenomicModel;

    alter table if exists GeneDiseaseAnnotation 
       add constraint FK8xs26m9hfc38nmy7gvu3cec3t 
       foreign key (subject_curie) 
       references Gene;

    alter table if exists GeneDiseaseAnnotation 
       add constraint FK3j5deigrhrwln0srh51vtw3m8 
       foreign key (id) 
       references DiseaseAnnotation;

    alter table if exists GeneDiseaseAnnotation_AUD 
       add constraint FKgdfxkba52f3wpivygqajpgveq 
       foreign key (id, REV) 
       references DiseaseAnnotation_AUD;

    alter table if exists GeneGenomicLocation_AUD 
       add constraint FKlln6of6vsrfvcbnku3x9na2k0 
       foreign key (REV) 
       references REVINFO;

    alter table if exists GenomicEntity 
       add constraint FKhi54si7gksfs3f6jrbytaddbi 
       foreign key (curie) 
       references BiologicalEntity;

    alter table if exists GenomicEntity_AUD 
       add constraint FKnd0sic0qo3ko71w4d9k5urg48 
       foreign key (curie, REV) 
       references BiologicalEntity_AUD;

    alter table if exists GenomicEntity_CrossReference 
       add constraint FK3fiksr8hbcttuwaiorcgeh5ip 
       foreign key (crossReferences_curie) 
       references CrossReference;

    alter table if exists GenomicEntity_CrossReference 
       add constraint FK9b9qofiu2sump8fnfxgux1lvl 
       foreign key (GenomicEntity_curie) 
       references GenomicEntity;

    alter table if exists GenomicEntity_CrossReference_AUD 
       add constraint FKs5gsr2myfi6i0mq3n9rix7lpd 
       foreign key (REV) 
       references REVINFO;

    alter table if exists GenomicEntity_secondaryIdentifiers 
       add constraint FKf36bpep9iuxsfpq7bdqqp2oay 
       foreign key (GenomicEntity_curie) 
       references GenomicEntity;

    alter table if exists GenomicEntity_secondaryIdentifiers_AUD 
       add constraint FKpt86y0ets51hu48asq8x2g98s 
       foreign key (REV) 
       references REVINFO;

    alter table if exists GenomicEntity_Synonym 
       add constraint FKes64s4i1gon9839dojaq6airf 
       foreign key (synonyms_id) 
       references Synonym;

    alter table if exists GenomicEntity_Synonym 
       add constraint FKrfh6t1dmhd1jbsklymyuvqk22 
       foreign key (genomicEntities_curie) 
       references GenomicEntity;

    alter table if exists GenomicEntity_Synonym_AUD 
       add constraint FKirf7xq12pkuqpna31dbdiylo4 
       foreign key (REV) 
       references REVINFO;

    alter table if exists GOTerm 
       add constraint FK4gf262ba8btx03wi3vl5vhfao 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists GOTerm_AUD 
       add constraint FK4kjm9hm06yutma1ilq04h967s 
       foreign key (curie, REV) 
       references OntologyTerm_AUD;

    alter table if exists MATerm 
       add constraint FKtlgqvrv4vuh8gqihevh6adya4 
       foreign key (curie) 
       references AnatomicalTerm;

    alter table if exists MATerm_AUD 
       add constraint FK7lfprbh8k8mnw9yf8ywp7xieg 
       foreign key (curie, REV) 
       references AnatomicalTerm_AUD;

    alter table if exists Molecule 
       add constraint FKnnf79fdaivbnqu0p9kes1jtd1 
       foreign key (curie) 
       references ChemicalTerm;

    alter table if exists Molecule_AUD 
       add constraint FKcbo1onn61w7v5ivh1e1h2tcd7 
       foreign key (curie, REV) 
       references ChemicalTerm_AUD;

    alter table if exists MPTerm 
       add constraint FKta9f30vmw7h1smmv68to1ipyq 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists MPTerm_AUD 
       add constraint FKg4sqxe4ofrkn9vaenvdrrffwt 
       foreign key (curie, REV) 
       references OntologyTerm_AUD;

    alter table if exists NCBITaxonTerm 
       add constraint FK47k37g37jc1e4wdt76ajmn0xk 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists NCBITaxonTerm_AUD 
       add constraint FKap27v3trsn5u9q93qb8ikabrf 
       foreign key (curie, REV) 
       references OntologyTerm_AUD;

    alter table if exists OntologyTerm_AUD 
       add constraint FKdxjp2u3w3xoi7p9j7huceg2ts 
       foreign key (REV) 
       references REVINFO;

    alter table if exists OntologyTerm_CrossReference 
       add constraint FK9508vlhm2u3xpuf5041d9ye8y 
       foreign key (crossReferences_curie) 
       references CrossReference;

    alter table if exists OntologyTerm_CrossReference 
       add constraint FK3e1a40poh1ehjk91h42bx7i45 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_CrossReference_AUD 
       add constraint FK15bpj1c3nu4aubev76oy5oth0 
       foreign key (REV) 
       references REVINFO;

    alter table if exists OntologyTerm_definitionUrls 
       add constraint FKnhkhso5kmei3t37mkhodkkfgt 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_definitionUrls_AUD 
       add constraint FKhddvwlroknt64hmthirfpo7no 
       foreign key (REV) 
       references REVINFO;

    alter table if exists OntologyTerm_secondaryIdentifiers 
       add constraint FKpkg5jfw6wypf4v43bpb4ergu7 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_secondaryIdentifiers_AUD 
       add constraint FKjvsj7p0oj0plbpxtu04cskfhs 
       foreign key (REV) 
       references REVINFO;

    alter table if exists OntologyTerm_subsets 
       add constraint FKchq4ex53obwegdhgxrovd5r53 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_subsets_AUD 
       add constraint FKsn5i9xgd2avp7co3c70lv37 
       foreign key (REV) 
       references REVINFO;

    alter table if exists OntologyTerm_synonyms 
       add constraint FKjf8xunyry3dy9njpqb01tvjsr 
       foreign key (OntologyTerm_curie) 
       references OntologyTerm;

    alter table if exists OntologyTerm_synonyms_AUD 
       add constraint FKl2ra6s3aosf68bgss49loflot 
       foreign key (REV) 
       references REVINFO;

    alter table if exists PaperHandle 
       add constraint FKb11h1yvb7lchgw07wxspntpsc 
       foreign key (reference_curie) 
       references Reference;

    alter table if exists PaperHandle_AUD 
       add constraint FKkvs5vkh768djlf41pf2t9qlho 
       foreign key (REV) 
       references REVINFO;

    alter table if exists Reference_AUD 
       add constraint FK897g2lxdu1btxkcikigm6j4wo 
       foreign key (REV) 
       references REVINFO;

    alter table if exists SOTerm 
       add constraint FKri7tkc9slvpex9v83peovegyt 
       foreign key (curie) 
       references OntologyTerm;

    alter table if exists SOTerm_AUD 
       add constraint FK5i3iqfnxf9hxjq6jmay2gm68g 
       foreign key (curie, REV) 
       references OntologyTerm_AUD;

    alter table if exists Synonym_AUD 
       add constraint FK8y4re95uhgku1km6nsauced0b 
       foreign key (REV) 
       references REVINFO;

    alter table if exists Vocabulary_AUD 
       add constraint FK2pe60ji8kxipj6qf3cd6hjda5 
       foreign key (REV) 
       references REVINFO;

    alter table if exists VocabularyTerm 
       add constraint FKpua589tn1kypabuee8ytlmfpf 
       foreign key (vocabulary_id) 
       references Vocabulary;

    alter table if exists VocabularyTerm_AUD 
       add constraint FKfg8df1h10nehkt3pw4cebbde8 
       foreign key (REV) 
       references REVINFO;

    alter table if exists VocabularyTerm_CrossReference 
       add constraint FK9uin0anwbl5mibr82471byv8r 
       foreign key (crossReferences_curie) 
       references CrossReference;

    alter table if exists VocabularyTerm_CrossReference 
       add constraint FK6qrhqmglrld3g7jny4n7ls4v1 
       foreign key (VocabularyTerm_id) 
       references VocabularyTerm;

    alter table if exists VocabularyTerm_CrossReference_AUD 
       add constraint FKo00dyud2urf5787jggfx57nyf 
       foreign key (REV) 
       references REVINFO;

    alter table if exists VocabularyTerm_textSynonyms 
       add constraint FKnb1g5wap721lwp2rn7gse6geq 
       foreign key (VocabularyTerm_id) 
       references VocabularyTerm;

    alter table if exists VocabularyTerm_textSynonyms_AUD 
       add constraint FKcf8u1gm2oabsasujo5y26gruv 
       foreign key (REV) 
       references REVINFO;

    alter table if exists WBbtTerm 
       add constraint FKqnxqrnadcxojeti2ienobdqh0 
       foreign key (curie) 
       references AnatomicalTerm;

    alter table if exists WBbtTerm_AUD 
       add constraint FKhu85m34h8hf95s453u3m4ed8y 
       foreign key (curie, REV) 
       references AnatomicalTerm_AUD;

    alter table if exists XcoTerm 
       add constraint FK35ywtb8qiadqbwsb706ebu81c 
       foreign key (curie) 
       references ExperimentalConditionOntologyTerm;

    alter table if exists XcoTerm_AUD 
       add constraint FKnlbuiyo3i6daerkmpim317bd3 
       foreign key (curie, REV) 
       references ExperimentalConditionOntologyTerm_AUD;

    alter table if exists ZecoTerm 
       add constraint FKqd3f6hcopl67fwai6viq07t88 
       foreign key (curie) 
       references ExperimentalConditionOntologyTerm;

    alter table if exists ZecoTerm_AUD 
       add constraint FKe5wuchgyjhb2orgvht50q2dah 
       foreign key (curie, REV) 
       references ExperimentalConditionOntologyTerm_AUD;

    alter table if exists ZfaTerm 
       add constraint FK572s3xiqi0y4gjblq8xjyk3f7 
       foreign key (curie) 
       references AnatomicalTerm;

    alter table if exists ZfaTerm_AUD 
       add constraint FKs66s1k4fon0to2kk7qfsm1xon 
       foreign key (curie, REV) 
       references AnatomicalTerm_AUD;
