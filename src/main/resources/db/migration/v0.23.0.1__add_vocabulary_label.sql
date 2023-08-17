-- Add vocbulary label columns
ALTER TABLE vocabulary ADD COLUMN vocabularylabel varchar (255) UNIQUE;
ALTER TABLE vocabularytermset ADD COLUMN vocabularylabel varchar (255) UNIQUE;
ALTER TABLE vocabulary_aud ADD COLUMN vocabularylabel varchar (255);
ALTER TABLE vocabularytermset_aud ADD COLUMN vocabularylabel varchar (255);

-- Copy values from name column
UPDATE vocabulary SET vocabularylabel = name;
UPDATE vocabularytermset SET vocabularylabel = name;

-- Apply NOT NULL constraint
ALTER TABLE vocabulary ALTER COLUMN vocabularylabel SET NOT NULL;
ALTER TABLE vocabularytermset ALTER COLUMN vocabularylabel SET NOT NULL;

-- Change values of labels
UPDATE vocabulary SET vocabularylabel = 'agm_subtype' WHERE name = 'Affected genomic model subtypes' OR name = 'Affected Genomic Model Subtype';
UPDATE vocabulary SET vocabularylabel = 'agr_da_eco_term' WHERE name = 'AGR disease annotation ECO terms' OR name = 'Disease Annotation ECO Term';
UPDATE vocabulary SET vocabularylabel = 'allele_collection' WHERE name = 'Allele collection vocabulary' OR name = 'Allele Collection';
UPDATE vocabulary SET vocabularylabel = 'allele_construct_predicate' WHERE name = 'Allele Construct Association Predicate';
UPDATE vocabulary SET vocabularylabel = 'allele_db_status' WHERE name = 'Allele database status vocabulary' OR name = 'Allele Database Status';
UPDATE vocabulary SET vocabularylabel = 'allele_functional_impact' WHERE name = 'Allele Functional Impact';
UPDATE vocabulary SET vocabularylabel = 'allele_generation_method_predicate' WHERE name = 'Allele Generation Method Association Predicate';
UPDATE vocabulary SET vocabularylabel = 'allele_germline_transmission_status' WHERE name = 'Allele Germline Transmission Status';
UPDATE vocabulary SET vocabularylabel = 'allele_inheritance_mode' WHERE name = 'Allele inheritance mode vocabulary' OR name = 'Allele Inheritance Mode';
UPDATE vocabulary SET vocabularylabel = 'allele_note_type' WHERE name = 'Allele note types' OR name = 'Allele Related Note Type';
UPDATE vocabulary SET vocabularylabel = 'allele_allele_predicate' WHERE name = 'Allele-Allele Association Predicates' OR name = 'Allele Allele Association Predicate';
UPDATE vocabulary SET vocabularylabel = 'annotation_type' WHERE name = 'Annotation types' OR name = 'Annotation Type';
UPDATE vocabulary SET vocabularylabel = 'condition_relation' WHERE name = 'Condition relation types' OR name = 'Condition Relation';
UPDATE vocabulary SET vocabularylabel = 'construct_genomic_entity_predicate' WHERE name = 'Construct Genomic Entity Association Predicate';
UPDATE vocabulary SET vocabularylabel = 'da_note_type' WHERE name = 'Disease annotation note types' OR name = 'Disease Annotation Related Note Type';
UPDATE vocabulary SET vocabularylabel = 'disease_genetic_modifier_relation' WHERE name = 'Disease genetic modifier relations' OR name = 'Disease Genetic Modifier Relation';
UPDATE vocabulary SET vocabularylabel = 'disease_qualifier' WHERE name = 'Disease qualifiers' OR name = 'Disease Qualifier';
UPDATE vocabulary SET vocabularylabel = 'disease_relation' WHERE name = 'Disease Relation Vocabulary' OR name = 'Disease Relation';
UPDATE vocabulary SET vocabularylabel = 'gene_note_type' WHERE name = 'Gene note types' OR name = 'Gene Related Note Type';
UPDATE vocabulary SET vocabularylabel = 'genetic_sex' WHERE name = 'Genetic sexes' OR name = 'Genetic Sex';
UPDATE vocabulary SET vocabularylabel = 'name_type' WHERE name = 'Name type' OR name = 'Name Type';
UPDATE vocabulary SET vocabularylabel = 'nomenclature_event' WHERE name = 'Nomenclature Events' OR name = 'Nomenclature Event';
UPDATE vocabulary SET vocabularylabel = 'ortho_best_score' WHERE name = 'Orthology best score';
UPDATE vocabulary SET vocabularylabel = 'ortho_confidence' WHERE name = 'Orthology confidence';
UPDATE vocabulary SET vocabularylabel = 'ortho_prediction_method' WHERE name = 'Orthology prediction methods';
UPDATE vocabulary SET vocabularylabel = 'synonym_scope' WHERE name = 'Synonym scope' OR name = 'Synonym Scope';
UPDATE vocabulary SET vocabularylabel = 'wb_transgene_integration_method' WHERE name = 'WB Transgene Integration Method';
UPDATE vocabularytermset SET vocabularylabel = 'agm_disease_relation' WHERE name = 'AGM disease relations';
UPDATE vocabularytermset SET vocabularylabel = 'allele_disease_relation' WHERE name = 'Allele disease relations';
UPDATE vocabularytermset SET vocabularylabel = 'full_name_type' WHERE name = 'Full name types';
UPDATE vocabularytermset SET vocabularylabel = 'gene_disease_relation' WHERE name = 'Gene disease relations';
UPDATE vocabularytermset SET vocabularylabel = 'ortho_best_rev_score' WHERE name = 'Orthology best reverse score';
UPDATE vocabularytermset SET vocabularylabel = 'symbol_name_type' WHERE name = 'Symbol name types';
UPDATE vocabularytermset SET vocabularylabel = 'systematic_name_type' WHERE name = 'Systematic name types';