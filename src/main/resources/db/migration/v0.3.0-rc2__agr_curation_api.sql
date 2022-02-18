--
-- PostgreSQL database dump
--

-- Dumped from database version 13.3
-- Dumped by pg_dump version 13.5 (Ubuntu 13.5-2.pgdg20.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: affectedgenomicmodel; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.affectedgenomicmodel (
    parental_population character varying(255),
    subtype character varying(255),
    curie character varying(255) NOT NULL
);


ALTER TABLE public.affectedgenomicmodel OWNER TO postgres;

--
-- Name: affectedgenomicmodel_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.affectedgenomicmodel_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    parental_population character varying(255),
    subtype character varying(255)
);


ALTER TABLE public.affectedgenomicmodel_aud OWNER TO postgres;

--
-- Name: agmdiseaseannotation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.agmdiseaseannotation (
    predicate character varying(255),
    id bigint NOT NULL,
    inferredallele_curie character varying(255),
    inferredgene_curie character varying(255),
    subject_curie character varying(255)
);


ALTER TABLE public.agmdiseaseannotation OWNER TO postgres;

--
-- Name: agmdiseaseannotation_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.agmdiseaseannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    predicate character varying(255),
    inferredallele_curie character varying(255),
    inferredgene_curie character varying(255),
    subject_curie character varying(255)
);


ALTER TABLE public.agmdiseaseannotation_aud OWNER TO postgres;

--
-- Name: allele; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.allele (
    description text,
    feature_type character varying(255),
    symbol character varying(255),
    curie character varying(255) NOT NULL
);


ALTER TABLE public.allele OWNER TO postgres;

--
-- Name: allele_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.allele_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    description text,
    feature_type character varying(255),
    symbol character varying(255)
);


ALTER TABLE public.allele_aud OWNER TO postgres;

--
-- Name: allele_genegenomiclocation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.allele_genegenomiclocation (
    allele_curie character varying(255) NOT NULL,
    genomiclocations_id bigint NOT NULL
);


ALTER TABLE public.allele_genegenomiclocation OWNER TO postgres;

--
-- Name: allele_genegenomiclocation_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.allele_genegenomiclocation_aud (
    rev integer NOT NULL,
    allele_curie character varying(255) NOT NULL,
    genomiclocations_id bigint NOT NULL,
    revtype smallint
);


ALTER TABLE public.allele_genegenomiclocation_aud OWNER TO postgres;

--
-- Name: allelediseaseannotation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.allelediseaseannotation (
    predicate character varying(255),
    id bigint NOT NULL,
    inferredgene_curie character varying(255),
    subject_curie character varying(255)
);


ALTER TABLE public.allelediseaseannotation OWNER TO postgres;

--
-- Name: allelediseaseannotation_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.allelediseaseannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    predicate character varying(255),
    inferredgene_curie character varying(255),
    subject_curie character varying(255)
);


ALTER TABLE public.allelediseaseannotation_aud OWNER TO postgres;

--
-- Name: anatomicalterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.anatomicalterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.anatomicalterm OWNER TO postgres;

--
-- Name: anatomicalterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.anatomicalterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.anatomicalterm_aud OWNER TO postgres;

--
-- Name: association; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.association (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    uniqueid character varying(2000)
);


ALTER TABLE public.association OWNER TO postgres;

--
-- Name: association_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.association_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint
);


ALTER TABLE public.association_aud OWNER TO postgres;

--
-- Name: biologicalentity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.biologicalentity (
    curie character varying(255) NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    taxon_curie character varying(255)
);


ALTER TABLE public.biologicalentity OWNER TO postgres;

--
-- Name: biologicalentity_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.biologicalentity_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    taxon_curie character varying(255)
);


ALTER TABLE public.biologicalentity_aud OWNER TO postgres;

--
-- Name: bulkfmsload; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkfmsload (
    datasubtype character varying(255),
    datatype character varying(255),
    id bigint NOT NULL
);


ALTER TABLE public.bulkfmsload OWNER TO postgres;

--
-- Name: bulkfmsload_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkfmsload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    datasubtype character varying(255),
    datatype character varying(255)
);


ALTER TABLE public.bulkfmsload_aud OWNER TO postgres;

--
-- Name: bulkload; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkload (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    backendbulkloadtype character varying(255),
    errormessage text,
    name character varying(255),
    ontologytype character varying(255),
    status character varying(255),
    group_id bigint,
    fileextension character varying(255)
);


ALTER TABLE public.bulkload OWNER TO postgres;

--
-- Name: bulkload_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    backendbulkloadtype character varying(255),
    errormessage text,
    name character varying(255),
    ontologytype character varying(255),
    status character varying(255),
    group_id bigint,
    fileextension character varying(255)
);


ALTER TABLE public.bulkload_aud OWNER TO postgres;

--
-- Name: bulkloadfile; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkloadfile (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    errormessage text,
    filesize bigint,
    localfilepath character varying(255),
    md5sum character varying(255),
    recordcount integer,
    s3path character varying(255),
    status character varying(255),
    bulkload_id bigint
);


ALTER TABLE public.bulkloadfile OWNER TO postgres;

--
-- Name: bulkloadfile_aud; Type: TABLE; Schema: public; Owner: postgres
--

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


ALTER TABLE public.bulkloadfile_aud OWNER TO postgres;

--
-- Name: bulkloadgroup; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkloadgroup (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    name character varying(255)
);


ALTER TABLE public.bulkloadgroup OWNER TO postgres;

--
-- Name: bulkloadgroup_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkloadgroup_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    name character varying(255)
);


ALTER TABLE public.bulkloadgroup_aud OWNER TO postgres;

--
-- Name: bulkmanualload; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkmanualload (
    datatype character varying(255),
    id bigint NOT NULL
);


ALTER TABLE public.bulkmanualload OWNER TO postgres;

--
-- Name: bulkmanualload_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkmanualload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    datatype character varying(255)
);


ALTER TABLE public.bulkmanualload_aud OWNER TO postgres;

--
-- Name: bulkscheduledload; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkscheduledload (
    cronschedule character varying(255),
    scheduleactive boolean,
    schedulingerrormessage text,
    id bigint NOT NULL
);


ALTER TABLE public.bulkscheduledload OWNER TO postgres;

--
-- Name: bulkscheduledload_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkscheduledload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    cronschedule character varying(255),
    scheduleactive boolean,
    schedulingerrormessage text
);


ALTER TABLE public.bulkscheduledload_aud OWNER TO postgres;

--
-- Name: bulkurlload; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkurlload (
    url character varying(255),
    id bigint NOT NULL
);


ALTER TABLE public.bulkurlload OWNER TO postgres;

--
-- Name: bulkurlload_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.bulkurlload_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    url character varying(255)
);


ALTER TABLE public.bulkurlload_aud OWNER TO postgres;

--
-- Name: chebiterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chebiterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.chebiterm OWNER TO postgres;

--
-- Name: chebiterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chebiterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.chebiterm_aud OWNER TO postgres;

--
-- Name: chemicalterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chemicalterm (
    formula character varying(255),
    inchi character varying(750),
    inchikey character varying(255),
    iupac character varying(500),
    smiles character varying(500),
    curie character varying(255) NOT NULL
);


ALTER TABLE public.chemicalterm OWNER TO postgres;

--
-- Name: chemicalterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.chemicalterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    formula character varying(255),
    inchi character varying(750),
    inchikey character varying(255),
    iupac character varying(500),
    smiles character varying(500)
);


ALTER TABLE public.chemicalterm_aud OWNER TO postgres;

--
-- Name: conditionrelation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.conditionrelation (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    uniqueid character varying(2000),
    conditionrelationtype character varying(255)
);


ALTER TABLE public.conditionrelation OWNER TO postgres;

--
-- Name: conditionrelation_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.conditionrelation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    conditionrelationtype character varying(255)
);


ALTER TABLE public.conditionrelation_aud OWNER TO postgres;

--
-- Name: conditionrelation_experimentalcondition; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.conditionrelation_experimentalcondition (
    conditionrelation_id bigint NOT NULL,
    conditions_id bigint NOT NULL
);


ALTER TABLE public.conditionrelation_experimentalcondition OWNER TO postgres;

--
-- Name: conditionrelation_experimentalcondition_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.conditionrelation_experimentalcondition_aud (
    rev integer NOT NULL,
    conditionrelation_id bigint NOT NULL,
    conditions_id bigint NOT NULL,
    revtype smallint
);


ALTER TABLE public.conditionrelation_experimentalcondition_aud OWNER TO postgres;

--
-- Name: crossreference; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.crossreference (
    curie character varying(255) NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    displayname character varying(255),
    prefix character varying(255)
);


ALTER TABLE public.crossreference OWNER TO postgres;

--
-- Name: crossreference_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.crossreference_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    displayname character varying(255),
    prefix character varying(255)
);


ALTER TABLE public.crossreference_aud OWNER TO postgres;

--
-- Name: crossreference_pageareas; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.crossreference_pageareas (
    crossreference_curie character varying(255) NOT NULL,
    pageareas character varying(255)
);


ALTER TABLE public.crossreference_pageareas OWNER TO postgres;

--
-- Name: crossreference_pageareas_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.crossreference_pageareas_aud (
    rev integer NOT NULL,
    crossreference_curie character varying(255) NOT NULL,
    pageareas character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.crossreference_pageareas_aud OWNER TO postgres;

--
-- Name: daoterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.daoterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.daoterm OWNER TO postgres;

--
-- Name: daoterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.daoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.daoterm_aud OWNER TO postgres;

--
-- Name: diseaseannotation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diseaseannotation (
    diseaserelation character varying(255),
    modid character varying(255),
    negated boolean DEFAULT false NOT NULL,
    id bigint NOT NULL,
    object_curie character varying(255),
    reference_curie character varying(255),
    singlereference_curie character varying(255)
);


ALTER TABLE public.diseaseannotation OWNER TO postgres;

--
-- Name: diseaseannotation_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diseaseannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    diseaserelation character varying(255),
    modid character varying(255),
    negated boolean DEFAULT false,
    object_curie character varying(255),
    reference_curie character varying(255),
    singlereference_curie character varying(255)
);


ALTER TABLE public.diseaseannotation_aud OWNER TO postgres;

--
-- Name: diseaseannotation_conditionrelation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diseaseannotation_conditionrelation (
    diseaseannotation_id bigint NOT NULL,
    conditionrelations_id bigint NOT NULL
);


ALTER TABLE public.diseaseannotation_conditionrelation OWNER TO postgres;

--
-- Name: diseaseannotation_conditionrelation_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diseaseannotation_conditionrelation_aud (
    rev integer NOT NULL,
    diseaseannotation_id bigint NOT NULL,
    conditionrelations_id bigint NOT NULL,
    revtype smallint
);


ALTER TABLE public.diseaseannotation_conditionrelation_aud OWNER TO postgres;

--
-- Name: diseaseannotation_ecoterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diseaseannotation_ecoterm (
    diseaseannotation_id bigint NOT NULL,
    evidencecodes_curie character varying(255) NOT NULL
);


ALTER TABLE public.diseaseannotation_ecoterm OWNER TO postgres;

--
-- Name: diseaseannotation_ecoterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diseaseannotation_ecoterm_aud (
    rev integer NOT NULL,
    diseaseannotation_id bigint NOT NULL,
    evidencecodes_curie character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.diseaseannotation_ecoterm_aud OWNER TO postgres;

--
-- Name: diseaseannotation_gene; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diseaseannotation_gene (
    diseaseannotation_id bigint NOT NULL,
    with_curie character varying(255) NOT NULL
);


ALTER TABLE public.diseaseannotation_gene OWNER TO postgres;

--
-- Name: diseaseannotation_gene_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.diseaseannotation_gene_aud (
    rev integer NOT NULL,
    diseaseannotation_id bigint NOT NULL,
    with_curie character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.diseaseannotation_gene_aud OWNER TO postgres;

--
-- Name: doterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.doterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.doterm OWNER TO postgres;

--
-- Name: doterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.doterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.doterm_aud OWNER TO postgres;

--
-- Name: ecoterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ecoterm (
    abbreviation character varying(255),
    curie character varying(255) NOT NULL
);


ALTER TABLE public.ecoterm OWNER TO postgres;

--
-- Name: ecoterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ecoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    abbreviation character varying(255)
);


ALTER TABLE public.ecoterm_aud OWNER TO postgres;

--
-- Name: emapaterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.emapaterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.emapaterm OWNER TO postgres;

--
-- Name: emapaterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.emapaterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.emapaterm_aud OWNER TO postgres;

--
-- Name: experimentalcondition; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.experimentalcondition (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    uniqueid character varying(2000),
    conditionquantity character varying(255),
    conditionstatement character varying(255),
    conditionanatomy_curie character varying(255),
    conditionchemical_curie character varying(255),
    conditionclass_curie character varying(255),
    conditiongeneontology_curie character varying(255),
    conditionid_curie character varying(255),
    conditiontaxon_curie character varying(255)
);


ALTER TABLE public.experimentalcondition OWNER TO postgres;

--
-- Name: experimentalcondition_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.experimentalcondition_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    conditionquantity character varying(255),
    conditionstatement character varying(255),
    conditionanatomy_curie character varying(255),
    conditionchemical_curie character varying(255),
    conditionclass_curie character varying(255),
    conditiongeneontology_curie character varying(255),
    conditionid_curie character varying(255),
    conditiontaxon_curie character varying(255)
);


ALTER TABLE public.experimentalcondition_aud OWNER TO postgres;

--
-- Name: experimentalcondition_paperhandle; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.experimentalcondition_paperhandle (
    experimentalcondition_id bigint NOT NULL,
    paperhandles_handle character varying(255) NOT NULL
);


ALTER TABLE public.experimentalcondition_paperhandle OWNER TO postgres;

--
-- Name: experimentalcondition_paperhandle_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.experimentalcondition_paperhandle_aud (
    rev integer NOT NULL,
    experimentalcondition_id bigint NOT NULL,
    paperhandles_handle character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.experimentalcondition_paperhandle_aud OWNER TO postgres;

--
-- Name: experimentalconditionontologyterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.experimentalconditionontologyterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.experimentalconditionontologyterm OWNER TO postgres;

--
-- Name: experimentalconditionontologyterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.experimentalconditionontologyterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.experimentalconditionontologyterm_aud OWNER TO postgres;

--
-- Name: gene; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gene (
    automatedgenedescription text,
    genesynopsis text,
    genesynopsisurl character varying(255),
    symbol character varying(255),
    curie character varying(255) NOT NULL,
    genetype_curie character varying(255)
);


ALTER TABLE public.gene OWNER TO postgres;

--
-- Name: gene_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gene_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    automatedgenedescription text,
    genesynopsis text,
    genesynopsisurl character varying(255),
    symbol character varying(255),
    genetype_curie character varying(255)
);


ALTER TABLE public.gene_aud OWNER TO postgres;

--
-- Name: gene_genegenomiclocation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gene_genegenomiclocation (
    gene_curie character varying(255) NOT NULL,
    genomiclocations_id bigint NOT NULL
);


ALTER TABLE public.gene_genegenomiclocation OWNER TO postgres;

--
-- Name: gene_genegenomiclocation_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.gene_genegenomiclocation_aud (
    rev integer NOT NULL,
    gene_curie character varying(255) NOT NULL,
    genomiclocations_id bigint NOT NULL,
    revtype smallint
);


ALTER TABLE public.gene_genegenomiclocation_aud OWNER TO postgres;

--
-- Name: genediseaseannotation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genediseaseannotation (
    predicate character varying(255),
    id bigint NOT NULL,
    sgdstrainbackground_curie character varying(255),
    subject_curie character varying(255)
);


ALTER TABLE public.genediseaseannotation OWNER TO postgres;

--
-- Name: genediseaseannotation_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genediseaseannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    predicate character varying(255),
    sgdstrainbackground_curie character varying(255),
    subject_curie character varying(255)
);


ALTER TABLE public.genediseaseannotation_aud OWNER TO postgres;

--
-- Name: genegenomiclocation; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genegenomiclocation (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    assembly character varying(255),
    endpos integer,
    startpos integer
);


ALTER TABLE public.genegenomiclocation OWNER TO postgres;

--
-- Name: genegenomiclocation_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genegenomiclocation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    assembly character varying(255),
    endpos integer,
    startpos integer
);


ALTER TABLE public.genegenomiclocation_aud OWNER TO postgres;

--
-- Name: genomicentity; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genomicentity (
    name text,
    curie character varying(255) NOT NULL
);


ALTER TABLE public.genomicentity OWNER TO postgres;

--
-- Name: genomicentity_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genomicentity_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    name text
);


ALTER TABLE public.genomicentity_aud OWNER TO postgres;

--
-- Name: genomicentity_crossreference; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genomicentity_crossreference (
    genomicentity_curie character varying(255) NOT NULL,
    crossreferences_curie character varying(255) NOT NULL
);


ALTER TABLE public.genomicentity_crossreference OWNER TO postgres;

--
-- Name: genomicentity_crossreference_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genomicentity_crossreference_aud (
    rev integer NOT NULL,
    genomicentity_curie character varying(255) NOT NULL,
    crossreferences_curie character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.genomicentity_crossreference_aud OWNER TO postgres;

--
-- Name: genomicentity_secondaryidentifiers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genomicentity_secondaryidentifiers (
    genomicentity_curie character varying(255) NOT NULL,
    secondaryidentifiers character varying(255)
);


ALTER TABLE public.genomicentity_secondaryidentifiers OWNER TO postgres;

--
-- Name: genomicentity_secondaryidentifiers_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genomicentity_secondaryidentifiers_aud (
    rev integer NOT NULL,
    genomicentity_curie character varying(255) NOT NULL,
    secondaryidentifiers character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.genomicentity_secondaryidentifiers_aud OWNER TO postgres;

--
-- Name: genomicentity_synonym; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genomicentity_synonym (
    genomicentities_curie character varying(255) NOT NULL,
    synonyms_id bigint NOT NULL
);


ALTER TABLE public.genomicentity_synonym OWNER TO postgres;

--
-- Name: genomicentity_synonym_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.genomicentity_synonym_aud (
    rev integer NOT NULL,
    genomicentities_curie character varying(255) NOT NULL,
    synonyms_id bigint NOT NULL,
    revtype smallint
);


ALTER TABLE public.genomicentity_synonym_aud OWNER TO postgres;

--
-- Name: goterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.goterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.goterm OWNER TO postgres;

--
-- Name: goterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.goterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.goterm_aud OWNER TO postgres;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.hibernate_sequence OWNER TO postgres;

--
-- Name: materm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.materm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.materm OWNER TO postgres;

--
-- Name: materm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.materm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.materm_aud OWNER TO postgres;

--
-- Name: molecule; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.molecule (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.molecule OWNER TO postgres;

--
-- Name: molecule_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.molecule_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.molecule_aud OWNER TO postgres;

--
-- Name: mpterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mpterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.mpterm OWNER TO postgres;

--
-- Name: mpterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.mpterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.mpterm_aud OWNER TO postgres;

--
-- Name: ncbitaxonterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ncbitaxonterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.ncbitaxonterm OWNER TO postgres;

--
-- Name: ncbitaxonterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ncbitaxonterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.ncbitaxonterm_aud OWNER TO postgres;

--
-- Name: ontologyterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm (
    curie character varying(255) NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    definition text,
    name character varying(2000),
    namespace character varying(255),
    obsolete boolean,
    type character varying(255)
);


ALTER TABLE public.ontologyterm OWNER TO postgres;

--
-- Name: ontologyterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    definition text,
    name character varying(2000),
    namespace character varying(255),
    obsolete boolean,
    type character varying(255)
);


ALTER TABLE public.ontologyterm_aud OWNER TO postgres;

--
-- Name: ontologyterm_crossreference; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_crossreference (
    ontologyterm_curie character varying(255) NOT NULL,
    crossreferences_curie character varying(255) NOT NULL
);


ALTER TABLE public.ontologyterm_crossreference OWNER TO postgres;

--
-- Name: ontologyterm_crossreference_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_crossreference_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    crossreferences_curie character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.ontologyterm_crossreference_aud OWNER TO postgres;

--
-- Name: ontologyterm_definitionurls; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_definitionurls (
    ontologyterm_curie character varying(255) NOT NULL,
    definitionurls text
);


ALTER TABLE public.ontologyterm_definitionurls OWNER TO postgres;

--
-- Name: ontologyterm_definitionurls_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_definitionurls_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    definitionurls text NOT NULL,
    revtype smallint
);


ALTER TABLE public.ontologyterm_definitionurls_aud OWNER TO postgres;

--
-- Name: ontologyterm_secondaryidentifiers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_secondaryidentifiers (
    ontologyterm_curie character varying(255) NOT NULL,
    secondaryidentifiers character varying(255)
);


ALTER TABLE public.ontologyterm_secondaryidentifiers OWNER TO postgres;

--
-- Name: ontologyterm_secondaryidentifiers_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_secondaryidentifiers_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    secondaryidentifiers character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.ontologyterm_secondaryidentifiers_aud OWNER TO postgres;

--
-- Name: ontologyterm_subsets; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_subsets (
    ontologyterm_curie character varying(255) NOT NULL,
    subsets character varying(255)
);


ALTER TABLE public.ontologyterm_subsets OWNER TO postgres;

--
-- Name: ontologyterm_subsets_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_subsets_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    subsets character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.ontologyterm_subsets_aud OWNER TO postgres;

--
-- Name: ontologyterm_synonyms; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_synonyms (
    ontologyterm_curie character varying(255) NOT NULL,
    synonyms text
);


ALTER TABLE public.ontologyterm_synonyms OWNER TO postgres;

--
-- Name: ontologyterm_synonyms_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.ontologyterm_synonyms_aud (
    rev integer NOT NULL,
    ontologyterm_curie character varying(255) NOT NULL,
    synonyms text NOT NULL,
    revtype smallint
);


ALTER TABLE public.ontologyterm_synonyms_aud OWNER TO postgres;

--
-- Name: paperhandle; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.paperhandle (
    handle character varying(255) NOT NULL,
    reference_curie character varying(255)
);


ALTER TABLE public.paperhandle OWNER TO postgres;

--
-- Name: paperhandle_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.paperhandle_aud (
    handle character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    reference_curie character varying(255)
);


ALTER TABLE public.paperhandle_aud OWNER TO postgres;

--
-- Name: person; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.person (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    apitoken character varying(255),
    email character varying(255),
    firstname character varying(255),
    lastname character varying(255),
    modid character varying(255),
    uniqueid character varying(255)
);


ALTER TABLE public.person OWNER TO postgres;

--
-- Name: reference; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reference (
    curie character varying(255) NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone
);


ALTER TABLE public.reference OWNER TO postgres;

--
-- Name: reference_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.reference_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL,
    revtype smallint
);


ALTER TABLE public.reference_aud OWNER TO postgres;

--
-- Name: revinfo; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.revinfo (
    rev integer NOT NULL,
    revtstmp bigint
);


ALTER TABLE public.revinfo OWNER TO postgres;

--
-- Name: soterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.soterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.soterm OWNER TO postgres;

--
-- Name: soterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.soterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.soterm_aud OWNER TO postgres;

--
-- Name: synonym; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.synonym (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    name character varying(255)
);


ALTER TABLE public.synonym OWNER TO postgres;

--
-- Name: synonym_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.synonym_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    name character varying(255)
);


ALTER TABLE public.synonym_aud OWNER TO postgres;

--
-- Name: vocabulary; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vocabulary (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    isobsolete boolean DEFAULT false NOT NULL,
    name character varying(255),
    vocabularydescription character varying(255)
);


ALTER TABLE public.vocabulary OWNER TO postgres;

--
-- Name: vocabulary_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vocabulary_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    isobsolete boolean DEFAULT false,
    name character varying(255),
    vocabularydescription character varying(255)
);


ALTER TABLE public.vocabulary_aud OWNER TO postgres;

--
-- Name: vocabularyterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vocabularyterm (
    id bigint NOT NULL,
    created timestamp without time zone,
    lastupdated timestamp without time zone,
    definition character varying(255),
    isobsolete boolean DEFAULT false NOT NULL,
    name character varying(255),
    vocabulary_id bigint,
    abbreviation character varying(255)
);


ALTER TABLE public.vocabularyterm OWNER TO postgres;

--
-- Name: vocabularyterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vocabularyterm_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    revtype smallint,
    definition character varying(255),
    isobsolete boolean DEFAULT false,
    name character varying(255),
    vocabulary_id bigint,
    abbreviation character varying(255)
);


ALTER TABLE public.vocabularyterm_aud OWNER TO postgres;

--
-- Name: vocabularyterm_crossreference; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vocabularyterm_crossreference (
    vocabularyterm_id bigint NOT NULL,
    crossreferences_curie character varying(255) NOT NULL
);


ALTER TABLE public.vocabularyterm_crossreference OWNER TO postgres;

--
-- Name: vocabularyterm_crossreference_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vocabularyterm_crossreference_aud (
    rev integer NOT NULL,
    vocabularyterm_id bigint NOT NULL,
    crossreferences_curie character varying(255) NOT NULL,
    revtype smallint
);


ALTER TABLE public.vocabularyterm_crossreference_aud OWNER TO postgres;

--
-- Name: vocabularyterm_textsynonyms; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vocabularyterm_textsynonyms (
    vocabularyterm_id bigint NOT NULL,
    textsynonyms text
);


ALTER TABLE public.vocabularyterm_textsynonyms OWNER TO postgres;

--
-- Name: vocabularyterm_textsynonyms_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.vocabularyterm_textsynonyms_aud (
    rev integer NOT NULL,
    vocabularyterm_id bigint NOT NULL,
    textsynonyms text NOT NULL,
    revtype smallint
);


ALTER TABLE public.vocabularyterm_textsynonyms_aud OWNER TO postgres;

--
-- Name: wbbtterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.wbbtterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.wbbtterm OWNER TO postgres;

--
-- Name: wbbtterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.wbbtterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.wbbtterm_aud OWNER TO postgres;

--
-- Name: xcoterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.xcoterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.xcoterm OWNER TO postgres;

--
-- Name: xcoterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.xcoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.xcoterm_aud OWNER TO postgres;

--
-- Name: zecoterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.zecoterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.zecoterm OWNER TO postgres;

--
-- Name: zecoterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.zecoterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.zecoterm_aud OWNER TO postgres;

--
-- Name: zfaterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.zfaterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.zfaterm OWNER TO postgres;

--
-- Name: zfaterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.zfaterm_aud (
    curie character varying(255) NOT NULL,
    rev integer NOT NULL
);


ALTER TABLE public.zfaterm_aud OWNER TO postgres;

--
-- Name: affectedgenomicmodel_aud affectedgenomicmodel_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.affectedgenomicmodel_aud
    ADD CONSTRAINT affectedgenomicmodel_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: affectedgenomicmodel affectedgenomicmodel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.affectedgenomicmodel
    ADD CONSTRAINT affectedgenomicmodel_pkey PRIMARY KEY (curie);


--
-- Name: agmdiseaseannotation_aud agmdiseaseannotation_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agmdiseaseannotation_aud
    ADD CONSTRAINT agmdiseaseannotation_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: agmdiseaseannotation agmdiseaseannotation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agmdiseaseannotation
    ADD CONSTRAINT agmdiseaseannotation_pkey PRIMARY KEY (id);


--
-- Name: allele_aud allele_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allele_aud
    ADD CONSTRAINT allele_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: allele_genegenomiclocation_aud allele_genegenomiclocation_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allele_genegenomiclocation_aud
    ADD CONSTRAINT allele_genegenomiclocation_aud_pkey PRIMARY KEY (rev, allele_curie, genomiclocations_id);


--
-- Name: allele allele_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allele
    ADD CONSTRAINT allele_pkey PRIMARY KEY (curie);


--
-- Name: allelediseaseannotation_aud allelediseaseannotation_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allelediseaseannotation_aud
    ADD CONSTRAINT allelediseaseannotation_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: allelediseaseannotation allelediseaseannotation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allelediseaseannotation
    ADD CONSTRAINT allelediseaseannotation_pkey PRIMARY KEY (id);


--
-- Name: anatomicalterm_aud anatomicalterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anatomicalterm_aud
    ADD CONSTRAINT anatomicalterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: anatomicalterm anatomicalterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anatomicalterm
    ADD CONSTRAINT anatomicalterm_pkey PRIMARY KEY (curie);


--
-- Name: association_aud association_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.association_aud
    ADD CONSTRAINT association_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: association association_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.association
    ADD CONSTRAINT association_pkey PRIMARY KEY (id);


--
-- Name: biologicalentity_aud biologicalentity_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.biologicalentity_aud
    ADD CONSTRAINT biologicalentity_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: biologicalentity biologicalentity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.biologicalentity
    ADD CONSTRAINT biologicalentity_pkey PRIMARY KEY (curie);


--
-- Name: bulkfmsload_aud bulkfmsload_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkfmsload_aud
    ADD CONSTRAINT bulkfmsload_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: bulkfmsload bulkfmsload_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkfmsload
    ADD CONSTRAINT bulkfmsload_pkey PRIMARY KEY (id);


--
-- Name: bulkload_aud bulkload_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkload_aud
    ADD CONSTRAINT bulkload_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: bulkload bulkload_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkload
    ADD CONSTRAINT bulkload_pkey PRIMARY KEY (id);


--
-- Name: bulkloadfile_aud bulkloadfile_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkloadfile_aud
    ADD CONSTRAINT bulkloadfile_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: bulkloadfile bulkloadfile_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkloadfile
    ADD CONSTRAINT bulkloadfile_pkey PRIMARY KEY (id);


--
-- Name: bulkloadgroup_aud bulkloadgroup_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkloadgroup_aud
    ADD CONSTRAINT bulkloadgroup_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: bulkloadgroup bulkloadgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkloadgroup
    ADD CONSTRAINT bulkloadgroup_pkey PRIMARY KEY (id);


--
-- Name: bulkmanualload_aud bulkmanualload_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkmanualload_aud
    ADD CONSTRAINT bulkmanualload_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: bulkmanualload bulkmanualload_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkmanualload
    ADD CONSTRAINT bulkmanualload_pkey PRIMARY KEY (id);


--
-- Name: bulkscheduledload_aud bulkscheduledload_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkscheduledload_aud
    ADD CONSTRAINT bulkscheduledload_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: bulkscheduledload bulkscheduledload_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkscheduledload
    ADD CONSTRAINT bulkscheduledload_pkey PRIMARY KEY (id);


--
-- Name: bulkurlload_aud bulkurlload_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkurlload_aud
    ADD CONSTRAINT bulkurlload_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: bulkurlload bulkurlload_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkurlload
    ADD CONSTRAINT bulkurlload_pkey PRIMARY KEY (id);


--
-- Name: chebiterm_aud chebiterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chebiterm_aud
    ADD CONSTRAINT chebiterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: chebiterm chebiterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chebiterm
    ADD CONSTRAINT chebiterm_pkey PRIMARY KEY (curie);


--
-- Name: chemicalterm_aud chemicalterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chemicalterm_aud
    ADD CONSTRAINT chemicalterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: chemicalterm chemicalterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chemicalterm
    ADD CONSTRAINT chemicalterm_pkey PRIMARY KEY (curie);


--
-- Name: conditionrelation_aud conditionrelation_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conditionrelation_aud
    ADD CONSTRAINT conditionrelation_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: conditionrelation_experimentalcondition_aud conditionrelation_experimentalcondition_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conditionrelation_experimentalcondition_aud
    ADD CONSTRAINT conditionrelation_experimentalcondition_aud_pkey PRIMARY KEY (rev, conditionrelation_id, conditions_id);


--
-- Name: conditionrelation conditionrelation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conditionrelation
    ADD CONSTRAINT conditionrelation_pkey PRIMARY KEY (id);


--
-- Name: crossreference_aud crossreference_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.crossreference_aud
    ADD CONSTRAINT crossreference_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: crossreference_pageareas_aud crossreference_pageareas_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.crossreference_pageareas_aud
    ADD CONSTRAINT crossreference_pageareas_aud_pkey PRIMARY KEY (rev, crossreference_curie, pageareas);


--
-- Name: crossreference crossreference_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.crossreference
    ADD CONSTRAINT crossreference_pkey PRIMARY KEY (curie);


--
-- Name: daoterm_aud daoterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daoterm_aud
    ADD CONSTRAINT daoterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: daoterm daoterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daoterm
    ADD CONSTRAINT daoterm_pkey PRIMARY KEY (curie);


--
-- Name: diseaseannotation_aud diseaseannotation_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_aud
    ADD CONSTRAINT diseaseannotation_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: diseaseannotation_conditionrelation_aud diseaseannotation_conditionrelation_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_conditionrelation_aud
    ADD CONSTRAINT diseaseannotation_conditionrelation_aud_pkey PRIMARY KEY (rev, diseaseannotation_id, conditionrelations_id);


--
-- Name: diseaseannotation_ecoterm_aud diseaseannotation_ecoterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_ecoterm_aud
    ADD CONSTRAINT diseaseannotation_ecoterm_aud_pkey PRIMARY KEY (rev, diseaseannotation_id, evidencecodes_curie);


--
-- Name: diseaseannotation_gene_aud diseaseannotation_gene_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_gene_aud
    ADD CONSTRAINT diseaseannotation_gene_aud_pkey PRIMARY KEY (rev, diseaseannotation_id, with_curie);


--
-- Name: diseaseannotation diseaseannotation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT diseaseannotation_pkey PRIMARY KEY (id);


--
-- Name: doterm_aud doterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doterm_aud
    ADD CONSTRAINT doterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: doterm doterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doterm
    ADD CONSTRAINT doterm_pkey PRIMARY KEY (curie);


--
-- Name: ecoterm_aud ecoterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ecoterm_aud
    ADD CONSTRAINT ecoterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: ecoterm ecoterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ecoterm
    ADD CONSTRAINT ecoterm_pkey PRIMARY KEY (curie);


--
-- Name: emapaterm_aud emapaterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.emapaterm_aud
    ADD CONSTRAINT emapaterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: emapaterm emapaterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.emapaterm
    ADD CONSTRAINT emapaterm_pkey PRIMARY KEY (curie);


--
-- Name: experimentalcondition_aud experimentalcondition_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition_aud
    ADD CONSTRAINT experimentalcondition_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: experimentalcondition_paperhandle_aud experimentalcondition_paperhandle_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition_paperhandle_aud
    ADD CONSTRAINT experimentalcondition_paperhandle_aud_pkey PRIMARY KEY (rev, experimentalcondition_id, paperhandles_handle);


--
-- Name: experimentalcondition experimentalcondition_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT experimentalcondition_pkey PRIMARY KEY (id);


--
-- Name: experimentalconditionontologyterm_aud experimentalconditionontologyterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalconditionontologyterm_aud
    ADD CONSTRAINT experimentalconditionontologyterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: experimentalconditionontologyterm experimentalconditionontologyterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalconditionontologyterm
    ADD CONSTRAINT experimentalconditionontologyterm_pkey PRIMARY KEY (curie);


--
-- Name: gene_aud gene_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gene_aud
    ADD CONSTRAINT gene_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: gene_genegenomiclocation_aud gene_genegenomiclocation_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gene_genegenomiclocation_aud
    ADD CONSTRAINT gene_genegenomiclocation_aud_pkey PRIMARY KEY (rev, gene_curie, genomiclocations_id);


--
-- Name: gene gene_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gene
    ADD CONSTRAINT gene_pkey PRIMARY KEY (curie);


--
-- Name: genediseaseannotation_aud genediseaseannotation_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genediseaseannotation_aud
    ADD CONSTRAINT genediseaseannotation_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: genediseaseannotation genediseaseannotation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genediseaseannotation
    ADD CONSTRAINT genediseaseannotation_pkey PRIMARY KEY (id);


--
-- Name: genegenomiclocation_aud genegenomiclocation_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genegenomiclocation_aud
    ADD CONSTRAINT genegenomiclocation_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: genegenomiclocation genegenomiclocation_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genegenomiclocation
    ADD CONSTRAINT genegenomiclocation_pkey PRIMARY KEY (id);


--
-- Name: genomicentity_aud genomicentity_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_aud
    ADD CONSTRAINT genomicentity_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: genomicentity_crossreference_aud genomicentity_crossreference_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_crossreference_aud
    ADD CONSTRAINT genomicentity_crossreference_aud_pkey PRIMARY KEY (rev, genomicentity_curie, crossreferences_curie);


--
-- Name: genomicentity genomicentity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity
    ADD CONSTRAINT genomicentity_pkey PRIMARY KEY (curie);


--
-- Name: genomicentity_secondaryidentifiers_aud genomicentity_secondaryidentifiers_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_secondaryidentifiers_aud
    ADD CONSTRAINT genomicentity_secondaryidentifiers_aud_pkey PRIMARY KEY (rev, genomicentity_curie, secondaryidentifiers);


--
-- Name: genomicentity_synonym_aud genomicentity_synonym_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_synonym_aud
    ADD CONSTRAINT genomicentity_synonym_aud_pkey PRIMARY KEY (rev, genomicentities_curie, synonyms_id);


--
-- Name: goterm_aud goterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.goterm_aud
    ADD CONSTRAINT goterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: goterm goterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.goterm
    ADD CONSTRAINT goterm_pkey PRIMARY KEY (curie);


--
-- Name: materm_aud materm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.materm_aud
    ADD CONSTRAINT materm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: materm materm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.materm
    ADD CONSTRAINT materm_pkey PRIMARY KEY (curie);


--
-- Name: molecule_aud molecule_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.molecule_aud
    ADD CONSTRAINT molecule_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: molecule molecule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.molecule
    ADD CONSTRAINT molecule_pkey PRIMARY KEY (curie);


--
-- Name: mpterm_aud mpterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mpterm_aud
    ADD CONSTRAINT mpterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: mpterm mpterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mpterm
    ADD CONSTRAINT mpterm_pkey PRIMARY KEY (curie);


--
-- Name: ncbitaxonterm_aud ncbitaxonterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ncbitaxonterm_aud
    ADD CONSTRAINT ncbitaxonterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: ncbitaxonterm ncbitaxonterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ncbitaxonterm
    ADD CONSTRAINT ncbitaxonterm_pkey PRIMARY KEY (curie);


--
-- Name: ontologyterm_aud ontologyterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_aud
    ADD CONSTRAINT ontologyterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: ontologyterm_crossreference_aud ontologyterm_crossreference_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_crossreference_aud
    ADD CONSTRAINT ontologyterm_crossreference_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, crossreferences_curie);


--
-- Name: ontologyterm_definitionurls_aud ontologyterm_definitionurls_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_definitionurls_aud
    ADD CONSTRAINT ontologyterm_definitionurls_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, definitionurls);


--
-- Name: ontologyterm ontologyterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm
    ADD CONSTRAINT ontologyterm_pkey PRIMARY KEY (curie);


--
-- Name: ontologyterm_secondaryidentifiers_aud ontologyterm_secondaryidentifiers_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_secondaryidentifiers_aud
    ADD CONSTRAINT ontologyterm_secondaryidentifiers_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, secondaryidentifiers);


--
-- Name: ontologyterm_subsets_aud ontologyterm_subsets_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_subsets_aud
    ADD CONSTRAINT ontologyterm_subsets_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, subsets);


--
-- Name: ontologyterm_synonyms_aud ontologyterm_synonyms_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_synonyms_aud
    ADD CONSTRAINT ontologyterm_synonyms_aud_pkey PRIMARY KEY (rev, ontologyterm_curie, synonyms);


--
-- Name: paperhandle_aud paperhandle_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.paperhandle_aud
    ADD CONSTRAINT paperhandle_aud_pkey PRIMARY KEY (handle, rev);


--
-- Name: paperhandle paperhandle_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.paperhandle
    ADD CONSTRAINT paperhandle_pkey PRIMARY KEY (handle);


--
-- Name: person person_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT person_pkey PRIMARY KEY (id);


--
-- Name: reference_aud reference_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reference_aud
    ADD CONSTRAINT reference_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: reference reference_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reference
    ADD CONSTRAINT reference_pkey PRIMARY KEY (curie);


--
-- Name: revinfo revinfo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.revinfo
    ADD CONSTRAINT revinfo_pkey PRIMARY KEY (rev);


--
-- Name: soterm_aud soterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.soterm_aud
    ADD CONSTRAINT soterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: soterm soterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.soterm
    ADD CONSTRAINT soterm_pkey PRIMARY KEY (curie);


--
-- Name: synonym_aud synonym_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.synonym_aud
    ADD CONSTRAINT synonym_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: synonym synonym_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.synonym
    ADD CONSTRAINT synonym_pkey PRIMARY KEY (id);


--
-- Name: person uk_585qcyc8qh7bg1fwgm1pj4fus; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.person
    ADD CONSTRAINT uk_585qcyc8qh7bg1fwgm1pj4fus UNIQUE (email);


--
-- Name: vocabulary uk_7a3owq9kyfv5eirj0bjkmifyf; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabulary
    ADD CONSTRAINT uk_7a3owq9kyfv5eirj0bjkmifyf UNIQUE (name);


--
-- Name: bulkloadfile uk_7nic6jxn8vx9mykmsjhmjck9k; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkloadfile
    ADD CONSTRAINT uk_7nic6jxn8vx9mykmsjhmjck9k UNIQUE (md5sum);


--
-- Name: conditionrelation uk_ett3ft5dxnonwnjd6lnjgme1o; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conditionrelation
    ADD CONSTRAINT uk_ett3ft5dxnonwnjd6lnjgme1o UNIQUE (uniqueid);


--
-- Name: diseaseannotation uk_hlsp8ic6sxwpd99k8gc90eq4a; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT uk_hlsp8ic6sxwpd99k8gc90eq4a UNIQUE (modid);


--
-- Name: association uk_t6dcflvn2bytt4xegpy2cnl1x; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.association
    ADD CONSTRAINT uk_t6dcflvn2bytt4xegpy2cnl1x UNIQUE (uniqueid);


--
-- Name: experimentalcondition uk_yb8nvuqbvlpy6e41sx37wtvr; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT uk_yb8nvuqbvlpy6e41sx37wtvr UNIQUE (uniqueid);


--
-- Name: vocabulary_aud vocabulary_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabulary_aud
    ADD CONSTRAINT vocabulary_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: vocabulary vocabulary_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabulary
    ADD CONSTRAINT vocabulary_pkey PRIMARY KEY (id);


--
-- Name: vocabularyterm_aud vocabularyterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm_aud
    ADD CONSTRAINT vocabularyterm_aud_pkey PRIMARY KEY (id, rev);


--
-- Name: vocabularyterm_crossreference_aud vocabularyterm_crossreference_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm_crossreference_aud
    ADD CONSTRAINT vocabularyterm_crossreference_aud_pkey PRIMARY KEY (rev, vocabularyterm_id, crossreferences_curie);


--
-- Name: vocabularyterm vocabularyterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm
    ADD CONSTRAINT vocabularyterm_pkey PRIMARY KEY (id);


--
-- Name: vocabularyterm_textsynonyms_aud vocabularyterm_textsynonyms_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm_textsynonyms_aud
    ADD CONSTRAINT vocabularyterm_textsynonyms_aud_pkey PRIMARY KEY (rev, vocabularyterm_id, textsynonyms);


--
-- Name: wbbtterm_aud wbbtterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.wbbtterm_aud
    ADD CONSTRAINT wbbtterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: wbbtterm wbbtterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.wbbtterm
    ADD CONSTRAINT wbbtterm_pkey PRIMARY KEY (curie);


--
-- Name: xcoterm_aud xcoterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.xcoterm_aud
    ADD CONSTRAINT xcoterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: xcoterm xcoterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.xcoterm
    ADD CONSTRAINT xcoterm_pkey PRIMARY KEY (curie);


--
-- Name: zecoterm_aud zecoterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zecoterm_aud
    ADD CONSTRAINT zecoterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: zecoterm zecoterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zecoterm
    ADD CONSTRAINT zecoterm_pkey PRIMARY KEY (curie);


--
-- Name: zfaterm_aud zfaterm_aud_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zfaterm_aud
    ADD CONSTRAINT zfaterm_aud_pkey PRIMARY KEY (curie, rev);


--
-- Name: zfaterm zfaterm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zfaterm
    ADD CONSTRAINT zfaterm_pkey PRIMARY KEY (curie);


--
-- Name: idx171k63a40d8huvbhohveql7so; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx171k63a40d8huvbhohveql7so ON public.ontologyterm_definitionurls USING btree (ontologyterm_curie);


--
-- Name: idx1c3xuyhjua7fn0s7tscxluby8; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx1c3xuyhjua7fn0s7tscxluby8 ON public.ontologyterm_crossreference USING btree (crossreferences_curie);


--
-- Name: idx41w2gn9m2s5mjdydwbsqjhfox; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx41w2gn9m2s5mjdydwbsqjhfox ON public.ontologyterm_crossreference USING btree (ontologyterm_curie);


--
-- Name: idx4snyoxumi6hnl3mugq9x4ep4p; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx4snyoxumi6hnl3mugq9x4ep4p ON public.vocabularyterm_crossreference USING btree (vocabularyterm_id);


--
-- Name: idx8veukg3tw21aorory1ei1c8on; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx8veukg3tw21aorory1ei1c8on ON public.genomicentity_synonym USING btree (genomicentities_curie);


--
-- Name: idxcc1trjvkm2h0x2d99imkm4c11; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxcc1trjvkm2h0x2d99imkm4c11 ON public.crossreference_pageareas USING btree (crossreference_curie);


--
-- Name: idxcmlmyaq41oab54whjt5cglo8v; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxcmlmyaq41oab54whjt5cglo8v ON public.genomicentity_crossreference USING btree (genomicentity_curie);


--
-- Name: idxips7lcqafkikxweue2p0h13t9; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxips7lcqafkikxweue2p0h13t9 ON public.ontologyterm_subsets USING btree (ontologyterm_curie);


--
-- Name: idxj6eavg6eannqn6uhvja6p4enf; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxj6eavg6eannqn6uhvja6p4enf ON public.diseaseannotation_gene USING btree (diseaseannotation_id);


--
-- Name: idxknjhcn64qms05eq8c8s2hhmxc; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxknjhcn64qms05eq8c8s2hhmxc ON public.vocabularyterm_textsynonyms USING btree (vocabularyterm_id);


--
-- Name: idxkyy0nwnxxbnoba74d1pmxwk2h; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxkyy0nwnxxbnoba74d1pmxwk2h ON public.genomicentity_secondaryidentifiers USING btree (genomicentity_curie);


--
-- Name: idxpydgr8unpmiig9jnsm89f55br; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxpydgr8unpmiig9jnsm89f55br ON public.ontologyterm_synonyms USING btree (ontologyterm_curie);


--
-- Name: idxsdesyork9yoruo27pe2cetjog; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxsdesyork9yoruo27pe2cetjog ON public.vocabularyterm_crossreference USING btree (crossreferences_curie);


--
-- Name: idxsvjjbf5eugfrbue5yo4jgarpn; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxsvjjbf5eugfrbue5yo4jgarpn ON public.ontologyterm_secondaryidentifiers USING btree (ontologyterm_curie);


--
-- Name: ontologyterm_crossreference_aud fk15bpj1c3nu4aubev76oy5oth0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_crossreference_aud
    ADD CONSTRAINT fk15bpj1c3nu4aubev76oy5oth0 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: association_aud fk2cnuv5m2xs6vaupmjsnce0sq6; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.association_aud
    ADD CONSTRAINT fk2cnuv5m2xs6vaupmjsnce0sq6 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: chemicalterm fk2fegif3wy9egh5r2yy8wplrwu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chemicalterm
    ADD CONSTRAINT fk2fegif3wy9egh5r2yy8wplrwu FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);


--
-- Name: diseaseannotation_conditionrelation_aud fk2i4y7kdvdurhtxhq4qb8ijy0c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_conditionrelation_aud
    ADD CONSTRAINT fk2i4y7kdvdurhtxhq4qb8ijy0c FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: vocabulary_aud fk2pe60ji8kxipj6qf3cd6hjda5; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabulary_aud
    ADD CONSTRAINT fk2pe60ji8kxipj6qf3cd6hjda5 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: experimentalcondition fk2rmhalgeg6rghpat78b2cpcoc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT fk2rmhalgeg6rghpat78b2cpcoc FOREIGN KEY (conditionchemical_curie) REFERENCES public.chemicalterm(curie);


--
-- Name: diseaseannotation_conditionrelation fk2wrhxll4ol0fxdpynbmue5stm; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_conditionrelation
    ADD CONSTRAINT fk2wrhxll4ol0fxdpynbmue5stm FOREIGN KEY (diseaseannotation_id) REFERENCES public.diseaseannotation(id);


--
-- Name: xcoterm fk35ywtb8qiadqbwsb706ebu81c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.xcoterm
    ADD CONSTRAINT fk35ywtb8qiadqbwsb706ebu81c FOREIGN KEY (curie) REFERENCES public.experimentalconditionontologyterm(curie);


--
-- Name: ontologyterm_crossreference fk3e1a40poh1ehjk91h42bx7i45; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_crossreference
    ADD CONSTRAINT fk3e1a40poh1ehjk91h42bx7i45 FOREIGN KEY (ontologyterm_curie) REFERENCES public.ontologyterm(curie);


--
-- Name: genomicentity_crossreference fk3fiksr8hbcttuwaiorcgeh5ip; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_crossreference
    ADD CONSTRAINT fk3fiksr8hbcttuwaiorcgeh5ip FOREIGN KEY (crossreferences_curie) REFERENCES public.crossreference(curie);


--
-- Name: genediseaseannotation fk3j5deigrhrwln0srh51vtw3m8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genediseaseannotation
    ADD CONSTRAINT fk3j5deigrhrwln0srh51vtw3m8 FOREIGN KEY (id) REFERENCES public.diseaseannotation(id);


--
-- Name: allelediseaseannotation fk3unb0kaxocbodllqe35hu4w0c; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allelediseaseannotation
    ADD CONSTRAINT fk3unb0kaxocbodllqe35hu4w0c FOREIGN KEY (id) REFERENCES public.diseaseannotation(id);


--
-- Name: daoterm fk3xjbjyyuqqyvspspeael1m7fe; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daoterm
    ADD CONSTRAINT fk3xjbjyyuqqyvspspeael1m7fe FOREIGN KEY (curie) REFERENCES public.anatomicalterm(curie);


--
-- Name: allele fk42r7586hi59wcwakfyr30l6l3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allele
    ADD CONSTRAINT fk42r7586hi59wcwakfyr30l6l3 FOREIGN KEY (curie) REFERENCES public.genomicentity(curie);


--
-- Name: diseaseannotation_ecoterm fk43rw1jai2kqggx518nsu8c4me; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_ecoterm
    ADD CONSTRAINT fk43rw1jai2kqggx518nsu8c4me FOREIGN KEY (diseaseannotation_id) REFERENCES public.diseaseannotation(id);


--
-- Name: ncbitaxonterm fk47k37g37jc1e4wdt76ajmn0xk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ncbitaxonterm
    ADD CONSTRAINT fk47k37g37jc1e4wdt76ajmn0xk FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);


--
-- Name: goterm fk4gf262ba8btx03wi3vl5vhfao; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.goterm
    ADD CONSTRAINT fk4gf262ba8btx03wi3vl5vhfao FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);


--
-- Name: goterm_aud fk4kjm9hm06yutma1ilq04h967s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.goterm_aud
    ADD CONSTRAINT fk4kjm9hm06yutma1ilq04h967s FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);


--
-- Name: gene_aud fk4n82maba8vniaxet7w2w1sfg4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gene_aud
    ADD CONSTRAINT fk4n82maba8vniaxet7w2w1sfg4 FOREIGN KEY (curie, rev) REFERENCES public.genomicentity_aud(curie, rev);


--
-- Name: genediseaseannotation fk51h0w9jsd45qw5f3v2v0o28mu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genediseaseannotation
    ADD CONSTRAINT fk51h0w9jsd45qw5f3v2v0o28mu FOREIGN KEY (sgdstrainbackground_curie) REFERENCES public.affectedgenomicmodel(curie);


--
-- Name: zfaterm fk572s3xiqi0y4gjblq8xjyk3f7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zfaterm
    ADD CONSTRAINT fk572s3xiqi0y4gjblq8xjyk3f7 FOREIGN KEY (curie) REFERENCES public.anatomicalterm(curie);


--
-- Name: diseaseannotation fk5a3i0leqdmstsdfpq1j1b15el; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT fk5a3i0leqdmstsdfpq1j1b15el FOREIGN KEY (id) REFERENCES public.association(id);


--
-- Name: biologicalentity fk5c19vicptarinu2wgj7xyhhum; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.biologicalentity
    ADD CONSTRAINT fk5c19vicptarinu2wgj7xyhhum FOREIGN KEY (taxon_curie) REFERENCES public.ncbitaxonterm(curie);


--
-- Name: biologicalentity_aud fk5hkwd2k49xql5qy0aby85qtad; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.biologicalentity_aud
    ADD CONSTRAINT fk5hkwd2k49xql5qy0aby85qtad FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: soterm_aud fk5i3iqfnxf9hxjq6jmay2gm68g; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.soterm_aud
    ADD CONSTRAINT fk5i3iqfnxf9hxjq6jmay2gm68g FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);


--
-- Name: experimentalconditionontologyterm fk5jlaea2evnqnrlf72jglhqq6p; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalconditionontologyterm
    ADD CONSTRAINT fk5jlaea2evnqnrlf72jglhqq6p FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);


--
-- Name: conditionrelation_experimentalcondition fk69kutljbycmp1vimotmlxp36j; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conditionrelation_experimentalcondition
    ADD CONSTRAINT fk69kutljbycmp1vimotmlxp36j FOREIGN KEY (conditionrelation_id) REFERENCES public.conditionrelation(id);


--
-- Name: diseaseannotation_gene fk6akpr16qusnfom0fdhjjevkb2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_gene
    ADD CONSTRAINT fk6akpr16qusnfom0fdhjjevkb2 FOREIGN KEY (with_curie) REFERENCES public.gene(curie);


--
-- Name: vocabularyterm_crossreference fk6qrhqmglrld3g7jny4n7ls4v1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm_crossreference
    ADD CONSTRAINT fk6qrhqmglrld3g7jny4n7ls4v1 FOREIGN KEY (vocabularyterm_id) REFERENCES public.vocabularyterm(id);


--
-- Name: bulkloadgroup_aud fk722g0iotb8v01pq0cej3w7gke; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkloadgroup_aud
    ADD CONSTRAINT fk722g0iotb8v01pq0cej3w7gke FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: diseaseannotation fk77fmab327prjh1sb7gk6na6ak; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT fk77fmab327prjh1sb7gk6na6ak FOREIGN KEY (reference_curie) REFERENCES public.reference(curie);


--
-- Name: chebiterm fk7enwyeblw2xt5yo5co0keko5f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chebiterm
    ADD CONSTRAINT fk7enwyeblw2xt5yo5co0keko5f FOREIGN KEY (curie) REFERENCES public.chemicalterm(curie);


--
-- Name: chebiterm_aud fk7grscrrhdcw9ek6agi78j4ca1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chebiterm_aud
    ADD CONSTRAINT fk7grscrrhdcw9ek6agi78j4ca1 FOREIGN KEY (curie, rev) REFERENCES public.chemicalterm_aud(curie, rev);


--
-- Name: materm_aud fk7lfprbh8k8mnw9yf8ywp7xieg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.materm_aud
    ADD CONSTRAINT fk7lfprbh8k8mnw9yf8ywp7xieg FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);


--
-- Name: bulkloadfile_aud fk7sl5m81qa11sr40chch9vg6uj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkloadfile_aud
    ADD CONSTRAINT fk7sl5m81qa11sr40chch9vg6uj FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: reference_aud fk897g2lxdu1btxkcikigm6j4wo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.reference_aud
    ADD CONSTRAINT fk897g2lxdu1btxkcikigm6j4wo FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: bulkscheduledload fk8br24n4em8tr58k9spdhep28f; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkscheduledload
    ADD CONSTRAINT fk8br24n4em8tr58k9spdhep28f FOREIGN KEY (id) REFERENCES public.bulkload(id);


--
-- Name: experimentalcondition_paperhandle_aud fk8lrpj619ga1aq4w6e28cxol4p; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition_paperhandle_aud
    ADD CONSTRAINT fk8lrpj619ga1aq4w6e28cxol4p FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: bulkmanualload_aud fk8md78mwbobme56gipe40vj2qj; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkmanualload_aud
    ADD CONSTRAINT fk8md78mwbobme56gipe40vj2qj FOREIGN KEY (id, rev) REFERENCES public.bulkload_aud(id, rev);


--
-- Name: genediseaseannotation fk8xs26m9hfc38nmy7gvu3cec3t; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genediseaseannotation
    ADD CONSTRAINT fk8xs26m9hfc38nmy7gvu3cec3t FOREIGN KEY (subject_curie) REFERENCES public.gene(curie);


--
-- Name: synonym_aud fk8y4re95uhgku1km6nsauced0b; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.synonym_aud
    ADD CONSTRAINT fk8y4re95uhgku1km6nsauced0b FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: ontologyterm_crossreference fk9508vlhm2u3xpuf5041d9ye8y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_crossreference
    ADD CONSTRAINT fk9508vlhm2u3xpuf5041d9ye8y FOREIGN KEY (crossreferences_curie) REFERENCES public.crossreference(curie);


--
-- Name: crossreference_pageareas_aud fk98pht4vedx64hgab6drv9mkv0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.crossreference_pageareas_aud
    ADD CONSTRAINT fk98pht4vedx64hgab6drv9mkv0 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: genomicentity_crossreference fk9b9qofiu2sump8fnfxgux1lvl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_crossreference
    ADD CONSTRAINT fk9b9qofiu2sump8fnfxgux1lvl FOREIGN KEY (genomicentity_curie) REFERENCES public.genomicentity(curie);


--
-- Name: gene_genegenomiclocation_aud fk9r6hcuks8roeulgycv5fs22t; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gene_genegenomiclocation_aud
    ADD CONSTRAINT fk9r6hcuks8roeulgycv5fs22t FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: vocabularyterm_crossreference fk9uin0anwbl5mibr82471byv8r; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm_crossreference
    ADD CONSTRAINT fk9uin0anwbl5mibr82471byv8r FOREIGN KEY (crossreferences_curie) REFERENCES public.crossreference(curie);


--
-- Name: gene fk9v4jtwy759c3cfub0uxye5rue; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gene
    ADD CONSTRAINT fk9v4jtwy759c3cfub0uxye5rue FOREIGN KEY (curie) REFERENCES public.genomicentity(curie);


--
-- Name: experimentalcondition fkagp6m2xqeu7bapu5hyh2pmha9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT fkagp6m2xqeu7bapu5hyh2pmha9 FOREIGN KEY (conditionid_curie) REFERENCES public.experimentalconditionontologyterm(curie);


--
-- Name: emapaterm_aud fkaipxoy4lm50q9mphk2yp7whyh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.emapaterm_aud
    ADD CONSTRAINT fkaipxoy4lm50q9mphk2yp7whyh FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);


--
-- Name: anatomicalterm_aud fkan2c886jcsep01s7rqibfghfh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anatomicalterm_aud
    ADD CONSTRAINT fkan2c886jcsep01s7rqibfghfh FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);


--
-- Name: ncbitaxonterm_aud fkap27v3trsn5u9q93qb8ikabrf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ncbitaxonterm_aud
    ADD CONSTRAINT fkap27v3trsn5u9q93qb8ikabrf FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);


--
-- Name: paperhandle fkb11h1yvb7lchgw07wxspntpsc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.paperhandle
    ADD CONSTRAINT fkb11h1yvb7lchgw07wxspntpsc FOREIGN KEY (reference_curie) REFERENCES public.reference(curie);


--
-- Name: allele_aud fkc4cub43jynmwqke9rpwpglhkt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allele_aud
    ADD CONSTRAINT fkc4cub43jynmwqke9rpwpglhkt FOREIGN KEY (curie, rev) REFERENCES public.genomicentity_aud(curie, rev);


--
-- Name: molecule_aud fkcbo1onn61w7v5ivh1e1h2tcd7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.molecule_aud
    ADD CONSTRAINT fkcbo1onn61w7v5ivh1e1h2tcd7 FOREIGN KEY (curie, rev) REFERENCES public.chemicalterm_aud(curie, rev);


--
-- Name: vocabularyterm_textsynonyms_aud fkcf8u1gm2oabsasujo5y26gruv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm_textsynonyms_aud
    ADD CONSTRAINT fkcf8u1gm2oabsasujo5y26gruv FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: ontologyterm_subsets fkchq4ex53obwegdhgxrovd5r53; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_subsets
    ADD CONSTRAINT fkchq4ex53obwegdhgxrovd5r53 FOREIGN KEY (ontologyterm_curie) REFERENCES public.ontologyterm(curie);


--
-- Name: experimentalcondition fkcl89ywjgllce228a0uo8fd0ee; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT fkcl89ywjgllce228a0uo8fd0ee FOREIGN KEY (conditiontaxon_curie) REFERENCES public.ncbitaxonterm(curie);


--
-- Name: emapaterm fkcm3tpjo7lxsx61pj7gs5y9f9u; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.emapaterm
    ADD CONSTRAINT fkcm3tpjo7lxsx61pj7gs5y9f9u FOREIGN KEY (curie) REFERENCES public.anatomicalterm(curie);


--
-- Name: bulkscheduledload_aud fkcy45x0q8y5riyg80olf0mtwyh; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkscheduledload_aud
    ADD CONSTRAINT fkcy45x0q8y5riyg80olf0mtwyh FOREIGN KEY (id, rev) REFERENCES public.bulkload_aud(id, rev);


--
-- Name: bulkmanualload fkd3pm3o2v9xb1uy15b241o56j; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkmanualload
    ADD CONSTRAINT fkd3pm3o2v9xb1uy15b241o56j FOREIGN KEY (id) REFERENCES public.bulkload(id);


--
-- Name: affectedgenomicmodel_aud fkd6m9in16kh1tqvln37a13r3hx; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.affectedgenomicmodel_aud
    ADD CONSTRAINT fkd6m9in16kh1tqvln37a13r3hx FOREIGN KEY (curie, rev) REFERENCES public.genomicentity_aud(curie, rev);


--
-- Name: ontologyterm_aud fkdxjp2u3w3xoi7p9j7huceg2ts; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_aud
    ADD CONSTRAINT fkdxjp2u3w3xoi7p9j7huceg2ts FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: zecoterm_aud fke5wuchgyjhb2orgvht50q2dah; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zecoterm_aud
    ADD CONSTRAINT fke5wuchgyjhb2orgvht50q2dah FOREIGN KEY (curie, rev) REFERENCES public.experimentalconditionontologyterm_aud(curie, rev);


--
-- Name: allelediseaseannotation fkerk9wpvk1ka4pkm0t1dqyyeyl; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allelediseaseannotation
    ADD CONSTRAINT fkerk9wpvk1ka4pkm0t1dqyyeyl FOREIGN KEY (subject_curie) REFERENCES public.allele(curie);


--
-- Name: genomicentity_synonym fkes64s4i1gon9839dojaq6airf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_synonym
    ADD CONSTRAINT fkes64s4i1gon9839dojaq6airf FOREIGN KEY (synonyms_id) REFERENCES public.synonym(id);


--
-- Name: gene_genegenomiclocation fkf0ean58ivdvmq1is9shvywe3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gene_genegenomiclocation
    ADD CONSTRAINT fkf0ean58ivdvmq1is9shvywe3 FOREIGN KEY (genomiclocations_id) REFERENCES public.genegenomiclocation(id);


--
-- Name: genomicentity_secondaryidentifiers fkf36bpep9iuxsfpq7bdqqp2oay; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_secondaryidentifiers
    ADD CONSTRAINT fkf36bpep9iuxsfpq7bdqqp2oay FOREIGN KEY (genomicentity_curie) REFERENCES public.genomicentity(curie);


--
-- Name: anatomicalterm fkfepti479fro1b09ybaltkofqu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.anatomicalterm
    ADD CONSTRAINT fkfepti479fro1b09ybaltkofqu FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);


--
-- Name: vocabularyterm_aud fkfg8df1h10nehkt3pw4cebbde8; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm_aud
    ADD CONSTRAINT fkfg8df1h10nehkt3pw4cebbde8 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: bulkurlload_aud fkfvaendvsykh6kly11h1207bdo; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkurlload_aud
    ADD CONSTRAINT fkfvaendvsykh6kly11h1207bdo FOREIGN KEY (id, rev) REFERENCES public.bulkscheduledload_aud(id, rev);


--
-- Name: mpterm_aud fkg4sqxe4ofrkn9vaenvdrrffwt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mpterm_aud
    ADD CONSTRAINT fkg4sqxe4ofrkn9vaenvdrrffwt FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);


--
-- Name: genediseaseannotation_aud fkgdfxkba52f3wpivygqajpgveq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genediseaseannotation_aud
    ADD CONSTRAINT fkgdfxkba52f3wpivygqajpgveq FOREIGN KEY (id, rev) REFERENCES public.diseaseannotation_aud(id, rev);


--
-- Name: daoterm_aud fkgif1cep78abowfodb5rxvoq1x; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.daoterm_aud
    ADD CONSTRAINT fkgif1cep78abowfodb5rxvoq1x FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);


--
-- Name: experimentalcondition_paperhandle fkgyta8a57cq00n4trlt5rtgick; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition_paperhandle
    ADD CONSTRAINT fkgyta8a57cq00n4trlt5rtgick FOREIGN KEY (experimentalcondition_id) REFERENCES public.experimentalcondition(id);


--
-- Name: ontologyterm_definitionurls_aud fkhddvwlroknt64hmthirfpo7no; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_definitionurls_aud
    ADD CONSTRAINT fkhddvwlroknt64hmthirfpo7no FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: bulkload_aud fkhhh8994753467hwa33blp22dc; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkload_aud
    ADD CONSTRAINT fkhhh8994753467hwa33blp22dc FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: experimentalcondition fkhi2109btsx06x2u9kdg7y7xp0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT fkhi2109btsx06x2u9kdg7y7xp0 FOREIGN KEY (conditiongeneontology_curie) REFERENCES public.goterm(curie);


--
-- Name: genomicentity fkhi54si7gksfs3f6jrbytaddbi; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity
    ADD CONSTRAINT fkhi54si7gksfs3f6jrbytaddbi FOREIGN KEY (curie) REFERENCES public.biologicalentity(curie);


--
-- Name: wbbtterm_aud fkhu85m34h8hf95s453u3m4ed8y; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.wbbtterm_aud
    ADD CONSTRAINT fkhu85m34h8hf95s453u3m4ed8y FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);


--
-- Name: gene fkiaxg0dhug3stym3gjovw598w1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gene
    ADD CONSTRAINT fkiaxg0dhug3stym3gjovw598w1 FOREIGN KEY (genetype_curie) REFERENCES public.soterm(curie);


--
-- Name: chemicalterm_aud fkieeg5x1a11dqom8dw4valm169; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.chemicalterm_aud
    ADD CONSTRAINT fkieeg5x1a11dqom8dw4valm169 FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);


--
-- Name: diseaseannotation_ecoterm_aud fkinnydpai0athl1jn4vpjqulnq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_ecoterm_aud
    ADD CONSTRAINT fkinnydpai0athl1jn4vpjqulnq FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: genomicentity_synonym_aud fkirf7xq12pkuqpna31dbdiylo4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_synonym_aud
    ADD CONSTRAINT fkirf7xq12pkuqpna31dbdiylo4 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: bulkfmsload fkj3lf8vdpiflwx2gisua4kurs1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkfmsload
    ADD CONSTRAINT fkj3lf8vdpiflwx2gisua4kurs1 FOREIGN KEY (id) REFERENCES public.bulkscheduledload(id);


--
-- Name: ontologyterm_synonyms fkjf8xunyry3dy9njpqb01tvjsr; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_synonyms
    ADD CONSTRAINT fkjf8xunyry3dy9njpqb01tvjsr FOREIGN KEY (ontologyterm_curie) REFERENCES public.ontologyterm(curie);


--
-- Name: ontologyterm_secondaryidentifiers_aud fkjvsj7p0oj0plbpxtu04cskfhs; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_secondaryidentifiers_aud
    ADD CONSTRAINT fkjvsj7p0oj0plbpxtu04cskfhs FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: allele_genegenomiclocation fkk3m8sj0e342pbhl2q56awc16a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allele_genegenomiclocation
    ADD CONSTRAINT fkk3m8sj0e342pbhl2q56awc16a FOREIGN KEY (genomiclocations_id) REFERENCES public.genegenomiclocation(id);


--
-- Name: diseaseannotation fkk6hg8sfqhqhlsdjmyex63bvo7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT fkk6hg8sfqhqhlsdjmyex63bvo7 FOREIGN KEY (singlereference_curie) REFERENCES public.reference(curie);


--
-- Name: bulkloadfile fkkakppk407vfefvttp4a5p6npg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkloadfile
    ADD CONSTRAINT fkkakppk407vfefvttp4a5p6npg FOREIGN KEY (bulkload_id) REFERENCES public.bulkload(id);


--
-- Name: diseaseannotation_aud fkkco190qeeoqsitv4p2ecvamtu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_aud
    ADD CONSTRAINT fkkco190qeeoqsitv4p2ecvamtu FOREIGN KEY (id, rev) REFERENCES public.association_aud(id, rev);


--
-- Name: conditionrelation_aud fkkcw0iu1vmw6ttm7g645947yyy; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conditionrelation_aud
    ADD CONSTRAINT fkkcw0iu1vmw6ttm7g645947yyy FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: affectedgenomicmodel fkke1qw7ijaa33fqv1bifsiwiv9; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.affectedgenomicmodel
    ADD CONSTRAINT fkke1qw7ijaa33fqv1bifsiwiv9 FOREIGN KEY (curie) REFERENCES public.genomicentity(curie);


--
-- Name: doterm_aud fkkgu80ih0f55tskr386gucsqh2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doterm_aud
    ADD CONSTRAINT fkkgu80ih0f55tskr386gucsqh2 FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);


--
-- Name: experimentalconditionontologyterm_aud fkkr4o08hq0jboq6g4ou5gmn8xd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalconditionontologyterm_aud
    ADD CONSTRAINT fkkr4o08hq0jboq6g4ou5gmn8xd FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);


--
-- Name: paperhandle_aud fkkvs5vkh768djlf41pf2t9qlho; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.paperhandle_aud
    ADD CONSTRAINT fkkvs5vkh768djlf41pf2t9qlho FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: ontologyterm_synonyms_aud fkl2ra6s3aosf68bgss49loflot; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_synonyms_aud
    ADD CONSTRAINT fkl2ra6s3aosf68bgss49loflot FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: bulkfmsload_aud fkl33k8qcbmbx4yssfnvlgicyri; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkfmsload_aud
    ADD CONSTRAINT fkl33k8qcbmbx4yssfnvlgicyri FOREIGN KEY (id, rev) REFERENCES public.bulkscheduledload_aud(id, rev);


--
-- Name: agmdiseaseannotation_aud fkl6x226295n9ms1kugrsi88efp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agmdiseaseannotation_aud
    ADD CONSTRAINT fkl6x226295n9ms1kugrsi88efp FOREIGN KEY (id, rev) REFERENCES public.diseaseannotation_aud(id, rev);


--
-- Name: genegenomiclocation_aud fklln6of6vsrfvcbnku3x9na2k0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genegenomiclocation_aud
    ADD CONSTRAINT fklln6of6vsrfvcbnku3x9na2k0 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: diseaseannotation_conditionrelation fklvpswihwk7vf7ijnyustt47na; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_conditionrelation
    ADD CONSTRAINT fklvpswihwk7vf7ijnyustt47na FOREIGN KEY (conditionrelations_id) REFERENCES public.conditionrelation(id);


--
-- Name: agmdiseaseannotation fklvr4o1waqclvbktjmyg6x25ls; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agmdiseaseannotation
    ADD CONSTRAINT fklvr4o1waqclvbktjmyg6x25ls FOREIGN KEY (subject_curie) REFERENCES public.affectedgenomicmodel(curie);


--
-- Name: crossreference_pageareas fkmd0dqivib8hjjtqiqsiyq0hgf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.crossreference_pageareas
    ADD CONSTRAINT fkmd0dqivib8hjjtqiqsiyq0hgf FOREIGN KEY (crossreference_curie) REFERENCES public.crossreference(curie);


--
-- Name: allelediseaseannotation_aud fkn5epg1m6f6l6cqh59ai23mws3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allelediseaseannotation_aud
    ADD CONSTRAINT fkn5epg1m6f6l6cqh59ai23mws3 FOREIGN KEY (id, rev) REFERENCES public.diseaseannotation_aud(id, rev);


--
-- Name: vocabularyterm_textsynonyms fknb1g5wap721lwp2rn7gse6geq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm_textsynonyms
    ADD CONSTRAINT fknb1g5wap721lwp2rn7gse6geq FOREIGN KEY (vocabularyterm_id) REFERENCES public.vocabularyterm(id);


--
-- Name: genomicentity_aud fknd0sic0qo3ko71w4d9k5urg48; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_aud
    ADD CONSTRAINT fknd0sic0qo3ko71w4d9k5urg48 FOREIGN KEY (curie, rev) REFERENCES public.biologicalentity_aud(curie, rev);


--
-- Name: allelediseaseannotation fknecrivvmqgg2ifhppubrjy5ey; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allelediseaseannotation
    ADD CONSTRAINT fknecrivvmqgg2ifhppubrjy5ey FOREIGN KEY (inferredgene_curie) REFERENCES public.gene(curie);


--
-- Name: gene_genegenomiclocation fknh14ykhb6xm1hnfak0lbifaex; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.gene_genegenomiclocation
    ADD CONSTRAINT fknh14ykhb6xm1hnfak0lbifaex FOREIGN KEY (gene_curie) REFERENCES public.gene(curie);


--
-- Name: ontologyterm_definitionurls fknhkhso5kmei3t37mkhodkkfgt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_definitionurls
    ADD CONSTRAINT fknhkhso5kmei3t37mkhodkkfgt FOREIGN KEY (ontologyterm_curie) REFERENCES public.ontologyterm(curie);


--
-- Name: xcoterm_aud fknlbuiyo3i6daerkmpim317bd3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.xcoterm_aud
    ADD CONSTRAINT fknlbuiyo3i6daerkmpim317bd3 FOREIGN KEY (curie, rev) REFERENCES public.experimentalconditionontologyterm_aud(curie, rev);


--
-- Name: molecule fknnf79fdaivbnqu0p9kes1jtd1; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.molecule
    ADD CONSTRAINT fknnf79fdaivbnqu0p9kes1jtd1 FOREIGN KEY (curie) REFERENCES public.chemicalterm(curie);


--
-- Name: vocabularyterm_crossreference_aud fko00dyud2urf5787jggfx57nyf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm_crossreference_aud
    ADD CONSTRAINT fko00dyud2urf5787jggfx57nyf FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: bulkload fko25bugche1vp384eme3exv04d; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkload
    ADD CONSTRAINT fko25bugche1vp384eme3exv04d FOREIGN KEY (group_id) REFERENCES public.bulkloadgroup(id);


--
-- Name: agmdiseaseannotation fko9dilcfxv6tw0oaeds0yss8op; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agmdiseaseannotation
    ADD CONSTRAINT fko9dilcfxv6tw0oaeds0yss8op FOREIGN KEY (inferredallele_curie) REFERENCES public.allele(curie);


--
-- Name: conditionrelation_experimentalcondition_aud fkogkeeb66oxunetml17wbxkogv; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conditionrelation_experimentalcondition_aud
    ADD CONSTRAINT fkogkeeb66oxunetml17wbxkogv FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: allele_genegenomiclocation fkomfoc4gujrmdg18e3xe3kisdp; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allele_genegenomiclocation
    ADD CONSTRAINT fkomfoc4gujrmdg18e3xe3kisdp FOREIGN KEY (allele_curie) REFERENCES public.allele(curie);


--
-- Name: experimentalcondition_aud fkos799amubpywlufc5ttysjp5h; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition_aud
    ADD CONSTRAINT fkos799amubpywlufc5ttysjp5h FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: experimentalcondition fkp0oqdnt9bmx68i84neufkcb3a; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT fkp0oqdnt9bmx68i84neufkcb3a FOREIGN KEY (conditionclass_curie) REFERENCES public.zecoterm(curie);


--
-- Name: agmdiseaseannotation fkp1rktcpoyvnr2f756ncdb8k24; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agmdiseaseannotation
    ADD CONSTRAINT fkp1rktcpoyvnr2f756ncdb8k24 FOREIGN KEY (id) REFERENCES public.diseaseannotation(id);


--
-- Name: conditionrelation_experimentalcondition fkp2129xf9bt872p0rycff6iy93; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.conditionrelation_experimentalcondition
    ADD CONSTRAINT fkp2129xf9bt872p0rycff6iy93 FOREIGN KEY (conditions_id) REFERENCES public.experimentalcondition(id);


--
-- Name: bulkurlload fkp6lnq5x8g34hc2i3b3focu4vg; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.bulkurlload
    ADD CONSTRAINT fkp6lnq5x8g34hc2i3b3focu4vg FOREIGN KEY (id) REFERENCES public.bulkscheduledload(id);


--
-- Name: diseaseannotation_ecoterm fkp79bf46xsyojpvjjguoe3vuuu; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_ecoterm
    ADD CONSTRAINT fkp79bf46xsyojpvjjguoe3vuuu FOREIGN KEY (evidencecodes_curie) REFERENCES public.ecoterm(curie);


--
-- Name: doterm fkp8el2duba9ym3l6gd5dy43swk; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.doterm
    ADD CONSTRAINT fkp8el2duba9ym3l6gd5dy43swk FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);


--
-- Name: diseaseannotation fkp8u0w7kiirnjfcmdl4oyjhcs3; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT fkp8u0w7kiirnjfcmdl4oyjhcs3 FOREIGN KEY (object_curie) REFERENCES public.doterm(curie);


--
-- Name: ontologyterm_secondaryidentifiers fkpkg5jfw6wypf4v43bpb4ergu7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_secondaryidentifiers
    ADD CONSTRAINT fkpkg5jfw6wypf4v43bpb4ergu7 FOREIGN KEY (ontologyterm_curie) REFERENCES public.ontologyterm(curie);


--
-- Name: genomicentity_secondaryidentifiers_aud fkpt86y0ets51hu48asq8x2g98s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_secondaryidentifiers_aud
    ADD CONSTRAINT fkpt86y0ets51hu48asq8x2g98s FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: vocabularyterm fkpua589tn1kypabuee8ytlmfpf; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.vocabularyterm
    ADD CONSTRAINT fkpua589tn1kypabuee8ytlmfpf FOREIGN KEY (vocabulary_id) REFERENCES public.vocabulary(id);


--
-- Name: zecoterm fkqd3f6hcopl67fwai6viq07t88; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zecoterm
    ADD CONSTRAINT fkqd3f6hcopl67fwai6viq07t88 FOREIGN KEY (curie) REFERENCES public.experimentalconditionontologyterm(curie);


--
-- Name: wbbtterm fkqnxqrnadcxojeti2ienobdqh0; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.wbbtterm
    ADD CONSTRAINT fkqnxqrnadcxojeti2ienobdqh0 FOREIGN KEY (curie) REFERENCES public.anatomicalterm(curie);


--
-- Name: experimentalcondition_paperhandle fkr26iemfq6lymbmntj55n3ln33; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition_paperhandle
    ADD CONSTRAINT fkr26iemfq6lymbmntj55n3ln33 FOREIGN KEY (paperhandles_handle) REFERENCES public.paperhandle(handle);


--
-- Name: allele_genegenomiclocation_aud fkr7xwt9rj6bxnmrlyd4h9d9oe7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.allele_genegenomiclocation_aud
    ADD CONSTRAINT fkr7xwt9rj6bxnmrlyd4h9d9oe7 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: ecoterm_aud fkrdtwy8r0gnnh6numgdbgi9e6s; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ecoterm_aud
    ADD CONSTRAINT fkrdtwy8r0gnnh6numgdbgi9e6s FOREIGN KEY (curie, rev) REFERENCES public.ontologyterm_aud(curie, rev);


--
-- Name: genomicentity_synonym fkrfh6t1dmhd1jbsklymyuvqk22; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_synonym
    ADD CONSTRAINT fkrfh6t1dmhd1jbsklymyuvqk22 FOREIGN KEY (genomicentities_curie) REFERENCES public.genomicentity(curie);


--
-- Name: soterm fkri7tkc9slvpex9v83peovegyt; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.soterm
    ADD CONSTRAINT fkri7tkc9slvpex9v83peovegyt FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);


--
-- Name: crossreference_aud fkricj7nn0u0fec2l2r2fo7al55; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.crossreference_aud
    ADD CONSTRAINT fkricj7nn0u0fec2l2r2fo7al55 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: genomicentity_crossreference_aud fks5gsr2myfi6i0mq3n9rix7lpd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.genomicentity_crossreference_aud
    ADD CONSTRAINT fks5gsr2myfi6i0mq3n9rix7lpd FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: zfaterm_aud fks66s1k4fon0to2kk7qfsm1xon; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.zfaterm_aud
    ADD CONSTRAINT fks66s1k4fon0to2kk7qfsm1xon FOREIGN KEY (curie, rev) REFERENCES public.anatomicalterm_aud(curie, rev);


--
-- Name: diseaseannotation_gene_aud fks6e9od45c5fv6nd4olb819vsd; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_gene_aud
    ADD CONSTRAINT fks6e9od45c5fv6nd4olb819vsd FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: ecoterm fkskvp24kfp723htxmk0m9ev4ns; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ecoterm
    ADD CONSTRAINT fkskvp24kfp723htxmk0m9ev4ns FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);


--
-- Name: ontologyterm_subsets_aud fksn5i9xgd2avp7co3c70lv37; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.ontologyterm_subsets_aud
    ADD CONSTRAINT fksn5i9xgd2avp7co3c70lv37 FOREIGN KEY (rev) REFERENCES public.revinfo(rev);


--
-- Name: experimentalcondition fksso9a3875a8t0ver6u6qciuap; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.experimentalcondition
    ADD CONSTRAINT fksso9a3875a8t0ver6u6qciuap FOREIGN KEY (conditionanatomy_curie) REFERENCES public.anatomicalterm(curie);


--
-- Name: mpterm fkta9f30vmw7h1smmv68to1ipyq; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.mpterm
    ADD CONSTRAINT fkta9f30vmw7h1smmv68to1ipyq FOREIGN KEY (curie) REFERENCES public.ontologyterm(curie);


--
-- Name: agmdiseaseannotation fktj1uj3to13fi4q32bc2p65lah; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.agmdiseaseannotation
    ADD CONSTRAINT fktj1uj3to13fi4q32bc2p65lah FOREIGN KEY (inferredgene_curie) REFERENCES public.gene(curie);


--
-- Name: materm fktlgqvrv4vuh8gqihevh6adya4; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.materm
    ADD CONSTRAINT fktlgqvrv4vuh8gqihevh6adya4 FOREIGN KEY (curie) REFERENCES public.anatomicalterm(curie);


--
-- Name: diseaseannotation_gene fky4jlhsgseecd3gkxjtwp28ba; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.diseaseannotation_gene
    ADD CONSTRAINT fky4jlhsgseecd3gkxjtwp28ba FOREIGN KEY (diseaseannotation_id) REFERENCES public.diseaseannotation(id);


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM rdsadmin;
REVOKE ALL ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

