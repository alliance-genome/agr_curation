import React, { useRef, useState } from 'react';

import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { ConditionRelationsDialog } from '../../components/ConditionRelationsDialog';
import { SingleReferenceTemplate } from '../../components/Templates/reference/SingleReferenceTemplate'; 
import { IdTemplate } from '../../components/Templates/IdTemplate'; 
import { GenomicEntityTemplate } from '../../components/Templates/genomicEntity/GenomicEntityTemplate'; 
import { GenomicEntityListTemplate } from '../../components/Templates/genomicEntity/GenomicEntityListTemplate';
import { BooleanTemplate } from '../../components/Templates/BooleanTemplate';
import { Button } from 'primereact/button';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';

export const PhenotypeAnnotationsTable = () => {

	const [isInEditMode, setIsInEditMode] = useState(false); //needs better name
	const [conditionRelationsData, setConditionRelationsData] = useState({
		conditionRelations: [],
		isInEdit: false,
		dialog: false,
		rowIndex: null,
		mailRowProps: {},
	});
	const [errorMessages, setErrorMessages] = useState({});
	const errorMessagesRef = useRef();
	errorMessagesRef.current = errorMessages;

	const [uiErrorMessages, setUiErrorMessages] = useState([]);
	const uiErrorMessagesRef = useRef();
	uiErrorMessagesRef.current = uiErrorMessages;

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);

	const sortMapping = {
		'phenotypeAnnotationObject.name': ['phenotypeAnnotationObject.curie', 'phenotypeAnnotationObject.namespace'],
		'phenotypeAnnotationSubject.symbol': ['phenotypeAnnotationSubject.name', 'phenotypeAnnotationSubject.modEntityId'],
		'sgdStrainBackground.name': ['sgdStrainBackground.modEntityId'],
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

	
	const columns = [{
		field: "uniqueId",
		header: "Unique ID",
		body: (rowData) => <IdTemplate id={rowData.uniqueId}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.uniqueidFilterConfig
	},
	{
		field: "phenotypeAnnotationSubject.symbol",
		header: "Subject",
		body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.phenotypeAnnotationSubject}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.phenotypeAnnotationSubjectFilterConfig
	},
	{
		field: "relation.name",
		header: "Phenotype Relation",
		sortable: true,
		filterConfig: FILTER_CONFIGS.paRelationFilterConfig
	},
	{
		field: "phenotypeAnnotationObject",
		header: "Phenotype",
		body: (rowData) => <IdTemplate id={rowData.phenotypeAnnotationObject}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.phenotypeAnnotationObjectFilterConfig
	},
	{
		field: "singleReference.primaryCrossReferenceCurie",
		header: "Reference",
		body: (rowData) => <SingleReferenceTemplate singleReference={rowData.singleReference}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.singleReferenceFilterConfig
	},
	{
		field: "conditionRelations.uniqueId",
		header: "Experimental Conditions",
		body: conditionRelationsTemplate,
		sortable: true,
		filterConfig: FILTER_CONFIGS.paConditionRelationsSummaryFilterConfig
	},
	{
		field: "sgdStrainBackground.name",
		header: "SGD Strain Background",
		body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.sgdStrainBackground}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.sgdStrainBackgroundFilterConfig
	},
	{
		field: "inferredGene.geneSymbol.displayText",
		header: "Inferred Gene",
		body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.inferredGene}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.inferredGeneFilterConfig,
	},
	{
		field: "assertedGenes.geneSymbol.displayText",
		header: "Asserted Genes",
		body: (rowData) => <GenomicEntityListTemplate genomicEntities={rowData.assertedGenes}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.assertedGenesFilterConfig
	},
	{
		field: "inferredAllele.alleleSymbol.displayText",
		header: "Inferred Allele",
		body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.inferredAllele}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.inferredAlleleFilterConfig,
	},
	{
		field: "assertedAllele.alleleSymbol.displayText",
		header: "Asserted Allele",
		body: (rowData) => <GenomicEntityTemplate genomicEntity={rowData.assertedAllele}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.assertedAlleleFilterConfig
	},
	{
		field: "dataProvider.sourceOrganization.abbreviation",
		header: "Data Provider",
		sortable: true,
		filterConfig: FILTER_CONFIGS.phenotypeDataProviderFilterConfig,
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
		body: (rowData) => <BooleanTemplate value={rowData.internal}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.internalFilterConfig
	},
	{
		field: "obsolete",
		header: "Obsolete",
		body: (rowData) => <BooleanTemplate value={rowData.obsolete}/>,
		sortable: true,
		filterConfig: FILTER_CONFIGS.obsoleteFilterConfig
	}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 10;
	});

	const initialTableState = getDefaultTableState("PhenotypeAnnotations", defaultColumnNames, undefined, widthsObject);

	return (
		<>
			<div className="card">
				<GenericDataTable
					endpoint="phenotype-annotation"
					tableName="Phenotype Annotations"
					columns={columns}
					defaultColumnNames={defaultColumnNames}
					initialTableState={initialTableState}
					toasts={{toast_topleft, toast_topright }}
					isEditable={false}
					isInEditMode={isInEditMode}
					setIsInEditMode={setIsInEditMode}
					sortMapping={sortMapping}
					errorObject={{errorMessages, setErrorMessages, uiErrorMessages, setUiErrorMessages}}
					deletionEnabled={false}
					deprecateOption={false}
					modReset={false}
					widthsObject={widthsObject}
					duplicationEnabled={false}
				/>
			</div>
			<ConditionRelationsDialog
				originalConditionRelationsData={conditionRelationsData}
				setOriginalConditionRelationsData={setConditionRelationsData}
				errorMessagesMainRow={errorMessages}
				setErrorMessagesMainRow={setErrorMessages}
			/>
		</>
	);
};

