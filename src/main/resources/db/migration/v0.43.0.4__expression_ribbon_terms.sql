INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Anatomical Structure Slim Terms Public Site', 'anatomical_structure_slim_terms_public_site', id, 'Ordered anatomical structure slims terms for public site' FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms';

INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Stage Slim Terms Public Site', 'stage_slim_terms_public_site', id, 'Ordered stage slims terms for public site' FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms';


INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'UBERON:0005409' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;


INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0005726' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0001009' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0000949' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0002330' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0002193' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0002423' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0002416' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0007037' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0002204' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0001016' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;


INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0001008' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0000990' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0001004' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0001032' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0002105' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0002104' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0000925' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0000924' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0000926' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0003104' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0001013' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0000026' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0016887' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:6005023' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0002539' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'anatomical_structure_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'Other' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'anatomical_structure_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;



INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'stage_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0000068' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'stage_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'post embryonic, pre-adult' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
WITH
  t1 AS (
    SELECT id FROM vocabularytermset WHERE vocabularylabel = 'stage_slim_terms_public_site'
  ),
  t2 AS (
    SELECT id FROM vocabularyterm WHERE name = 'UBERON:0000113' AND vocabulary_id = (
      SELECT id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms'
    )
  )
SELECT t1.id, t2.id FROM t1, t2;


-- Create Cellular Components GO Slim Terms vocabulary and terms
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Cellular Components GO Slim Terms', 'cellular_components_go_slim_terms');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005576', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005886', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0045202', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0030054', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0042995', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0031410', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005768', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005773', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005794', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005783', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005829', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005739', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005634', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005694', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0005856', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'GO:0032991', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'otherLocations', id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';



INSERT INTO vocabularytermset (id, name, vocabularylabel, vocabularytermsetvocabulary_id, vocabularytermsetdescription)
	SELECT nextval('vocabularytermset_seq'), 'Cellular Components Slim Terms Public Site', 'cellular_component_slim_terms_public_site', id, 'Ordered cellular components slims terms for public site' FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms';


INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'GO:0005576' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;


INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005886' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0045202' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0030054' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0042995' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0031410' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005768' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005773' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005794' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;


INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005783' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005829' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005739' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005634' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005694' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0005856' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'GO:0032991' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

  INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
  WITH
    t1 AS (
      SELECT id FROM vocabularytermset WHERE vocabularylabel = 'cellular_component_slim_terms_public_site'
    ),
    t2 AS (
      SELECT id FROM vocabularyterm WHERE name = 'otherLocations' AND vocabulary_id = (
        SELECT id FROM vocabulary WHERE vocabularylabel = 'cellular_components_go_slim_terms'
      )
    )
  SELECT t1.id, t2.id FROM t1, t2;

