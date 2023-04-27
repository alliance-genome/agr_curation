package org.alliancegenome.curation_api.model.bridges;

import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.types.IndexFieldType;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.TypeBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.TypeBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;

public class BiologicalEntityTypeBridge implements TypeBinder {

	@Override
	public void bind(TypeBindingContext context) {
		context.dependencies().use("curie").use("taxon");

		IndexSchemaElement schemaElement = context.indexSchemaElement();
		
		IndexFieldType<String> type = context.typeFactory().asString().analyzer("autocompleteAnalyzer").searchAnalyzer("autocompleteSearchAnalyzer").toIndexFieldType();
		IndexFieldType<String> keywordType = context.typeFactory().asString().searchable(Searchable.YES).sortable(Sortable.YES).projectable(Projectable.YES).normalizer("sortNormalizer").toIndexFieldType();
		
		context.bridge(BiologicalEntity.class, new Bridge(
				schemaElement.field("name", type).toReference(),
				schemaElement.field("symbol", type).toReference(),
				schemaElement.field("taxon", type).toReference(),
				schemaElement.field("curie", type).toReference(),
				schemaElement.field("name_keyword", keywordType).toReference(),
				schemaElement.field("symbol_keyword", keywordType).toReference(),
				schemaElement.field("taxon_keyword", keywordType).toReference(),
				schemaElement.field("curie_keyword", keywordType).toReference()
				));
	}

	@SuppressWarnings("rawtypes")
	private static class Bridge implements TypeBridge<BiologicalEntity> {

		private final IndexFieldReference<String> nameField;
		private final IndexFieldReference<String> symbolField;
		private final IndexFieldReference<String> taxonField;
		private final IndexFieldReference<String> curieField;
		private final IndexFieldReference<String> nameKeywordField;
		private final IndexFieldReference<String> symbolKeywordField;
		private final IndexFieldReference<String> taxonKeywordField;
		private final IndexFieldReference<String> curieKeywordField;
		
		private Bridge(IndexFieldReference<String> nameField, IndexFieldReference<String> symbolField,
			IndexFieldReference<String> taxonField, IndexFieldReference<String> curieField, IndexFieldReference<String> nameKeywordField, IndexFieldReference<String> symbolKeywordField,
			IndexFieldReference<String> taxonKeywordField, IndexFieldReference<String> curieKeywordField) {
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
		public void write(DocumentElement target, BiologicalEntity bridgedElement, TypeBridgeWriteContext context) {
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
					Gene gene = (Gene) bridgedElement;
					symbol = gene.getGeneSymbol() == null ? null : gene.getGeneSymbol().getDisplayText();
					name = gene.getGeneFullName() == null ? null : gene.getGeneFullName().getDisplayText();
				} else if (bridgedElement instanceof Allele) {
					Allele allele = (Allele) bridgedElement;
					symbol = allele.getAlleleSymbol() == null ? null : allele.getAlleleSymbol().getDisplayText();
					name = allele.getAlleleFullName() == null ? null : allele.getAlleleFullName().getDisplayText();
				} else if (bridgedElement instanceof AffectedGenomicModel) {
					AffectedGenomicModel agm = (AffectedGenomicModel) bridgedElement;
					name = agm.getName();
					symbol = null;
				} else {
					name = null;
					symbol = null;
				}
			}
			
			target.addValue(this.symbolField, symbol);
			target.addValue(this.nameField, name);
			target.addValue(this.taxonField, taxon);
			target.addValue(this.curieField, curie);
			target.addValue(this.symbolKeywordField, symbol);
			target.addValue(this.nameKeywordField, name);
			target.addValue(this.taxonKeywordField, taxon);
			target.addValue(this.curieKeywordField, curie);
		}

	}
}
