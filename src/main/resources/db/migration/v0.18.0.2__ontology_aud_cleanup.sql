ALTER TABLE atpterm_aud DROP CONSTRAINT fki65mb9a7b4el107fk4ox2f0kd; --
ALTER TABLE chebiterm_aud DROP CONSTRAINT fk7grscrrhdcw9ek6agi78j4ca1; --
ALTER TABLE daoterm_aud DROP CONSTRAINT fkgif1cep78abowfodb5rxvoq1x; --
ALTER TABLE doterm_aud DROP CONSTRAINT fkkgu80ih0f55tskr386gucsqh2; --
ALTER TABLE ecoterm_aud DROP CONSTRAINT fkrdtwy8r0gnnh6numgdbgi9e6s; --
ALTER TABLE emapaterm_aud DROP CONSTRAINT fkaipxoy4lm50q9mphk2yp7whyh; --
ALTER TABLE fbdvterm_aud DROP CONSTRAINT fkvtaradvq4e6fdjecf2m4ujap; --
ALTER TABLE goterm_aud DROP CONSTRAINT fk4kjm9hm06yutma1ilq04h967s; --
ALTER TABLE materm_aud DROP CONSTRAINT fk7lfprbh8k8mnw9yf8ywp7xieg; --
ALTER TABLE mmusdvterm_aud DROP CONSTRAINT fk19prhk8fikp11bpxxm2tqxh6u; --
ALTER TABLE mpterm_aud DROP CONSTRAINT fkjw611qjy95wa8gjjthb1uptjy; --
ALTER TABLE obiterm_aud DROP CONSTRAINT obiterm_aud_curie_rev_fk; --
ALTER TABLE roterm_aud DROP CONSTRAINT fk8wmlph21s6vviddt2tx63fhqn;
ALTER TABLE soterm_aud DROP CONSTRAINT fk5i3iqfnxf9hxjq6jmay2gm68g;
ALTER TABLE wbbtterm_aud DROP CONSTRAINT fkhu85m34h8hf95s453u3m4ed8y;
ALTER TABLE wblsterm_aud DROP CONSTRAINT fkbug8ndvvjf8e7e3rpcih06j63;
ALTER TABLE wbphenotypeterm_aud DROP CONSTRAINT fk48fs5fxgn3sfvyyiqpgoquuma;
ALTER TABLE xbaterm_aud DROP CONSTRAINT fkha6obinkag86qlcpemxmgv4ly;
ALTER TABLE xbedterm_aud DROP CONSTRAINT fkshxj981p427yuk4qgtvcirp5k;
ALTER TABLE xbsterm_aud DROP CONSTRAINT fkij9o8qar117chfev5ghkupl3o;
ALTER TABLE xcoterm_aud DROP CONSTRAINT fknlbuiyo3i6daerkmpim317bd3;
ALTER TABLE xpoterm_aud DROP CONSTRAINT fkbqybirrf1obv6esgeaj8os211;
ALTER TABLE xsmoterm_aud DROP CONSTRAINT fkjdlhxyvw79i14932dqdb1bx8f;
ALTER TABLE molecule_aud DROP CONSTRAINT fkcbo1onn61w7v5ivh1e1h2tcd7;
ALTER TABLE zfaterm_aud DROP CONSTRAINT fks66s1k4fon0to2kk7qfsm1xon;
ALTER TABLE zecoterm_aud DROP CONSTRAINT fke5wuchgyjhb2orgvht50q2dah;
ALTER TABLE zfsterm_aud DROP CONSTRAINT fk27dwwh5ekpa60ug4ystuwwn6w;
ALTER TABLE ncbitaxonterm_aud DROP CONSTRAINT fkap27v3trsn5u9q93qb8ikabrf; --
ALTER TABLE stageterm_aud DROP CONSTRAINT fkbe4dl5s3i7ga7hqryddog2g0f;
ALTER TABLE anatomicalterm_aud DROP CONSTRAINT fkan2c886jcsep01s7rqibfghfh; --
ALTER TABLE phenotypeterm_aud DROP CONSTRAINT fksap791c8unrey4xnqcydm8kv1;
ALTER TABLE chemicalterm_aud DROP CONSTRAINT fkieeg5x1a11dqom8dw4valm169; --
ALTER TABLE experimentalconditionontologyterm_aud DROP CONSTRAINT fkkr4o08hq0jboq6g4ou5gmn8xd; --
ALTER TABLE ontologyterm_aud DROP CONSTRAINT fkdxjp2u3w3xoi7p9j7huceg2ts; --

DELETE FROM atpterm_aud;
DELETE FROM chebiterm_aud;
DELETE FROM daoterm_aud;
DELETE FROM doterm_aud;
DELETE FROM ecoterm_aud;
DELETE FROM emapaterm_aud;
DELETE FROM fbdvterm_aud;
DELETE FROM goterm_aud;
DELETE FROM materm_aud;
DELETE FROM mmusdvterm_aud;
DELETE FROM mpterm_aud;
DELETE FROM ncbitaxonterm_aud;
DELETE FROM obiterm_aud;
DELETE FROM roterm_aud;
DELETE FROM soterm_aud;
DELETE FROM wbbtterm_aud;
DELETE FROM wblsterm_aud;
DELETE FROM xbaterm_aud;
DELETE FROM xbedterm_aud;
DELETE FROM xbsterm_aud;
DELETE FROM xcoterm_aud;
DELETE FROM xpoterm_aud;
DELETE FROM wbphenotypeterm_aud;
DELETE FROM xsmoterm_aud;
DELETE FROM zecoterm_aud;
DELETE FROM zfaterm_aud;
DELETE FROM zfsterm_aud;
DELETE FROM molecule_aud;

DELETE FROM phenotypeterm_aud;
DELETE FROM anatomicalterm_aud;
DELETE FROM phenotypeterm_aud;
DELETE FROM chemicalterm_aud;
DELETE FROM experimentalconditionontologyterm_aud;
DELETE FROM stageterm_aud;

DELETE FROM ontologyterm_crossreference_aud;
DELETE FROM ontologyterm_definitionurls_aud;
DELETE FROM ontologyterm_isa_ancestor_descendant_aud;
DELETE FROM ontologyterm_isa_parent_children_aud;
DELETE FROM ontologyterm_secondaryidentifiers_aud;
DELETE FROM ontologyterm_subsets_aud;
DELETE FROM ontologyterm_synonym_aud;

DELETE FROM ontologyterm_aud;

ALTER TABLE daoterm_aud ADD CONSTRAINT fkgif1cep78abowfodb5rxvoq1x FOREIGN KEY (curie, rev) REFERENCES anatomicalterm_aud(curie, rev);
ALTER TABLE xbaterm_aud ADD CONSTRAINT fkha6obinkag86qlcpemxmgv4ly FOREIGN KEY (curie, rev) REFERENCES anatomicalterm_aud(curie, rev);
ALTER TABLE wbbtterm_aud ADD CONSTRAINT fkhu85m34h8hf95s453u3m4ed8y FOREIGN KEY (curie, rev) REFERENCES anatomicalterm_aud(curie, rev);
ALTER TABLE zfaterm_aud ADD CONSTRAINT fks66s1k4fon0to2kk7qfsm1xon FOREIGN KEY (curie, rev) REFERENCES anatomicalterm_aud(curie, rev);	
ALTER TABLE emapaterm_aud ADD CONSTRAINT fkaipxoy4lm50q9mphk2yp7whyh FOREIGN KEY (curie, rev) REFERENCES anatomicalterm_aud(curie, rev);
ALTER TABLE materm_aud ADD CONSTRAINT fk7lfprbh8k8mnw9yf8ywp7xieg FOREIGN KEY (curie, rev) REFERENCES anatomicalterm_aud(curie, rev);

ALTER TABLE mpterm_aud ADD CONSTRAINT fkjw611qjy95wa8gjjthb1uptjy FOREIGN KEY (curie, rev) REFERENCES phenotypeterm_aud(curie, rev);
ALTER TABLE wbphenotypeterm_aud ADD CONSTRAINT fk48fs5fxgn3sfvyyiqpgoquuma FOREIGN KEY (curie, rev) REFERENCES phenotypeterm_aud(curie, rev);
ALTER TABLE xpoterm_aud ADD CONSTRAINT fkbqybirrf1obv6esgeaj8os211 FOREIGN KEY (curie, rev) REFERENCES phenotypeterm_aud(curie, rev);
	
ALTER TABLE chebiterm_aud ADD CONSTRAINT fk7grscrrhdcw9ek6agi78j4ca1 FOREIGN KEY (curie, rev) REFERENCES chemicalterm_aud(curie, rev);
ALTER TABLE xsmoterm_aud ADD CONSTRAINT fkjdlhxyvw79i14932dqdb1bx8f FOREIGN KEY (curie, rev) REFERENCES chemicalterm_aud(curie, rev);
ALTER TABLE molecule_aud ADD CONSTRAINT fkcbo1onn61w7v5ivh1e1h2tcd7 FOREIGN KEY (curie, rev) REFERENCES chemicalterm_aud(curie, rev);

ALTER TABLE xcoterm_aud ADD CONSTRAINT fknlbuiyo3i6daerkmpim317bd3 FOREIGN KEY (curie, rev) REFERENCES experimentalconditionontologyterm_aud(curie, rev);
ALTER TABLE zecoterm_aud ADD CONSTRAINT fke5wuchgyjhb2orgvht50q2dah FOREIGN KEY (curie, rev) REFERENCES experimentalconditionontologyterm_aud(curie, rev);
	
ALTER TABLE fbdvterm_aud ADD CONSTRAINT fkvtaradvq4e6fdjecf2m4ujap FOREIGN KEY (curie, rev) REFERENCES stageterm_aud(curie, rev);
ALTER TABLE mmusdvterm_aud ADD CONSTRAINT fk19prhk8fikp11bpxxm2tqxh6u FOREIGN KEY (curie, rev) REFERENCES stageterm_aud(curie, rev);
ALTER TABLE wblsterm_aud ADD CONSTRAINT fkbug8ndvvjf8e7e3rpcih06j63 FOREIGN KEY (curie, rev) REFERENCES stageterm_aud(curie, rev);
ALTER TABLE xbsterm_aud ADD CONSTRAINT fkij9o8qar117chfev5ghkupl3o FOREIGN KEY (curie, rev) REFERENCES stageterm_aud(curie, rev);
ALTER TABLE zfsterm_aud ADD CONSTRAINT fk27dwwh5ekpa60ug4ystuwwn6w FOREIGN KEY (curie, rev) REFERENCES stageterm_aud(curie, rev);

ALTER TABLE stageterm_aud ADD CONSTRAINT fkbe4dl5s3i7ga7hqryddog2g0f FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE experimentalconditionontologyterm_aud ADD CONSTRAINT fkkr4o08hq0jboq6g4ou5gmn8xd FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE chemicalterm_aud ADD CONSTRAINT fkieeg5x1a11dqom8dw4valm169 FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE phenotypeterm_aud ADD CONSTRAINT fksap791c8unrey4xnqcydm8kv1 FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE anatomicalterm_aud ADD CONSTRAINT fkan2c886jcsep01s7rqibfghfh FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);

ALTER TABLE atpterm_aud ADD CONSTRAINT fki65mb9a7b4el107fk4ox2f0kd FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE doterm_aud ADD CONSTRAINT fkkgu80ih0f55tskr386gucsqh2 FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE ecoterm_aud ADD CONSTRAINT fkrdtwy8r0gnnh6numgdbgi9e6s FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE goterm_aud ADD CONSTRAINT fk4kjm9hm06yutma1ilq04h967s FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE obiterm_aud ADD CONSTRAINT obiterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE roterm_aud ADD CONSTRAINT fk8wmlph21s6vviddt2tx63fhqn FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE soterm_aud ADD CONSTRAINT fk5i3iqfnxf9hxjq6jmay2gm68g FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE xbedterm_aud ADD CONSTRAINT fkshxj981p427yuk4qgtvcirp5k FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
ALTER TABLE ncbitaxonterm_aud ADD CONSTRAINT fkap27v3trsn5u9q93qb8ikabrf FOREIGN KEY (curie, rev) REFERENCES ontologyterm_aud(curie, rev);
	
ALTER TABLE ontologyterm_aud ADD CONSTRAINT fkdxjp2u3w3xoi7p9j7huceg2ts FOREIGN KEY (rev) REFERENCES revinfo(rev);

