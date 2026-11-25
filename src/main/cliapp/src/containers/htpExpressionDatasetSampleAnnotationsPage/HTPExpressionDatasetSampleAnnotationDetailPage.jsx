import { useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { ProgressSpinner } from 'primereact/progressspinner';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import ErrorBoundary from '../../components/Error/ErrorBoundary';
import { StickyHeader } from '../../components/StickyHeader';
import { DetailPageHeading } from '../../components/DetailPageHeading';
import { DetailPageFieldWrapper } from '../../components/DetailPageFieldWrapper';
import { FormTableWrapper } from '../../components/FormTableWrapper';
import { FormFieldVisibilityMenu, FormSection, useFormFieldVisibility } from '../../components/FormFieldVisibility';
import { StringTemplate } from '../../components/Templates/StringTemplate';
import { StringListTemplate } from '../../components/Templates/StringListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { ExternalDataBaseEntityTable } from '../htpExpressionDatasetAnnotationsPage/ExternalDataBaseEntityTable';
import { RelatedNotesTable } from '../htpExpressionDatasetAnnotationsPage/RelatedNotesTable';
import { BioSampleAgeTable } from './BioSampleAgeTable';
import { BioSampleGenomicInformationTable } from './BioSampleGenomicInformationTable';
import { MicroarraySampleDetailsTable } from './MicroarraySampleDetailsTable';
import { HTPExpressionDatasetSampleAnnotationService } from '../../service/HTPExpressionDatasetSampleAnnotationService';
import { WIDE_DETAIL_FIELD_LAYOUT } from '../../components/Editors/fields/fieldLayouts';

export const HTP_EXPRESSION_DATASET_SAMPLE_DETAIL_TOGGLEABLE_FIELDS = [
	'Expression Sample',
	'Sample Curie',
	'Sample Title',
	'Sample Taxon',
	'Sample Type',
	'Sample Age',
	'Sample Locations',
	'Biological Reference',
	'Sample Assembly Versions',
	'Sample Abundance',
	'Sample Assay Used',
	'Sample Genomic Information',
	'Sample Genetic Sex',
	'Microarray Sample Details',
	'Sample Sequencing Format',
	'Sample Related Notes',
	'Sample Data Provider',
	'Sample Updated By',
	'Sample Date Updated',
	'Sample Created By',
	'Sample Date Created',
	'Sample Internal',
	'Sample Obsolete',
];

const locationText = (site) => {
	const structureName = site?.anatomicalStructure?.name;
	const substructureName = site?.anatomicalSubstructure?.name;
	if (!structureName) return substructureName;
	return substructureName ? `${structureName} (${substructureName})` : structureName;
};

/**
 * Read-only detail page for a single HTP Expression Dataset Sample Annotation (SCRUM-6534).
 */
export default function HTPExpressionDatasetSampleAnnotationDetailPage() {
	const { identifier } = useParams();
	const htpExpressionDatasetSampleAnnotationService = useMemo(
		() => new HTPExpressionDatasetSampleAnnotationService(),
		[]
	);
	const { visibleFields, setVisibleFields, showAllFields, isVisible } = useFormFieldVisibility(
		'HTPExpressionDatasetSampleDetail',
		HTP_EXPRESSION_DATASET_SAMPLE_DETAIL_TOGGLEABLE_FIELDS
	);

	const { isPending, data: queryData } = useQuery({
		queryKey: [identifier],
		queryFn: () => htpExpressionDatasetSampleAnnotationService.getHTPExpressionDatasetSampleAnnotation(identifier),
		placeholderData: (previousData) => previousData,
		refetchOnWindowFocus: false,
	});

	const sample = queryData?.data?.entity;

	if (isPending)
		return (
			<div className="flex align-items-center justify-content-center h-screen">
				<ProgressSpinner />
			</div>
		);

	return (
		<>
			<ErrorBoundary>
				<StickyHeader>
					<Splitter className="bg-primary-reverse border-none lg:h-5rem" gutterSize={0}>
						<SplitterPanel size={70} className="flex justify-content-start min-w-0 ml-5 py-3 ">
							<DetailPageHeading
								entityType="HTP Expression Dataset Sample Annotation"
								entity={sample}
								label={sample?.htpExpressionSampleTitle}
								identifier={sample?.htpExpressionSample?.curie}
							/>
						</SplitterPanel>
						<SplitterPanel size={30} className="flex align-items-center justify-content-end gap-2 py-3">
							<FormFieldVisibilityMenu
								toggleableFields={HTP_EXPRESSION_DATASET_SAMPLE_DETAIL_TOGGLEABLE_FIELDS}
								visibleFields={visibleFields}
								setVisibleFields={setVisibleFields}
								showAllFields={showAllFields}
							/>
						</SplitterPanel>
					</Splitter>
				</StickyHeader>
				<div className="mt-8">
					<FormSection isVisible={isVisible('Expression Sample')}>
						<FormTableWrapper
							tableName="Expression Sample"
							showTable={!!sample?.htpExpressionSample}
							table={
								<ExternalDataBaseEntityTable
									entities={sample?.htpExpressionSample ? [sample.htpExpressionSample] : []}
								/>
							}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Curie')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Curie"
							formField={<StringTemplate string={sample?.curie} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Title')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Title"
							formField={<StringTemplate string={sample?.htpExpressionSampleTitle} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Taxon')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Taxon"
							formField={<OntologyTermTemplate term={sample?.taxon} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Type')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Type"
							formField={<OntologyTermTemplate term={sample?.htpExpressionSampleType} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Age')}>
						<FormTableWrapper
							tableName="Sample Age"
							showTable={!!sample?.htpExpressionSampleAge}
							table={
								<BioSampleAgeTable entities={sample?.htpExpressionSampleAge ? [sample.htpExpressionSampleAge] : []} />
							}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Locations')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Locations"
							formField={<StringListTemplate list={sample?.htpExpressionSampleLocations?.map(locationText)} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Biological Reference')}>
						<FormTableWrapper
							tableName="Biological Reference"
							showTable={sample?.datasetIds?.length > 0}
							table={<ExternalDataBaseEntityTable entities={sample?.datasetIds} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Assembly Versions')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Assembly Versions"
							formField={<StringListTemplate list={sample?.assemblyVersions} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Abundance')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Abundance"
							formField={<StringTemplate string={sample?.abundance} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Assay Used')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Assay Used"
							formField={<OntologyTermTemplate term={sample?.expressionAssayUsed} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Genomic Information')}>
						<FormTableWrapper
							tableName="Sample Genomic Information"
							showTable={!!sample?.genomicInformation}
							table={
								<BioSampleGenomicInformationTable
									entities={sample?.genomicInformation ? [sample.genomicInformation] : []}
								/>
							}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Genetic Sex')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Genetic Sex"
							formField={<StringTemplate string={sample?.geneticSex?.name} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Microarray Sample Details')}>
						<FormTableWrapper
							tableName="Microarray Sample Details"
							showTable={!!sample?.microarraySampleDetails}
							table={
								<MicroarraySampleDetailsTable
									entities={sample?.microarraySampleDetails ? [sample.microarraySampleDetails] : []}
								/>
							}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Sequencing Format')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Sequencing Format"
							formField={<StringTemplate string={sample?.sequencingFormat?.name} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Related Notes')}>
						<FormTableWrapper
							tableName="Sample Related Notes"
							showTable={sample?.relatedNotes?.length > 0}
							table={<RelatedNotesTable relatedNotes={sample?.relatedNotes || []} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Data Provider')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Data Provider"
							formField={<StringTemplate string={sample?.dataProvider?.abbreviation} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Updated By')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Updated By"
							formField={<StringTemplate string={sample?.updatedBy?.uniqueId} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Date Updated')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Date Updated"
							formField={<StringTemplate string={sample?.dateUpdated} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Created By')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Created By"
							formField={<StringTemplate string={sample?.createdBy?.uniqueId} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Date Created')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Date Created"
							formField={<StringTemplate string={sample?.dateCreated} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Internal')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Internal"
							formField={<BooleanTemplate value={sample?.internal} />}
						/>
					</FormSection>

					<FormSection isVisible={isVisible('Sample Obsolete')}>
						<DetailPageFieldWrapper
							{...WIDE_DETAIL_FIELD_LAYOUT}
							fieldName="Sample Obsolete"
							formField={<BooleanTemplate value={sample?.obsolete} />}
						/>
					</FormSection>
				</div>
			</ErrorBoundary>
		</>
	);
}
