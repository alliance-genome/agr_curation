CREATE TABLE tmp_dpxref_id AS SELECT DISTINCT id FROM crossreference WHERE referencedcurie IN ('WB','FB','SGD','RGD','MGI','ZFIN','XB','XBXL','XBXT','OMIM');

UPDATE phenotypeannotation SET dataprovidercrossreference_id = NULL;
UPDATE htpexpressiondatasetannotation SET dataprovidercrossreference_id = NULL;

UPDATE biologicalentity SET dataprovidercrossreference_id = NULL WHERE dataprovidercrossreference_id IN (SELECT id FROM tmp_dpxref_id);

DELETE FROM crossreference WHERE id IN (SELECT id FROM tmp_dpxref_id);

DROP TABLE tmp_dpxref_id;