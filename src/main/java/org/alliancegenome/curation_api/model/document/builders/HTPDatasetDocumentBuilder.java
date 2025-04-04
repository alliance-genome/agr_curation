package org.alliancegenome.curation_api.model.document.builders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.document.es.HTPDatasetSearchResultDocument;
import org.alliancegenome.curation_api.model.entities.AnatomicalSite;
import org.alliancegenome.curation_api.model.entities.ExternalDataBaseEntity;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;
import org.alliancegenome.curation_api.model.entities.VocabularyTerm;

import com.okta.commons.lang.Collections;

import io.quarkus.logging.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HTPDatasetDocumentBuilder {

	public static HTPDatasetSearchResultDocument buildSearchResultDocument(HTPExpressionDatasetAnnotation htpDataset, List<HTPExpressionDatasetSampleAnnotation> sampleAnnots) {
		HTPDatasetSearchResultDocument doc = new HTPDatasetSearchResultDocument();

		if(htpDataset.getDataProvider() != null){
			String dataProvider = htpDataset.getDataProvider().getAbbreviation();
			doc.setDataProvider(dataProvider);
		}

		if(htpDataset.getName() != null){
			doc.setName(htpDataset.getName());
			doc.setNameKey(htpDataset.getName());
		}

		if(htpDataset.getHtpExpressionDataset() != null){
			String curie = htpDataset.getHtpExpressionDataset().getCurie();
			doc.setCurie(curie);
		}

		if(htpDataset.getRelatedNote() != null){
			String summary = htpDataset.getRelatedNote().getFreeText();
			doc.setSummary(summary);
		}

		//TODO: clean this up
		if(htpDataset.getHtpExpressionDataset() != null){
			if(htpDataset.getHtpExpressionDataset().getPreferredCrossReference() != null){
				if(htpDataset.getHtpExpressionDataset().getPreferredCrossReference().getResourceDescriptorPage() != null){
					if(htpDataset.getHtpExpressionDataset().getPreferredCrossReference().getResourceDescriptorPage().getUrlTemplate() != null){
						String href = htpDataset.getHtpExpressionDataset()
						.getPreferredCrossReference()
						.getUrlFromResourceDescriptorPage(doc.getCurie());
						doc.setHref(href);
					}
				}
			}
		}

		if(htpDataset.getCategoryTags() != null){
			Set<String> tags = new HashSet<>(htpDataset.getCategoryTags()
				.stream().map( term -> term.getName()).collect(Collectors.toList()));

			doc.setTags(tags);
		}

		if(!Collections.isEmpty(sampleAnnots)){
			Set<String> whereExpressed = new HashSet<>();
			Set<String> anatomicalExpression = new HashSet<>();
			Set<String> anatomicalExpressionWithParents = new HashSet<>();
			Set<String> sampleIds = new HashSet<>();

			for(HTPExpressionDatasetSampleAnnotation sampleAnnot : sampleAnnots){

				whereExpressed.addAll(
					sampleAnnot
						.getHtpExpressionSampleLocations()
						.stream()
						.map((site) -> {
							return site.getAnatomicalStructure().getName();
						})
						.collect(Collectors.toList())
				);

				anatomicalExpression.addAll(
					sampleAnnot
						.getHtpExpressionSampleLocations()
						.stream()
						.flatMap((site) -> {
							return site.getAnatomicalStructureUberonTerms().stream().map(term -> term.getName());
						})
						.collect(Collectors.toList())
				);

				anatomicalExpressionWithParents.addAll(
					sampleAnnot
						.getHtpExpressionSampleLocations()
						.stream()
						.flatMap((site) -> {
							return site.getAnatomicalStructure().getIsaAncestors().stream().map(parent -> parent.getName());
						})
						.collect(Collectors.toList())
				);

				sampleIds.add(sampleAnnot.getHtpExpressionSample().getCurie());
			}

			doc.setWhereExpressed(whereExpressed);
			doc.setAnatomicalExpression(anatomicalExpression);
			doc.setAnatomicalExpressionWithParents(anatomicalExpressionWithParents);
			doc.setSampleIds(sampleIds);
		}

		return doc;
	}
}
