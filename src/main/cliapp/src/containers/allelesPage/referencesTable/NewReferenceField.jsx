import { useState } from 'react';
import { Button } from 'primereact/button';
import { SingleReferenceDetailPageEditor } from '../../../components/Editors/autocomplete/references/SingleReferenceDetailPageEditor';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { SingleReferenceAdditionalFieldData } from '../../../components/FieldData/SingleReferenceAdditionalFieldData';
import { useSubResource } from '../../../components/SubResourcesContext';
import { prepareReferences } from './utils';

/**
 * The references section's picker. Adds the chosen reference to the section's rows, unless a row
 * already holds it.
 */
export const NewReferenceField = () => {
	const { setReferences } = useSubResource('references');
	const [reference, setReference] = useState(null);
	const [errorMessages, setErrorMessages] = useState({});

	const createNewReferenceHandler = (event) => {
		event.preventDefault();

		if (!reference || typeof reference === 'string') {
			setErrorMessages({ references: 'Must select reference from dropdown' });
			return;
		}

		const [newReference] = prepareReferences([reference]);
		setReferences((previous) =>
			previous.some((row) => row.curie === newReference.curie) ? previous : [newReference, ...previous]
		);
		setReference(null);
		setErrorMessages({});
	};

	const referencesOnChangeHandler = (event) => {
		setReference(event.target.value);
	};
	return (
		<Splitter gutterSize="0" className="border-none surface-ground">
			<SplitterPanel size={20}>
				<SingleReferenceDetailPageEditor
					reference={reference}
					onReferenceValueChange={referencesOnChangeHandler}
					errorMessages={errorMessages}
				/>
			</SplitterPanel>
			<SplitterPanel size={20}>
				<Button label="Add Reference" onClick={createNewReferenceHandler} className="w-6 p-button-text" />
			</SplitterPanel>
			<SplitterPanel size={60}>
				<SingleReferenceAdditionalFieldData fieldData={reference} />
			</SplitterPanel>
		</Splitter>
	);
};
