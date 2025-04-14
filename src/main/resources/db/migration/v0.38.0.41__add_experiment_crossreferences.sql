CREATE TABLE public.geneexpressionexperiment_crossreference (
    geneexpressionexperiment_id bigint NOT NULL,
    crossreferences_id bigint NOT NULL
);

ALTER TABLE ONLY public.geneexpressionexperiment_crossreference ADD CONSTRAINT uk4iuct6if4r4edsjvuwawwb61g UNIQUE (crossreferences_id);

CREATE INDEX gee_crossreference_crossreferences_index ON public.geneexpressionexperiment_crossreference USING btree (crossreferences_id);
CREATE INDEX gee_crossreference_geneexpressionexperiment_index ON public.geneexpressionexperiment_crossreference USING btree (geneexpressionexperiment_id);

ALTER TABLE ONLY public.geneexpressionexperiment_crossreference
    ADD CONSTRAINT fkjhiyltsfwpstp20je8ruckv7k FOREIGN KEY (geneexpressionexperiment_id) REFERENCES public.geneexpressionexperiment(id);
ALTER TABLE ONLY public.geneexpressionexperiment_crossreference
    ADD CONSTRAINT fkpebq3g4092pi4q4licvigwhp6 FOREIGN KEY (crossreferences_id) REFERENCES public.crossreference(id);




