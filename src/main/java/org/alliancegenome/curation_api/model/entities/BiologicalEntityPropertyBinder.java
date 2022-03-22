package org.alliancegenome.curation_api.model.entities;


import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.engine.backend.types.IndexFieldType;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;


public class BiologicalEntityPropertyBinder implements PropertyBinder {
    
    @Override
    public void bind(PropertyBindingContext context) {
        context.dependencies().use("curie").use("taxon");
        
        IndexSchemaObjectField diseaseGeneticModifierField = context.indexSchemaElement().objectField("diseaseGeneticModifier");
        IndexFieldType<String> stringFieldType = context.typeFactory().asString().toIndexFieldType();
        context.bridge(BiologicalEntity.class, new BiologicalEntityValueBridge(
                diseaseGeneticModifierField.toReference(),
                diseaseGeneticModifierField.field("name", stringFieldType).toReference(),
                diseaseGeneticModifierField.field("symbol", stringFieldType).toReference(),
                diseaseGeneticModifierField.field("taxon", stringFieldType).toReference(),
                diseaseGeneticModifierField.field("curie", stringFieldType).toReference(),
                diseaseGeneticModifierField.field("name_keyword", stringFieldType).toReference(),
                diseaseGeneticModifierField.field("symbol_keyword", stringFieldType).toReference(),
                diseaseGeneticModifierField.field("taxon_keyword", stringFieldType).toReference(),
                diseaseGeneticModifierField.field("curie_keyword", stringFieldType).toReference()
            ));
    }
    
    @SuppressWarnings("rawtypes")
    private static class BiologicalEntityValueBridge implements PropertyBridge<BiologicalEntity> {
        
        private final IndexObjectFieldReference diseaseGeneticModifierField;
        private final IndexFieldReference<String> nameField;
        private final IndexFieldReference<String> symbolField;
        private final IndexFieldReference<String> taxonField;
        private final IndexFieldReference<String> curieField;
        private final IndexFieldReference<String> nameKeywordField;
        private final IndexFieldReference<String> symbolKeywordField;
        private final IndexFieldReference<String> taxonKeywordField;
        private final IndexFieldReference<String> curieKeywordField;
        
        private BiologicalEntityValueBridge(IndexObjectFieldReference diseaseGeneticModifierField,
                IndexFieldReference<String> nameField,
                IndexFieldReference<String> symbolField,
                IndexFieldReference<String> taxonField,
                IndexFieldReference<String> curieField,
                IndexFieldReference<String> nameKeywordField,
                IndexFieldReference<String> symbolKeywordField,
                IndexFieldReference<String> taxonKeywordField,
                IndexFieldReference<String> curieKeywordField) {
            this.diseaseGeneticModifierField = diseaseGeneticModifierField;
            this.nameField = nameField;
            this.symbolField = symbolField;
            this.taxonField = taxonField;
            this.curieField = curieField;
            this.nameKeywordField = nameKeywordField;
            this.symbolKeywordField = symbolKeywordField;
            this.taxonKeywordField = taxonKeywordField;
            this.curieKeywordField = curieKeywordField;
        }
        
        @Override
        public void write(DocumentElement target, BiologicalEntity bridgedElement, PropertyBridgeWriteContext context) {
            @SuppressWarnings("unchecked")
            String symbol;
            String name;
            String curie;
            String taxon;
            
            if (bridgedElement == null) {
                symbol = null;
                taxon = null;
                name = null;
                curie = null;
            } else {
                curie = bridgedElement.getCurie();
                taxon = bridgedElement.getTaxon().getCurie();
            
                if (bridgedElement instanceof Gene) {
                    Gene gene = (Gene)bridgedElement;
                    symbol = gene.getSymbol();
                    name = gene.getName();
                } else if (bridgedElement instanceof Allele) {
                    Allele allele = (Allele)bridgedElement;
                    symbol = allele.getSymbol();
                    name = allele.getName();
                } else if (bridgedElement instanceof AffectedGenomicModel) {
                    AffectedGenomicModel agm = (AffectedGenomicModel)bridgedElement;
                    name = agm.getName();
                    symbol = null;
                } else {
                    name = null;
                    symbol = null;
                }
            }
            DocumentElement diseaseGeneticModifier = target.addObject(this.diseaseGeneticModifierField);
            diseaseGeneticModifier.addValue(this.symbolField, symbol);
            diseaseGeneticModifier.addValue(this.nameField, name);
            diseaseGeneticModifier.addValue(this.taxonField, taxon);
            diseaseGeneticModifier.addValue(this.curieField, curie);
            diseaseGeneticModifier.addValue(this.symbolKeywordField, symbol);
            diseaseGeneticModifier.addValue(this.nameKeywordField, name);
            diseaseGeneticModifier.addValue(this.taxonKeywordField, taxon);
            diseaseGeneticModifier.addValue(this.curieKeywordField, curie);
        }
        
    }
}
