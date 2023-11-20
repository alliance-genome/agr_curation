import React, { useState, useRef } from 'react';

import { GenericDataTable } from '../../components/GenericDataTable/GenericDataTable';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';

import { Card } from 'primereact/card';
import { Tooltip } from "primereact/tooltip";
import { Toast } from 'primereact/toast';
import { getDefaultTableState } from '../../service/TableStateService';
import { FILTER_CONFIGS } from '../../constants/FilterFields';

export const LiteratureReferenceTable = () => {

		const [isInEditMode, setIsInEditMode] = useState(true);
		const [errorMessages, setErrorMessages] = useState({});

		const toast_topleft = useRef(null);
		const toast_topright = useRef(null);

		const crossReferenceTemplate = (rowData) => {
				if (rowData && rowData.cross_references) {
						const sortedCross_References= rowData.cross_references.sort((a, b) => (a.curie > b.curie) ? 1 : -1);
						return (<div>
								<ul stype={{ listStypeType: 'none' }}>
										{sortedCross_References.map((a, index) =>
												<li key={index}>
														<EllipsisTableCell>
																{a.curie}
														</EllipsisTableCell>
												</li>
										)}
								</ul>
						</div>);
				}
		};

		const titleTemplate = (rowData) => {
				return (
						<>
								<EllipsisTableCell otherClasses={`${"TITLE_"}${rowData.curie.replace(':', '')}`}>
										{rowData.title}
								</EllipsisTableCell>
								<Tooltip target={`.${"TITLE_"}${rowData.curie.replace(':', '')}`} content={rowData.title} style={{ width: '450px', maxWidth: '450px' }}/>
						</>
				);
		};

		const abstractTemplate = (rowData) => {
				return (
						<>
								<EllipsisTableCell otherClasses={`${"ABSTRACT_"}${rowData.curie.replace(':', '')}`}>
										{rowData.abstract}
								</EllipsisTableCell>
								<Tooltip target={`.${"ABSTRACT_"}${rowData.curie.replace(':', '')}`} content={rowData.abstract} style={{ width: '450px', maxWidth: '450px' }}/>
						</>
				);
		};

		const citationTemplate = (rowData) => {
				return (
						<>
								<EllipsisTableCell otherClasses={`${"CITATION_"}${rowData.curie.replace(':', '')}`}>
										{rowData.citation}
								</EllipsisTableCell>
								<Tooltip target={`.${"CITATION_"}${rowData.curie.replace(':', '')}`} content={rowData.citation} style={{ width: '450px', maxWidth: '450px' }}/>
						</>
				);
		};

		const columns = [{
						field: "curie",
						header: "Curie",
						sortable: { isInEditMode },
						filter: true,
						filterConfig: FILTER_CONFIGS.curieFilterConfig, 
				}, {
						field: "cross_references.curie",
						header: "Cross References",
						sortable: isInEditMode,
						body: crossReferenceTemplate,
						filter: true,
						filterConfig: FILTER_CONFIGS.literatureCrossReferenceFilterConfig, 
				}, {
						field: "title",
						header: "Title",
						sortable: isInEditMode,
						filter: true,
						body : titleTemplate,
						filterConfig: FILTER_CONFIGS.titleFilterConfig, 
				}, {
						field: "abstract",
						header: "Abstract",
						sortable: isInEditMode,
						filter: true,
						body : abstractTemplate,
						filterConfig: FILTER_CONFIGS.abstractFilterConfig, 
				}, {
						field: "citation",
						header: "Citation",
						sortable: isInEditMode,
						filter: true,
						body : citationTemplate,
						filterConfig: FILTER_CONFIGS.citationFilterConfig, 
				}, {
						field: "short_citation",
						header: "Short Citation",
						sortable: { isInEditMode },
						filter: true,
						filterConfig: FILTER_CONFIGS.literatureShortCitationFilterConfig,
				}
		];
		const defaultColumnNames = columns.map((col) => {
			return col.header;
		});
		const widthsObject = {};

		columns.forEach((col) => {
			widthsObject[col.field] = 20;
		});
	
		const initialTableState = getDefaultTableState("LiteratureReferences", defaultColumnNames, undefined, widthsObject);

		return (
						<Card>
								<Toast ref={toast_topleft} position="top-left" />
								<Toast ref={toast_topright} position="top-right" />
								<GenericDataTable 
									endpoint="literature-reference" 
									tableName="Literature References" 
									dataKey="curie" 
									columns={columns}	 
									defaultColumnNames={defaultColumnNames}
									initialTableState={initialTableState}
									isEditable={false}
									isInEditMode={isInEditMode}
									setIsInEditMode={setIsInEditMode}
									toasts={{toast_topleft, toast_topright }}
									errorObject = {{errorMessages, setErrorMessages}}
									widthsObject={widthsObject}
								/>
						</Card>
		);

}
