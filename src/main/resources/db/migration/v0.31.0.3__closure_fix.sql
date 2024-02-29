ALTER TABLE OntologyTerm ADD COLUMN OntologyTermType VARCHAR(64);

UPDATE OntologyTerm o SET OntologyTermType = 'ECOTerm' FROM ECOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'SOTerm' FROM SOTerm t WHERE o.id = t.id;

UPDATE OntologyTerm o SET OntologyTermType = 'XSMOTerm' FROM XSMOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'CHEBITerm' FROM CHEBITerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'Molecule' FROM Molecule t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'ZECOTerm' FROM ZECOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'XCOTerm' FROM XCOTerm t WHERE o.id = t.id;

UPDATE OntologyTerm o SET OntologyTermType = 'DOTerm' FROM DOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'GOTerm' FROM GOTerm t WHERE o.id = t.id;

UPDATE OntologyTerm o SET OntologyTermType = 'XBSTerm' FROM XBSTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'ZFSTerm' FROM ZFSTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'WBPhenotypeTerm' FROM WBPhenotypeTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'WBLSTerm' FROM WBLSTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'MMUSDVTerm' FROM MMUSDVTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'FBDVTerm' FROM FBDVTerm t WHERE o.id = t.id;

UPDATE OntologyTerm o SET OntologyTermType = 'APOTerm' FROM APOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'DPOTerm' FROM DPOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'HPTerm' FROM HPTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'MPTerm' FROM MPTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'XPOTerm' FROM XPOTerm t WHERE o.id = t.id;

UPDATE OntologyTerm o SET OntologyTermType = 'CLTerm' FROM CLTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'DAOTerm' FROM DAOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'EMAPATerm' FROM EMAPATerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'MATerm' FROM MATerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'UBERONTerm' FROM UBERONTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'WBBTTerm' FROM WBBTTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'XBATerm' FROM XBATerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'ZFATerm' FROM ZFATerm t WHERE o.id = t.id;

UPDATE OntologyTerm o SET OntologyTermType = 'ATPTerm' FROM ATPTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'BSPOTerm' FROM BSPOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'CMOTerm' FROM CMOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'GENOTerm' FROM GENOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'MITerm' FROM MITerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'MMOTerm' FROM MMOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'MODTerm' FROM MODTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'MPATHTerm' FROM MPATHTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'OBITerm' FROM OBITerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'PATOTerm' FROM PATOTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'PWTerm' FROM PWTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'ROTerm' FROM ROTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'RSTerm' FROM RSTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'VTTerm' FROM VTTerm t WHERE o.id = t.id;
UPDATE OntologyTerm o SET OntologyTermType = 'XBEDTerm' FROM XBEDTerm t WHERE o.id = t.id;

UPDATE OntologyTerm o SET OntologyTermType = 'NCBITaxonTerm' FROM NCBITaxonTerm t WHERE o.id = t.id;

UPDATE OntologyTerm SET OntologyTermType = 'MPTerm' WHERE namespace = 'MPheno.ontology' and OntologyTermType is null;

CREATE INDEX ontologyterm_ontologytermtype_index ON OntologyTerm USING btree (OntologyTermType);

-- Copy Data from old tables
ALTER TABLE OntologyTerm ADD COLUMN abbreviation varchar(255);
UPDATE OntologyTerm o SET abbreviation = e.abbreviation FROM ECOTerm e WHERE o.id = e.id;

ALTER TABLE OntologyTerm ADD COLUMN formula varchar(255);
ALTER TABLE OntologyTerm ADD COLUMN inchi varchar(750);
ALTER TABLE OntologyTerm ADD COLUMN inchiKey varchar(255);
ALTER TABLE OntologyTerm ADD COLUMN iupac varchar(500);
ALTER TABLE OntologyTerm ADD COLUMN smiles varchar(500);
UPDATE OntologyTerm o SET inchi = ch.inchi, inchikey = ch.inchikey, iupac = ch.iupac, formula = ch.formula, smiles = ch.smiles FROM ChemicalTerm ch WHERE o.id = ch.id;


-- Remove FK's to term tables

--Drop ECOTerm
ALTER TABLE diseaseAnnotation_ecoterm RENAME TO diseaseannotation_ontologyterm;
ALTER TABLE diseaseannotation_ontologyterm DROP CONSTRAINT diseaseannotation_ecoterm_evidencecodes_id_fk;
ALTER TABLE diseaseannotation_ontologyterm ADD CONSTRAINT diseaseannotation_ecoterm_evidencecodes_id_fk FOREIGN KEY (evidencecodes_id) REFERENCES OntologyTerm (id);
ALTER TABLE allelegenomicentityassociation DROP CONSTRAINT allelegenomicentityassociation_evidencecode_id_fk;
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_evidencecode_id_fk FOREIGN KEY (evidencecode_id) REFERENCES OntologyTerm (id);
ALTER TABLE genetogeneorthologycurated DROP CONSTRAINT genetogeneorthologycurated_evidencecode_id_fk;
ALTER TABLE genetogeneorthologycurated ADD CONSTRAINT genetogeneorthologycurated_evidencecode_id_fk FOREIGN KEY (evidencecode_id) REFERENCES OntologyTerm (id);
DROP TABLE ECOTerm;

--Drop SOTerm
ALTER TABLE allelemutationtypeslotannotation_soterm RENAME TO allelemutationtypeslotannotation_ontologyterm;
ALTER TABLE allelemutationtypeslotannotation_ontologyterm DROP CONSTRAINT allelemutationtypesa_soterm_mutationtypes_id_fk;
ALTER TABLE allelemutationtypeslotannotation_ontologyterm ADD CONSTRAINT allelemutationtypesa_soterm_mutationtypes_id_fk FOREIGN KEY (mutationtypes_id) REFERENCES OntologyTerm (id);
ALTER TABLE gene DROP CONSTRAINT gene_genetype_id_fk;
ALTER TABLE gene ADD CONSTRAINT gene_genetype_id_fk FOREIGN KEY (genetype_id) REFERENCES OntologyTerm (id);
ALTER TABLE variant DROP CONSTRAINT variant_sourcegeneralconsequence_id_fk;
ALTER TABLE variant ADD CONSTRAINT variant_sourcegeneralconsequence_id_fk FOREIGN KEY (sourcegeneralconsequence_id) REFERENCES OntologyTerm (id);
ALTER TABLE variant DROP CONSTRAINT variant_varianttype_id_fk;
ALTER TABLE variant ADD CONSTRAINT variant_varianttype_id_fk FOREIGN KEY (varianttype_id) REFERENCES OntologyTerm (id);
DROP TABLE SOTerm;

--Drop NCBITaxonTerm
ALTER TABLE biologicalentity DROP CONSTRAINT biologicalentity_taxon_id_fk;
ALTER TABLE biologicalentity ADD CONSTRAINT biologicalentity_taxon_id_fk FOREIGN KEY (taxon_id) REFERENCES OntologyTerm (id);
ALTER TABLE constructcomponentslotannotation DROP CONSTRAINT constructcomponentslotannotation_taxon_id_fk;
ALTER TABLE constructcomponentslotannotation ADD CONSTRAINT constructcomponentslotannotation_taxon_id_fk FOREIGN KEY (taxon_id) REFERENCES ncbitaxonterm (id);
ALTER TABLE experimentalcondition DROP CONSTRAINT experimentalcondition_conditiontaxon_id_fk;
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditiontaxon_id_fk FOREIGN KEY (conditiontaxon_id) REFERENCES OntologyTerm (id);
ALTER TABLE species DROP CONSTRAINT species_taxon_id_fk;
ALTER TABLE species ADD CONSTRAINT species_taxon_id_fk FOREIGN KEY (taxon_id) REFERENCES OntologyTerm (id);
ALTER TABLE constructcomponentslotannotation DROP CONSTRAINT constructcomponentslotannotation_taxon_id_fk;
ALTER TABLE constructcomponentslotannotation ADD CONSTRAINT constructcomponentslotannotation_taxon_id_fk FOREIGN KEY (taxon_id) REFERENCES ncbitaxonterm (id);
ALTER TABLE constructcomponentslotannotation DROP CONSTRAINT constructcomponentslotannotation_taxon_id_fk;
ALTER TABLE constructcomponentslotannotation ADD CONSTRAINT constructcomponentslotannotation_taxon_id_fk FOREIGN KEY (taxon_id) REFERENCES OntologyTerm (id);
DROP TABLE NCBITaxonTerm;


--Drop ChemicalTerm
ALTER TABLE experimentalcondition DROP CONSTRAINT experimentalcondition_conditionchemical_id_fk;
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditionchemical_id_fk FOREIGN KEY (conditionchemical_id) REFERENCES OntologyTerm (id);
DROP TABLE XSMOTerm;
DROP TABLE CHEBITerm;
DROP TABLE Molecule;
DROP TABLE ChemicalTerm;

--Drop ExperimentalConditionOntologyTerm
ALTER TABLE experimentalcondition DROP CONSTRAINT experimentalcondition_conditionclass_id_fk;
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditionclass_id_fk FOREIGN KEY (conditionclass_id) REFERENCES OntologyTerm (id);
DROP TABLE ZECOTerm;

DROP TABLE XCOTerm;

ALTER TABLE experimentalcondition DROP CONSTRAINT experimentalcondition_conditionid_id_fk;
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditionid_id_fk FOREIGN KEY (conditionid_id) REFERENCES OntologyTerm (id);
DROP TABLE ExperimentalConditionOntologyTerm;


--Drop StageTerms
DROP TABLE XBSTerm;
DROP TABLE ZFSTerm;
DROP TABLE WBLSTerm;
DROP TABLE MMUSDVTerm;
DROP TABLE FBDVTerm;
DROP TABLE StageTerm;


--Drop PhenotypeTerm
DROP TABLE APOTerm;
DROP TABLE DPOTerm;
DROP TABLE HPTerm;
DROP TABLE MPTerm;
DROP TABLE WBPhenotypeTerm;
DROP TABLE XPOTerm;

ALTER TABLE allelefunctionalimpactslotannotation DROP CONSTRAINT allelefunctionalimpactslotannotation_phenotypeterm_id_fk;
ALTER TABLE allelefunctionalimpactslotannotation ADD CONSTRAINT allelefunctionalimpactslotannotation_phenotypeterm_id_fk FOREIGN KEY (phenotypeterm_id) REFERENCES OntologyTerm (id);
ALTER TABLE alleleinheritancemodeslotannotation DROP CONSTRAINT alleleinheritancemode_phenotypeterm_id_fk;
ALTER TABLE alleleinheritancemodeslotannotation ADD CONSTRAINT alleleinheritancemode_phenotypeterm_id_fk FOREIGN KEY (phenotypeterm_id) REFERENCES OntologyTerm (id);
DROP TABLE PhenotypeTerm;


--Drop AnatomicalTerm
DROP TABLE CLTerm;
DROP TABLE DAOTerm;
DROP TABLE EMAPATerm;
DROP TABLE MATerm;
DROP TABLE UBERONTerm;
DROP TABLE WBBTTerm;
DROP TABLE XBATerm;
DROP TABLE ZFATerm;

ALTER TABLE experimentalcondition DROP CONSTRAINT experimentalcondition_conditionanatomy_id_fk;
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditionanatomy_id_fk FOREIGN KEY (conditionanatomy_id) REFERENCES OntologyTerm (id);
DROP TABLE AnatomicalTerm;


--Drop All Other OntologyTerm tables
DROP TABLE ATPTerm;
DROP TABLE BSPOTerm;
DROP TABLE CMOTerm;
DROP TABLE GENOTerm;
DROP TABLE MITerm;
DROP TABLE MMOTerm;
DROP TABLE MODTerm;
DROP TABLE MPATHTerm;
DROP TABLE OBITerm;
DROP TABLE PATOTerm;
DROP TABLE PWTerm;
DROP TABLE ROTerm;
DROP TABLE RSTerm;
DROP TABLE VTTerm;
DROP TABLE XBEDTerm;

ALTER TABLE diseaseannotation DROP CONSTRAINT diseaseannotation_diseaseannotationobject_id_fk;
ALTER TABLE diseaseannotation ADD CONSTRAINT diseaseannotation_diseaseannotationobject_id_fk FOREIGN KEY (diseaseannotationobject_id) REFERENCES OntologyTerm (id);
DROP TABLE DOTerm;

ALTER TABLE experimentalcondition DROP CONSTRAINT experimentalcondition_conditiongeneontology_id_fk;
ALTER TABLE experimentalcondition ADD CONSTRAINT experimentalcondition_conditiongeneontology_id_fk FOREIGN KEY (conditiongeneontology_id) REFERENCES OntologyTerm (id);
DROP TABLE GOTerm;
