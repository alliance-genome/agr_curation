CREATE SEQUENCE agmdiseaseannotation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'agmdiseaseannotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE agmdiseaseannotation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		curie character varying(255),
		modentityid character varying(255),
		modinternalid character varying(255),
		uniqueid character varying(3500),
		negated boolean DEFAULT false NOT NULL,
		createdby_id bigint,
		updatedby_id bigint,
		singlereference_id bigint,
		dataprovider_id bigint,
		annotationtype_id bigint,
		diseaseannotationobject_id bigint,
		diseasegeneticmodifierrelation_id bigint,
		geneticsex_id bigint,
		relation_id bigint,
		secondarydataprovider_id bigint,
		assertedallele_id bigint,
		diseaseannotationsubject_id bigint,
		inferredallele_id bigint,
		inferredgene_id bigint
);

INSERT INTO agmdiseaseannotation
	SELECT id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		negated,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		annotationtype_id,
		diseaseannotationobject_id,
		diseasegeneticmodifierrelation_id,
		geneticsex_id,
		relation_id,
		secondarydataprovider_id,
		assertedallele_id,
		diseaseannotationsubject_id,
		inferredallele_id,
		inferredgene_id FROM Association WHERE AssociationType = 'AGMDiseaseAnnotation';


CREATE SEQUENCE agmphenotypeannotation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'AGMPhenotypeAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE agmphenotypeannotation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		curie character varying(255),
		modentityid character varying(255),
		modinternalid character varying(255),
		uniqueid character varying(3500),
		phenotypeannotationobject character varying(255),
		createdby_id bigint,
		updatedby_id bigint,
		singlereference_id bigint,
		dataprovider_id bigint,
		crossreference_id bigint,
		relation_id bigint,
		assertedallele_id bigint,
		inferredallele_id bigint,
		inferredgene_id bigint,
		phenotypeannotationsubject_id bigint
);

INSERT INTO agmphenotypeannotation
	SELECT 
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		phenotypeannotationobject,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		crossreference_id,
		relation_id,
		assertedallele_id,
		inferredallele_id,
		inferredgene_id,
		phenotypeannotationsubject_id FROM Association WHERE AssociationType = 'AGMPhenotypeAnnotation';

CREATE SEQUENCE allelediseaseannotation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'AlleleDiseaseAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE allelediseaseannotation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		curie character varying(255),
		modentityid character varying(255),
		modinternalid character varying(255),
		uniqueid character varying(3500),
		negated boolean DEFAULT false NOT NULL,
		createdby_id bigint,
		updatedby_id bigint,
		singlereference_id bigint,
		dataprovider_id bigint,
		annotationtype_id bigint,
		diseaseannotationobject_id bigint,
		diseasegeneticmodifierrelation_id bigint,
		geneticsex_id bigint,
		relation_id bigint,
		secondarydataprovider_id bigint,
		diseaseannotationsubject_id bigint,
		inferredgene_id bigint
);

INSERT INTO allelediseaseannotation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		negated,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		annotationtype_id,
		diseaseannotationobject_id,
		diseasegeneticmodifierrelation_id,
		geneticsex_id,
		relation_id,
		secondarydataprovider_id,
		diseaseannotationsubject_id,
		inferredgene_id FROM Association WHERE AssociationType = 'AlleleDiseaseAnnotation';


CREATE SEQUENCE allelegeneassociation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'AlleleGeneAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE allelegeneassociation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		createdby_id bigint,
		updatedby_id bigint,
		evidencecode_id bigint,
		relatednote_id bigint,
		relation_id bigint,
		alleleassociationsubject_id bigint,
		allelegeneassociationobject_id bigint
);

INSERT INTO allelegeneassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		createdby_id,
		updatedby_id,
		evidencecode_id,
		relatednote_id,
		relation_id,
		alleleassociationsubject_id,
		allelegeneassociationobject_id FROM Association WHERE AssociationType = 'AlleleGeneAssociation';


CREATE SEQUENCE allelephenotypeannotation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'AllelePhenotypeAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE allelephenotypeannotation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		curie character varying(255),
		modentityid character varying(255),
		modinternalid character varying(255),
		uniqueid character varying(3500),
		phenotypeannotationobject character varying(255),
		createdby_id bigint,
		updatedby_id bigint,
		singlereference_id bigint,
		dataprovider_id bigint,
		crossreference_id bigint,
		relation_id bigint,
		inferredgene_id bigint,
		phenotypeannotationsubject_id bigint
);

INSERT INTO allelephenotypeannotation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		phenotypeannotationobject,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		crossreference_id,
		relation_id,
		inferredgene_id,
		phenotypeannotationsubject_id FROM Association WHERE AssociationType = 'AllelePhenotypeAnnotation';


CREATE SEQUENCE codingsequencegenomiclocationassociation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'CodingSequenceGenomicLocationAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE codingsequencegenomiclocationassociation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		"end" integer,
		start integer,
		phase integer,
		strand character varying(1),
		createdby_id bigint,
		updatedby_id bigint,
		relation_id bigint,
		codingsequenceassociationsubject_id bigint,
		codingsequencegenomiclocationassociationobject_id bigint
);

INSERT INTO codingsequencegenomiclocationassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		"end",
		start,
		phase,
		strand,
		createdby_id,
		updatedby_id,
		relation_id,
		codingsequenceassociationsubject_id,
		codingsequencegenomiclocationassociationobject_id FROM Association WHERE AssociationType = 'CodingSequenceGenomicLocationAssociation';

CREATE SEQUENCE constructgenomicentityassociation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'ConstructGenomicEntityAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE constructgenomicentityassociation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		createdby_id bigint,
		updatedby_id bigint,
		constructassociationsubject_id bigint,
		constructgenomicentityassociationobject_id bigint,
		relation_id bigint
);

INSERT INTO constructgenomicentityassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		createdby_id,
		updatedby_id,
		constructassociationsubject_id,
		constructgenomicentityassociationobject_id,
		relation_id FROM Association WHERE AssociationType = 'ConstructGenomicEntityAssociation';


CREATE SEQUENCE exongenomiclocationassociation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'ExonGenomicLocationAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE exongenomiclocationassociation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		"end" integer,
		start integer,
		strand character varying(1),
		createdby_id bigint,
		updatedby_id bigint,
		relation_id bigint,
		exonassociationsubject_id bigint,
		exongenomiclocationassociationobject_id bigint
);

INSERT INTO exongenomiclocationassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		"end",
		start,
		strand,
		createdby_id,
		updatedby_id,
		relation_id,
		exonassociationsubject_id,
		exongenomiclocationassociationobject_id FROM Association WHERE AssociationType = 'ExonGenomicLocationAssociation';

CREATE SEQUENCE genediseaseannotation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'GeneExpressionAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE genediseaseannotation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		curie character varying(255),
		modentityid character varying(255),
		modinternalid character varying(255),
		uniqueid character varying(3500),
		negated boolean DEFAULT false NOT NULL,
		createdby_id bigint,
		updatedby_id bigint,
		singlereference_id bigint,
		dataprovider_id bigint,
		annotationtype_id bigint,
		diseaseannotationobject_id bigint,
		diseasegeneticmodifierrelation_id bigint,
		geneticsex_id bigint,
		relation_id bigint,
		secondarydataprovider_id bigint,
		diseaseannotationsubject_id bigint,
		sgdstrainbackground_id bigint
);

INSERT INTO genediseaseannotation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		negated,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		annotationtype_id,
		diseaseannotationobject_id,
		diseasegeneticmodifierrelation_id,
		geneticsex_id,
		relation_id,
		secondarydataprovider_id,
		diseaseannotationsubject_id,
		sgdstrainbackground_id FROM Association WHERE AssociationType = 'GeneExpressionAnnotation';

CREATE SEQUENCE genephenotypeannotation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'GenePhenotypeAnnotation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE genephenotypeannotation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		curie character varying(255),
		modentityid character varying(255),
		modinternalid character varying(255),
		uniqueid character varying(3500),
		phenotypeannotationobject character varying(255),
		createdby_id bigint,
		updatedby_id bigint,
		singlereference_id bigint,
		dataprovider_id bigint,
		crossreference_id bigint,
		relation_id bigint,
		phenotypeannotationsubject_id bigint,
		sgdstrainbackground_id bigint
);

INSERT INTO genephenotypeannotation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		curie,
		modentityid,
		modinternalid,
		uniqueid,
		phenotypeannotationobject,
		createdby_id,
		updatedby_id,
		singlereference_id,
		dataprovider_id,
		crossreference_id,
		relation_id,
		phenotypeannotationsubject_id,
		sgdstrainbackground_id FROM Association WHERE AssociationType = 'GenePhenotypeAnnotation';

CREATE SEQUENCE public.sequencetargetingreagentgeneassociation_seq
		START WITH 1
		INCREMENT BY 50
		NO MINVALUE
		NO MAXVALUE
		CACHE 1;

SELECT setval( 'SequenceTargetingReagentGeneAssociation_seq', (SELECT (((MAX(id) + 50) / 50) * 50) FROM Association) );

CREATE TABLE public.sequencetargetingreagentgeneassociation (
		id bigint NOT NULL,
		datecreated timestamp(6) with time zone,
		dateupdated timestamp(6) with time zone,
		dbdatecreated timestamp(6) with time zone,
		dbdateupdated timestamp(6) with time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		createdby_id bigint,
		updatedby_id bigint,
		relation_id bigint,
		sequencetargetingreagentassociationsubject_id bigint,
		sequencetargetingreagentgeneassociationobject_id bigint
);

INSERT INTO public.sequencetargetingreagentgeneassociation
	SELECT
		id,
		datecreated,
		dateupdated,
		dbdatecreated,
		dbdateupdated,
		internal,
		obsolete,
		createdby_id,
		updatedby_id,
		relation_id,
		sequencetargetingreagentassociationsubject_id,
		sequencetargetingreagentgeneassociationobject_id FROM Association WHERE AssociationType = 'SequenceTargetingReagentGeneAssociation';


CREATE SEQUENCE public.transcriptcodingsequenceassociation_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.transcriptcodingsequenceassociation (
    id bigint NOT NULL,
    datecreated timestamp(6) with time zone,
    dateupdated timestamp(6) with time zone,
    dbdatecreated timestamp(6) with time zone,
    dbdateupdated timestamp(6) with time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    updatedby_id bigint,
    relation_id bigint,
    transcriptassociationsubject_id bigint,
    transcriptcodingsequenceassociationobject_id bigint
);

CREATE SEQUENCE public.transcriptexonassociation_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.transcriptexonassociation (
    id bigint NOT NULL,
    datecreated timestamp(6) with time zone,
    dateupdated timestamp(6) with time zone,
    dbdatecreated timestamp(6) with time zone,
    dbdateupdated timestamp(6) with time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    updatedby_id bigint,
    relation_id bigint,
    transcriptassociationsubject_id bigint,
    transcriptexonassociationobject_id bigint
);

CREATE SEQUENCE public.transcriptgeneassociation_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.transcriptgeneassociation (
    id bigint NOT NULL,
    datecreated timestamp(6) with time zone,
    dateupdated timestamp(6) with time zone,
    dbdatecreated timestamp(6) with time zone,
    dbdateupdated timestamp(6) with time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    updatedby_id bigint,
    relation_id bigint,
    transcriptassociationsubject_id bigint,
    transcriptgeneassociationobject_id bigint
);

CREATE SEQUENCE public.transcriptgenomiclocationassociation_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE public.transcriptgenomiclocationassociation (
    id bigint NOT NULL,
    datecreated timestamp(6) with time zone,
    dateupdated timestamp(6) with time zone,
    dbdatecreated timestamp(6) with time zone,
    dbdateupdated timestamp(6) with time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    "end" integer,
    start integer,
    phase integer,
    strand character varying(1),
    createdby_id bigint,
    updatedby_id bigint,
    relation_id bigint,
    transcriptassociationsubject_id bigint,
    transcriptgenomiclocationassociationobject_id bigint
);




















