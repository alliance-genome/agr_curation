import React, { useState, useEffect, useRef } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ConfirmDialog, confirmDialog } from 'primereact/confirmdialog';
import { SystemService } from '../service/SystemService';
import { CLASSES } from '../constants/Classes';
import { NumberTemplate } from './Templates/NumberTemplate';

export const EntityCountsComponent = () => {
	const [tableData, setTableData] = useState({});
	const [reindexing, setReindexing] = useState({});
	const toast = useRef(null);

	useEffect(() => {
		let isMounted = true;
		const systemService = new SystemService();

		let _tableData = {};

		systemService.getSiteSummary().then((res) => {
			if (isMounted) {
				for (const key in res.entity) {
					if (!CLASSES[key]) continue;

					const { type } = CLASSES[key];

					if (!_tableData[type]) {
						_tableData[type] = [];
					}

					_tableData[type].push({
						name: CLASSES[key].name,
						link: CLASSES[key].link,
						type: CLASSES[key].type,
						isIndexed: CLASSES[key].isIndexed,
						reindexEndpoint: CLASSES[key].reindexEndpoint,
						dbCount: res.entity[key]['dbCount'],
						esCount: res.entity[key]['esCount'],
					});
				}
				setTableData(_tableData);
			}
		});
		return () => {
			isMounted = false;
		};
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, []);

	const nameHyperlinkTemplate = (rowData) => {
		if (!rowData.link) {
			return rowData.name;
		}
		return <a href={rowData.link}>{rowData.name}</a>;
	};

	const reindex = async (rowData) => {
		const { name, reindexEndpoint } = rowData;
		setReindexing((prev) => ({ ...prev, [reindexEndpoint]: true }));
		try {
			await new SystemService().reindex(reindexEndpoint);
			toast.current.show({ severity: 'success', summary: 'Reindex Started', detail: name });
		} catch (error) {
			toast.current.show({
				severity: 'error',
				summary: 'Reindex Failed',
				detail: `${name}: ${error?.response?.status ?? ''} ${error?.message ?? ''}`,
			});
		} finally {
			setReindexing((prev) => ({ ...prev, [reindexEndpoint]: false }));
		}
	};

	const confirmReindex = (rowData) => {
		confirmDialog({
			header: 'Reindex',
			message: `Reindex ${rowData.name}? The search index for this type will be dropped and rebuilt, so search results will be incomplete until it finishes.`,
			icon: 'pi pi-exclamation-triangle',
			accept: () => reindex(rowData),
		});
	};

	const reindexTemplate = (rowData) => {
		if (!rowData.reindexEndpoint) {
			return null;
		}
		return (
			<Button
				icon="pi pi-refresh"
				className="p-button-sm p-button-text"
				tooltip={`Reindex ${rowData.name}`}
				tooltipOptions={{ position: 'left' }}
				aria-label={`Reindex ${rowData.name}`}
				loading={!!reindexing[rowData.reindexEndpoint]}
				onClick={() => confirmReindex(rowData)}
			/>
		);
	};

	const getRowClass = (rowData) => {
		console.log(rowData);
		if (!rowData.isIndexed) return;

		if (rowData?.dbCount !== rowData.esCount) {
			return 'bg-gray-500 text-white';
		}
	};

	return (
		<>
			<Toast ref={toast} />
			<ConfirmDialog />
			<div className="grid nested dashboard">
				<div className="col-3">
					<DataTable
						header="Entities"
						value={tableData.entity}
						sortField="name"
						sortOrder={1}
						rowClassName={(rowData) => getRowClass(rowData)}
					>
						<Column field="name" header="Entity Name" body={nameHyperlinkTemplate} />
						<Column
							field="dbCount"
							header="Database Entity Count"
							body={(rowData) => <NumberTemplate number={rowData.dbCount} />}
						/>
						<Column
							field="esCount"
							header="Search index Entity Count"
							body={(rowData) => <NumberTemplate number={rowData.esCount} />}
						/>
						<Column header="Reindex" body={reindexTemplate} />
					</DataTable>
				</div>
				<div className="col-3">
					<DataTable
						header="Ontologies"
						value={tableData.ontology}
						sortField="name"
						sortOrder={1}
						rowClassName={(rowData) => getRowClass(rowData)}
					>
						<Column field="name" header="Ontology Name" body={nameHyperlinkTemplate} />
						<Column
							field="dbCount"
							header="Database Term Count"
							body={(rowData) => <NumberTemplate number={rowData.dbCount} />}
						/>
						<Column
							field="esCount"
							header="Search index Term Count"
							body={(rowData) => <NumberTemplate number={rowData.esCount} />}
						/>
						<Column header="Reindex" body={reindexTemplate} />
					</DataTable>
				</div>
				<div className="col-3">
					<DataTable
						header="Associations"
						value={tableData.association}
						sortField="name"
						sortOrder={1}
						rowClassName={(rowData) => getRowClass(rowData)}
					>
						<Column field="name" header="Association Type" body={nameHyperlinkTemplate} />
						<Column
							field="dbCount"
							header="Database Association Count"
							body={(rowData) => <NumberTemplate number={rowData.dbCount} />}
						/>
						<Column
							field="esCount"
							header="Search index Term Count"
							body={(rowData) => <NumberTemplate number={rowData.esCount} />}
						/>
						<Column header="Reindex" body={reindexTemplate} />
					</DataTable>
				</div>
				<div className="col-3">
					<DataTable
						header="System"
						value={tableData.system}
						sortField="name"
						sortOrder={1}
						rowClassName={(rowData) => getRowClass(rowData)}
					>
						<Column field="name" header="System Name" body={nameHyperlinkTemplate} />
						<Column
							field="dbCount"
							header="Database Object Count"
							body={(rowData) => <NumberTemplate number={rowData.dbCount} />}
						/>
						<Column
							field="esCount"
							header="Search index Object Count"
							body={(rowData) => <NumberTemplate number={rowData.esCount} />}
						/>
					</DataTable>
				</div>
			</div>
		</>
	);
};
