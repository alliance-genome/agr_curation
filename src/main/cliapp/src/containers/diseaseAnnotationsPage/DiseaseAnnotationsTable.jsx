import React, { useRef, useState, useMemo } from 'react';
import { useMutation } from '@tanstack/react-query';
import { Toast } from 'primereact/toast';

import { SearchService } from '../../service/SearchService';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { Endpoints } from '../../constants/Endpoints';
import { DiseaseAnnotationService } from '../../service/DiseaseAnnotationService';
import { RelatedNotesEditDialog } from '../../components/RelatedNotesEditDialog';
import { RelatedNotesReadOnlyDialog } from '../../components/RelatedNotesReadOnlyDialog';
import { ConditionRelationsEditDialog } from '../../components/ConditionRelationsEditDialog';
import { ConditionRelationsReadOnlyDialog } from '../../components/ConditionRelationsReadOnlyDialog';

import { SingleReferenceTemplate } from '../../components/Templates/reference/SingleReferenceTemplate';
import { ObjectListTemplate } from '../../components/Templates/ObjectListTemplate';
import { IdTemplate } from '../../components/Templates/IdTemplate';
import { OntologyTermTemplate } from '../../components/Templates/OntologyTermTemplate';
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate';
import { GenomicEntityListTemplate } from '../../components/Templates/genomicEntity/GenomicEntityListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { NotTemplate } from '../../components/Templates/NotTemplate';
import { CountDialogTemplate } from '../../components/Templates/dialog/CountDialogTemplate';
import { TextDialogTemplate } from '../../components/Templates/dialog/TextDialogTemplate';
import { StringTemplate } from '../../components/Templates/StringTemplate';

import { NotEditor } from '../../components/Editors/dropdown/not/NotEditor';
import { ControlledVocabularyTableEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyTableEditor';
import { BooleanTableEditor } from '../../components/Editors/dropdown/boolean/BooleanTableEditor';
import { DialogTriggerEditor } from '../../components/Editors/dialog/DialogTriggerEditor';
import { ConditionHandleTableEditor } from '../../components/Editors/dropdown/conditionHandle/ConditionHandleTableEditor';
import { BiologicalEntityTableEditor } from '../../components/Editors/autocomplete/biologicalEntity/BiologicalEntityTableEditor';
import { DiseaseTableEditor } from '../../components/Editors/autocomplete/ontology/DiseaseTableEditor';
import { EvidenceCodesTableEditor } from '../../components/Editors/autocomplete/ontology/EvidenceCodesTableEditor';
import { WithTableEditor } from '../../components/Editors/autocomplete/gene/WithTableEditor';
import { AssertedGenesTableEditor } from '../../components/Editors/autocomplete/gene/AssertedGenesTableEditor';
import { DiseaseGeneticModifierGenesTableEditor } from '../../components/Editors/autocomplete/gene/DiseaseGeneticModifierGenesTableEditor';
import { AssertedAllelesTableEditor } from '../../components/Editors/autocomplete/allele/AssertedAllelesTableEditor';
import { DiseaseGeneticModifierAllelesTableEditor } from '../../components/Editors/autocomplete/allele/DiseaseGeneticModifierAllelesTableEditor';
import { SgdStrainBackgroundTableEditor } from '../../components/Editors/autocomplete/agm/SgdStrainBackgroundTableEditor';
import { DiseaseGeneticModifierAgmsTableEditor } from '../../components/Editors/autocomplete/agm/DiseaseGeneticModifierAgmsTableEditor';
import { SingleReferenceTableEditor } from '../../components/Editors/autocomplete/references/SingleReferenceTableEditor';
import { ControlledVocabularyMultiSelectTableEditor } from '../../components/Editors/dropdown/vocabulary/ControlledVocabularyMultiSelectTableEditor';

import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { useVocabularyTermSetService } from '../../service/useVocabularyTermSetService';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { Button } from 'primereact/button';
import { setNewEntity } from '../../utils/utils';
import { diseaseQualifiersSort, evidenceCodesSort } from '../../components/Templates/utils/sortMethods';
import { useNewAnnotationReducer } from './useNewAnnotationReducer';
import { NewAnnotationForm } from './NewAnnotationForm';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useGetTableData } from '../../service/useGetTableData';
import { useGetUserSettings } from '../../service/useGetUserSettings';

export const DiseaseAnnotationsTable = () => {
	const [isInEditMode, setIsInEditMode] = useState(false); //needs better name
	const [totalRecords, setTotalRecords] = useState(0);
	const [conditionRelationsData, setConditionRelationsData] = useState({
		conditionRelations: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});
	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});
	const { newAnnotationState, newAnnotationDispatch } = useNewAnnotationReducer();

	const relationsTerms = useControlledVocabularyService('disease_relation');
	const agmRelationTerms = useVocabularyTermSetService('agm_disease_relation');
	const alleleRelationTerms = useVocabularyTermSetService('allele_disease_relation');
	const geneRelationTerms = useVocabularyTermSetService('gene_disease_relation');
	const geneticSexTerms = useControlledVocabularyService('genetic_sex');
	const annotationTypeTerms = useControlledVocabularyService('annotation_type');
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const geneticModifierRelationTerms = useControlledVocabularyService('disease_genetic_modifier_relation');
	const diseaseQualifiersTerms = useControlledVocabularyService('disease_qualifier');

	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [uiErrorMessages, setUiErrorMessages] = useState([]);
	const uiErrorMessagesRef = useRef();
	uiErrorMessagesRef.current = uiErrorMessages;

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const [diseaseAnnotations, setDiseaseAnnotations] = useState([]);

	let diseaseAnnotationService = new DiseaseAnnotationService();

	const sortMapping = {
		'diseaseAnnotationObject.name': ['diseaseAnnotationObject.curie', 'diseaseAnnotationObject.namespace'],
		'diseaseAnnotationSubject.symbol': ['diseaseAnnotationSubject.name', 'diseaseAnnotationSubject.primaryExternalId'],
		'with.geneSymbol.displayText': ['with.geneFullName.displayText', 'with.primaryExternalId'],
		'sgdStrainBackground.name': ['sgdStrainBackground.primaryExternalId'],
		'diseaseGeneticModifier.symbol': ['diseaseGeneticModifier.name', 'diseaseGeneticModifier.primaryExternalId'],
	};

	const mutation = useMutation({
		mutationFn: (updatedAnnotation) => {
			return diseaseAnnotationService.saveDiseaseAnnotation(updatedAnnotation);
		},
	});

	const handleNewAnnotationOpen = () => {
		newAnnotationDispatch({ type: 'OPEN_DIALOG' });
	};

	const handleDuplication = (rowData) => {
		newAnnotationDispatch({ type: 'DUPLICATE_ROW', rowData });
		newAnnotationDispatch({ type: 'SET_IS_ENABLED', value: true });
		if (rowData.type === 'AGMDiseaseAnnotation') {
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: true });
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_ALLELE_ENABLED', value: true });
		}

		if (rowData.type === 'AlleleDiseaseAnnotation') {
			newAnnotationDispatch({ type: 'SET_IS_ASSERTED_GENE_ENABLED', value: true });
		}

		if (rowData.relatedNotes && rowData.relatedNotes.length > 0) {
			newAnnotationDispatch({ type: 'SET_RELATED_NOTES_EDITING_ROWS', relatedNotes: rowData.relatedNotes });
		}

		if (rowData.conditionRelations && rowData.conditionRelations.length > 0) {
			newAnnotationDispatch({
				type: 'SET_CONDITION_RELATIONS_EDITING_ROWS',
				conditionRelations: rowData.conditionRelations,
			});
		}

		handleNewAnnotationOpen();
	};

	const handleRelatedNotesOpen = (relatedNotes) => {
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = relatedNotes;
		_relatedNotesData['dialog'] = true;
		_relatedNotesData['isInEdit'] = false;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const handleRelatedNotesOpenInEdit = (event, rowProps, isInEdit) => {
		const { rowIndex } = rowProps;
		let _relatedNotesData = {};
		_relatedNotesData['originalRelatedNotes'] = rowProps?.rowData?.relatedNotes;
		_relatedNotesData['dialog'] = true;
		_relatedNotesData['isInEdit'] = isInEdit;
		_relatedNotesData['rowIndex'] = rowIndex;
		_relatedNotesData['mainRowProps'] = rowProps;
		setRelatedNotesData(() => ({
			..._relatedNotesData,
		}));
	};

	const handleConditionRelationsOpen = (conditionRelations) => {
		let _conditionRelationsData = {};
		_conditionRelationsData['originalConditionRelations'] = conditionRelations;
		_conditionRelationsData['dialog'] = true;
		_conditionRelationsData['isInEdit'] = false;
		setConditionRelationsData(() => ({
			..._conditionRelationsData,
		}));
	};

	const handleConditionRelationsOpenInEdit = (event, rowProps, isInEdit) => {
		const { rowIndex } = rowProps;
		let _conditionRelationsData = {};
		_conditionRelationsData['originalConditionRelations'] = rowProps.rowData.conditionRelations;
		_conditionRelationsData['dialog'] = true;
		_conditionRelationsData['isInEdit'] = isInEdit;
		_conditionRelationsData['rowIndex'] = rowIndex;
		_conditionRelationsData['mainRowProps'] = rowProps;
		setConditionRelationsData(() => ({
			..._conditionRelationsData,
		}));
	};

	const getRelationTermSet = (editorOptions) => {
		let diseaseRelationTerms = relationsTerms;
		if (editorOptions.rowData?.diseaseAnnotationSubject?.type === 'Gene') {
			diseaseRelationTerms = geneRelationTerms;
		} else if (editorOptions.rowData?.diseaseAnnotationSubject?.type === 'Allele') {
			diseaseRelationTerms = alleleRelationTerms;
		} else if (editorOptions.rowData?.diseaseAnnotationSubject?.type === 'AffectedGenomicModel') {
			diseaseRelationTerms = agmRelationTerms;
		}

		return diseaseRelationTerms;
	};

	const uniqueIdEditorTemplate = (editorOptions) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`c${editorOptions.rowData.id}`}>
					{editorOptions.rowData.uniqueId}
				</EllipsisTableCell>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[editorOptions.rowIndex]}
					errorField={'uniqueId'}
				/>
			</>
		);
	};

	const columns = useMemo(
		() => [
			{
				field: 'uniqueId',
				header: 'Unique ID',
				body: (rowData) => <IdTemplate id={rowData.uniqueId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
				editor: (editorOptions) => uniqueIdEditorTemplate(editorOptions),
			},
			{
				field: 'primaryExternalId',
				header: 'MOD Annotation ID',
				body: (rowData) => <IdTemplate id={rowData.primaryExternalId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.primaryExternalIdFilterConfig,
			},
			{
				field: 'modInternalId',
				header: 'MOD Internal ID',
				body: (rowData) => <IdTemplate id={rowData.modInternalId} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.modInternalIdFilterConfig,
			},
			{
				field: 'diseaseAnnotationSubject',
				columnKey: 'diseaseAnnotationSubject.symbol',
				header: 'Subject',
				body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.diseaseAnnotationSubject} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.diseaseAnnotationSubjectFieldConfig,
				editor: (editorOptions) => (
					<BiologicalEntityTableEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						uiErrorMessagesRef={uiErrorMessagesRef}
					/>
				),
			},
			{
				field: 'relation',
				columnKey: 'relation.name',
				header: 'Disease Relation',
				body: (rowData) => rowData.relation?.name,
				sortable: true,
				filterConfig: FILTER_CONFIGS.relationFilterConfig,
				editor: (editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="relation"
						options={getRelationTermSet(editorOptions)}
						showClear={false}
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'negated',
				header: 'NOT',
				body: (rowData) => <NotTemplate value={rowData.negated} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.negatedFilterConfig,
				editor: (editorOptions) => (
					<NotEditor value={editorOptions.value} editorChange={editorOptions.editorCallback} />
				),
			},
			{
				field: 'diseaseAnnotationObject',
				columnKey: 'diseaseAnnotationObject.name',
				header: 'Disease',
				body: (rowData) => <OntologyTermTemplate term={rowData.diseaseAnnotationObject} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.diseaseAnnotationObjectFilterConfig,
				editor: (editorOptions) => (
					<DiseaseTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'evidenceItem',
				columnKey: 'evidenceItem.primaryCrossReferenceCurie',
				header: 'Reference',
				body: (rowData) => <SingleReferenceTemplate singleReference={rowData.evidenceItem} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.evidenceItemFilterConfig,
				editor: (editorOptions) => (
					<SingleReferenceTableEditor
						editorOptions={editorOptions}
						field="evidenceItem"
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'evidenceCodes',
				columnKey: 'evidenceCodes.abbreviation',
				header: 'Evidence Code',
				body: (rowData) => (
					<ObjectListTemplate
						list={rowData.evidenceCodes}
						sortMethod={evidenceCodesSort}
						stringTemplate={(item) => `${item.abbreviation} - ${item.name} (${item.curie})`}
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.evidenceCodesFilterConfig,
				editor: (editorOptions) => (
					<EvidenceCodesTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'with',
				columnKey: 'with.geneSymbol.displayText',
				header: 'With',
				body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.with} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.withFilterConfig,
				editor: (editorOptions) => (
					<WithTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'relatedNotes',
				columnKey: 'relatedNotes.freeText',
				header: 'Related Notes',
				body: (rowData) => (
					<CountDialogTemplate entities={rowData.relatedNotes} handleOpen={handleRelatedNotesOpen} text={'Notes'} />
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
				editor: (editorOptions) => {
					const count = editorOptions.rowData.relatedNotes?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleRelatedNotesOpenInEdit}
							errorField="relatedNotes"
							displayText={count ? `Notes(${count}) ` : null}
							addText="Add Note"
						/>
					);
				},
			},
			{
				field: 'conditionRelations',
				columnKey: 'conditionRelations.handle',
				header: 'Experiments',
				body: (rowData) => {
					if (!rowData.conditionRelations?.[0]?.handle) return null;
					return (
						<TextDialogTemplate
							entity={rowData.conditionRelations}
							handleOpen={handleConditionRelationsOpen}
							text={rowData.conditionRelations[0].handle}
							underline={false}
						/>
					);
				},
				sortable: true,
				filterConfig: FILTER_CONFIGS.daConditionRelationsHandleFilterConfig,
				editor: (editorOptions) => (
					<ConditionHandleTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'conditionRelations',
				columnKey: 'conditionRelations.uniqueId',
				header: 'Experimental Conditions',
				body: (rowData) => {
					if (rowData.conditionRelations?.[0]?.handle) return null;
					return (
						<CountDialogTemplate
							entities={rowData.conditionRelations}
							handleOpen={handleConditionRelationsOpen}
							text={'Conditions'}
						/>
					);
				},
				sortable: true,
				filterConfig: FILTER_CONFIGS.daConditionRelationsSummaryFilterConfig,
				editor: (editorOptions) => {
					if (editorOptions.rowData?.conditionRelations?.[0]?.handle) return null;
					const count = editorOptions.rowData?.conditionRelations?.length;
					return (
						<DialogTriggerEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							onOpenInEdit={handleConditionRelationsOpenInEdit}
							errorField="conditionRelations"
							displayText={count ? `Conditions (${count})` : null}
							addText="Add Condition"
						/>
					);
				},
			},
			{
				field: 'geneticSex',
				columnKey: 'geneticSex.name',
				header: 'Genetic Sex',
				body: (rowData) => rowData.geneticSex?.name,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geneticSexFilterConfig,
				editor: (editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="geneticSex"
						options={geneticSexTerms}
						errorMessagesRef={errorMessagesRef}
						showClear={true}
					/>
				),
			},
			{
				field: 'diseaseQualifiers',
				columnKey: 'diseaseQualifiers.name',
				header: 'Disease Qualifiers',
				body: (rowData) => (
					<ObjectListTemplate
						list={rowData.diseaseQualifiers}
						sortMethod={diseaseQualifiersSort}
						stringTemplate={(item) => item.name}
					/>
				),
				sortable: true,
				filterConfig: FILTER_CONFIGS.diseaseQualifiersFilterConfig,
				editor: (editorOptions) => (
					<ControlledVocabularyMultiSelectTableEditor
						editorOptions={editorOptions}
						field="diseaseQualifiers"
						options={diseaseQualifiersTerms}
						errorMessagesRef={errorMessagesRef}
					/>
				),
			},
			{
				field: 'sgdStrainBackground',
				columnKey: 'sgdStrainBackground.name',
				header: 'SGD Strain Background',
				body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.sgdStrainBackground} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.sgdStrainBackgroundFilterConfig,
				editor: (editorOptions) => {
					if (editorOptions.rowData.type !== 'GeneDiseaseAnnotation') return null;
					return (
						<SgdStrainBackgroundTableEditor
							editorOptions={editorOptions}
							errorMessagesRef={errorMessagesRef}
							uiErrorMessagesRef={uiErrorMessagesRef}
						/>
					);
				},
			},
			{
				field: 'annotationType',
				columnKey: 'annotationType.name',
				header: 'Annotation Type',
				body: (rowData) => rowData.annotationType?.name,
				sortable: true,
				filterConfig: FILTER_CONFIGS.annotationTypeFilterConfig,
				editor: (editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="annotationType"
						options={annotationTypeTerms}
						errorMessagesRef={errorMessagesRef}
						showClear={true}
					/>
				),
			},
			{
				field: 'diseaseGeneticModifierRelation',
				columnKey: 'diseaseGeneticModifierRelation.name',
				header: 'Genetic Modifier Relation',
				body: (rowData) => rowData.diseaseGeneticModifierRelation?.name,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geneticModifierRelationFilterConfig,
				editor: (editorOptions) => (
					<ControlledVocabularyTableEditor
						editorOptions={editorOptions}
						field="diseaseGeneticModifierRelation"
						options={geneticModifierRelationTerms}
						errorMessagesRef={errorMessagesRef}
						showClear={true}
					/>
				),
			},
			{
				field: 'diseaseGeneticModifierAgms',
				columnKey: 'diseaseGeneticModifierAgms.name',
				header: 'Genetic Modifier AGMs',
				body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.diseaseGeneticModifierAgms} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geneticModifierAgmsFilterConfig,
				editor: (editorOptions) => (
					<DiseaseGeneticModifierAgmsTableEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						uiErrorMessagesRef={uiErrorMessagesRef}
					/>
				),
			},
			{
				field: 'diseaseGeneticModifierAlleles',
				columnKey: 'diseaseGeneticModifierAlleles.alleleSymbol.displayText',
				header: 'Genetic Modifier Alleles',
				body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.diseaseGeneticModifierAlleles} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geneticModifierAllelesFilterConfig,
				editor: (editorOptions) => (
					<DiseaseGeneticModifierAllelesTableEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						uiErrorMessagesRef={uiErrorMessagesRef}
					/>
				),
			},
			{
				field: 'diseaseGeneticModifierGenes',
				columnKey: 'diseaseGeneticModifierGenes.geneSymbol.displayText',
				header: 'Genetic Modifier Genes',
				body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.diseaseGeneticModifierGenes} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.geneticModifierGenesFilterConfig,
				editor: (editorOptions) => (
					<DiseaseGeneticModifierGenesTableEditor
						editorOptions={editorOptions}
						errorMessagesRef={errorMessagesRef}
						uiErrorMessagesRef={uiErrorMessagesRef}
					/>
				),
			},
			{
				field: 'inferredGene.geneSymbol.displayText',
				header: 'Inferred Gene',
				body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.inferredGene} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.inferredGeneFilterConfig,
			},
			{
				field: 'assertedGenes',
				columnKey: 'assertedGenes.geneSymbol.displayText',
				header: 'Asserted Genes',
				body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.assertedGenes} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.assertedGenesFilterConfig,
				editor: (editorOptions) => {
					if (editorOptions.rowData.type === 'GeneDiseaseAnnotation') return null;
					return <AssertedGenesTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />;
				},
			},
			{
				field: 'inferredAllele.alleleSymbol.displayText',
				header: 'Inferred Allele',
				body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.inferredAllele} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.inferredAlleleFilterConfig,
			},
			{
				field: 'assertedAlleles',
				columnKey: 'assertedAlleles.alleleSymbol.displayText',
				header: 'Asserted Alleles',
				body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.assertedAlleles} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.assertedAllelesFilterConfig,
				editor: (editorOptions) => {
					if (editorOptions.rowData.type !== 'AGMDiseaseAnnotation') return null;
					return <AssertedAllelesTableEditor editorOptions={editorOptions} errorMessagesRef={errorMessagesRef} />;
				},
			},
			{
				field: 'dataProvider.abbreviation',
				header: 'Data Provider',
				sortable: true,
				filterConfig: FILTER_CONFIGS.diseaseDataProviderFilterConfig,
			},
			{
				field: 'secondaryDataProvider.abbreviation',
				header: 'Secondary Data Provider',
				sortable: true,
				filterConfig: FILTER_CONFIGS.secondaryDataProviderFilterConfig,
			},
			{
				field: 'updatedBy.uniqueId',
				header: 'Updated By',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.updatedBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
			},
			{
				field: 'dateUpdated',
				header: 'Date Updated',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.dateUpdated} />,
				filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
			},
			{
				field: 'createdBy.uniqueId',
				header: 'Created By',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.createdBy?.uniqueId} />,
				filterConfig: FILTER_CONFIGS.createdByFilterConfig,
			},
			{
				field: 'dateCreated',
				header: 'Date Created',
				sortable: true,
				body: (rowData) => <StringTemplate string={rowData.dateCreated} />,
				filterConfig: FILTER_CONFIGS.dateCreatedFilterConfig,
			},
			{
				field: 'internal',
				header: 'Internal',
				body: (rowData) => <BooleanTemplate value={rowData.internal} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.internalFilterConfig,
				editor: (editorOptions) => (
					<BooleanTableEditor editorOptions={editorOptions} field="internal" errorMessagesRef={errorMessagesRef} />
				),
			},
			{
				field: 'obsolete',
				header: 'Obsolete',
				body: (rowData) => <BooleanTemplate value={rowData.obsolete} />,
				sortable: true,
				filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
				editor: (editorOptions) => (
					<BooleanTableEditor editorOptions={editorOptions} field="obsolete" errorMessagesRef={errorMessagesRef} />
				),
			},
		],
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[
			relationsTerms,
			agmRelationTerms,
			alleleRelationTerms,
			geneRelationTerms,
			geneticSexTerms,
			annotationTypeTerms,
			geneticModifierRelationTerms,
			diseaseQualifiersTerms,
		]
	);

	const DEFAULT_COLUMN_WIDTH = 10;
	const SEARCH_ENDPOINT = Endpoints.Annotation.DISEASE_ANNOTATION;
	const defaultFilters = { obsoleteFilter: { obsolete: { queryString: 'false' } } };

	const initialTableState = useMemo(
		() => getDefaultTableState('DiseaseAnnotations', columns, DEFAULT_COLUMN_WIDTH, defaultFilters),
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[columns]
	);

	const { settings: tableState, mutate: setTableState } = useGetUserSettings(
		initialTableState.tableSettingsKeyName,
		initialTableState
	);

	const { isLoading, isFetching } = useGetTableData({
		tableState,
		endpoint: SEARCH_ENDPOINT,
		sortMapping,
		setIsInEditMode,
		setEntities: setDiseaseAnnotations,
		setTotalRecords,
		toast_topleft,
		searchService,
	});

	const headerButtons = (disabled = false) => {
		return (
			<>
				<Button label="New Annotation" icon="pi pi-plus" onClick={handleNewAnnotationOpen} disabled={disabled} />
				&nbsp;&nbsp;
			</>
		);
	};

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint={SEARCH_ENDPOINT}
					tableName="Disease Annotations"
					entities={diseaseAnnotations}
					setEntities={setDiseaseAnnotations}
					totalRecords={totalRecords}
					setTotalRecords={setTotalRecords}
					tableState={tableState}
					setTableState={setTableState}
					columns={columns}
					isEditable={true}
					mutation={mutation}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{ toast_topleft, toast_topright }}
					errorObject={{ errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages }}
					headerButtons={headerButtons}
					deletionEnabled={true}
					deletionMethod={diseaseAnnotationService.deleteDiseaseAnnotation}
					deprecationMethod={diseaseAnnotationService.deprecateDiseaseAnnotation}
					deprecateOption={true}
					modReset={true}
					handleDuplication={handleDuplication}
					duplicationEnabled={true}
					defaultColumnWidth={DEFAULT_COLUMN_WIDTH}
					fetching={isFetching || isLoading}
					defaultFilters={defaultFilters}
				/>
			</div>
			<NewAnnotationForm
				newAnnotationState={newAnnotationState}
				newAnnotationDispatch={newAnnotationDispatch}
				searchService={searchService}
				relationsTerms={relationsTerms}
				negatedTerms={booleanTerms?.terms || []}
				setNewDiseaseAnnotation={(newAnnotation, queryClient) =>
					setNewEntity(tableState, setDiseaseAnnotations, newAnnotation, queryClient)
				}
			/>
			<RelatedNotesEditDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet="da_note_type"
				showReferences={false}
			/>
			<RelatedNotesReadOnlyDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				showReferences={false}
			/>
			<ConditionRelationsEditDialog
				originalConditionRelationsData={conditionRelationsData}
				setOriginalConditionRelationsData={setConditionRelationsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<ConditionRelationsReadOnlyDialog
				originalConditionRelationsData={conditionRelationsData}
				setOriginalConditionRelationsData={setConditionRelationsData}
			/>
		</>
	);
};
