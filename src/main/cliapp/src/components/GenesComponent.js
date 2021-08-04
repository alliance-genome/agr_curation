import React, { useState, useEffect } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { GeneService } from '../service/GeneService';

export const GenesComponent = () => {

    const [genes, setGenes] = useState(null);

    useEffect(() => {
        const geneService = new GeneService();
        geneService.getGenes().then(data => setGenes(data));
    }, []);

    return (
            <div>
                <div className="card">
                    <DataTable value={genes}>
                        <Column field="code" header="Code"></Column>
                        <Column field="name" header="Name"></Column>
                        <Column field="category" header="Category"></Column>
                        <Column field="quantity" header="Quantity"></Column>
                    </DataTable>
                </div>
            </div>
    )
}
