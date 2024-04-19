import React, { useRef, useState } from 'react';
import { useMutation } from 'react-query';
import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { internalTemplate, obsoleteTemplate } from '../../components/AuditedObjectComponent';
import { ErrorMessageComponent } from '../../components/Error/ErrorMessageComponent';
import { VariantService } from '../../service/VariantService';
import { RelatedNotesDialog } from '../../components/RelatedNotesDialog';
import { TaxonTableEditor } from '../../components/Editors/taxon/TaxonTableEditor';
import { VariantTypeTableEditor } from '../../components/Editors/variantType/VariantTypeTableEditor';
import { SourceGeneralConsequenceTableEditor } from '../../components/Editors/sourceGeneralConsequence/SourceGeneralConsequenceTableEditor';
import { BooleanTableEditor } from '../../components/Editors/boolean/BooleanTableEditor';

import { Tooltip } from 'primereact/tooltip';
import { Toast } from 'primereact/toast';
import { Button } from 'primereact/button';
import { EditMessageTooltip } from '../../components/EditMessageTooltip';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { ControlledVocabularyDropdown } from '../../components/ControlledVocabularySelector';
import { CrossReferencesTemplate } from '../../components/Templates/CrossReferencesTemplate';

export const VariantsTable = () => {

	const [isInEditMode, setIsInEditMode] = useState(false);
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;
	
	const [relatedNotesData, setRelatedNotesData] = useState({
		relatedNotes: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mainRowProps: {},
	});

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	let variantService = new VariantService();

	const mutation = useMutation(updatedVariant => {
		if (!variantService) {
			variantService = new VariantService();
		}
		return variantService.saveVariant(updatedVariant);
	});

	const variantStatusTerms = useControlledVocabularyService('variant_status');

	const taxonTemplate = (rowData) => {
		if (rowData?.taxon) {
			return (
				<>
					<EllipsisTableCell otherClasses={`${"TAXON_NAME_"}${rowData.id}${rowData.taxon.curie.replace(':', '')}`}>
						{rowData.taxon.name} ({rowData.taxon.curie})
					</EllipsisTableCell>
					<Tooltip target={`.${"TAXON_NAME_"}${rowData.id}${rowData.taxon.curie.replace(':', '')}`} content= {`${rowData.taxon.name} (${rowData.taxon.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
				</>
			);
		}
	}

	const sourceGeneralConsequenceTemplate = (rowData) => {
		if (rowData?.sourceGeneralConsequence) {
			return (
				<>
					<EllipsisTableCell otherClasses={`${"SGC_"}${rowData.id}${rowData.sourceGeneralConsequence.curie.replace(':', '')}`}>
						{rowData.sourceGeneralConsequence?.name} ({rowData.sourceGeneralConsequence?.curie})
					</EllipsisTableCell>
					<Tooltip target={`.${"SGC_"}${rowData.id}${rowData.sourceGeneralConsequence?.curie.replace(':', '')}`} content= {`${rowData.sourceGeneralConsequence?.name} (${rowData.sourceGeneralConsequence?.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
				</>
			);
		}
	}

	const variantTypeTemplate = (rowData) => {
		if (rowData?.variantType) {
			return (
				<>
					<EllipsisTableCell otherClasses={`${"SGC_"}${rowData.id}${rowData.variantType.curie.replace(':', '')}`}>
						{rowData.variantType?.name} ({rowData.variantType?.curie})
					</EllipsisTableCell>
					<Tooltip target={`.${"SGC_"}${rowData.id}${rowData.variantType?.curie.replace(':', '')}`} content= {`${rowData.variantType?.name} (${rowData.variantType?.curie})`} style={{ width: '250px', maxWidth: '450px' }}/>
				</>
			);
		}
	}

	const onVariantStatusEditorValueChange = (props, event) => {
		let updatedVariants = [...props.props.value];
		updatedVariants[props.rowIndex].variantStatus = event.value;
	};

	const variantStatusEditor = (props) => {
		return (
			<>
				<ControlledVocabularyDropdown
					field="variantStatus"
					options={variantStatusTerms}
					editorChange={onVariantStatusEditorValueChange}
					props={props}
					showClear={true}
				/>
				<ErrorMessageComponent errorMessages={errorMessagesRef.current[props.rowIndex]} errorField={"geneticSex"} />
			</>
		);
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
					<EditMessageTooltip object="variant"/>
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

	
	const columns = [
		{
			field: "curie",
			header: "Curie",
			sortable: { isInEditMode },
			filterConfig: FILTER_CONFIGS.curieFilterConfig,
		},
		{
			field: "modEntityId",
			header: "MOD Entity ID",
			sortable:  true,
			filterConfig: FILTER_CONFIGS.modentityidFilterConfig,
		},
		{
			field: "modInternalId",
			header: "MOD Internal ID",
			sortable:  true,
			filterConfig: FILTER_CONFIGS.modinternalidFilterConfig,
		},
		{
			field: "taxon.name",
			header: "Taxon",
			body: taxonTemplate,
			sortable: true,
			filterConfig: FILTER_CONFIGS.taxonFilterConfig,
			editor: (props) => <TaxonTableEditor rowProps={props} errorMessagesRef={errorMessagesRef}/>
		},
		{
			field: "variantType.name",
			header: "Variant Type",
			body: variantTypeTemplate,
			sortable: true,
			filterConfig: FILTER_CONFIGS.variantTypeFilterConfig,
			editor: (props) => <VariantTypeTableEditor rowProps={props} errorMessagesRef={errorMessagesRef}/>
		},
		{
			field: "variantStatus.name",
			header: "Variant Status",
			sortable: true,
			filterConfig: FILTER_CONFIGS.variantStatusFilterConfig,
			editor: (props) => variantStatusEditor(props)
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
			field: "sourceGeneralConsequence.name",
			header: "Source General Consequence",
			body: sourceGeneralConsequenceTemplate,
			sortable: true,
			filterConfig: FILTER_CONFIGS.sourceGeneralConsequenceFilterConfig,
			editor: (props) => <SourceGeneralConsequenceTableEditor rowProps={props} errorMessagesRef={errorMessagesRef}/>
		},
		{
			field: "dataProvider.sourceOrganization.abbreviation",
			header: "Data Provider",
			sortable: true,
			filterConfig: FILTER_CONFIGS.variantDataProviderFilterConfig,
		},
		{
			field: "crossReferences.displayName",
			header: "Cross References",
			sortable: true,
			filterConfig: FILTER_CONFIGS.crossReferencesFilterConfig,
			body: (rowData) => <CrossReferencesTemplate xrefs={rowData.crossReferences}/>
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
			filter: true,
			filterConfig: FILTER_CONFIGS.dateUpdatedFilterConfig
		},
		{
			field: "createdBy.uniqueId",
			header: "Created By",
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.createdByFilterConfig
		},
		{
			field: "dateCreated",
			header: "Date Created",
			sortable: true,
			filter: true,
			filterConfig: FILTER_CONFIGS.dataCreatedFilterConfig
		},
		{
			field: "internal",
			header: "Internal",
			body: internalTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.internalFilterConfig,
			sortable: true,
			editor: (props) => (
				<BooleanTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} field={"internal"} showClear={false}/>
			)
		},
		{
			field: "obsolete",
			header: "Obsolete",
			body: obsoleteTemplate,
			filter: true,
			filterConfig: FILTER_CONFIGS.obsoleteFilterConfig,
			sortable: true,
			editor: (props) => (
				<BooleanTableEditor rowProps={props} errorMessagesRef={errorMessagesRef} field={"obsolete"}  showClear={false}/>
			)
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 10;
	});

	const initialTableState = getDefaultTableState("Variants", defaultColumnNames, undefined, widthsObject);

	return (
		<>
			<div className="card">
				<Toast ref={toast_topleft} position="top-left" />
				<Toast ref={toast_topright} position="top-right" />
				<GenericDataTable
					dataKey="id"
					endpoint="variant"
					tableName="Variants"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					isEditable={true}
					hasDetails={false}
					mutation={mutation}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					toasts={{toast_topleft, toast_topright }}
					errorObject = {{errorMessages, setErrorMessages}}
					widthsObject={widthsObject}
				/>
			</div>
			<RelatedNotesDialog
				originalRelatedNotesData={relatedNotesData}
				setOriginalRelatedNotesData={setRelatedNotesData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
				noteTypeVocabularyTermSet='variant_note_type'
			/>
		</>
	);
};
