DELETE FROM reference_crossreference
	WHERE reference_curie IN (
		SELECT curie FROM informationcontententity
			WHERE obsolete = true
	);
	
DELETE FROM reference
	WHERE curie IN (
		SELECT curie FROM informationcontententity
			WHERE obsolete = true
	);
	
DELETE FROM slotannotation_informationcontententity
	WHERE evidence_curie IN (
		SELECT curie FROM informationcontententity
			WHERE obsolete = true
	);
	
DELETE FROM informationcontententity
	WHERE obsolete = true;
	
DELETE FROM ontologyterm_crossreference;
	
DELETE FROM genomicentity_crossreference;

DELETE FROM crossreference c
	WHERE NOT EXISTS (
		SELECT null FROM reference_crossreference r
			WHERE c.id = r.crossreferences_id
	) AND NOT EXISTS (
		SELECT null FROM dataprovider d
			WHERE c.id = d.crossreference_id
	);