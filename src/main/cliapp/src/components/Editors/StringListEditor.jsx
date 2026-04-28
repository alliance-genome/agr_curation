import { InputText } from 'primereact/inputtext';
import { useSyncedState } from '../../hooks/useSyncedState';

export function StringListEditor({ editorOptions, fieldName }) {
	const initialValue = Array.isArray(editorOptions.rowData[fieldName])
		? editorOptions.rowData[fieldName].join(', ')
		: '';
	const [fieldValue, setFieldValue] = useSyncedState(initialValue);

	const onChange = (e) => {
		const value = e.target.value;
		setFieldValue(value);
		const asArray = value
			? value
					.split(',')
					.map((s) => s.trim())
					.filter((s) => s.length > 0)
			: [];
		editorOptions.editorCallback(asArray);
	};

	return <InputText aria-label={fieldName} value={fieldValue} onChange={onChange} style={{ width: '100%' }} />;
}
