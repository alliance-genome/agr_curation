import React, {useState, useRef} from 'react';
import {EllipsisTableCell} from "../../components/EllipsisTableCell";
import {ListTableCell} from "../../components/ListTableCell";
import {Tooltip} from 'primereact/tooltip';
import {GenericDataTable} from '../../components/GenericDataTable/GenericDataTable';
import {getDefaultTableState} from '../../service/TableStateService';
import {FILTER_CONFIGS} from '../../constants/FilterFields';

export const OrthologyTable = () => {

	const toast_topleft = useRef(null);
	const toast_topright = useRef(null);
	const [errorMessages, setErrorMessages] = useState({});
	const [isEnabled, setIsEnabled] = useState(true);

	const subjectGeneTemplate = (rowData) => {
		if (rowData?.subjectGene?.geneSymbol) {
			return (
				<>
					<div className={`overflow-hidden text-overflow-ellipsis a${rowData.id}${rowData.subjectGene.curie.replace(':', '')}`}
						dangerouslySetInnerHTML={{
							__html: rowData.subjectGene.geneSymbol.displayText + ' (' + rowData.subjectGene.curie + ')'
						}}
					/>
					<Tooltip target={`.a${rowData.id}${rowData.subjectGene.curie.replace(':', '')}`}>
						<div dangerouslySetInnerHTML={{
							__html: rowData.subjectGene.geneSymbol.displayText + ' (' + rowData.subjectGene.curie + ')'
						}}
						/>
					</Tooltip>
				</>
			)
		}
	};

	const objectGeneTemplate = (rowData) => {
		if (rowData?.objectGene?.geneSymbol) {
			return (
				<>
					<div className={`overflow-hidden text-overflow-ellipsis b${rowData.id}${rowData.objectGene.curie.replace(':', '')}`}
						dangerouslySetInnerHTML={{
							__html: rowData.objectGene.geneSymbol.displayText + ' (' + rowData.objectGene.curie + ')'
						}}
					/>
					<Tooltip target={`.b${rowData.id}${rowData.objectGene.curie.replace(':', '')}`}>
						<div dangerouslySetInnerHTML={{
							__html: rowData.objectGene.geneSymbol.displayText + ' (' + rowData.objectGene.curie + ')'
						}}
						/>
					</Tooltip>
				</>
			)
		}
	};

	const subjectGeneTaxonTemplate = (rowData) => {
		if (rowData?.subjectGene?.taxon) {
			return (
				<>
					<div className={`overflow-hidden text-overflow-ellipsis c${rowData.id}${rowData.subjectGene.taxon.curie.replace(':', '')}`}
						dangerouslySetInnerHTML={{
							__html: rowData.subjectGene.taxon.name + ' (' + rowData.subjectGene.taxon.curie + ')'
						}}
					/>
					<Tooltip target={`.c${rowData.id}${rowData.subjectGene.taxon.curie.replace(':', '')}`}>
						<div dangerouslySetInnerHTML={{
							__html: rowData.subjectGene.taxon.name + ' (' + rowData.subjectGene.taxon.curie + ')'
						}}
						/>
					</Tooltip>
				</>
			)
		}
	};

	const objectGeneTaxonTemplate = (rowData) => {
		if (rowData?.objectGene?.taxon) {
			return (
				<>
					<div className={`overflow-hidden text-overflow-ellipsis d${rowData.id}${rowData.objectGene.taxon.curie.replace(':', '')}`}
						dangerouslySetInnerHTML={{
							__html: rowData.objectGene.taxon.name + ' (' + rowData.objectGene.taxon.curie + ')'
						}}
					/>
					<Tooltip target={`.d${rowData.id}${rowData.objectGene.taxon.curie.replace(':', '')}`}>
						<div dangerouslySetInnerHTML={{
							__html: rowData.objectGene.taxon.name + ' (' + rowData.objectGene.taxon.curie + ')'
						}}
						/>
					</Tooltip>
				</>
			)
		}
	};

	const strictFilterTemplate = (rowData) => {
		if (rowData && rowData.strictFilter !== null && rowData.strictFilter !== undefined) {
				return <div>{JSON.stringify(rowData.strictFilter)}</div>;
		}
	};

	const moderateFilterTemplate = (rowData) => {
		if (rowData && rowData.moderateFilter !== null && rowData.moderateFilter !== undefined) {
				return <div>{JSON.stringify(rowData.moderateFilter)}</div>;
		}
	};

	const predictionMethodsMatchedTemplate = (rowData) => {
		if (rowData.predictionMethodsMatched) {
			const listTemplate = (method) => {
				return (
					<EllipsisTableCell>
						{method.name}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<ListTableCell template={listTemplate} listData={rowData.predictionMethodsMatched} showBullets={true}/>
				</>
			);
		}
	};

	const predictionMethodsNotMatchedTemplate = (rowData) => {
		if (rowData.predictionMethodsNotMatched) {
			const listTemplate = (method) => {
				return (
					<EllipsisTableCell>
						{method.name}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<ListTableCell template={listTemplate} listData={rowData.predictionMethodsNotMatched} showBullets={true}/>
				</>
			);
		}
	};

	const predictionMethodsNotCalledTemplate = (rowData) => {
		if (rowData.predictionMethodsNotCalled) {
			const listTemplate = (method) => {
				return (
					<EllipsisTableCell>
						{method.name}
					</EllipsisTableCell>
				);
			};
			return (
				<>
					<ListTableCell template={listTemplate} listData={rowData.predictionMethodsNotCalled} showBullets={true}/>
				</>
			);
		}
	};

	const columns = [
		{
			field: "subjectGene.geneSymbol.displayText",
			header: "Subject Gene",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.subjectGeneFilterConfig,
			body: subjectGeneTemplate
		},
		{
			field: "objectGene.geneSymbol.displayText",
			header: "Object Gene",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.objectGeneFilterConfig,
			body: objectGeneTemplate
		},
		{
			field: "subjectGene.taxon.name",
			header: "Subject Gene Taxon",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.subjectGeneTaxonFilterConfig,
			body: subjectGeneTaxonTemplate
		},
		{
			field: "objectGene.taxon.name",
			header: "Object Gene Taxon",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.objectGeneTaxonFilterConfig,
			body: objectGeneTaxonTemplate
		},
		{
			field: "isBestScore.name",
			header: "Best Score",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.isBestScoreFilterConfig
		},
		{
			field: "isBestScoreReverse.name",
			header: "Best Reverse Score",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.isBestScoreReverseFilterConfig
		},
		{
			field: "confidence.name",
			header: "Confidence",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.confidenceFilterConfig
		},
		{
			field: "predictionMethodsMatched.name",
			header: "Prediction Methods Matched",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.predictionMethodsMatchedFilterConfig,
			body: predictionMethodsMatchedTemplate
		},
		{
			field: "predictionMethodsNotMatched.name",
			header: "Prediction Methods Not Matched",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.predictionMethodsNotMatchedFilterConfig,
			body: predictionMethodsNotMatchedTemplate
		},
		{
			field: "predictionMethodsNotCalled.name",
			header: "Prediction Methods Not Called",
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.predictionMethodsNotCalledFilterConfig,
			body: predictionMethodsNotCalledTemplate
		},
		{
			field: "strictFilter",
			header: "Strict Filter",
			body: strictFilterTemplate,
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.strictFilterFilterConfig,
		},
		{
			field: "moderateFilter",
			header: "Moderate Filter",
			body: moderateFilterTemplate,
			sortable: isEnabled,
			filterConfig: FILTER_CONFIGS.moderateFilterFilterConfig,
		}
	];

	const defaultColumnNames = columns.map((col) => {
		return col.header;
	});

	const widthsObject = {};

	columns.forEach((col) => {
		widthsObject[col.field] = 15;
	});

	const initialTableState = getDefaultTableState("", defaultColumnNames, undefined, widthsObject);

	return (
		<div className="card">
			<GenericDataTable
				endpoint="orthologygenerated"
				tableName="Orthology"
				columns={columns}
				defaultColumnNames={defaultColumnNames}
				errorObject = {{errorMessages, setErrorMessages}}
				toasts={{toast_topleft, toast_topright }}
				initialTableState={initialTableState}
				curieFields={["subjectGene", "objectGene", "subjectGene.taxon", "objectGene.taxon"]}
				idFields={["isBestScore", "isBestScoreReverse", "confidence", "predictionMethodsMatched", "predictionMethodsNotMatched", "predictionMethodsNotCalled"]}
				isEnabled={isEnabled}
				setIsEnabled={setIsEnabled}
				widthsObject={widthsObject}
			/>
		</div>
	)
}
