ALTER TABLE diseaseannotation
  ADD COLUMN IF NOT EXISTS diseasegeneticmodifierrelation VARCHAR (255),
  ADD COLUMN IF NOT EXISTS annotationtype VARCHAR (255),
  ADD COLUMN IF NOT EXISTS geneticsex VARCHAR (255),
  ADD COLUMN diseaserelation_id BIGINT,
  ADD COLUMN diseasegeneticmodifierrelation_id BIGINT,
  ADD COLUMN annotationtype_id BIGINT,
  ADD COLUMN geneticsex_id BIGINT;

ALTER TABLE conditionRelation
  ADD COLUMN conditionrelationtype_id BIGINT;

UPDATE diseaseannotation
  SET diseaserelation_id = subquery.id
  FROM (
      SELECT vocabularyterm.id as id, vocabularyterm.name as name
        FROM vocabularyterm
        INNER JOIN vocabulary
          ON vocabulary.id = vocabularyterm.vocabulary_id
        WHERE vocabulary.name = 'Disease Relation Vocabulary'
  ) AS subquery
  WHERE diseaseannotation.diseaserelation = subquery.name;

UPDATE diseaseannotation
  SET geneticsex_id = subquery.id
  FROM (
    SELECT vocabularyterm.id as id, vocabularyterm.name as name
      FROM vocabularyterm
      INNER JOIN vocabulary
        ON vocabulary.id = vocabularyterm.vocabulary_id
      WHERE vocabulary.name = 'Genetic sexes'
  ) AS subquery
  WHERE diseaseannotation.geneticsex = subquery.name;

UPDATE diseaseannotation
  SET annotationtype_id = subquery.id
  FROM (
    SELECT vocabularyterm.id as id, vocabularyterm.name as name
      FROM vocabularyterm
        INNER JOIN vocabulary
          ON vocabulary.id = vocabularyterm.vocabulary_id
        WHERE vocabulary.name = 'Annotation types'
    ) AS subquery
    WHERE diseaseannotation.annotationtype = subquery.name;

UPDATE diseaseannotation
  SET annotationtype_id = subquery.id
  FROM (
    SELECT vocabularyterm.id as id, vocabularyterm.name as name
      FROM vocabularyterm
        INNER JOIN vocabulary
          ON vocabulary.id = vocabularyterm.vocabulary_id
        WHERE vocabulary.name = 'Disease genetic modifier relations'
    ) AS subquery
    WHERE diseaseannotation.annotationtype = subquery.name;

UPDATE conditionrelation
  SET conditionrelationtype_id = subquery.id
  FROM (
    SELECT vocabularyterm.id as id, vocabularyterm.name as name
      FROM vocabularyterm
        INNER JOIN vocabulary
          ON vocabulary.id = vocabularyterm.vocabulary_id
        WHERE vocabulary.name = 'Condition relation types'
    ) AS subquery
    WHERE conditionrelation.conditionrelationtype = subquery.name;

ALTER TABLE diseaseannotation
  DROP COLUMN diseaserelation,
  DROP COLUMN diseasegeneticmodifierrelation,
  DROP COLUMN annotationtype,
  DROP COLUMN geneticsex;

ALTER TABLE conditionRelation
  DROP COLUMN conditionrelationtype;

ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT fk9xjbpkdh1ffv0568wb8op838n FOREIGN KEY (annotationtype_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT fkgtlivor9244ndobm9w5c0lxff FOREIGN KEY (diseaserelation_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT fklns1tn5kseoys0xvq0f26h2vl FOREIGN KEY (geneticsex_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.conditionrelation
    ADD CONSTRAINT fkn8t4joy3iheftpbxt0omvxl52 FOREIGN KEY (conditionrelationtype_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT fknagkqf0yu1qib0wkp8s0n60hn FOREIGN KEY (diseasegeneticmodifierrelation_id) REFERENCES public.vocabularyterm(id);


