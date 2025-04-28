package org.alliancegenome.curation_api.model.bridges;

import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.entities.AffectedGenomicModel;
import org.alliancegenome.curation_api.model.entities.Allele;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.alliancegenome.curation_api.model.entities.BiologicalEntity;
import org.alliancegenome.curation_api.model.entities.CodingSequence;
import org.alliancegenome.curation_api.model.entities.Exon;
import org.alliancegenome.curation_api.model.entities.Gene;
import org.alliancegenome.curation_api.model.entities.SequenceTargetingReagent;
import org.alliancegenome.curation_api.model.entities.Transcript;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AgmSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.AlleleSynonymSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSecondaryIdSlotAnnotation;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.GeneSynonymSlotAnnotation;
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

public class BiologicalEntityTypeBridge implements TypeBinder {

	@Override
	public void bind(TypeBindingContext context) {
		
		context.dependencies().useRootOnly();
		
		IndexSchemaElement schemaElement = context.indexSchemaElement();
		
		IndexFieldType<String> type = context.typeFactory().asString().analyzer("autocompleteAnalyzer").searchAnalyzer("autocompleteSearchAnalyzer").toIndexFieldType();
		IndexFieldType<String> keywordType = context.typeFactory().asString().searchable(Searchable.YES).sortable(Sortable.YES).projectable(Projectable.YES).normalizer("sortNormalizer").toIndexFieldType();
		
		context.bridge(BiologicalEntity.class, new Bridge(
				schemaElement.field("name", type).toReference(),
				schemaElement.field("symbol", type).toReference(),
				schemaElement.field("synonyms", type).multiValued().toReference(),
				schemaElement.field("secondaryIds", type).multiValued().toReference(),
				schemaElement.field("name_keyword", keywordType).toReference(),
				schemaElement.field("symbol_keyword", keywordType).toReference(),
				schemaElement.field("synonyms_keyword", keywordType).multiValued().toReference(),
				schemaElement.field("secondaryIds_keyword", keywordType).multiValued().toReference()
				));
	}

	private static class Bridge implements TypeBridge<BiologicalEntity> {

		private final IndexFieldReference<String> nameField;
		private final IndexFieldReference<String> symbolField;
		private final IndexFieldReference<String> synonymsField;
		private final IndexFieldReference<String> secondaryIdsField;
		private final IndexFieldReference<String> nameKeywordField;
		private final IndexFieldReference<String> symbolKeywordField;
		private final IndexFieldReference<String> synonymsKeywordField;
		private final IndexFieldReference<String> secondaryIdsKeywordField;
		
		private Bridge(IndexFieldReference<String> nameField,
				IndexFieldReference<String> symbolField,
				IndexFieldReference<String> synonymsField,
				IndexFieldReference<String> secondaryIdsField,
				IndexFieldReference<String> nameKeywordField,
				IndexFieldReference<String> symbolKeywordField,
				IndexFieldReference<String> synonymsKeywordField,
				IndexFieldReference<String> secondaryIdsKeywordField
			) {
			this.nameField = nameField;
			this.symbolField = symbolField;
			this.synonymsField = synonymsField;
			this.secondaryIdsField = secondaryIdsField;
			this.nameKeywordField = nameKeywordField;
			this.symbolKeywordField = symbolKeywordField;
			this.synonymsKeywordField = synonymsKeywordField;
			this.secondaryIdsKeywordField = secondaryIdsKeywordField;
		}

		@Override
		public void write(DocumentElement target, BiologicalEntity bridgedElement, TypeBridgeWriteContext context) {
			String symbol = null;
			String name = null;
			List<String> synonyms = null;
			List<String> secondaryIds = null;

			if (bridgedElement != null) {
				if (bridgedElement instanceof Gene) {
					Gene gene = (Gene) bridgedElement;
					symbol = gene.getGeneSymbol() == null ? null : gene.getGeneSymbol().getFormatText();
					name = gene.getGeneFullName() == null ? null : gene.getGeneFullName().getFormatText();
					if (CollectionUtils.isNotEmpty(gene.getGeneSynonyms())) {
						synonyms = gene.getGeneSynonyms().stream().map(GeneSynonymSlotAnnotation::getFormatText).collect(Collectors.toList());
					}
					if (CollectionUtils.isNotEmpty(gene.getGeneSecondaryIds())) {
						secondaryIds = gene.getGeneSecondaryIds().stream().map(GeneSecondaryIdSlotAnnotation::getSecondaryId).collect(Collectors.toList());
					}
				} else if (bridgedElement instanceof Allele) {
					Allele allele = (Allele) bridgedElement;
					symbol = allele.getAlleleSymbol() == null ? null : allele.getAlleleSymbol().getFormatText();
					name = allele.getAlleleFullName() == null ? null : allele.getAlleleFullName().getFormatText();
					if (CollectionUtils.isNotEmpty(allele.getAlleleSynonyms())) {
						synonyms = allele.getAlleleSynonyms().stream().map(AlleleSynonymSlotAnnotation::getFormatText).collect(Collectors.toList());
					}
					if (CollectionUtils.isNotEmpty(allele.getAlleleSecondaryIds())) {
						secondaryIds = allele.getAlleleSecondaryIds().stream().map(AlleleSecondaryIdSlotAnnotation::getSecondaryId).collect(Collectors.toList());
					}
				} else if (bridgedElement instanceof AffectedGenomicModel) {
					AffectedGenomicModel agm = (AffectedGenomicModel) bridgedElement;
					name = agm.getAgmFullName() == null ? null : agm.getAgmFullName().getFormatText();
					if (CollectionUtils.isNotEmpty(agm.getAgmSynonyms())) {
						synonyms = agm.getAgmSynonyms().stream().map(AgmSynonymSlotAnnotation::getFormatText).collect(Collectors.toList());
					}
					if (CollectionUtils.isNotEmpty(agm.getAgmSecondaryIds())) {
						secondaryIds = agm.getAgmSecondaryIds().stream().map(AgmSecondaryIdSlotAnnotation::getSecondaryId).collect(Collectors.toList());
					}
				} else if (bridgedElement instanceof SequenceTargetingReagent) {
					SequenceTargetingReagent sqtr = (SequenceTargetingReagent) bridgedElement;
					name = sqtr.getName();
					synonyms = sqtr.getSynonyms();
				} else if (bridgedElement instanceof Transcript) {
					Transcript transcript = (Transcript) bridgedElement;
					name = transcript.getName();
				} else if (bridgedElement instanceof Exon) {
					Exon exon = (Exon) bridgedElement;
					name = exon.getName();
				} else if (bridgedElement instanceof CodingSequence) {
					CodingSequence cds = (CodingSequence) bridgedElement;
					name = cds.getName();
				} else if (bridgedElement instanceof AssemblyComponent) {
					AssemblyComponent ac = (AssemblyComponent) bridgedElement;
					name = ac.getName();
				} else if (bridgedElement instanceof Variant) {
					Variant variant = (Variant) bridgedElement;
					synonyms = variant.getSynonyms();
				}
			}
			
			target.addValue(this.nameField, name);
			target.addValue(this.nameKeywordField, name);
			target.addValue(this.symbolKeywordField, symbol);
			target.addValue(this.symbolField, symbol);
			if (CollectionUtils.isNotEmpty(synonyms)) {
				for (String synonym : synonyms) {
					target.addValue(this.synonymsField, synonym);
					target.addValue(this.synonymsKeywordField, synonym);
				}
			}
			if (CollectionUtils.isNotEmpty(secondaryIds)) {
				for (String secondaryId : secondaryIds) {
					target.addValue(this.secondaryIdsField, secondaryId);
					target.addValue(this.secondaryIdsKeywordField, secondaryId);
				}
			}
		}

	}
}
