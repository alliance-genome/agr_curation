import React, { useRef, useState } from 'react';
import { useMutation } from 'react-query';
import { Toast } from 'primereact/toast';

import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { SubjectAutocompleteTemplate } from '../../components/Autocomplete/SubjectAutocompleteTemplate';
import { EvidenceAutocompleteTemplate } from '../../components/Autocomplete/EvidenceAutocompleteTemplate';
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { EditMessageTooltip } from '../../components/EditMessageTooltip';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { SearchService } from '../../service/SearchService';
import { DiseaseAnnotationService } from '../../service/DiseaseAnnotationService';
import { RelatedNotesDialog } from './RelatedNotesDialog';
import { ConditionRelationsDialog } from './ConditionRelationsDialog';

import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { ConditionRelationHandleDropdown } from '../../components/ConditionRelationHandleSelector';
import { ControlledVocabularyMultiSelectDropdown } from '../../components/ControlledVocabularyMultiSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { Button } from 'primereact/button';
import { Tooltip } from 'primereact/tooltip';
import { getRefString, autocompleteSearch, buildAutocompleteFilter, defaultAutocompleteOnChange, multipleAutocompleteOnChange } from '../../utils/utils';
import { useNewAnnotationReducer } from "./useNewAnnotaionReducer";
import { NewAnnotationForm } from "./NewAnnotationForm";
import { internalTemplate, obsoleteTemplate } from '../../components/AuditedObjectComponent';
import { AutocompleteMultiEditor } from "../../components/Autocomplete/AutocompleteMultiEditor";
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';

export const DiseaseAnnotationsTable = () => {

	const [isEnabled, setIsEnabled] = useState(true); //needs better name
	const [conditionRelationsData, setConditionRelationsData] = useState({
		conditionRelations: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mailRowProps: {},
	});
	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});
	const { newAnnotationState, newAnnotationDispatch } = useNewAnnotationReducer();

	const diseaseRelationsTerms = useControlledVocabularyService('Disease Relation Vocabulary');
	const geneticSexTerms = useControlledVocabularyService('Genetic sexes');
	const annotationTypeTerms = useControlledVocabularyService('Annotation types')
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const geneticModifierRelationTerms = useControlledVocabularyService('Disease genetic modifier relations');
	const diseaseQualifiersTerms = useControlledVocabularyService('Disease qualifiers');

	const [newDiseaseAnnotation, setNewDiseaseAnnotation] = useState(null);

	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [uiErrorMessages, setUiErrorMessages] = useState([]);
	const uiErrorMessagesRef = useRef();
	uiErrorMessagesRef.current = uiErrorMessages;

	const searchService = new SearchService();

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	let diseaseAnnotationService = new DiseaseAnnotationService();

	const sortMapping = {
		'object.name': ['object.curie', 'object.namespace'],
		'subject.symbol': ['subject.name', 'subject.curie'],
		'with.geneSymbol.displayText': ['with.geneFullName.displayText', 'with.curie'],
		'sgdStrainBackground.name': ['sgdStrainBackground.curie'],
		'diseaseGeneticModifier.symbol': ['diseaseGeneticModifier.name', 'diseaseGeneticModifier.curie']
	};



	const mutation = useMutation(updatedAnnotation => {
		return diseaseAnnotationService.saveDiseaseAnnotation(updatedAnnotation);
	});

	const handleNewAnnotationOpen = () => {
		newAnnotationDispatch({type: "OPEN_DIALOG"})
	};

	const handleRelatedNotesOpen = (event, rowData, isInEdit) => {
		let _relatedNotesData = {};
		_relatedNotesData["originalRelatedNotes"] = rowData.relatedNotes;
		_relatedNotesData["dialog"] = true;
		_relatedNotesData["isInEdit"] = isInEdit;
		setRelatedNotesData(() => ({
			..._relatedNotesData
		}));
	};

	const handleRelatedNotesOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _relatedNotesData = {};
		_relatedNotesData["originalRelatedNotes"] = rowProps.rowData.relatedNotes;
		_relatedNotesData["dialog"] = true;
		_relatedNotesData["isInEdit"] = isInEdit;
		_relatedNotesData["rowIndex"] = index;
		_relatedNotesData["mainRowProps"] = rowProps;
		setRelatedNotesData(() => ({
			..._relatedNotesData
		}));
	};

	const handleConditionRelationsOpen = (event, rowData, isInEdit) => {
		let _conditionRelationsData = {};
		_conditionRelationsData["originalConditionRelations"] = rowData.conditionRelations;
		_conditionRelationsData["dialog"] = true;
		_conditionRelationsData["isInEdit"] = isInEdit;
		setConditionRelationsData(() => ({
			..._conditionRelationsData
		}));
	};

	const handleConditionRelationsOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _conditionRelationsData = {};
		_conditionRelationsData["originalConditionRelations"] = rowProps.rowData.conditionRelations;
		_conditionRelationsData["dialog"] = true;
		_conditionRelationsData["isInEdit"] = isInEdit;
		_conditionRelationsData["rowIndex"] = index;
		_conditionRelationsData["mainRowProps"] = rowProps;
		setConditionRelationsData(() => ({
			..._conditionRelationsData
		}));
	};

	const withTemplate = (rowData) => {
		if (rowData && rowData.with) {
			const sortedWithGenes = rowData.with.sort((a, b) => (a.geneSymbol.displayText > b.geneSymbol.displayText) ? 1 : (a.curie === b.curie) ? 1 : -1);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item.geneSymbol.displayText + ' (' + item.curie + ')'}
					</EllipsisTableCell>
				);
			};
			return <ListTableCell template={listTemplate} listData={sortedWithGenes}/>
		}
	};

	const assertedGenesBodyTemplate = (rowData) => {
		if (rowData && rowData.assertedGenes && rowData.assertedGenes.length > 0) {
			const sortedAssertedGenes = rowData.assertedGenes.sort((a, b) => (a.geneSymbol?.displayText > b.geneSymbol?.displayText) ? 1 : (a.curie === b.curie) ? 1 : -1);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item.geneSymbol?.displayText + ' (' + item.curie + ')'}
					</EllipsisTableCell>
				);
			};

			return (
				<>
					<div className={`a${rowData.id}${rowData.assertedGenes[0].curie.replace(':', '')}`}>
						<ListTableCell template={listTemplate} listData={sortedAssertedGenes}/>
					</div>
					<Tooltip target={`.a${rowData.id}${rowData.assertedGenes[0].curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={sortedAssertedGenes}/>
					</Tooltip>
				</>
			);
		}
	};

	const evidenceTemplate = (rowData) => {
		if (rowData?.evidenceCodes && rowData.evidenceCodes.length > 0) {
			const sortedEvidenceCodes = rowData.evidenceCodes.sort((a, b) => (a.abbreviation > b.abbreviation) ? 1 : (a.curie === b.curie) ? 1 : -1);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item.abbreviation + ' - ' + item.name + ' (' + item.curie + ')'}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<div className={`a${rowData.id}${rowData.evidenceCodes[0].curie.replace(':', '')}`}>
						<ListTableCell template={listTemplate} listData={sortedEvidenceCodes}/>
					</div>
					<Tooltip target={`.a${rowData.id}${rowData.evidenceCodes[0].curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={sortedEvidenceCodes}/>
					</Tooltip>
				</>
			);
		}
	};

	const singleReferenceBodyTemplate = (rowData) => {
		if (rowData && rowData.singleReference) {
			let refString = getRefString(rowData.singleReference);
			return (
				<>
					<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${rowData.singleReference.curie.replace(':', '')}`}
						dangerouslySetInnerHTML={{
							__html: refString
						}}
					/>
					<Tooltip target={`.a${rowData.id}${rowData.singleReference.curie.replace(':', '')}`}>
						<div dangerouslySetInnerHTML={{
							__html: refString
						}}
						/>
					</Tooltip>
				</>
			);

		}
	};

	const inferredGeneBodyTemplate = (rowData) => {
		if (rowData && rowData.inferredGene) {
			return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis ig${rowData.id}${rowData.inferredGene.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.inferredGene.geneSymbol.displayText + ' (' + rowData.inferredGene.curie + ')'
							}}
						/>
						<Tooltip target={`.ig${rowData.id}${rowData.inferredGene.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.inferredGene.geneSymbol.displayText + ' (' + rowData.inferredGene.curie + ')'
							}}
							/>
						</Tooltip>
					</>
				)
		}
	};

	const inferredAlleleBodyTemplate = (rowData) => {
		if (rowData && rowData.inferredAllele) {
			if (rowData.inferredAllele.alleleSymbol.displayText) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis ia${rowData.id}${rowData.inferredAllele.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.inferredAllele.alleleSymbol.displayText + ' (' + rowData.inferredAllele.curie + ')'
							}}
						/>
						<Tooltip target={`.ia${rowData.id}${rowData.inferredAllele.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.inferredAllele.alleleSymbol.displayText + ' (' + rowData.inferredAllele.curie + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis ia${rowData.id}${rowData.inferredAllele.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.inferredAllele.alleleFullName.displayText + ' (' + rowData.inferredAllele.curie + ')'
							}}
						/>
						<Tooltip target={`.ia${rowData.id}${rowData.inferredAllele.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.inferredAllele.alleleFullName.displayText + ' (' + rowData.inferredAllele.curie + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			}
		}
	};

	const assertedAlleleBodyTemplate = (rowData) => {
		if (rowData && rowData.assertedAllele) {
			if (rowData.assertedAllele.alleleSymbol) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis aa${rowData.id}${rowData.assertedAllele.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.assertedAllele.alleleSymbol.displayText + ' (' + rowData.assertedAllele.curie + ')'
							}}
						/>
						<Tooltip target={`.aa${rowData.id}${rowData.assertedAllele.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.assertedAllele.alleleSymbol.displayText + ' (' + rowData.assertedAllele.curie + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis aa${rowData.id}${rowData.assertedAllele.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.assertedAllele.alleleFullName.displayText + ' (' + rowData.assertedAllele.curie + ')'
							}}
						/>
						<Tooltip target={`.aa${rowData.id}${rowData.assertedAllele.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.assertedAllele.alleleFullName.displayText + ' (' + rowData.assertedAllele.curie + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			}
		}
	};

	const diseaseQualifiersBodyTemplate = (rowData) => {
		if (rowData && rowData.diseaseQualifiers) {
			const sortedDiseaseQualifiers = rowData.diseaseQualifiers.sort((a, b) => (a.name > b.name) ? 1 : -1);
			const listTemplate = (item) => item.name;
			return <ListTableCell template={listTemplate} listData={sortedDiseaseQualifiers}/>
		}
	};

	const negatedTemplate = (rowData) => {
		if (rowData && rowData.negated !== null && rowData.negated !== undefined) {
			return <EllipsisTableCell>{JSON.stringify(rowData.negated)}</EllipsisTableCell>;
		}
	};

	const relatedNotesTemplate = (rowData) => {
		if (rowData?.relatedNotes) {
			return (
				<Button className="p-button-text"
					onClick={(event) => { handleRelatedNotesOpen(event, rowData, false) }} >
					<span style={{ textDecoration: 'underline' }}>
						{`Notes(${rowData.relatedNotes.length})`}
					</span>
				</Button>
			)
		}
	};

	const relatedNotesEditor = (props) => {
		if (props?.rowData?.relatedNotes) {
			return (
				<>
				<div>
					<Button className="p-button-text"
						onClick={(event) => { handleRelatedNotesOpenInEdit(event, props, true) }} >
						<span style={{ textDecoration: 'underline' }}>
							{`Notes(${props.rowData.relatedNotes.length}) `}
							<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
						</span>&nbsp;&nbsp;&nbsp;&nbsp;
						<EditMessageTooltip/>
					</Button>
				</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"relatedNotes"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		} else {
			return (
				<>
					<div>
						<Button className="p-button-text"
							onClick={(event) => { handleRelatedNotesOpenInEdit(event, props, true) }} >
							<span style={{ textDecoration: 'underline' }}>
								Add Note
								<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
							</span>&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip/>
						</Button>
					</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"relatedNotes"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		}
	};

	const conditionRelationsTemplate = (rowData) => {
		if (rowData?.conditionRelations && !rowData.conditionRelations[0]?.handle) {
			return (
				<Button className="p-button-text"
					onClick={(event) => { handleConditionRelationsOpen(event, rowData) }} >
					<span style={{ textDecoration: 'underline' }}>
						{`Conditions (${rowData.conditionRelations.length})`}
					</span>
				</Button>
			)
		}
	};

	const conditionRelationsEditor = (props) => {

		if (props.rowData?.conditionRelations) {
			const handle = props.rowData.conditionRelations[0]?.handle;

			if (handle) return conditionRelationsTemplate(props.rowData);

			return (
				<>
				<div>
					<Button className="p-button-text"
						onClick={(event) => { handleConditionRelationsOpenInEdit(event, props, true) }} >
						<span style={{ textDecoration: 'underline' }}>
							{!handle && `Conditions (${props.rowData.conditionRelations.length})`}
							{handle && handle}
							<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
						</span>&nbsp;&nbsp;&nbsp;&nbsp;
						<EditMessageTooltip/>
					</Button>
				</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"conditionRelations"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		} else {
			return (
				<>
					<div>
						<Button className="p-button-text"
							onClick={(event) => { handleConditionRelationsOpenInEdit(event, props, true) }} >
							<span style={{ textDecoration: 'underline' }}>
								Add Condition
								<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
							</span>&nbsp;&nbsp;&nbsp;&nbsp;
							<EditMessageTooltip/>
						</Button>
					</div>
				</>
			)

		}
	};

	const conditionRelationHandleTemplate = (rowData) => {
		if (rowData?.conditionRelations && rowData.conditionRelations[0]?.handle) {
			let handle = rowData.conditionRelations[0].handle;
			return (
				<Button className="p-button-text"
					onClick={(event) => { handleConditionRelationsOpen(event, rowData) }} >
					<span style={{ textDecoration: 'underline' }}>
						{handle && handle}
					</span>
				</Button>
			)
		}
	};

	const conditionRelationHandleEditor = (props) => {
		if (props.rowData?.conditionRelations && props.rowData.conditionRelations[0]?.handle) {
			return (
			<>
				<ConditionRelationHandleDropdown
					field="conditionRelationHandle"
					editorChange={onConditionRelationHandleEditorValueChange}
					props={props}
					showClear={false}
					placeholderText={props.rowData.conditionRelations[0].handle}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"conditionRelationHandle"} />
			</>
		);
		}
	};

	const onConditionRelationHandleEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (typeof event.value === "object") {
			updatedAnnotations[props.rowIndex].conditionRelations[0] = event.value;
		} else {
			updatedAnnotations[props.rowIndex].conditionRelations[0].handle = event.value;
		}
	};

	const diseaseBodyTemplate = (rowData) => {
		if (rowData.object) {
			return (
				<>
					<EllipsisTableCell otherClasses={`a${rowData.id}${rowData.object.curie.replace(':', '')}`}>{rowData.object.name} ({rowData.object.curie})</EllipsisTableCell>
					<Tooltip target={`.a${rowData.id}${rowData.object.curie.replace(':', '')}`} content={`${rowData.object.name} (${rowData.object.curie})`} />
				</>
			)
		}
	};

	const onDiseaseRelationEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].diseaseRelation = event.value;
		}
	};

	const diseaseRelationEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="diseaseRelation"
					options={diseaseRelationsTerms}
					editorChange={onDiseaseRelationEditorValueChange}
					props={props}
					showClear={false}
					placeholderText={props.rowData.diseaseRelation.name}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"diseaseRelation"} />
			</>
		);
	};

	const onGeneticSexEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		updatedAnnotations[props.rowIndex].geneticSex = event.value;
	};

	const geneticSexEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="geneticSex"
					options={geneticSexTerms}
					editorChange={onGeneticSexEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"geneticSex"} />
			</>
		);
	};

	const onAnnotationTypeEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		updatedAnnotations[props.rowIndex].annotationType = event.value;
	};

	const annotationTypeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="annotationType"
					options={annotationTypeTerms}
					editorChange={onAnnotationTypeEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"annotationType"} />
			</>
		);
	};

	const onGeneticModifierRelationEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		updatedAnnotations[props.rowIndex].diseaseGeneticModifierRelation = event.value;
	};

	const geneticModifierRelationEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="diseaseGeneticModifierRelation"
					options={geneticModifierRelationTerms}
					editorChange={onGeneticModifierRelationEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"diseaseGeneticModifierRelation"} />
			</>
		);
	};

	const onDiseaseQualifiersEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].diseaseQualifiers = event.value;
		}
	};

	const diseaseQualifiersEditor = (props) => {
		let placeholderText = '';
		if (props.rowData.diseaseQualifiers) {
			let placeholderTextElements = [];
			props.rowData.diseaseQualifiers.forEach((x, i) =>
				placeholderTextElements.push(x.name));
			placeholderText = placeholderTextElements.join();

		}
		return (
			<>
				<ControlledVocabularyMultiSelectDropdown
					options={diseaseQualifiersTerms}
					editorChange={onDiseaseQualifiersEditorValueChange}
					props={props}
					placeholderText={placeholderText}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"diseaseQualifiers"} />
			</>
		);
	};

	const onNegatedEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].negated = JSON.parse(event.value.name);
		}
	};

	const negatedEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onNegatedEditorValueChange}
					props={props}
					field={"negated"}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"negated"} />
			</>
		);
	};

	const onInternalEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].internal = JSON.parse(event.value.name);
		}
	};

	const internalEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onInternalEditorValueChange}
					props={props}
					field={"internal"}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"internal"} />
			</>
		);
	};

	const onObsoleteEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].obsolete = JSON.parse(event.value.name);
		}
	};

	const obsoleteEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onObsoleteEditorValueChange}
					props={props}
					field={"obsolete"}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"obsolete"} />
			</>
		);
	};

	const onSubjectValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "subject", setFieldValue);
	};

	const subjectSearch = (event, setFiltered, setQuery, props) => {
		const autocompleteFields = getSubjectAutocompleteFields(props);
		const endpoint = getSubjectEndpoint(props);
		const filterName = "subjectFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const subjectEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					initialValue={props.rowData.subject?.curie}
					search={subjectSearch}
					rowProps={props}
					searchService={searchService}
					fieldName='subject'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onSubjectValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"subject"}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={"subject"}
				/>
			</>
		);
	};

	const getSubjectEndpoint = (props) => {
		if (props.rowData?.type === "GeneDiseaseAnnotation") return 'gene';
		if (props.rowData?.type === "AlleleDiseaseAnnotation") return 'allele';
		if (props.rowData?.type === "AGMDiseaseAnnotation") return 'agm';
		return 'biologicalentity';
	};

	const getSubjectAutocompleteFields = (props) => {
		let subjectFields = ["curie", "crossReferences.referencedCurie", "secondaryIdentifiers"];
		if (props.rowData.type === "AGMDiseaseAnnotation") {
			subjectFields.push("name");
		} else if (props.rowData.type === "AlleleDiseaseAnnotation") {
			subjectFields.push("alleleFullName.displayText", "alleleSymbol.displayText", "alleleSynonyms.displayText");
		} else if (props.rowData.type === "GeneDiseaseAnnotation") {
			subjectFields.push("geneFullName.displayText", "geneSymbol.displayText", "geneSynonyms.displayText");
		}
		return subjectFields;
	};
	const onSgdStrainBackgroundValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "sgdStrainBackground", setFieldValue);
	};

	const sgdStrainBackgroundSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["name", "curie", "crossReferences.referencedCurie", "secondaryIdentifiers"];
		const endpoint = "agm";
		const filterName = "sgdStrainBackgroundFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			taxonFilter: {
				"taxon.name": {
					queryString: "Saccharomyces cerevisiae"
				}
			},
		}
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const sgdStrainBackgroundEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					rowProps={props}
					initialValue={props.rowData.sgdStrainBackground?.curie}
					search={sgdStrainBackgroundSearch}
					searchService={searchService}
					fieldName='sgdStrainBackground'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onSgdStrainBackgroundValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"sgdStrainBackground"}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={"sgdStrainBackground"}
				/>
			</>
		);
	};

	const onGeneticModifierValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "diseaseGeneticModifier", setFieldValue);
	};

	const geneticModifierSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["geneSymbol.displayText", "geneFullName.displayText", "geneSynonyms.displayText", "alleleSymbol.displayText", "alleleFullName.displayText", "alleleSynonyms.displayText", "name", "curie", "crossReferences.referencedCurie", "secondaryIdentifiers"];
		const endpoint = "biologicalentity";
		const filterName = "geneticModifierFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const geneticModifierEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={geneticModifierSearch}
					initialValue={props.rowData.diseaseGeneticModifier?.curie}
					rowProps={props}
					fieldName='diseaseGeneticModifier'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onGeneticModifierValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"diseaseGeneticModifier"}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={"diseaseGeneticModifier"}
				/>
			</>
		);
	};

	const onAssertedAlleleValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "assertedAllele", setFieldValue);
	};

	const assertedAlleleSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["alleleSymbol.displayText", "alleleFullName.displayText", "curie", "crossReferences.referencedCurie", "secondaryIdentifiers", "alleleSynonyms.displayText"];
		const endpoint = "allele";
		const filterName = "assertedAlleleFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const assertedAlleleEditorTemplate = (props) => {
		if (props.rowData.type === "AGMDiseaseAnnotation") {
			return (
				<>
					<AutocompleteEditor
						search={assertedAlleleSearch}
						initialValue={props.rowData.assertedAllele?.curie}
						rowProps={props}
						fieldName='assertedAllele'
						valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
							<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
						onValueChangeHandler={onAssertedAlleleValueChange}
					/>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={"assertedAllele"}
					/>
					<ErrorMessageComponent
						errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
						errorField={"assertedAllele"}
					/>
				</>
			);
		} else {
			return null;
		}
	};

	const onDiseaseValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "object", setFieldValue);
	};

	const diseaseSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["curie", "name", "crossReferences.referencedCurie", "secondaryIdentifiers", "synonyms.name"];
		const endpoint = "doterm";
		const filterName = "diseaseFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			obsoleteFilter: {
				"obsolete": {
					queryString: false
				}
			}
		}
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const diseaseEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={diseaseSearch}
					initialValue={props.rowData.object?.curie}
					rowProps={props}
					fieldName='object'
					onValueChangeHandler={onDiseaseValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"object"}
				/>
			</>
		);
	};

	const onAssertedGeneValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "assertedGenes", setFieldValue);
	};

	const assertedGenesSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["geneSymbol.displayText", "geneFullName.displayText", "curie", "crossReferences.referencedCurie", "secondaryIdentifiers", "geneSynonyms.displayText"];
		const endpoint = "gene";
		const filterName = "assertedGenesFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const assertedGenesEditorTemplate = (props) => {
		if (props.rowData.type === "GeneDiseaseAnnotation") {
			return null;
		} else {
			return (
				<>
					<AutocompleteMultiEditor
						search={assertedGenesSearch}
						initialValue={props.rowData.assertedGenes}
						rowProps={props}
						fieldName='assertedGenes'
						valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
							<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
						onValueChangeHandler={onAssertedGeneValueChange}
					/>
					<ErrorMessageComponent
						errorMessages={errorMessagesRef.current[props.rowIndex]}
						errorField={"assertedGenes"}
					/>
				</>
			);
		}
	};

	const onWithValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "with", setFieldValue);
	};

	const withSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["geneSymbol.displayText", "geneFullName.displayText", "curie", "crossReferences.referencedCurie", "secondaryIdentifiers", "geneSynonyms.displayText"];
		const endpoint = "gene";
		const filterName = "withFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			taxonFilter: {
				"taxon.curie": {
					queryString: "NCBITaxon:9606"
				}
			},
		}

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}
	const withEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={withSearch}
					initialValue={props.rowData.with}
					rowProps={props}
					fieldName='with'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onWithValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField="with"
				/>
			</>
		);
	};

	const onEvidenceValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "evidenceCodes", setFieldValue);
	};

	const evidenceSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["curie", "name", "abbreviation"];
		const endpoint = "ecoterm";
		const filterName = "evidenceFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		const otherFilters = {
			obsoleteFilter: {
				"obsolete": {
					queryString: false
				}
			},
			subsetFilter: {
				"subsets": {
					queryString: "agr_eco_terms"
				}
			}
		}

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const evidenceEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={evidenceSearch}
					initialValue={props.rowData.evidenceCodes}
					rowProps={props}
					fieldName='evidenceCodes'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<EvidenceAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onEvidenceValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField="evidenceCodes"
				/>
			</>
		);
	};

	const subjectBodyTemplate = (rowData) => {
		if (rowData.subject) {
			if (rowData.subject.geneSymbol) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${rowData.subject.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.subject.geneSymbol.displayText + ' (' + rowData.subject.curie + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${rowData.subject.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.subject.geneSymbol.displayText + ' (' + rowData.subject.curie + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else if (rowData.subject.alleleSymbol) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${rowData.subject.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.subject.alleleSymbol.displayText + ' (' + rowData.subject.curie + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${rowData.subject.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.subject.alleleSymbol.displayText + ' (' + rowData.subject.curie + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else if (rowData.subject.geneFullName) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${rowData.subject.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.subject.geneFullName.displayText + ' (' + rowData.subject.curie + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${rowData.subject.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.subject.geneFullName.displayText + ' (' + rowData.subject.curie + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else if (rowData.subject.alleleFullName) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${rowData.subject.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.subject.alleleFullName.displayText + ' (' + rowData.subject.curie + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${rowData.subject.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.subject.alleleFullName.displayText + ' (' + rowData.subject.curie + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else if (rowData.subject.name) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${rowData.subject.curie.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.subject.name + ' (' + rowData.subject.curie + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${rowData.subject.curie.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.subject.name + ' (' + rowData.subject.curie + ')'
								}}
							/>
						</Tooltip>
					</>
				)
			} else {
				return <div className='overflow-hidden text-overflow-ellipsis' >{rowData.subject.curie}</div>;
			}
		}
	};

	const sgdStrainBackgroundBodyTemplate = (rowData) => {
		if (rowData.sgdStrainBackground) {
			if (rowData.sgdStrainBackground.name) {
				return <div className='overflow-hidden text-overflow-ellipsis'
					dangerouslySetInnerHTML={{
						__html: rowData.sgdStrainBackground.name + ' (' + rowData.sgdStrainBackground.curie + ')'
					}}
				/>;
			} else {
				return <div className='overflow-hidden text-overflow-ellipsis' >{rowData.sgdStrainBackground.curie}</div>;
			}
		}
	};

	const geneticModifierBodyTemplate = (rowData) => {
		if (rowData.diseaseGeneticModifier) {
			if (rowData.diseaseGeneticModifier.geneSymbol || rowData.diseaseGeneticModifier.alleleSymbol) {
				let symbolValue = rowData.diseaseGeneticModifier.geneSymbol ? rowData.diseaseGeneticModifier.geneSymbol.displayText : rowData.diseaseGeneticModifier.alleleSymbol.displayText;
				return <div className='overflow-hidden text-overflow-ellipsis'
					dangerouslySetInnerHTML={{
						__html: symbolValue + ' (' + rowData.diseaseGeneticModifier.curie + ')'
					}}
				/>;
			} else if (rowData.diseaseGeneticModifier.name) {
				return <div className='overflow-hidden text-overflow-ellipsis'
					dangerouslySetInnerHTML={{
						__html: rowData.diseaseGeneticModifier.name + ' (' + rowData.diseaseGeneticModifier.curie + ')'
					}}
				/>;
			} else {
				return <div className='overflow-hidden text-overflow-ellipsis' >{rowData.diseaseGeneticModifier.curie}</div>;
			}
		}
	};

	const onReferenceValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "singleReference", setFieldValue);
	};

	const referenceSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["curie", "cross_references.curie"];
		const endpoint = "literature-reference";
		const filterName = "curieFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const referenceEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={referenceSearch}
					initialValue={() => getRefString(props.rowData.singleReference)}
					rowProps={props}
					fieldName='singleReference'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onReferenceValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"singleReference"}
				/>
			</>
		);
	};

	const uniqueIdBodyTemplate = (rowData) => {
		return (
			//the 'a' at the start is a hack since css selectors can't start with a number
			<>
				<EllipsisTableCell otherClasses={`a${rowData.id}`}>
					{rowData.uniqueId}
				</EllipsisTableCell>
				<Tooltip target={`.a${rowData.id}`} content={rowData.uniqueId} />
			</>
		)
	};

	const modEntityIdBodyTemplate = (rowData) => {
		return (
			//the 'a' at the start is a hack since css selectors can't start with a number
			<>
				<EllipsisTableCell otherClasses={`a${rowData.id}`}>
					{rowData.modEntityId}
				</EllipsisTableCell>
				<Tooltip target={`.a${rowData.id}`} content={rowData.modEntityId} />
			</>
		)
	};

	const sgdStrainBackgroundEditorSelector = (props) => {
		if (props.rowData.type === "GeneDiseaseAnnotation") {
			return sgdStrainBackgroundEditorTemplate(props);
		}
		else {
			return null;
		}
	}

	const columns = [{
		field: "uniqueId",
		header: "Unique ID",
		body: uniqueIdBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
	},
	{
		field: "modEntityId",
		header: "MOD Annotation ID",
		body: modEntityIdBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
	},
	{
		field: "subject.symbol",
		header: "Subject",
		body: subjectBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.subjectFieldConfig,
		editor: (props) => subjectEditorTemplate(props),
	},
	{
		field: "diseaseRelation.name",
		header: "Disease Relation",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.diseaseRelationFilterConfig,
		editor: (props) => diseaseRelationEditor(props)
	},
	{
		field: "negated",
		header: "Negated",
		body: negatedTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.negatedFilterConfig,
		editor: (props) => negatedEditor(props)
	},
	{
		field: "object.name",
		header: "Disease",
		body: diseaseBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.objectFilterConfig,
		editor: (props) => diseaseEditorTemplate(props),
	},
	{
		field: "singleReference.curie",
		header: "Reference",
		body: singleReferenceBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.singleReferenceFilterConfig,
		editor: (props) => referenceEditorTemplate(props),
		
	},
	{
		field: "evidenceCodes.abbreviation",
		header: "Evidence Code",
		body: evidenceTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.evidenceCodesFilterConfig,
		editor: (props) => evidenceEditorTemplate(props)
	},
	{
		field: "with.geneSymbol.displayText",
		header: "With",
		body: withTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.withFilterConfig,
		editor: (props) => withEditorTemplate(props)
	},
	{
		field: "relatedNotes.freeText",
		header: "Related Notes",
		body: relatedNotesTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.relatedNotesFilterConfig,
		editor: relatedNotesEditor
	},
	{
		field: "conditionRelations.handle",
		header: "Experiments",
		body: conditionRelationHandleTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.daConditionRelationsHandleFilterConfig,
		editor: (props) => conditionRelationHandleEditor(props)
	},
	{
		field: "conditionRelations.uniqueId",
		header: "Experimental Conditions",
		body: conditionRelationsTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.daConditionRelationsSummaryFilterConfig,
		editor: (props) => conditionRelationsEditor(props)
	},
	{
		field: "geneticSex.name",
		header: "Genetic Sex",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.geneticSexFilterConfig,
		editor: (props) => geneticSexEditor(props)
	},
	{
		field: "diseaseQualifiers.name",
		header: "Disease Qualifiers",
		body: diseaseQualifiersBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.diseaseQualifiersFilterConfig,
		editor: (props) => diseaseQualifiersEditor(props)
	},
	{
		field: "sgdStrainBackground.name",
		header: "SGD Strain Background",
		body: sgdStrainBackgroundBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.sgdStrainBackgroundFilterConfig,
		editor: (props) => sgdStrainBackgroundEditorSelector(props)
	},
	{
		field: "annotationType.name",
		header: "Annotation Type",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.annotationTypeFilterConfig,
		editor: (props) => annotationTypeEditor(props)
	},
	{
		field: "diseaseGeneticModifierRelation.name",
		header: "Genetic Modifier Relation",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.geneticModifierRelationFilterConfig,
		editor: (props) => geneticModifierRelationEditor(props)
	},
	{
		field: "diseaseGeneticModifier.symbol",
		header: "Genetic Modifier",
		body: geneticModifierBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.geneticModifierFilterConfig,
		editor: (props) => geneticModifierEditorTemplate(props),
	},
	{
		field: "inferredGene.geneSymbol.displayText",
		header: "Inferred Gene",
		body: inferredGeneBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.inferredGeneFilterConfig,
	},
	{
		field: "assertedGenes.geneSymbol.displayText",
		header: "Asserted Genes",
		body: assertedGenesBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.assertedGenesFilterConfig,
		editor: (props) => assertedGenesEditorTemplate(props),
	},
	{
		field: "inferredAllele.alleleSymbol.displayText",
		header: "Inferred Allele",
		body: inferredAlleleBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.inferredAlleleFilterConfig,
	},
	{
		field: "assertedAllele.alleleSymbol.displayText",
		header: "Asserted Allele",
		body: assertedAlleleBodyTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.assertedAlleleFilterConfig,
		editor: (props) => assertedAlleleEditorTemplate(props),
	},
	{
		field: "dataProvider.sourceOrganization.abbreviation",
		header: "Data Provider",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.dataProviderFilterConfig,
	},
	{
		field: "secondaryDataProvider.sourceOrganization.abbreviation",
		header: "Secondary Data Provider",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.secondaryDataProviderFilterConfig,
	},
	{
		field: "updatedBy.uniqueId",
		header: "Updated By",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
	},
	{
		field: "dateUpdated",
		header: "Date Updated",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
	},
	{
		field: "createdBy.uniqueId",
		header: "Created By",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.createdByFilterConfig,
	},
	{
		field: "dateCreated",
		header: "Date Created",
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
	},
	{
		field: "internal",
		header: "Internal",
		body: internalTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.internalFilterConfig,
		editor: (props) => internalEditor(props)
	},
	{
		field: "obsolete",
		header: "Obsolete",
		body: obsoleteTemplate,
		sortable: isEnabled,
		filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
		editor: (props) => obsoleteEditor(props)
	}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const initialTableState = getDefaultTableState("DiseaseAnnotations", defaultColumnNames);

	const headerButtons = () => {
		return (
			<>
				<Button label="New Annotation" icon="pi pi-plus" onClick={handleNewAnnotationOpen} />&nbsp;&nbsp;
			</>
		);
	};

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					endpoint="disease-annotation"
					tableName="Disease Annotations"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					isEditable={true}
					sortMapping={sortMapping}
					mutation={mutation}
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					initialColumnWidth={10}
					errorObject={{errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages}}
					headerButtons={headerButtons}
					newEntity={newDiseaseAnnotation}
					deletionEnabled={true}
					deletionMethod={diseaseAnnotationService.deleteDiseaseAnnotation}
					deprecationMethod={diseaseAnnotationService.deprecateDiseaseAnnotation}
					deprecateOption={true}
					modReset={true}
				/>
			</div>
			<NewAnnotationForm
				newAnnotationState={newAnnotationState}
				newAnnotationDispatch={newAnnotationDispatch}
				searchService={searchService}
				diseaseRelationsTerms={diseaseRelationsTerms}
				negatedTerms={booleanTerms}
				setNewDiseaseAnnotation={setNewDiseaseAnnotation}
			/>
			<RelatedNotesDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
			<ConditionRelationsDialog
				originalConditionRelationsData={conditionRelationsData}
				setOriginalConditionRelationsData={setConditionRelationsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
