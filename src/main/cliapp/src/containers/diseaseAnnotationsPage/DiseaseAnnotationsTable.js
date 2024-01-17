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
import { RelatedNotesDialog } from '../../components/RelatedNotesDialog';
import { ConditionRelationsDialog } from './ConditionRelationsDialog';
import { WithTemplate } from '../../components/Templates/WithTemplate';
import { AssertedGenesBodyTemplate } from '../../components/Templates/AssertedGenesBodyTemplate';
import { EvidenceTemplate } from '../../components/Templates/EvidenceTemplate'; 
import { SingleReferenceBodyTemplate } from '../../components/Templates/SingleReferenceBodyTemplate'; 
import { InferredGeneBodyTemplate } from '../../components/Templates/InferredGeneBodyTemplate'; 
import { InferredAlleleBodyTemplate } from '../../components/Templates/InferredAlleleBodyTemplate'; 
import { AssertedAlleleBodyTemplate } from '../../components/Templates/AssertedAlleleBodyTemplate'; 
import { DiseaseQualifiersBodyTemplate } from '../../components/Templates/DiseaseQualifiersBodyTemplate'; 
import { NegatedTemplate } from '../../components/Templates/NegatedTemplate'; 



import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { ConditionRelationHandleDropdown } from '../../components/ConditionRelationHandleSelector';
import { ControlledVocabularyMultiSelectDropdown } from '../../components/ControlledVocabularyMultiSelector';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { Button } from 'primereact/button';
import { Tooltip } from 'primereact/tooltip';
import { getRefString, autocompleteSearch, buildAutocompleteFilter, defaultAutocompleteOnChange, multipleAutocompleteOnChange, getIdentifier } from '../../utils/utils';
import { useNewAnnotationReducer } from "./useNewAnnotationReducer";
import { NewAnnotationForm } from "./NewAnnotationForm";
import { internalTemplate, obsoleteTemplate } from '../../components/AuditedObjectComponent';
import { AutocompleteMultiEditor } from "../../components/Autocomplete/AutocompleteMultiEditor";
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';

export const DiseaseAnnotationsTable = () => {

	const [isInEditMode, setIsInEditMode] = useState(false); //needs better name
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

	const relationsTerms = useControlledVocabularyService('disease_relation');
	const geneticSexTerms = useControlledVocabularyService('genetic_sex');
	const annotationTypeTerms = useControlledVocabularyService('annotation_type')
	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const geneticModifierRelationTerms = useControlledVocabularyService('disease_genetic_modifier_relation');
	const diseaseQualifiersTerms = useControlledVocabularyService('disease_qualifier');

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
		'diseaseAnnotationObject.name': ['diseaseAnnotationObject.curie', 'diseaseAnnotationObject.namespace'],
		'diseaseAnnotationSubject.symbol': ['diseaseAnnotationSubject.name', 'diseaseAnnotationSubject.modEntityId'],
		'with.geneSymbol.displayText': ['with.geneFullName.displayText', 'with.modEntityId'],
		'sgdStrainBackground.name': ['sgdStrainBackground.modEntityId'],
		'diseaseGeneticModifier.symbol': ['diseaseGeneticModifier.name', 'diseaseGeneticModifier.modEntityId']
	};



	const mutation = useMutation(updatedAnnotation => {
		return diseaseAnnotationService.saveDiseaseAnnotation(updatedAnnotation);
	});

	const handleNewAnnotationOpen = () => {
		newAnnotationDispatch({type: "OPEN_DIALOG"})
	};

	const handleDuplication = (rowData) => {
		newAnnotationDispatch({type: "DUPLICATE_ROW", rowData});
		newAnnotationDispatch({type: "SET_IS_ENABLED", value: true});
		if(rowData.type === "AGMDiseaseAnnotation") {
			newAnnotationDispatch({type: "SET_IS_ASSERTED_GENE_ENABLED", value: true});
			newAnnotationDispatch({type: "SET_IS_ASSERTED_ALLELE_ENABLED", value: true});
		}

		if(rowData.type === "AlleleDiseaseAnnotation") {
			newAnnotationDispatch({type: "SET_IS_ASSERTED_GENE_ENABLED", value: true});
		}

		if(rowData.relatedNotes && rowData.relatedNotes.length > 0){
			newAnnotationDispatch({type: "SET_RELATED_NOTES_EDITING_ROWS", relatedNotes: rowData.relatedNotes})
		}

		if(rowData.conditionRelations && rowData.conditionRelations.length > 0){
			newAnnotationDispatch({type: "SET_CONDITION_RELATIONS_EDITING_ROWS", conditionRelations: rowData.conditionRelations})
		}

		handleNewAnnotationOpen();
	}

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

	const relatedNotesTemplate = (rowData) => {
		if (rowData?.relatedNotes) {
			return (
				<Button className="p-button-text"
					onClick={(event) => { handleRelatedNotesOpen(event, rowData, false) }} >
					<span className="-my-4 p-1 underline">
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
					<span className= "-my-4 p-1 underline">
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
					<span class= "-my-4 p-1 underline">
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
		if (rowData.diseaseAnnotationObject) {
			return (
				<>
					<EllipsisTableCell otherClasses={`a${rowData.id}${rowData.diseaseAnnotationObject.curie.replace(':', '')}`}>{rowData.diseaseAnnotationObject.name} ({rowData.diseaseAnnotationObject.curie})</EllipsisTableCell>
					<Tooltip target={`.a${rowData.id}${rowData.diseaseAnnotationObject.curie.replace(':', '')}`} content={`${rowData.diseaseAnnotationObject.name} (${rowData.diseaseAnnotationObject.curie})`} />
				</>
			)
		}
	};

	const onRelationEditorValueChange = (props, event) => {
		let updatedAnnotations = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAnnotations[props.rowIndex].relation = event.value;
		}
	};

	const relationEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="relation"
					options={relationsTerms}
					editorChange={onRelationEditorValueChange}
					props={props}
					showClear={false}
					placeholderText={props.rowData.relation.name}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"relation"} />
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
		defaultAutocompleteOnChange(props, event, "diseaseAnnotationSubject", setFieldValue, "modEntityId");
	};

	const subjectSearch = (event, setFiltered, setQuery, props) => {
		const autocompleteFields = getSubjectAutocompleteFields(props);
		const endpoint = getSubjectEndpoint(props);
		const filterName = "diseaseAnnotationSubjectFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setQuery(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const subjectEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteEditor
					initialValue={getIdentifier(props.rowData.diseaseAnnotationSubject)}
					search={subjectSearch}
					rowProps={props}
					searchService={searchService}
					subField='modEntityId'
					fieldName='diseaseAnnotationSubject'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onSubjectValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"diseaseAnnotationSubject"}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={"diseaseAnnotationSubject"}
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
		let subjectFields = ["curie", "modEntityId", "modInternalId", "crossReferences.referencedCurie"];
		if (props.rowData.type === "AGMDiseaseAnnotation") {
			subjectFields.push("name");
		} else if (props.rowData.type === "AlleleDiseaseAnnotation") {
			subjectFields.push("alleleFullName.formatText", "alleleFullName.displayText", "alleleSymbol.formatText", "alleleSymbol.displayText", "alleleSynonyms.formatText", "alleleSynonyms.displayText", "alleleSecondaryIds.secondaryId");
		} else if (props.rowData.type === "GeneDiseaseAnnotation") {
			subjectFields.push("geneFullName.formatText", "geneFullName.displayText", "geneSymbol.formatText", "geneSymbol.displayText", "geneSynonyms.formatText", "geneSynonyms.displayText", "geneSystematicName.formatText", "geneSystematicName.displayText", "geneSecondaryIds.secondaryId");
		}
		return subjectFields;
	};
	const onSgdStrainBackgroundValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "sgdStrainBackground", setFieldValue, "modEntityId");
	};

	const sgdStrainBackgroundSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["name", "curie", "modEntityId", "modInternalId", "crossReferences.referencedCurie"];
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
					initialValue={getIdentifier(props.rowData.sgdStrainBackground)}
					search={sgdStrainBackgroundSearch}
					searchService={searchService}
					fieldName='sgdStrainBackground'
					subField='modEntityId'
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

	const onGeneticModifiersValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "diseaseGeneticModifiers", setFieldValue);
	};

	const geneticModifiersSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["geneSymbol.formatText", "geneSymbol.displayText", "geneFullName.formatText", "geneFullName.displayText", "geneSynonyms.formatText", "geneSynonyms.displayText", "geneSystematicName.formatText", "geneSystematicName.displayText", "geneSecondaryIds.secondaryId", "alleleSymbol.formatText", "alleleFullName.formatText", "alleleFullName.displayText", "alleleSynonyms.formatText", "alleleSynonyms.displayText", "name", "curie", "modEntityId", "modInternalId", "crossReferences.referencedCurie", "alleleSecondaryIds.secondaryId"];
		const endpoint = "biologicalentity";
		const filterName = "geneticModifiersFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const geneticModifiersEditorTemplate = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={geneticModifiersSearch}
					initialValue={props.rowData.diseaseGeneticModifiers}
					rowProps={props}
					fieldName='diseaseGeneticModifiers'
					subField='modEntityId'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<SubjectAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onGeneticModifiersValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"diseaseGeneticModifiers"}
				/>
				<ErrorMessageComponent
					errorMessages={uiErrorMessagesRef.current[props.rowIndex]}
					errorField={"diseaseGeneticModifiers"}
				/>
			</>
		);
	};

	const onAssertedAlleleValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "assertedAllele", setFieldValue, "modEntityId");
	};

	const assertedAlleleSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["alleleSymbol.formatText", "alleleSymbol.displayText", "alleleFullName.formatText", "alleleFullName.displayText", "curie", "modEntityId", "modInternalId", "crossReferences.referencedCurie", "alleleSecondaryIds.secondaryId", "alleleSynonyms.formatText", "alleleSynonyms.displayText"];
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
						initialValue={getIdentifier(props.rowData.assertedAllele)}
						rowProps={props}
						fieldName='assertedAllele'
						subField='modEntityId'
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
		defaultAutocompleteOnChange(props, event, "diseaseAnnotationObject", setFieldValue);
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
					initialValue={props.rowData.diseaseAnnotationObject?.curie}
					rowProps={props}
					fieldName='diseaseAnnotationObject'
					onValueChangeHandler={onDiseaseValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"diseaseAnnotationObject"}
				/>
			</>
		);
	};

	const onAssertedGeneValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "assertedGenes", setFieldValue);
	};

	const assertedGenesSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["geneSymbol.formatText", "geneSymbol.displayText", "geneFullName.formatText", "geneFullName.displayText", "curie", "modEntityId", "modInternalId", "crossReferences.referencedCurie", "geneSynonyms.formatText", "geneSynonyms.displayText", "geneSystematicName.formatText", "geneSystematicName.displayText"];
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
						subField='modEntityId'
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
		const autocompleteFields = ["geneSymbol.formatText", "geneSymbol.displayText", "geneFullName.formatText", "geneFullName.displayText", "modEntityId", "modInternalId", "curie", "crossReferences.referencedCurie", "geneSynonyms.formatText", "geneSynonyms.displayText", "geneSystematicName.formatText", "geneSystematicName.displayText", "geneSecondaryIds.secondaryId"];
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
					subField='modEntityId'
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
		if (rowData.diseaseAnnotationSubject) {
			const identifier = getIdentifier(rowData.diseaseAnnotationSubject);
			if (rowData.diseaseAnnotationSubject.geneSymbol) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${identifier.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.geneSymbol.displayText + ' (' + identifier + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${identifier.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.geneSymbol.displayText + ' (' + identifier + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else if (rowData.diseaseAnnotationSubject.alleleSymbol) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${identifier.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.alleleSymbol.displayText + ' (' + identifier + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${identifier.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.alleleSymbol.displayText + ' (' + identifier + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else if (rowData.diseaseAnnotationSubject.geneFullName) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${identifier.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.geneFullName.displayText + ' (' + identifier + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${identifier.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.geneFullName.displayText + ' (' + identifier + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else if (rowData.diseaseAnnotationSubject.alleleFullName) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${identifier.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.alleleFullName.displayText + ' (' + identifier + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${identifier.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.alleleFullName.displayText + ' (' + identifier + ')'
							}}
							/>
						</Tooltip>
					</>
				)
			} else if (rowData.diseaseAnnotationSubject.name) {
				return (
					<>
						<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${identifier.replace(':', '')}`}
							dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.name + ' (' + identifier + ')'
							}}
						/>
						<Tooltip target={`.a${rowData.id}${identifier.replace(':', '')}`}>
							<div dangerouslySetInnerHTML={{
								__html: rowData.diseaseAnnotationSubject.name + ' (' + identifier + ')'
								}}
							/>
						</Tooltip>
					</>
				)
			} else {
				return <div className='overflow-hidden text-overflow-ellipsis' >{identifier}</div>;
			}
		}
	};

	const sgdStrainBackgroundBodyTemplate = (rowData) => {
		if (rowData.sgdStrainBackground) {
			const identifier = getIdentifier(rowData.sgdStrainBackground);
			if (rowData.sgdStrainBackground.name) {
				return <div className='overflow-hidden text-overflow-ellipsis'
					dangerouslySetInnerHTML={{
						__html: rowData.sgdStrainBackground.name + ' (' + identifier + ')'
					}}
				/>;
			} else {
				return <div className='overflow-hidden text-overflow-ellipsis' >{identifier}</div>;
			}
		}
	};

	const geneticModifiersBodyTemplate = (rowData) => {
		if (rowData?.diseaseGeneticModifiers && rowData.diseaseGeneticModifiers.length > 0) {
			const diseaseGeneticModifierStrings = [];
			rowData.diseaseGeneticModifiers.forEach((dgm) => {
				const identifier = getIdentifier(dgm);
				if (dgm.geneSymbol || dgm.alleleSymbol) {
					let symbolValue = dgm.geneSymbol ? dgm.geneSymbol.displayText : dgm.alleleSymbol.displayText;
					diseaseGeneticModifierStrings.push(symbolValue + ' (' + identifier + ')');
				} else if (dgm.name) {
					diseaseGeneticModifierStrings.push(dgm.name + ' (' + identifier + ')');
				} else {
					diseaseGeneticModifierStrings.push(identifier);
				}
			});
			const sortedDiseaseGeneticModifierStrings = diseaseGeneticModifierStrings.sort();
			const listTemplate = (dgmString) => {
				return (
					<EllipsisTableCell>
						<div dangerouslySetInnerHTML={{__html: dgmString}}/>
					</EllipsisTableCell>
				)
			};
			return (
				<>
					<div className={`-my-4 p-1 a${rowData.id}${getIdentifier(rowData.diseaseGeneticModifiers[0]).replace(':', '')}`}>
						<ListTableCell template={listTemplate} listData={sortedDiseaseGeneticModifierStrings}/>
					</div>
					<Tooltip target={`.a${rowData.id}${getIdentifier(rowData.diseaseGeneticModifiers[0]).replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={sortedDiseaseGeneticModifierStrings}/>
					</Tooltip>
				</>
			);
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

	//MOVE
	const uniqueIdBodyTemplate = (rowData) => {
		return (
			//the 'a' at the start is a hack since css selectors can't start with a number
			<>
				<EllipsisTableCell otherClasses={`c${rowData.id}`}>
					{rowData.uniqueId}
				</EllipsisTableCell>
				<Tooltip target={`.c${rowData.id}`} content={rowData.uniqueId} />
			</>
		)
	};

	const uniqueIdEditorTemplate = (props) => {
		return (
			<>
				<EllipsisTableCell otherClasses={`c${props.rowData.id}`}>
					{props.rowData.uniqueId}
				</EllipsisTableCell>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"uniqueId"}
				/>
			</>
		);
	};

	//MOVE
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

	//MOVE
	const modInternalIdBodyTemplate = (rowData) => {
		return (
			//the 'a' at the start is a hack since css selectors can't start with a number
			<>
				<EllipsisTableCell otherClasses={`b${rowData.id}`}>
					{rowData.modInternalId}
				</EllipsisTableCell>
				<Tooltip target={`.b${rowData.id}`} content={rowData.modInternalId} />
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
		sortable: true,
		filterConfig: FILTER_CONFIGS.uniqueidFilterConfig,
		editor: (props) => uniqueIdEditorTemplate(props)
	},
	{
		field: "modEntityId",
		header: "MOD Annotation ID",
		body: modEntityIdBodyTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
	},
	{
		field: "modInternalId",
		header: "MOD Internal ID",
		body: modInternalIdBodyTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
	},
	{
		field: "diseaseAnnotationSubject.symbol",
		header: "Subject",
		body: subjectBodyTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.diseaseAnnotationSubjectFieldConfig,
		editor: (props) => subjectEditorTemplate(props),
	},
	{
		field: "relation.name",
		header: "Disease Relation",
		sortable: true,
		filterConfig: FILTER_CONFIGS.relationFilterConfig,
		editor: (props) => relationEditor(props)
	},
	{
		field: "negated",
		header: "Negated",
		body: (rowData) => <NegatedTemplate rowData={rowData}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.negatedFilterConfig,
		editor: (props) => negatedEditor(props)
	},
	{
		field: "diseaseAnnotationObject.name",
		header: "Disease",
		body: diseaseBodyTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.diseaseAnnotationObjectFilterConfig,
		editor: (props) => diseaseEditorTemplate(props),
	},
	{
		field: "singleReference.primaryCrossReferenceCurie",
		header: "Reference",
		body: (rowData) => <SingleReferenceBodyTemplate rowData={rowData}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.singleReferenceFilterConfig,
		editor: (props) => referenceEditorTemplate(props),

	},
	{
		field: "evidenceCodes.abbreviation",
		header: "Evidence Code",
		body: (rowData) => <EvidenceTemplate rowData={rowData}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.evidenceCodesFilterConfig,
		editor: (props) => evidenceEditorTemplate(props)
	},
	{
		field: "with.geneSymbol.displayText",
		header: "With",
		body: (rowData) => <WithTemplate rowData={rowData}/>,
		sortable: true,
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
		sortable: true,
		filterConfig: FILTER_CONFIGS.geneticSexFilterConfig,
		editor: (props) => geneticSexEditor(props)
	},
	{
		field: "diseaseQualifiers.name",
		header: "Disease Qualifiers",
		body: (rowData) => <DiseaseQualifiersBodyTemplate rowData={rowData}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.diseaseQualifiersFilterConfig,
		editor: (props) => diseaseQualifiersEditor(props)
	},
	{
		field: "sgdStrainBackground.name",
		header: "SGD Strain Background",
		body: sgdStrainBackgroundBodyTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.sgdStrainBackgroundFilterConfig,
		editor: (props) => sgdStrainBackgroundEditorSelector(props)
	},
	{
		field: "annotationType.name",
		header: "Annotation Type",
		sortable: true,
		filterConfig: FILTER_CONFIGS.annotationTypeFilterConfig,
		editor: (props) => annotationTypeEditor(props)
	},
	{
		field: "diseaseGeneticModifierRelation.name",
		header: "Genetic Modifier Relation",
		sortable: true,
		filterConfig: FILTER_CONFIGS.geneticModifierRelationFilterConfig,
		editor: (props) => geneticModifierRelationEditor(props)
	},
	{
		field: "diseaseGeneticModifiers.symbol",
		header: "Genetic Modifiers",
		body: geneticModifiersBodyTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.geneticModifiersFilterConfig,
		editor: (props) => geneticModifiersEditorTemplate(props),
	},
	{
		field: "inferredGene.geneSymbol.displayText",
		header: "Inferred Gene",
		body: (rowData) => <InferredGeneBodyTemplate rowData={rowData}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.inferredGeneFilterConfig,
	},
	{
		field: "assertedGenes.geneSymbol.displayText",
		header: "Asserted Genes",
		body: (rowData) => <AssertedGenesBodyTemplate rowData={rowData}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.assertedGenesFilterConfig,
		editor: (props) => assertedGenesEditorTemplate(props),
	},
	{
		field: "inferredAllele.alleleSymbol.displayText",
		header: "Inferred Allele",
		body: (rowData) => <InferredAlleleBodyTemplate rowData={rowData}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.inferredAlleleFilterConfig,
	},
	{
		field: "assertedAllele.alleleSymbol.displayText",
		header: "Asserted Allele",
		body: (rowData) => <AssertedAlleleBodyTemplate rowData={rowData}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.assertedAlleleFilterConfig,
		editor: (props) => assertedAlleleEditorTemplate(props),
	},
	{
		field: "dataProvider.sourceOrganization.abbreviation",
		header: "Data Provider",
		sortable: true,
		filterConfig: FILTER_CONFIGS.diseaseDataProviderFilterConfig,
	},
	{
		field: "secondaryDataProvider.sourceOrganization.abbreviation",
		header: "Secondary Data Provider",
		sortable: true,
		filterConfig: FILTER_CONFIGS.secondaryDataProviderFilterConfig,
	},
	{
		field: "updatedBy.uniqueId",
		header: "Updated By",
		sortable: true,
		filterConfig: FILTER_CONFIGS.updatedByFilterConfig,
	},
	{
		field: "dateUpdated",
		header: "Date Updated",
		sortable: true,
		filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig,
	},
	{
		field: "createdBy.uniqueId",
		header: "Created By",
		sortable: true,
		filterConfig: FILTER_CONFIGS.createdByFilterConfig,
	},
	{
		field: "dateCreated",
		header: "Date Created",
		sortable: true,
		filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig,
	},
	{
		field: "internal",
		header: "Internal",
		body: internalTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.internalFilterConfig,
		editor: (props) => internalEditor(props)
	},
	{
		field: "obsolete",
		header: "Obsolete",
		body: obsoleteTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
		editor: (props) => obsoleteEditor(props)
	}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 10;
	});

	const initialTableState = getDefaultTableState("DiseaseAnnotations", defaultColumnNames, undefined, widthsObject);

	const headerButtons = (disabled=false) => {
		return (
			<>
				<Button label="New Annotation" icon="pi pi-plus" onClick={handleNewAnnotationOpen} disabled={disabled} />&nbsp;&nbsp;
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
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{toast_topleft, toast_topright }}
					errorObject={{errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages}}
					headerButtons={headerButtons}
					newEntity={newDiseaseAnnotation}
					deletionEnabled={true}
					deletionMethod={diseaseAnnotationService.deleteDiseaseAnnotation}
					deprecationMethod={diseaseAnnotationService.deprecateDiseaseAnnotation}
					deprecateOption={true}
					modReset={true}
					widthsObject={widthsObject}
					handleDuplication={handleDuplication}
					duplicationEnabled={true}
				/>
			</div>
			<NewAnnotationForm
				newAnnotationState={newAnnotationState}
				newAnnotationDispatch={newAnnotationDispatch}
				searchService={searchService}
				relationsTerms={relationsTerms}
				negatedTerms={booleanTerms}
				setNewDiseaseAnnotation={setNewDiseaseAnnotation}
			/>
			<RelatedNotesDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet='da_note_type'
				showReferences={false}
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

