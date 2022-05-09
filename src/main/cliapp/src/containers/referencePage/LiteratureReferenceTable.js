import React, { useState, useRef, useEffect } from 'react';
import { useQuery } from 'react-query';

import { useSessionStorage } from '../../service/useSessionStorage';
import { useSetDefaultColumnOrder } from '../../utils/useSetDefaultColumnOrder';
import { Messages } from 'primereact/messages';
import { MultiSelect } from 'primereact/multiselect';
import { FilterComponentInputText } from '../../components/FilterComponentInputText';
import { EllipsisTableCell } from '../../components/EllipsisTableCell';
import { returnSorted, filterColumns, orderColumns, reorderArray, setDefaultColumnOrder } from '../../utils/utils';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Card } from 'primereact/card';
import { SearchService } from '../../service/SearchService';
import { DataTableHeaderFooterTemplate } from "../../components/DataTableHeaderFooterTemplate";
import {Tooltip} from "primereact/tooltip";


export const LiteratureReferenceTable = () => {

    const defaultColumnNames = ["Curie", "Cross References", "Title", "Abstract", "Citation"];

    let initialTableState = {
        page: 0,
        first: 0,
        rows: 50,
        multiSortMeta: [],
        selectedColumnNames: defaultColumnNames,
        filters: {},
        isFirst: true,
    };

    const [tableState, setTableState] = useSessionStorage("referenceTableSettings", initialTableState);

    const [references, setReferences] = useState(null);
    const [totalRecords, setTotalRecords] = useState(0);
    const [isEnabled, setIsEnabled] = useState(true);
    const [columnList, setColumnList] = useState([]);

    const searchService = new SearchService();
    const errorMessage = useRef(null);
    const dataTable = useRef(null);


    useQuery(['references', tableState],
        () => searchService.search("literature-reference", tableState.rows, tableState.page, tableState.multiSortMeta, tableState.filters), {
        onSuccess: (data) => {
            setIsEnabled(true);
            setReferences(data.results);
            setTotalRecords(data.totalResults);
        },
        onError: (error) => {
            errorMessage.current.show([
                { severity: 'error', summary: 'Error', detail: error.message, sticky: true }
            ])
        },
        keepPreviousData: true
    });

  const setIsFirst = (value) => {
    let _tableState = {
      ...tableState,
      isFirst: value,
    };

    setTableState(_tableState);
  }


    const onLazyLoad = (event) => {
        let _tableState = {
            ...tableState,
            rows: event.rows,
            page: event.page,
            first: event.first
        };

        setTableState(_tableState);
    }

    const onFilter = (filtersCopy) => {
        let _tableState = {
            ...tableState,
            filters: { ...filtersCopy }
        }
        setTableState(_tableState);
    };

    const onSort = (event) => {
        let _tableState = {
            ...tableState,
            multiSortMeta: returnSorted(event, tableState.multiSortMeta)
        }
        setTableState(_tableState);
    };

    const setSelectedColumnNames = (newValue) => {
        let _tableState = {
            ...tableState,
            selectedColumnNames: newValue
        };

        setTableState(_tableState);
    };

    const createMultiselectComponent = (tableState,defaultColumnNames,isEnabled) => {
        return (<MultiSelect
            value={tableState.selectedColumnNames}
            options={defaultColumnNames}
            onChange={e => setSelectedColumnNames(e.value)}
            style={{ width: '20em', textAlign: 'center' }}
            disabled={!isEnabled}
        />);
    };

    const header = (
        <DataTableHeaderFooterTemplate
            title = {"Literature References Table"}
            tableState = {tableState}
            defaultColumnNames = {defaultColumnNames}
            multiselectComponent = {createMultiselectComponent(tableState,defaultColumnNames,isEnabled)}
            onclickEvent = {(event) => resetTableState(event)}
            isEnabled = {isEnabled}
        />
    );

    const filterComponentTemplate = (filterName, fields) => {
        return (<FilterComponentInputText
            isEnabled={isEnabled}
            fields={fields}
            filterName={filterName}
            currentFilters={tableState.filters}
            onFilter={onFilter}
        />);
    };

    const crossReferenceTemplate = (rowData) => {
        if (rowData && rowData.cross_references) {
            const sortedCross_References = rowData.cross_references.sort((a, b) => (a.curie > b.curie) ? 1 : -1);
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
            sortable: { isEnabled },
            filter: true,
            filterElement: filterComponentTemplate("curieFilter", ["curie"])
        }, {
            field: "cross_references.curie",
            header: "Cross References",
            sortable: isEnabled,
            body: crossReferenceTemplate,
            filter: true,
            filterElement: filterComponentTemplate("cross_referencesFilter", ["cross_references.curie"])
        }, {
            field: "title",
            header: "Title",
            sortable: isEnabled,
            filter: true,
            body : titleTemplate,
            filterElement: filterComponentTemplate("titleFilter", ["title"])
        }, {
            field: "abstract",
            header: "Abstract",
            sortable: isEnabled,
            filter: true,
            body : abstractTemplate,
            filterElement: filterComponentTemplate("abstractFilter", ["abstract"])
        }, {
            field: "citation",
            header: "Citation",
            sortable: isEnabled,
            filter: true,
            body : citationTemplate,
            filterElement: filterComponentTemplate("citationFilter", ["citation"])
        }
    ];

  useSetDefaultColumnOrder(columns, dataTable, defaultColumnNames, setIsFirst, tableState.isFirst);

    const [columnWidths, setColumnWidths] = useState(() => {
        const width = 20;

        const widthsObject = {};

        columns.forEach((col) => {
          widthsObject[col.field] = width;
        });

        return widthsObject;
    });

    useEffect(() => {
        const filteredColumns = filterColumns(columns, tableState.selectedColumnNames);
        const orderedColumns = orderColumns(filteredColumns, tableState.selectedColumnNames);
        setColumnList(
            orderedColumns.map((col) => {
                return <Column
                    style={{'minWidth':`${columnWidths[col.field]}vw`, 'maxWidth': `${columnWidths[col.field]}vw`}}
                    headerClassName='surface-0'
                    columnKey={col.field}
                    key={col.field}
                    field={col.field}
                    header={col.header}
                    sortable={isEnabled}
                    filter={col.filter}
                    showFilterMenu={false}
                    filterElement={col.filterElement}
                    body={col.body}
                />;
            })
        );
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [tableState, isEnabled, columnWidths]);

    const resetTableState = () => {
          let _tableState = {
            ...initialTableState,
            isFirst: false,
          };

          setTableState(_tableState);
          setDefaultColumnOrder(columns, dataTable, defaultColumnNames);
          const _columnWidths = {...columnWidths};

          Object.keys(_columnWidths).map((key) => {
            _columnWidths[key] = 20;
          });

          setColumnWidths(_columnWidths);
          dataTable.current.el.children[1].scrollLeft = 0;
    }

    const colReorderHandler = (event) => {
        let _columnNames = [...tableState.selectedColumnNames];
        _columnNames = reorderArray(_columnNames, event.dragIndex, event.dropIndex);
        setSelectedColumnNames(_columnNames);
    };

    const handleColumnResizeEnd = (event) => {
        const currentWidth = event.element.clientWidth;
        const delta = event.delta;
        const newWidth = Math.floor(((currentWidth + delta) / window.innerWidth) * 100);
        const field = event.column.props.field;

        const _columnWidths = {...columnWidths};

        _columnWidths[field] = newWidth;
        setColumnWidths(_columnWidths);
    };


    return (
            <Card>
                <Messages ref={errorMessage} />
                <DataTable value={references} className="p-datatable-sm" header={header} reorderableColumns
                    ref={dataTable} scrollHeight="62vh" scrollable
                    tableClassName='w-12 p-datatable-md'
                    filterDisplay="row"
                    paginator totalRecords={totalRecords} onPage={onLazyLoad} lazy first={tableState.first}
                    onColReorder={colReorderHandler}
                    sortMode="multiple" removableSort onSort={onSort} multiSortMeta={tableState.multiSortMeta}
                    paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
                    resizableColumns columnResizeMode="expand" showGridlines onColumnResizeEnd={handleColumnResizeEnd}
                    currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={tableState.rows} rowsPerPageOptions={[10, 20, 50, 100, 250, 1000]}
                >
                    {columnList}
                </DataTable>
            </Card>
    );

}
