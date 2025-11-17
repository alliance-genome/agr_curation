package org.alliancegenome.curation_api.model.document.builders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.model.document.es.HTPDatasetSearchResultDocument;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetAnnotation;
import org.alliancegenome.curation_api.model.entities.HTPExpressionDatasetSampleAnnotation;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HTPDatasetDocumentBuilder {

	public static HTPDatasetSearchResultDocument buildSearchResultDocument(
			HTPExpressionDatasetAnnotation htpDatasetAnnotation) {
		HTPDatasetSearchResultDocument doc = new HTPDatasetSearchResultDocument();

		List<HTPExpressionDatasetSampleAnnotation> sampleAnnots = htpDatasetAnnotation.getHtpExpressionDataset()
				.getHtpExpressionDatasetSampleAnnotation();

		if (htpDatasetAnnotation.getDataProvider() != null) {
			String dataProvider = htpDatasetAnnotation.getDataProvider().getAbbreviation();
			doc.setDataProvider(dataProvider);
		}

		if (htpDatasetAnnotation.getName() != null) {
			doc.setName(htpDatasetAnnotation.getName());
			doc.setNameKey(htpDatasetAnnotation.getName());
		}

		if (htpDatasetAnnotation.getHtpExpressionDataset() != null) {
			String curie = htpDatasetAnnotation.getHtpExpressionDataset().getCurie();
			doc.setCurie(curie);
		}

		if (htpDatasetAnnotation.getRelatedNote() != null) {
			String summary = htpDatasetAnnotation.getRelatedNote().getFreeText();
			doc.setSummary(summary);
		}

		if (htpDatasetAnnotation.getHtpExpressionDataset() != null) {
			if (htpDatasetAnnotation.getHtpExpressionDataset().getPreferredCrossReference() != null) {
				if (htpDatasetAnnotation.getHtpExpressionDataset().getPreferredCrossReference()
						.getUrlFromResourceDescriptorPage(doc.getCurie()) != null) {
					String identifier;
					if (doc.getDataProvider().equals("ZFIN")) {
						identifier = htpDatasetAnnotation.getHtpExpressionDataset().getPreferredCrossReference()
								.getReferencedCurie();
					} else {
						identifier = doc.getCurie();
					}
					doc.setHref(htpDatasetAnnotation.getHtpExpressionDataset().getPreferredCrossReference()
							.getUrlFromResourceDescriptorPage(identifier));
				}
			}
		}

		if (htpDatasetAnnotation.getCategoryTags() != null) {
			Set<String> tags = new HashSet<>(htpDatasetAnnotation.getCategoryTags()
					.stream().map(term -> term.getName()).collect(Collectors.toList()));

			doc.setTags(tags);
		}

		if (htpDatasetAnnotation.getReferences() != null) {
			Set<String> crossReferences = new HashSet<>();

			crossReferences.addAll(
					htpDatasetAnnotation.getHtpExpressionDataset()
							.getCrossReferences()
							.stream()
							.map(xref -> xref.getReferencedCurie())
							.collect(Collectors.toList()));

			doc.setCrossReferences(crossReferences);
		}

		if (sampleAnnots != null && !sampleAnnots.isEmpty()) {
			Set<String> whereExpressed = new HashSet<>();
			Set<String> anatomicalExpression = new HashSet<>();
			Set<String> anatomicalExpressionWithParents = new HashSet<>();
			Set<String> sampleIds = new HashSet<>();
			Set<String> assays = new HashSet<>();
			Set<String> sex = new HashSet<>();
			String species = new String();

			for (HTPExpressionDatasetSampleAnnotation sampleAnnot : sampleAnnots) {
				species = sampleAnnot.getTaxon().getName();

				if (sampleAnnot.getGeneticSex() != null) {
					sex.add(sampleAnnot.getGeneticSex().getName());
				}

				whereExpressed.addAll(
						sampleAnnot
								.getHtpExpressionSampleLocations()
								.stream()
								.map(site -> {
									return site.getAnatomicalStructure().getName();
								}).collect(Collectors.toList()));

				anatomicalExpression.addAll(
						sampleAnnot
								.getHtpExpressionSampleLocations()
								.stream()
								.flatMap(site -> {
									return site.getAnatomicalStructureUberonTerms().stream().map(term -> term.getName());
								}).collect(Collectors.toList()));

				anatomicalExpressionWithParents.addAll(
						sampleAnnot
								.getHtpExpressionSampleLocations()
								.stream()
								.flatMap(site -> {
									return site.getAnatomicalStructure().getAncestors().stream()
											.map(ancestor -> ancestor.getClosureObject().getName());
								}).collect(Collectors.toList()));

				if (sampleAnnot.getHtpExpressionSample() != null) {
					sampleIds.add(sampleAnnot.getHtpExpressionSample().getCurie());
				}

				assays.addAll(
						sampleAnnot
								.getExpressionAssayUsed()
								.getSynonyms()
								.stream()
								.filter(synonym -> {
									return synonym.getIsDisplaySynonym();
								}).map(synonym -> {
									return synonym.getName();
								}).collect(Collectors.toList()));
			}

			doc.setWhereExpressed(whereExpressed);
			doc.setAnatomicalExpression(anatomicalExpression);
			doc.setAnatomicalExpressionWithParents(anatomicalExpressionWithParents);
			doc.setSampleIds(sampleIds);
			doc.setAssays(assays);
			doc.setSpecies(species);
			doc.setSex(sex);
		}

		return doc;
	}
}
