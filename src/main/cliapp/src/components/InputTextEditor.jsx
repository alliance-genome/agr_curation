import React from 'react';
import { InputText } from 'primereact/inputtext';
import { useSyncedState } from '../hooks/useSyncedState';

export function InputTextEditor({ editorOptions, fieldName }) {
	const [fieldValue, setFieldValue] = useSyncedState(editorOptions.rowData[fieldName] ?? '');

	const onChange = (e) => {
		const value = e.target.value;
		setFieldValue(value);
		editorOptions.editorCallback(value);
	};

	return <InputText aria-label={fieldName} value={fieldValue} onChange={onChange} style={{ width: '100%' }} />;
}
