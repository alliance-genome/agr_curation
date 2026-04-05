-- ============================================================
-- GENE -> dependent loads
-- ============================================================

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'FB Gene Load' AND c.name IN (
    'FB Allele Association Load', 'FB Construct Association Load', 'FB Disease Annotation Load',
    'FB Expression Load', 'FB GAF Load', 'Genetic Interaction Load', 'Molecular Interaction Load',
    'FB Phenotype Load', 'FB Biogrid Orcs Load', 'FB Expression Atlas', 'FB GEO CrossReference',
    'FB GFF Gene Load', 'FB GFF Transcript Load', 'FB Orthology Load', 'FB Paralogy Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'HUMAN Gene Load' AND c.name IN (
    'Human Disease Annotation Load', 'HUMAN GAF Load', 'Genetic Interaction Load', 'Molecular Interaction Load',
    'HUMAN Phenotype Load', 'HUMAN Biogrid Orcs Load', 'HUMAN Expression Atlas', 'HUMAN GEO CrossReference',
    'Human GFF Gene Load', 'Human GFF Transcript Load', 'Human Orthology Load', 'HUMAN Paralogy Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'MGI Gene Load' AND c.name IN (
    'MGI Allele Association Load', 'MGI Construct Association Load', 'MGI Disease Annotation Load',
    'MGI Expression Load', 'MGI GAF Load', 'Genetic Interaction Load', 'Molecular Interaction Load',
    'MGI Phenotype Load', 'MGI Biogrid Orcs Load', 'MGI Expression Atlas', 'MGI GEO CrossReference',
    'MGI GFF Gene Load', 'MGI GFF Transcript Load', 'MGI Orthology Load', 'MGI Paralogy Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'RGD Gene Load' AND c.name IN (
    'RGD Allele Association Load', 'RGD Disease Annotation Load', 'RGD Expression Load',
    'RGD GAF Load', 'Genetic Interaction Load', 'Molecular Interaction Load',
    'RGD Phenotype Load', 'RGD Expression Atlas', 'RGD GEO CrossReference',
    'RGD GFF Gene Load', 'RGD GFF Transcript Load', 'RGD Orthology Load', 'RGD Paralogy Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'SGD Gene Load' AND c.name IN (
    'SGD Allele Association Load', 'SGD Disease Annotation', 'SGD Expression Load',
    'SGD GAF Load', 'Genetic Interaction Load', 'Molecular Interaction Load',
    'SGD Phenotype Load', 'SGD Expression Atlas', 'SGD GEO CrossReference',
    'SGD GFF Gene Load', 'SGD GFF Transcript Load', 'SGD Orthology Load', 'SGD Paralogy Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'WB Gene Load' AND c.name IN (
    'WB Allele Association Load', 'WB Construct Association Load', 'WB Disease Annotation Load',
    'WB Expression Load', 'WB GAF Load', 'Genetic Interaction Load', 'Molecular Interaction Load',
    'WB Phenotype Load', 'WB Expression Atlas', 'WB GEO CrossReference',
    'WB GFF Gene Load', 'WB GFF Transcript Load', 'WB Orthology Load', 'WB Paralogy Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'XB Gene Load' AND c.name IN (
    'XB GAF Load', 'Genetic Interaction Load', 'Molecular Interaction Load',
    'XBXL Expression Load', 'XBXT Expression Load', 'XBXL Phenotype Load', 'XBXT Phenotype Load',
    'XBXL GFF Gene Load', 'XBXT GFF Gene Load', 'XBXL GFF Transcript Load', 'XBXT GFF Transcript Load',
    'XBXL Orthology Load', 'XBXT Orthology Load', 'XBXT Paralogy Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'ZFIN Gene Load' AND c.name IN (
    'ZFIN Allele Association Load', 'ZFIN Construct Association Load', 'ZFIN AGM Disease Annotation Load',
    'ZFIN Expression Load', 'ZFIN GAF Load', 'Genetic Interaction Load', 'Molecular Interaction Load',
    'ZFIN Phenotype Load', 'ZFIN Expression Atlas', 'ZFIN GEO CrossReference',
    'ZFIN GFF Gene Load', 'ZFIN GFF Transcript Load', 'ZFIN Orthology Load', 'ZFIN Paralogy Load',
    'ZFIN Sequence Targeting Reagent Load');

-- ============================================================
-- ALLELE -> dependent loads
-- ============================================================

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'FB Allele Load' AND c.name IN (
    'FB Allele Association Load', 'FB Construct Association Load', 'FB Disease Annotation Load', 'FB Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'MGI Allele Load' AND c.name IN (
    'MGI Allele Association Load', 'MGI Construct Association Load', 'MGI Disease Annotation Load', 'MGI Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'RGD Allele Load' AND c.name IN (
    'RGD Allele Association Load', 'RGD Disease Annotation Load', 'RGD Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'SGD Allele Load' AND c.name IN (
    'SGD Allele Association Load', 'SGD Disease Annotation', 'SGD Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'WB Allele Load' AND c.name IN (
    'WB Allele Association Load', 'WB Construct Association Load', 'WB Disease Annotation Load', 'WB Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'ZFIN Allele Load' AND c.name IN (
    'ZFIN Allele Association Load', 'ZFIN Construct Association Load', 'ZFIN AGM Disease Annotation Load', 'ZFIN Phenotype Load');

-- ============================================================
-- AGM -> dependent loads
-- ============================================================

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'FB AGM Load' AND c.name IN (
    'FB Construct Association Load', 'FB Disease Annotation Load', 'FB Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'MGI AGM Load' AND c.name IN (
    'MGI Construct Association Load', 'MGI Disease Annotation Load', 'MGI Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'RGD AGM Load' AND c.name IN (
    'RGD Disease Annotation Load', 'RGD Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'SGD AGM Load' AND c.name IN (
    'SGD Disease Annotation', 'SGD Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'WB AGM Load' AND c.name IN (
    'WB Construct Association Load', 'WB Disease Annotation Load', 'WB Phenotype Load');

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'ZFIN AGM Load' AND c.name IN (
    'ZFIN Construct Association Load', 'ZFIN AGM Disease Annotation Load', 'ZFIN Phenotype Load');

-- ============================================================
-- CONSTRUCT -> dependent loads
-- ============================================================

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'FB Construct Load' AND c.name = 'FB Construct Association Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'MGI Construct Load' AND c.name = 'MGI Construct Association Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'WB Construct Load' AND c.name = 'WB Construct Association Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'ZFIN Construct Load' AND c.name = 'ZFIN Construct Association Load';

-- ============================================================
-- VARIATION (FMS Variant) -> dependent loads
-- ============================================================

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'FB Variant Load' AND c.name = 'FB VEP Transcript Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'MGI Variant Load' AND c.name = 'MGI VEP Transcript Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'RGD Variant Load' AND c.name = 'RGD VEP Transcript Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'WB Variant Load' AND c.name = 'WB VEP Transcript Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'ZFIN Variant Load' AND c.name = 'ZFIN VEP Transcript Load';

-- ============================================================
-- GFF TRANSCRIPT -> dependent loads
-- ============================================================

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'FB GFF Transcript Load' AND c.name IN ('FB GFF CDS Load', 'FB GFF Exon Load', 'FB VEP Transcript Load');
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'Human GFF Transcript Load' AND c.name IN ('Human GFF CDS Load', 'Human GFF Exon Load');
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'MGI GFF Transcript Load' AND c.name IN ('MGI GFF CDS Load', 'MGI GFF Exon Load', 'MGI VEP Transcript Load');
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'RGD GFF Transcript Load' AND c.name IN ('RGD GFF CDS Load', 'RGD GFF Exon Load', 'RGD VEP Transcript Load');
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'SGD GFF Transcript Load' AND c.name IN ('SGD GFF CDS Load', 'SGD GFF Exon Load');
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'WB GFF Transcript Load' AND c.name IN ('WB GFF CDS Load', 'WB GFF Exon Load', 'WB VEP Transcript Load');
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'XBXL GFF Transcript Load' AND c.name IN ('XBXL GFF CDS Load', 'XBXL GFF Exon Load');
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'XBXT GFF Transcript Load' AND c.name IN ('XBXT GFF CDS Load', 'XBXT GFF Exon Load');
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'ZFIN GFF Transcript Load' AND c.name IN ('ZFIN GFF CDS Load', 'ZFIN GFF Exon Load', 'ZFIN VEP Transcript Load');

-- ============================================================
-- VEP TRANSCRIPT -> dependent loads
-- ============================================================

INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'FB VEP Transcript Load' AND c.name = 'FB VEP Gene Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'MGI VEP Transcript Load' AND c.name = 'MGI VEP Gene Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'RGD VEP Transcript Load' AND c.name = 'RGD VEP Gene Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'WB VEP Transcript Load' AND c.name = 'WB VEP Gene Load';
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
SELECT c.id, p.id FROM bulkload p, bulkload c
WHERE p.name = 'ZFIN VEP Transcript Load' AND c.name = 'ZFIN VEP Gene Load';
