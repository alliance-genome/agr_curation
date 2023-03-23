SELECT g.id INTO mgi_gene_annotations FROM genediseaseannotation g
	INNER JOIN diseaseannotation d ON g.id = d.id
	INNER JOIN dataprovider p ON d.dataprovider_id = p.id
	INNER JOIN organization o ON p.sourceorganization_id = o.id
	WHERE o.abbreviation = 'MGI';

ALTER TABLE mgi_gene_annotations ADD PRIMARY KEY (id);

DELETE FROM genediseaseannotation
	USING mgi_gene_annotations
	WHERE genediseaseannotation.id = mgi_gene_annotations.id;

DELETE FROM diseaseannotation_conditionrelation
	USING mgi_gene_annotations
	WHERE diseaseannotation_conditionrelation.diseaseannotation_id = mgi_gene_annotations.id;

DELETE FROM diseaseannotation_ecoterm
	USING mgi_gene_annotations
	WHERE diseaseannotation_ecoterm.diseaseannotation_id = mgi_gene_annotations.id;

DELETE FROM diseaseannotation_gene
	USING mgi_gene_annotations
	WHERE diseaseannotation_gene.diseaseannotation_id = mgi_gene_annotations.id;
	
SELECT dn.relatednotes_id INTO mgi_gene_annotation_notes FROM diseaseannotation_note dn
	INNER JOIN mgi_gene_annotations m ON dn.diseaseannotation_id = m.id;
	
ALTER TABLE mgi_gene_annotation_notes ADD PRIMARY KEY (relatednotes_id);
	
DELETE FROM diseaseannotation_note
	USING mgi_gene_annotations
	WHERE diseaseannotation_note.diseaseannotation_id = mgi_gene_annotations.id;
	
DELETE FROM note
	USING mgi_gene_annotation_notes
	WHERE note.id = mgi_gene_annotation_notes.relatednotes_id;

DROP TABLE mgi_gene_annotation_notes;
	
DELETE FROM diseaseannotation_vocabularyterm
	USING mgi_gene_annotations
	WHERE diseaseannotation_vocabularyterm.diseaseannotation_id = mgi_gene_annotations.id;

DELETE FROM diseaseannotation
	USING mgi_gene_annotations
	WHERE diseaseannotation.id = mgi_gene_annotations.id;
	
DELETE FROM association
	USING mgi_gene_annotations
	WHERE association.id = mgi_gene_annotations.id;

DROP TABLE mgi_gene_annotations;