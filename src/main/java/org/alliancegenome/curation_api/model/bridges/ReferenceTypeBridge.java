package org.alliancegenome.curation_api.model.bridges;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.CrossReference;
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
		context.dependencies().useRootOnly();

		IndexSchemaElement schemaElement = context.indexSchemaElement();
		
		IndexFieldType<String> type = context.typeFactory().asString().analyzer("autocompleteAnalyzer").searchAnalyzer("autocompleteSearchAnalyzer").toIndexFieldType();
		IndexFieldType<String> keywordType = context.typeFactory().asString().searchable(Searchable.YES).sortable(Sortable.YES).projectable(Projectable.YES).normalizer("sortNormalizer").toIndexFieldType();
		
		context.bridge(Reference.class, new Bridge(
				schemaElement.field("primaryCrossReferenceCurie", type).toReference(),
				schemaElement.field("crossReferenceCuries", type).multiValued().toReference(),
				schemaElement.field("primaryCrossReferenceCurie_keyword", keywordType).toReference(),
				schemaElement.field("crossReferenceCuries_keyword", keywordType).multiValued().toReference()
				));
	}

	private static class Bridge implements TypeBridge<Reference> {

		private final IndexFieldReference<String> primaryCrossReferenceCurieField;
		private final IndexFieldReference<String> crossReferenceCuriesField;
		private final IndexFieldReference<String> primaryCrossReferenceCurieKeywordField;
		private final IndexFieldReference<String> crossReferenceCuriesKeywordField;
		
		private Bridge(IndexFieldReference<String> primaryCrossReferenceCurieField,
			IndexFieldReference<String> crossReferenceCuriesField,
			IndexFieldReference<String> primaryCrossReferenceCurieKeywordField,
			IndexFieldReference<String> crossReferenceCuriesKeywordField) {
			this.primaryCrossReferenceCurieField = primaryCrossReferenceCurieField;
			this.crossReferenceCuriesField = crossReferenceCuriesField;
			this.primaryCrossReferenceCurieKeywordField = primaryCrossReferenceCurieKeywordField;
			this.crossReferenceCuriesKeywordField = crossReferenceCuriesKeywordField;
		}

		@Override
		public void write(DocumentElement target, Reference bridgedElement, TypeBridgeWriteContext context) {
			String primaryCrossReferenceCurie = null;
			List<String> crossReferenceCuries = null;

			if (bridgedElement != null) {
				Collection<CrossReference> xrefs = bridgedElement.getCrossReferences();
				if (CollectionUtils.isNotEmpty(xrefs)) {
					primaryCrossReferenceCurie = bridgedElement.getReferenceID();
					crossReferenceCuries = xrefs.stream().map(CrossReference::getReferencedCurie).collect(Collectors.toList());
				}
			}
			
			target.addValue(this.primaryCrossReferenceCurieField, primaryCrossReferenceCurie);
			target.addValue(this.primaryCrossReferenceCurieKeywordField, primaryCrossReferenceCurie);
			if (CollectionUtils.isNotEmpty(crossReferenceCuries)) {
				for (String crossReferenceCurie : crossReferenceCuries) {
					target.addValue(this.crossReferenceCuriesField, crossReferenceCurie);
					target.addValue(this.crossReferenceCuriesKeywordField, crossReferenceCurie);
				}
			}
		}

	}
}
