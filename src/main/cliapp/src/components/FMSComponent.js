import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { FMSService } from '../service/FMSService';

export const FMSComponent = () => {

  const [dataFiles, setDataFiles] = useState(null);
  const [multiSortMeta, setMultiSortMeta] = useState([]);
  const [filters, setFilters] = useState({});
  const [snapShotDate, setSnapShotDate] = useState(0);
  const [first, setFirst] = useState(0);
  const [rows, setRows] = useState(20);
  const [releases, setReleases] = useState([]);
  const [selectedRelease, setSelectedRelease] = useState(['4.2.0']);
  //const [totalRecords, setTotalRecords] = useState(0);

  useEffect(() => {
    const fmsService = new FMSService();
    fmsService.getReleases().then(results => {
      console.log(results);
      setReleases(results.reverse());
      //selectedRelease = results[0].releaseVersio;
      //setSelectedRelease(results[0].releaseVersion);
      //setDataFiles(results.dataFiles);
    });

    fmsService.getSnapshot(selectedRelease).then(results => {
      setDataFiles(results.dataFiles);
      setSnapShotDate(results.snapShotDate);
    });

  }, [selectedRelease]);

  const onFilter = (event) => {
    //console.log("On Filter: ");
    //console.log(event.filters);
    setFilters(event.filters);
  }

  const onSort = (event) => {
    //console.log("On Sort: ");
    //console.log(event);
    var found = false;
    var newSort = [...multiSortMeta];

    newSort.forEach((o) => {
      if(o.field === event.multiSortMeta[0].field) {
        o.order = event.multiSortMeta[0].order;
        found = true;
      }
    });

    if(!found) {
      setMultiSortMeta(newSort.concat(event.multiSortMeta));
    } else {
      setMultiSortMeta(newSort);
    }
  }

  const symbolTemplate = (rowData) => {
    return <a href={rowData.s3Url}>Download</a>
  };

  const uploadTemplate = (rowData) => {
    return <div>{new Date(rowData.uploadDate).toGMTString()}</div>
  };

  const customPage = (event) => {
    setRows(event.rows);
    //setPage(event.page);
    setFirst(event.first);
  };

  const onReleaseChange = (event) => {
    console.log(event);
    setSelectedRelease(event.value);
  };

  return (
      <div>
        <div className="card">
          <h2>FMS Data</h2>
          Release Version: <Dropdown value={selectedRelease} optionValue="releaseVersion" options={releases} optionLabel="releaseVersion" placeholder="Choose Release Version" onChange={onReleaseChange} />
          <br />Snapshot Date: { new Date(snapShotDate).toGMTString() }
          <DataTable value={dataFiles} className="p-datatable-sm"
            paginator onPage={customPage} first={first}
            sortMode="multiple" removableSort onSort={onSort} multiSortMeta={multiSortMeta}
            onFilter={onFilter} filters={filters}
            paginatorTemplate="CurrentPageReport FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink RowsPerPageDropdown"
            currentPageReportTemplate="Showing {first} to {last} of {totalRecords}" rows={rows} rowsPerPageOptions={[10,20,50,100,250,1000]}
          >

            <Column field="md5Sum" header="MD5" sortable filter></Column>
            <Column field="uploadDate" header="Uploaded" body={uploadTemplate} sortable filter></Column>
            <Column field="dataType.name" header="Data Type" sortable filter></Column>
            <Column field="dataSubType.name" header="Data Sub Type" sortable filter></Column>
            <Column field="s3Url" header="Download" body={symbolTemplate}></Column>
          </DataTable>
        </div>
      </div>
  )
}
