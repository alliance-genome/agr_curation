ALTER TABLE CodingSequenceGenomicLocationAssociation RENAME TO CDSGLAssociation;
ALTER TABLE CodingSequenceGenomicLocationAssociation_InformationContentEntity RENAME TO CDSGLAssociation_InformationContentEntity;
ALTER TABLE SequenceTargetingReagentGeneAssociation RENAME TO SQTRGeneAssociation;
ALTER TABLE SequenceTargetingReagentGeneAssociation_InformationContentEntity RENAME TO SQTRGeneAssociation_InformationContentEntity;

ALTER SEQUENCE codingsequencegenomiclocationassociation_seq RENAME TO cdsglassociation_seq;
ALTER SEQUENCE sequencetargetingreagentgeneassociation_seq RENAME TO sqtrgeneassociation_seq;
