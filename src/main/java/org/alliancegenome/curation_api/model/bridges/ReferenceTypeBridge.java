package org.alliancegenome.curation_api.model.bridges;

import org.alliancegenome.curation_api.model.entities.Reference;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.types.IndexFieldType;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.TypeBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.TypeBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.TypeBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.TypeBridgeWriteContext;

public class ReferenceTypeBridge implements TypeBinder {

	@Override
	public void bind(TypeBindingContext context) {
		context.dependencies().use("crossReferences");

		IndexSchemaElement schemaElement = context.indexSchemaElement();
		
		IndexFieldType<String> type = context.typeFactory().asString().analyzer("autocompleteAnalyzer").searchAnalyzer("autocompleteSearchAnalyzer").toIndexFieldType();
		IndexFieldType<String> keywordType = context.typeFactory().asString().searchable(Searchable.YES).sortable(Sortable.YES).projectable(Projectable.YES).normalizer("sortNormalizer").toIndexFieldType();
		
		context.bridge(Reference.class, new Bridge(
				schemaElement.field("primaryCrossReferenceCurie", type).toReference(),
				schemaElement.field("primaryCrossReferenceCurie_keyword", keywordType).toReference()
				));
	}

	private static class Bridge implements TypeBridge<Reference> {

		private final IndexFieldReference<String> primaryCrossReferenceCurieField;
		private final IndexFieldReference<String> primaryCrossReferenceCurieKeywordField;
		
		private Bridge(IndexFieldReference<String> primaryCrossReferenceCurieField,
			IndexFieldReference<String> primaryCrossReferenceCurieKeywordField) {
			this.primaryCrossReferenceCurieField = primaryCrossReferenceCurieField;
			this.primaryCrossReferenceCurieKeywordField = primaryCrossReferenceCurieKeywordField;
		}

		@Override
		public void write(DocumentElement target, Reference bridgedElement, TypeBridgeWriteContext context) {
			String primaryCrossReferenceCurie;

			if (bridgedElement == null || CollectionUtils.isEmpty(bridgedElement.getCrossReferences())) {
				primaryCrossReferenceCurie = null;
			} else {
				primaryCrossReferenceCurie = bridgedElement.getReferenceID();
			}
			
			target.addValue(this.primaryCrossReferenceCurieField, primaryCrossReferenceCurie);
			target.addValue(this.primaryCrossReferenceCurieKeywordField, primaryCrossReferenceCurie);
		}

	}
}
