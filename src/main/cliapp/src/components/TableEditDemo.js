import React, { useState, useEffect, useRef } from 'react';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { InputText } from 'primereact/inputtext';
import { InputNumber } from 'primereact/inputnumber';
import { Dropdown } from 'primereact/dropdown';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { ProductService } from '../service/ProductService';


export const TableEditDemo = () => {
    const [products1, setProducts1] = useState(null);
    const [products2, setProducts2] = useState(null);
    const [products3, setProducts3] = useState(null);
    const [products4, setProducts4] = useState(null);
    const [editingRows, setEditingRows] = useState({});
    const [editingCellRows, setEditingCellRows] = useState([]);
    const toast = useRef(null);
    const columns = [
        { field: 'code', header: 'Code' },
        { field: 'name', header: 'Name' },
        { field: 'quantity', header: 'Quantity' },
        { field: 'price', header: 'Price' }
    ];

    const statuses = [
        { label: 'In Stock', value: 'INSTOCK' },
        { label: 'Low Stock', value: 'LOWSTOCK' },
        { label: 'Out of Stock', value: 'OUTOFSTOCK' }
    ];

    let originalRows = {};
    let originalRows2 = {};

    const dataTableFuncMap = {
        'products1': setProducts1,
        'products2': setProducts2,
        'products3': setProducts3,
        'products4': setProducts4
    };

    const productService = new ProductService();

    useEffect(() => {
        fetchProductData('products1');
        fetchProductData('products2');
        fetchProductData('products3');
        fetchProductData('products4');
    }, []); // eslint-disable-line react-hooks/exhaustive-deps

    const fetchProductData = (productStateKey) => {
        productService.getProductsSmall().then(data => dataTableFuncMap[`${productStateKey}`](data));
    }

    const positiveIntegerValidator = (e) => {
        const { rowData, field } = e.columnProps;
        return isPositiveInteger(rowData[field]);
    }

    const emptyValueValidator = (e) => {
        const { rowData, field } = e.columnProps;
        return rowData[field].trim().length > 0;
    }

    const isPositiveInteger = (val) => {
        let str = String(val);
        str = str.trim();
        if (!str) {
            return false;
        }
        str = str.replace(/^0+/, "") || "0";
        let n = Math.floor(Number(str));
        return n !== Infinity && String(n) === str && n >= 0;
    }

    const onEditorInit = (e) => {
        const { rowIndex: index, field, rowData } = e.columnProps;
        let _editingCellRows = [...editingCellRows];
        if (!editingCellRows[index]) {
            _editingCellRows[index] = { ...rowData };
        }
        _editingCellRows[index][field] = products2[index][field];
        setEditingCellRows(_editingCellRows);
    };

    const onEditorCancel = (e) => {
        const { rowIndex: index, field } = e.columnProps;
        let products = [...products2];
        let _editingCellRows = [...editingCellRows];
        products[index][field] = _editingCellRows[index][field];
        delete _editingCellRows[index][field];
        setEditingCellRows(_editingCellRows);

        setProducts2(products);
    };

    const onEditorSubmit = (e) => {
        const { rowIndex: index, field } = e.columnProps;
        let _editingCellRows = [...editingCellRows];
        delete _editingCellRows[index][field];
        setEditingCellRows(_editingCellRows);
    };

    const onRowEditInit = (event) => {
        originalRows[event.index] = { ...products3[event.index] };
    }

    const onRowEditCancel = (event) => {
        console.log(originalRows);
        let products = [...products3];
        products[event.index] = originalRows[event.index];
        delete originalRows[event.index];

        setProducts3(products);
    }

    const onRowEditInit2 = (event) => {
        originalRows2[event.index] = { ...products4[event.index] };
    }

    const onRowEditCancel2 = (event) => {
        let products = [...products4];
        products[event.index] = originalRows2[event.index];
        delete originalRows2[event.index];

        setProducts4(products);
    }

    const onRowEditChange = (event) => {
        setEditingRows(event.data);
    }

    const setActiveRowIndex = (index) => {
        let products = [...products4];
        originalRows2[index] = { ...products[index] };
        let _editingRows = { ...editingRows, ...{ [`${products[index].id}`]: true } };
        setEditingRows(_editingRows);
    }

    const getStatusLabel = (status) => {
        switch (status) {
            case 'INSTOCK':
                return 'In Stock';

            case 'LOWSTOCK':
                return 'Low Stock';

            case 'OUTOFSTOCK':
                return 'Out of Stock';

            default:
                return 'NA';
        }
    }

    const onEditorValueChange = (productKey, props, value) => {
        let updatedProducts = [...props.value];
        updatedProducts[props.rowIndex][props.field] = value;
        dataTableFuncMap[`${productKey}`](updatedProducts);
    }

    const inputTextEditor = (productKey, props, field) => {
        return <InputText type="text" value={props.rowData[field]} onChange={(e) => onEditorValueChange(productKey, props, e.target.value)} />;
    }

    const codeEditor = (productKey, props) => {
        return inputTextEditor(productKey, props, 'code');
    }

    const nameEditor = (productKey, props) => {
        return inputTextEditor(productKey, props, 'name');
    }

    const priceEditor = (productKey, props) => {
        return <InputNumber value={props.rowData['price']} onValueChange={(e) => onEditorValueChange(productKey, props, e.value)} mode="currency" currency="USD" locale="en-US" />
    }

    const statusEditor = (productKey, props) => {
        return (
            <Dropdown value={props.rowData['inventoryStatus']} options={statuses} optionLabel="label" optionValue="value"
                      onChange={(e) => onEditorValueChange(productKey, props, e.value)} style={{ width: '100%' }} placeholder="Select a Status"
                      itemTemplate={(option) => {
                          return <span className={`product-badge status-${option.value.toLowerCase()}`}>{option.label}</span>
                      }} />
        );
    }

    const statusBodyTemplate = (rowData) => {
        return getStatusLabel(rowData.inventoryStatus);
    }

    const priceBodyTemplate = (rowData) => {
        return new Intl.NumberFormat('en-US', {style: 'currency', currency: 'USD'}).format(rowData.price);
    }

    return (
        <div className="datatable-editing-demo">
            <Toast ref={toast} />

            <div className="card">
                <h5>Basic Cell Editing</h5>
                <DataTable value={products1} editMode="cell" className="editable-cells-table">
                    <Column field="code" header="Code" editor={(props) => codeEditor('products1', props)}></Column>
                    <Column field="name" header="Name" editor={(props) => nameEditor('products1', props)}></Column>
                    <Column field="inventoryStatus" header="Status" body={statusBodyTemplate} editor={(props) => statusEditor('products1', props)}></Column>
                    <Column field="price" header="Price" body={priceBodyTemplate} editor={(props) => priceEditor('products1', props)}></Column>
                </DataTable>
            </div>

            <div className="card">
                <h5>Advanced Cell Editing</h5>
                <p>Custom implementation with validations, dynamic columns and reverting values with the escape key.</p>
                <DataTable value={products2} editMode="cell" className="editable-cells-table">
                    {
                        columns.map(col => {
                            const { field, header } = col;
                            const validator = (field === 'quantity' || field === 'price') ? positiveIntegerValidator : emptyValueValidator;
                            return <Column key={field} field={field} header={header} body={field === 'price' && priceBodyTemplate}
                                           editor={(props) => inputTextEditor('products2', props, field)} editorValidator={validator}
                                           onEditorInit={onEditorInit} onEditorCancel={onEditorCancel} onEditorSubmit={onEditorSubmit} />
                        })
                    }
                </DataTable>
            </div>

            <div className="card">
                <h5>Row Editing</h5>
                <DataTable value={products3} editMode="row" dataKey="id" onRowEditInit={onRowEditInit} onRowEditCancel={onRowEditCancel}>
                    <Column field="code" header="Code" editor={(props) => codeEditor('products3', props)}></Column>
                    <Column field="name" header="Name" editor={(props) => nameEditor('products3', props)}></Column>
                    <Column field="inventoryStatus" header="Status" body={statusBodyTemplate} editor={(props) => statusEditor('products3', props)}></Column>
                    <Column field="price" header="Price" body={priceBodyTemplate} editor={(props) => priceEditor('products3', props)}></Column>
                    <Column rowEditor headerStyle={{ width: '7rem' }} bodyStyle={{ textAlign: 'center' }}></Column>
                </DataTable>
            </div>

            <div className="card">
                <h5>Programmatic</h5>
                <div className="p-pt-2 p-pb-4">
                    <Button onClick={() => setActiveRowIndex(0)} className="p-button-text" label="Activate 1st" />
                    <Button onClick={() => setActiveRowIndex(2)} className="p-button-text" label="Activate 3rd" />
                    <Button onClick={() => setActiveRowIndex(4)} className="p-button-text" label="Activate 5th" />
                </div>

                <DataTable value={products4} editMode="row" dataKey="id" editingRows={editingRows} onRowEditChange={onRowEditChange} onRowEditInit={onRowEditInit2} onRowEditCancel={onRowEditCancel2}>
                    <Column field="code" header="Code" editor={(props) => codeEditor('products4', props)}></Column>
                    <Column field="name" header="Name" editor={(props) => nameEditor('products4', props)}></Column>
                    <Column field="inventoryStatus" header="Status" body={statusBodyTemplate} editor={(props) => statusEditor('products4', props)}></Column>
                    <Column field="price" header="Price" body={priceBodyTemplate} editor={(props) => priceEditor('products4', props)}></Column>
                    <Column rowEditor headerStyle={{ width: '7rem' }} bodyStyle={{ textAlign: 'center' }}></Column>
                </DataTable>
            </div>
        </div>
    );
}

