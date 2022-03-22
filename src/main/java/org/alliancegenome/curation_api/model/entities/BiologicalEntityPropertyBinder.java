package org.alliancegenome.curation_api.model.entities;

import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.engine.backend.types.*;
import org.hibernate.search.engine.backend.types.dsl.StringIndexFieldTypeOptionsStep;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;


public class BiologicalEntityPropertyBinder implements PropertyBinder {
    
    @Override
    public void bind(PropertyBindingContext context) {
        context.dependencies().use("curie").use("taxon");
        
        String fieldName = (String) context.param("fieldName");
        
        IndexSchemaObjectField biologicalEntityField = context.indexSchemaElement().objectField(fieldName);
        StringIndexFieldTypeOptionsStep<?> fullTextField = context.typeFactory().asString().analyzer("autocompleteAnalyzer").searchAnalyzer("autocompleteSearchAnalyzer");
        StringIndexFieldTypeOptionsStep<?> keywordField = context.typeFactory().asString().searchable(Searchable.YES).sortable(Sortable.YES).projectable(Projectable.YES).normalizer("sortNormalizer");
        
        context.bridge(BiologicalEntity.class, new BiologicalEntityValueBridge(
                biologicalEntityField.toReference(),
                biologicalEntityField.field("name", fullTextField).toReference(),
                biologicalEntityField.field("symbol", fullTextField).toReference(),
                biologicalEntityField.field("taxon", fullTextField).toReference(),
                biologicalEntityField.field("curie", fullTextField).toReference(),
                biologicalEntityField.field("name_keyword", keywordField).toReference(),
                biologicalEntityField.field("symbol_keyword", keywordField).toReference(),
                biologicalEntityField.field("taxon_keyword", keywordField).toReference(),
                biologicalEntityField.field("curie_keyword", keywordField).toReference()
            ));
    }
    
    @SuppressWarnings("rawtypes")
    private static class BiologicalEntityValueBridge implements PropertyBridge<BiologicalEntity> {
        
        private final IndexObjectFieldReference biologicalEntityField;
        private final IndexFieldReference<String> nameField;
        private final IndexFieldReference<String> symbolField;
        private final IndexFieldReference<String> taxonField;
        private final IndexFieldReference<String> curieField;
        private final IndexFieldReference<String> nameKeywordField;
        private final IndexFieldReference<String> symbolKeywordField;
        private final IndexFieldReference<String> taxonKeywordField;
        private final IndexFieldReference<String> curieKeywordField;
        
        private BiologicalEntityValueBridge(IndexObjectFieldReference biologicalEntityField,
                IndexFieldReference<String> nameField,
                IndexFieldReference<String> symbolField,
                IndexFieldReference<String> taxonField,
                IndexFieldReference<String> curieField,
                IndexFieldReference<String> nameKeywordField,
                IndexFieldReference<String> symbolKeywordField,
                IndexFieldReference<String> taxonKeywordField,
                IndexFieldReference<String> curieKeywordField) {
            this.biologicalEntityField = biologicalEntityField;
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
            DocumentElement biologicalEntity = target.addObject(this.biologicalEntityField);
            biologicalEntity.addValue(this.symbolField, symbol);
            biologicalEntity.addValue(this.nameField, name);
            biologicalEntity.addValue(this.taxonField, taxon);
            biologicalEntity.addValue(this.curieField, curie);
            biologicalEntity.addValue(this.symbolKeywordField, symbol);
            biologicalEntity.addValue(this.nameKeywordField, name);
            biologicalEntity.addValue(this.taxonKeywordField, taxon);
            biologicalEntity.addValue(this.curieKeywordField, curie);
        }
        
    }
}
