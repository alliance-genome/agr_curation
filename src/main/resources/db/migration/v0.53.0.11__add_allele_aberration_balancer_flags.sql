-- SCRUM-6561: bring the Java Allele model up to LinkML 2.18.0.
--
-- 2.18.0 adds is_aberration and is_balancer, needed for FlyBase alleles. Two older boolean slots,
-- is_extrachromosomal and is_integrated, were also never implemented on our side, so all four are
-- added together: of the five is_* slots allele.yaml defines for Allele, only is_extinct existed.
--
-- Nullable, with no default. All four are required: false in LinkML, and a three-state
-- true/false/unknown is meaningful here — an allele we have never assessed is not the same as one
-- assessed and found negative. That matches the existing isextinct column and the
-- BooleanAndNullValueBridge the entity uses for indexing.
--
-- Note on is_balancer: LinkML documents balancers as a subtype of aberration, so it is only
-- applicable where is_aberration is true. The schema states that in prose rather than as a rule,
-- so no constraint is added here and none is enforced in the validators.

ALTER TABLE allele ADD COLUMN isextrachromosomal boolean;
ALTER TABLE allele ADD COLUMN isintegrated       boolean;
ALTER TABLE allele ADD COLUMN isaberration       boolean;
ALTER TABLE allele ADD COLUMN isbalancer         boolean;
