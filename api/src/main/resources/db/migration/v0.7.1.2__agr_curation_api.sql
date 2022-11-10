--
-- Name: atpterm; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.atpterm (
    curie character varying(255) NOT NULL
);


ALTER TABLE public.atpterm OWNER TO postgres;

--
-- Name: atpterm_aud; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.atpterm_aud (
                                    curie character varying(255) NOT NULL,
                                    rev integer NOT NULL
);


ALTER TABLE public.atpterm_aud OWNER TO postgres;