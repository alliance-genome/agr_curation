import React, { Component } from 'react';
import {GeneService} from '../service/GeneService';
import {DataTable} from 'primereact/datatable';
import {Column} from 'primereact/column';

export class GeneTable extends Component {

    constructor() {
        super();
        this.state = {
        };

        this.geneservice = new GeneService();
    }

    componentDidMount() {
        this.geneservice.getGenes().then(data => this.setState({genes: data}));
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
                    <DataTable value={this.state.genes} resizableColumns className="p-datatable-sm">
                        <Column field="curie" header="Curie" sortable></Column>
                        <Column field="taxon" header="Taxon" sortable></Column>
                        <Column field="dataSubType.name" header="Data Sub Type" sortable></Column>
                        <Column field="uploadDate" header="Upload Date" sortable></Column>
                        <Column field="s3Url" header="Download" body={this.downloadLinkTemplate}></Column>
                    </DataTable>
                </div>
            </div>
        );
    }
}
