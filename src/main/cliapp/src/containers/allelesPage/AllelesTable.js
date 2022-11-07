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
import { AutocompleteEditor } from '../../components/Autocomplete/AutocompleteEditor';
import { InputTextEditor } from '../../components/InputTextEditor';
import { LiteratureAutocompleteTemplate } from '../../components/Autocomplete/LiteratureAutocompleteTemplate';

import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import {defaultAutocompleteOnChange, autocompleteSearch, buildAutocompleteFilter, getRefStrings, multipleAutocompleteOnChange} from '../../utils/utils';
import {AutocompleteMultiEditor} from "../../components/Autocomplete/AutocompleteMultiEditor";

export const AllelesTable = () => {

	const [isEnabled, setIsEnabled] = useState(true);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
	const sequencingStatusTerms = useControlledVocabularyService('Sequencing status vocabulary');
	const inCollectionTerms = useControlledVocabularyService('Allele collection vocabulary');
	const inheritanceModeTerms = useControlledVocabularyService('Allele inheritance mode vocabulary');

	const searchService = new SearchService();
	let alleleService = new AlleleService();

	const aggregationFields = [
		'inCollection.name', 'sequencingStatus.name', 'inheritanceMode.name'
	];

	const mutation = useMutation(updatedAllele => {
		if (!alleleService) {
			alleleService = new AlleleService();
		}
		return alleleService.saveAllele(updatedAllele);
	});

	const symbolTemplate = (rowData) => {
		if (rowData?.symbol) {
			return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.symbol }} />
		}
	}

	const nameTemplate = (rowData) => {
		if (rowData?.name) {
			return <div className='overflow-hidden text-overflow-ellipsis' dangerouslySetInnerHTML={{ __html: rowData.name }} />
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

	const onInCollectionEditorValueChange = (props, event) => {
		let updatedAlleles = [...props.props.value];
		updatedAlleles[props.rowIndex].inCollection = event.value;
	};

	const inCollectionEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="inCollection"
					options={inCollectionTerms}
					editorChange={onInCollectionEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"inCollection"} />
			</>
		);
	};

	const onSequencingStatusEditorValueChange = (props, event) => {
		let updatedAlleles = [...props.props.value];
		updatedAlleles[props.rowIndex].sequencingStatus = event.value;
	};

	const sequencingStatusEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="sequencingStatus"
					options={sequencingStatusTerms}
					editorChange={onSequencingStatusEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"sequencingStatus"} />
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
		const otherFilters = {
			obsoleteFilter: {
				"obsolete": {
					queryString: false
				}
			}
		}
		setQuery(event.query);
		const filter = buildAutocompleteFilter(event, autocompleteFields);
		autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
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

	const freeTextEditor = (props, fieldname) => {
		return (
			<>
				<InputTextEditor
					rowProps={props}
					fieldName={fieldname}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={fieldname} />
			</>
		);
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
			field: "name",
			header: "Name",
			body: nameTemplate,
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "nameFilter", fields: ["name"]},
			editor: (props) => freeTextEditor(props, "name")
		},
		{
			field: "symbol",
			header: "Symbol",
			body: symbolTemplate,
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "input", filterName: "symbolFilter", fields: ["symbol"]},
			editor: (props) => freeTextEditor(props, "symbol")
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
			filterElement: {type: "multiselect", filterName: "inCollectionFilter", fields: ["inCollection.name"], useKeywordFields: true},
			editor: (props) => inCollectionEditor(props)
		},
		{
			field: "sequencingStatus.name",
			header: "Sequencing Status",
			sortable: isEnabled,
			filter: true,
			filterElement: {type: "multiselect", filterName: "sequencingStatusFilter", fields: ["sequencingStatus.name"], useKeywordFields: true},
			editor: (props) => sequencingStatusEditor(props)
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
	);
};
