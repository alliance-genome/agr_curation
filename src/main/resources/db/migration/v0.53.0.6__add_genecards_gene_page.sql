-- SCRUM-6455: GeneCards asked to be linked from Alliance gene pages, so that users moving between
-- nomenclature, model-organism data and the aggregated human view can get back to them.
--
-- Registered as a page on the HGNC descriptor rather than as a GeneCards descriptor of its own, so
-- the cross reference's curie (HGNC:1100) and the descriptor it hangs off agree. Pointing a page at
-- a third-party host is the established arrangement: NCBI_Gene owns the biogrid/orcs page that links
-- to biogrid.org, and HGNC's own gene/MODinteractions pages link to RGD.
--
-- Keyed on the HGNC id, not the HGNC symbol. GeneCards support either, but symbols are mutable: a
-- symbol-keyed link rots silently, and after a symbol transfer it resolves to the wrong gene. The
-- HGNC id is already every human gene's primaryExternalId, so nothing has to be loaded for this.
--
-- [%s] is substituted with the local part of the curie, not the whole curie, as in HGNC's own
-- default template (https://bioregistry.io/hgnc:[%s] -> hgnc:1100). So HGNC:1100 resolves to
-- https://www.genecards.org/card/1100.
--
-- Both id-keyed forms resolve, verified against genecards.org: /card/1100 and /card/HGNC:1100. The
-- bare-id form is used below. The prefix-explicit alternative (.../card/HGNC:[%s]) is equally valid
-- and is the convention HGNC's own default template follows (https://bioregistry.io/hgnc:[%s]); it
-- was not chosen only because the bare form is shorter and equally unambiguous here.
--
-- Done as a migration, not through the curation UI, because the page has to exist in alpha, beta and
-- production and a UI-created row propagates to none of them. Safe from being clobbered only because
-- SCRUM-6253 (v0.51.2.2) disabled the nightly AGR Resource Descriptors Load: that load treats
-- agr_schemas/resourceDescriptors.yaml as authoritative and deletes any page absent from it. If it
-- is ever re-enabled, this page must be added to that file or it will be removed.

INSERT INTO resourcedescriptorpage (id, name, urltemplate, resourcedescriptor_id, internal, obsolete, dbdatecreated)
SELECT nextval('resourcedescriptorpage_seq'),
       'gene/genecards',
       'https://www.genecards.org/card/[%s]',
       rd.id,
       false,
       false,
       now()
FROM resourcedescriptor rd
WHERE rd.prefix = 'HGNC'
  AND NOT EXISTS (
        SELECT 1 FROM resourcedescriptorpage p
        WHERE p.resourcedescriptor_id = rd.id
          AND p.name = 'gene/genecards');
