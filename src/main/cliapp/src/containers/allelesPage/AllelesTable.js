import React, { useRef, useState } from 'react';
import { useMutation } from 'react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { ListTableCell } from '../../components/ListTableCell';
import { internalTemplate, obsoleteTemplate } from '../../components/AuditedObjectComponent';
import { TrueFalseDropdown } from '../../components/TrueFalseDropDownSelector';
import { ErrorMessageComponent } from '../../components/ErrorMessageComponent';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { AlleleService } from '../../service/AlleleService';
import { SearchService } from '../../service/SearchService';
import { MutationTypesDialog } from './MutationTypesDialog';
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';
import { VocabTermAutocompleteTemplate } from '../../components/Autocomplete/VocabTermAutocompleteTemplate';

import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { defaultAutocompleteOnChange, autocompleteSearch, buildAutocompleteFilter, getRefStrings, multipleAutocompleteOnChange } from '../../utils/utils';
import { AutocompleteMultiEditor } from "../../components/Autocomplete/AutocompleteMultiEditor";

export const AllelesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [mutationTypesData, setMutationTypesData] = useState({
		mutationTypes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const inheritanceModeTerms = useControlledVocabularyService('Allele inheritance mode vocabulary');

	const searchService = new SearchService();
	let alleleService = new AlleleService();

	const aggregationFields = [
		'inheritanceMode.name'
	];

	const mutation = useMutation(updatedAllele => {
		if (!alleleService) {
			alleleService = new AlleleService();
		}
		return alleleService.saveAllele(updatedAllele);
	});

	const symbolTemplate = (rowData) => {
		if (rowData?.alleleSymbol) {
			return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.alleleSymbol.displayText }} />
		}
	}

	const nameTemplate = (rowData) => {
		if (rowData?.alleleFullName) {
			return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.alleleFullName.displayText }} />
		}
	}

	const taxonTemplate = (rowData) => {
		if (rowData?.taxon) {
			return (
				<>
					<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`}>
						{rowData.taxon.name} ({rowData.taxon.curie})
					</EllipsisTableCell>
					<Tooltip target={`.${"TAXON_NAME_"}${rowData.curie.replace(':', '')}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
				</>
			);
		}
	}

	const onInheritanceModeEditorValueChange = (props, event) => {
		let updatedAlleles = [...props.props.value];
		updatedAlleles[props.rowIndex].inheritanceMode = event.value;
	};

	const inheritanceModeEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="inheritanceMode"
					options={inheritanceModeTerms}
					editorChange={onInheritanceModeEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"inheritanceMode"} />
			</>
		);
	};

	const onInCollectionValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "inCollection", setFieldValue, "name");
	};

	const inCollectionEditor = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={inCollectionSearch}
					initialValue={props.rowData.inCollection?.name}
					rowProps={props}
					fieldName='inCollection'
					onValueChangeHandler={onInCollectionValueChange}
					valueDisplay={(item, setAutocompleteSelectedItem, op, query) =>
						<VocabTermAutocompleteTemplate item={item} op={op} query={query} setAutocompleteSelectedItem={setAutocompleteSelectedItem}/>}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField='inCollection'
				/>
			</>
		);
	};

	const isExtinctTemplate = (rowData) => {
		if (rowData && rowData.isExtinct !== null && rowData.isExtinct !== undefined) {
			return <EllipsisTableCell>{JSON.stringify(rowData.isExtinct)}</EllipsisTableCell>;
		}
	};

	const referencesTemplate = (rowData) => {
		if (rowData && rowData.references && rowData.references.length > 0) {
			const refStrings = getRefStrings(rowData.references);
			const listTemplate = (item) => {
				return (
					<EllipsisTableCell>
						{item}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<div className={`${rowData.curie.replace(':','')}${rowData.references[0].curie.replace(':', '')}`}>
						<ListTableCell template={listTemplate} listData={refStrings}/>
					</div>
					<Tooltip target={`.${rowData.curie.replace(':','')}${rowData.references[0].curie.replace(':', '')}`} style={{ width: '450px', maxWidth: '450px' }} position='left'>
						<ListTableCell template={listTemplate} listData={refStrings}/>
					</Tooltip>
				</>
			);

		}
	};

	const inCollectionSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["name"];
		const endpoint = "vocabularyterm";
		const filterName = "taxonFilter";
		const otherFilters = {
			vocabularyFilter: {
				"vocabulary.name": {
					queryString: "Allele collection vocabulary"
				}
			}
		}
		setQuery(event.query);
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
	}

	const onReferenceValueChange = (event, setFieldValue, props) => {
		multipleAutocompleteOnChange(props, event, "references", setFieldValue);
	};

	const referenceSearch = (event, setFiltered, setInputValue) => {
		const autocompleteFields = ["curie", "cross_references.curie"];
		const endpoint = "literature-reference";
		const filterName = "curieFilter";
		const filter = buildAutocompleteFilter(event, autocompleteFields);

		setInputValue(event.query);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const referencesEditor = (props) => {
		return (
			<>
				<AutocompleteMultiEditor
					search={referenceSearch}
					initialValue={props.rowData.references}
					rowProps={props}
					fieldName='references'
					valueDisplay={(item, setAutocompleteHoverItem, op, query) =>
						<LiteratureAutocompleteTemplate item={item} setAutocompleteHoverItem={setAutocompleteHoverItem} op={op} query={query}/>}
					onValueChangeHandler={onReferenceValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField={"references"}
				/>
			</>
		);
	};

	const onTaxonValueChange = (event, setFieldValue, props) => {
		defaultAutocompleteOnChange(props, event, "taxon", setFieldValue);
	};

	const taxonSearch = (event, setFiltered, setQuery) => {
		const autocompleteFields = ["curie", "name", "crossReferences.curie", "secondaryIdentifiers", "synonyms.name"];
		const endpoint = "ncbitaxonterm";
		const filterName = "taxonFilter";
		setQuery(event.query);
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
	}

	const taxonEditor = (props) => {
		return (
			<>
				<AutocompleteEditor
					search={taxonSearch}
					initialValue={props.rowData.taxon?.curie}
					rowProps={props}
					fieldName='taxon'
					onValueChangeHandler={onTaxonValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField='taxon'
				/>
			</>
		);
	};

	const onInternalEditorValueChange = (props, event) => {
		let updatedAlleles = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAlleles[props.rowIndex].internal = JSON.parse(event.value.name);
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
		let updatedAlleles = [...props.props.value];
		if (event.value || event.value === '') {
			updatedAlleles[props.rowIndex].obsolete = JSON.parse(event.value.name);
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

	const onIsExtinctEditorValueChange = (props, event) => {
		let updatedAlleles = [...props.props.value];

		if (event.value && event.value !== '') {
			updatedAlleles[props.rowIndex].isExtinct = JSON.parse(event.value.name);
		} else {
			updatedAlleles[props.rowIndex].isExtinct = null;
		}
	};

	const isExtinctEditor = (props) => {
		return (
			<>
				<TrueFalseDropdown
					options={booleanTerms}
					editorChange={onIsExtinctEditorValueChange}
					props={props}
					field={"isExtinct"}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"isExtinct"} />
			</>
		);
	};

	const mutationTypesTemplate = (rowData) => {
		if (rowData?.alleleMutationTypes) {
			const mutationTypeSet = new Set();
			for(var i = 0; i < rowData.alleleMutationTypes.length; i++){
				if (rowData.alleleMutationTypes[i].mutationTypes) {
					for(var j = 0; j < rowData.alleleMutationTypes[i].mutationTypes.length; j++) {
						let mtString = rowData.alleleMutationTypes[i].mutationTypes[j].name + ' (' +
							rowData.alleleMutationTypes[i].mutationTypes[j].curie + ')';
						mutationTypeSet.add(mtString);
					}
				}
			}
			if (mutationTypeSet.size > 0) {
				const sortedMutationTypes = Array.from(mutationTypeSet).sort();
				const listTemplate = (item) => {
					return (
						<span style={{ textDecoration: 'underline' }}>
							{item && item}
						</span>
					);
				};
				return (
					<>
						<Button className="p-button-text"
							onClick={(event) => { handleMutationTypesOpen(event, rowData, false) }} >
							<ListTableCell template={listTemplate} listData={sortedMutationTypes}/>
						</Button>
					</>
				);
			}
		}
	};

	const mutationTypesEditor = (props) => {
		if (props?.rowData?.alleleMutationTypes) {
			return (
				<>
				<div>
					<Button className="p-button-text"
						onClick={(event) => { handleMutationTypesOpenInEdit(event, props, true) }} >
						<span style={{ textDecoration: 'underline' }}>
							{`Mutation Types(${props.rowData.alleleMutationTypes.length}) `}
							<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
						</span>&nbsp;&nbsp;&nbsp;&nbsp;
						<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px',	 }}/>
						<span className="exclamation-icon" data-pr-tooltip="Edits made to this field will only be saved to the database once the entire annotation is saved.">
							<i className="pi pi-exclamation-circle" style={{ 'fontSize': '1em' }}></i>
						</span>
					</Button>
				</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleMutationTypes"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		} else {
			return (
				<>
					<div>
						<Button className="p-button-text"
							onClick={(event) => { handleMutationTypesOpenInEdit(event, props, true) }} >
							<span style={{ textDecoration: 'underline' }}>
								Add Mutation Type
								<i className="pi pi-user-edit" style={{ 'fontSize': '1em' }}></i>
							</span>&nbsp;&nbsp;&nbsp;&nbsp;
							<Tooltip target=".exclamation-icon" style={{ width: '250px', maxWidth: '250px',	 }}/>
							<span className="exclamation-icon" data-pr-tooltip="Edits made to this field will only be saved to the database once the entire annotation is saved.">
								<i className="pi pi-exclamation-circle" style={{ 'fontSize': '1em' }}></i>
							</span>
						</Button>
					</div>
					<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"alleleMutationTypes"} style={{ 'fontSize': '1em' }}/>
				</>
			)
		}
	};



	const handleMutationTypesOpen = (event, rowData, isInEdit) => {
		let _mutationTypesData = {};
		_mutationTypesData["originalMutationTypes"] = rowData.alleleMutationTypes;
		_mutationTypesData["dialog"] = true;
		_mutationTypesData["isInEdit"] = isInEdit;
		setMutationTypesData(() => ({
			..._mutationTypesData
		}));
	};

	const handleMutationTypesOpenInEdit = (event, rowProps, isInEdit) => {
		const { rows } = rowProps.props;
		const { rowIndex } = rowProps;
		const index = rowIndex % rows;
		let _mutationTypesData = {};
		_mutationTypesData["originalMutationTypes"] = rowProps.rowData.alleleMutationTypes;
		_mutationTypesData["dialog"] = true;
		_mutationTypesData["isInEdit"] = isInEdit;
		_mutationTypesData["rowIndex"] = index;
		_mutationTypesData["mainRowProps"] = rowProps;
		setMutationTypesData(() => ({
			..._mutationTypesData
		}));
	};

	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: { isEnabled },
			filter: true,
			filterElement: {type: "input", filterName: "curieFilter", fields: ["curie"]},
		},
		{
			field: "alleleFullName.displayText",
			header: "Name",
			body: nameTemplate,
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "nameFilter", fields: ["alleleFullName.displayText", "alleleFullName.formatText"]}
		},
		{
			field: "alleleSymbol.displayText",
			header: "Symbol",
			body: symbolTemplate,
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "symbolFilter", fields: ["alleleSymbol.displayText", "alleleSymbol.formatText"]}
		},
		{
			field: "taxon.name",
			header: "Taxon",
			body: taxonTemplate,
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "taxonFilter", fields: ["taxon.curie","taxon.name"]},
			editor: (props) => taxonEditor(props)
		},
		{
			field: "alleleMutationTypes.mutationTypes.name",
			header: "Mutation Types",
			body: mutationTypesTemplate,
			editor: (props) => mutationTypesEditor(props),
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "mutationTypesFilter", fields: ["alleleMutationTypes.mutationTypes.curie", "alleleMutationTypes.mutationTypes.name", "alleleMutationTypes.evidence.curie"]}
		},
		{
			field: "references.curie",
			header: "References",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "referencesFilter", fields: ["references.curie", "references.crossReferences.curie"]},
			body: referencesTemplate,
			editor: (props) => referencesEditor(props)
		},
		{
			field: "inheritanceMode.name",
			header: "Inheritance Mode",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "multiselect", filterName: "inheritanceModeFilter", fields: ["inheritanceMode.name"], useKeywordFields: true},
			editor: (props) => inheritanceModeEditor(props)
		},
		{
			field: "inCollection.name",
			header: "In Collection",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "inCollectionFilter", fields: ["inCollection.name"], useKeywordFields: true},
			editor: (props) => inCollectionEditor(props)
		},
		{
			field: "isExtinct",
			header: "Is Extinct",
			body: isExtinctTemplate,
			filter: true,
			filterElement: {type: "dropdown", filterName: "isExtinctFilter", fields: ["isExtinct"], options: [{ text: "true" }, { text: "false" }], optionField: "text"},
			sortable: isEnabled,
			editor: (props) => isExtinctEditor(props)
		},{
			field: "updatedBy.uniqueId",
			header: "Updated By",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "updatedByFilter", fields: ["updatedBy.uniqueId"]},
		},
		{
			field: "dateUpdated",
			header: "Date Updated",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "dateUpdatedFilter", fields: ["dateUpdated"]},
		},
		{
			field: "createdBy.uniqueId",
			header: "Created By",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "createdByFilter", fields: ["createdBy.uniqueId"]},
		},
		{
			field: "dateCreated",
			header: "Date Created",
			sortable: isEnabled,
			filter: true,
			filterType: "Date",
			filterElement: {type: "input", filterName: "dateCreatedFilter", fields: ["dataCreated"]},
		},
		{
			field: "internal",
			header: "Internal",
			body: internalTemplate,
			filter: true,
			filterElement: {type: "dropdown", filterName: "internalFilter", fields: ["internal"], options: [{ text: "true" }, { text: "false" }], optionField: "text"},
			sortable: isEnabled,
			editor: (props) => internalEditor(props)
		},
		{
			field: "obsolete",
			header: "Obsolete",
			body: obsoleteTemplate,
			filter: true,
			filterElement: {type: "dropdown", filterName: "obsoleteFilter", fields: ["obsolete"], options: [{ text: "true" }, { text: "false" }], optionField: "text"},
			sortable: isEnabled,
			editor: (props) => obsoleteEditor(props)
		}
	];

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					dataKey="curie"
					endpoint="allele"
					tableName="Alleles"
					columns={columns}
					aggregationFields={aggregationFields}
					isEditable={true}
					mutation={mutation}
					isEnabled={isEnabled}
					setIsEnabled={setIsEnabled}
					toasts={{toast_topleft, toast_topright }}
					initialColumnWidth={10}
					errorObject = {{errorMessages, setErrorMessages}}
				/>
			</div>
			<MutationTypesDialog
				originalMutationTypesData={mutationTypesData}
				setOriginalMutationTypesData={setMutationTypesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};
