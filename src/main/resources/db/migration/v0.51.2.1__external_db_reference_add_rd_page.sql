-- SCRUM-6204: give ExternalDatabaseReference its own resourceDescriptorPage so OMIM/Orphanet
-- phenotype references carry linkout metadata. Mirrors the CrossReference pattern from SCRUM-6121.

ALTER TABLE externaldatabasereference
	ADD COLUMN resourcedescriptorpage_id BIGINT;

ALTER TABLE externaldatabasereference
	ADD CONSTRAINT externaldatabasereference_resourcedescriptorpage_id_fk
	FOREIGN KEY (resourcedescriptorpage_id)
	REFERENCES resourcedescriptorpage(id);

CREATE INDEX externaldatabasereference_resourcedescriptorpage_index
	ON externaldatabasereference (resourcedescriptorpage_id);

-- Backfill existing rows: try (prefix, 'reference') first, then fall back to (prefix, 'default').
-- Mirrors ResourceDescriptorPageService.resolvePageForReferenceCurie, which reaches
-- ResourceDescriptorService.getByPrefixOrSynonym -- a prefix-OR-synonym match. The synonym
-- branch is what lets curies like "ORPHA:1234" resolve to the "Orphanet" descriptor
-- (which lists "ORPHA" in resourcedescriptor_synonyms).
UPDATE externaldatabasereference edr
SET resourcedescriptorpage_id = COALESCE(
	(SELECT rdp.id
	   FROM informationcontententity ice
	   JOIN resourcedescriptor rd
	     ON rd.prefix = SPLIT_PART(ice.curie, ':', 1)
	     OR rd.id IN (SELECT rds.resourcedescriptor_id
	                    FROM resourcedescriptor_synonyms rds
	                   WHERE rds.synonyms = SPLIT_PART(ice.curie, ':', 1))
	   JOIN resourcedescriptorpage rdp
	     ON rdp.resourcedescriptor_id = rd.id
	    AND rdp.name = 'reference'
	  WHERE ice.id = edr.id
	  LIMIT 1),
	(SELECT rdp.id
	   FROM informationcontententity ice
	   JOIN resourcedescriptor rd
	     ON rd.prefix = SPLIT_PART(ice.curie, ':', 1)
	     OR rd.id IN (SELECT rds.resourcedescriptor_id
	                    FROM resourcedescriptor_synonyms rds
	                   WHERE rds.synonyms = SPLIT_PART(ice.curie, ':', 1))
	   JOIN resourcedescriptorpage rdp
	     ON rdp.resourcedescriptor_id = rd.id
	    AND rdp.name = 'default'
	  WHERE ice.id = edr.id
	  LIMIT 1)
);
