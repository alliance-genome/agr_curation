UPDATE bulkscheduledload SET cronschedule = '0 0 22 ? * SUN-THU' WHERE id IN (SELECT id from BULKLOAD WHERE backendbulkloadtype = 'GEOXREF');
DELETE FROM bulkurlload WHERE id IN (SELECT id from BULKLOAD WHERE backendbulkloadtype = 'GEOXREF');
UPDATE bulkloadgroup SET name = 'File Management System (FMS) GEO CrossReference Load' WHERE name = 'GEO CrossReference Load';

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GEOXREF', 'FB' FROM bulkload WHERE name = 'FB GEO CrossReference';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GEOXREF', 'HUMAN' FROM bulkload WHERE name = 'HUMAN GEO CrossReference';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GEOXREF', 'MGI' FROM bulkload WHERE name = 'MGI GEO CrossReference';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GEOXREF', 'RGD' FROM bulkload WHERE name = 'RGD GEO CrossReference';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GEOXREF', 'SGD' FROM bulkload WHERE name = 'SGD GEO CrossReference';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GEOXREF', 'WB' FROM bulkload WHERE name = 'WB GEO CrossReference';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype) SELECT id, 'GEOXREF', 'ZFIN' FROM bulkload WHERE name = 'ZFIN GEO CrossReference';