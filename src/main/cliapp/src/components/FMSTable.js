import React, { Component } from 'react';
import {FMSService} from '../service/FMSService';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';

export class FMSTable extends Component {

    constructor() {
        super();
        this.state = {
        };

        this.fmsservice = new FMSService();
    }

    componentDidMount() {
        this.fmsservice.getRelease().then(data => this.setState({dataFiles: data.snapShot.dataFiles, snapShot: data.snapShot, snapShotDate: data.snapShot.snapShotDate, releaseVersion: data.snapShot.releaseVersion.releaseVersion}));
    }

    downloadLinkTemplate(rowData) {
        return (
            <React.Fragment>
                <a href={rowData.s3Url}>Download</a>
            </React.Fragment>
        );
    }

    render() {        
        return (
            <div className="p-grid p-fluid dashboard">
                <div className="card">
                    Snapshot Date: {this.state.snapShotDate}<br />Release Version: {this.state.releaseVersion}
                    <DataTable value={this.state.dataFiles} resizableColumns className="p-datatable-sm">
                        <Column field="md5Sum" header="MD5Sum" sortable></Column>
                        <Column field="dataType.name" header="Data Type" sortable></Column>
                        <Column field="dataSubType.name" header="Data Sub Type" sortable></Column>
                        <Column field="uploadDate" header="Upload Date" sortable></Column>
                        <Column field="s3Url" header="Download" body={this.downloadLinkTemplate}>

								</Column>
                    </DataTable>
                </div>
            </div>
        );
    }
}
