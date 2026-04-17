import { useState } from 'react';
import { Button } from 'primereact/button';
import { addDataKey, generateCrossRefSearchField } from '../utils';
import { SingleReferenceFormEditor } from '../../../components/Editors/references/SingleReferenceFormEditor';
import { Splitter, SplitterPanel } from 'primereact/splitter';
import { SingleReferenceAdditionalFieldData } from '../../../components/FieldData/SingleReferenceAdditionalFieldData';

export const NewReferenceField = ({ state, dispatch }) => {
	const [reference, setReference] = useState(null);

	const createNewReferenceHandler = (event) => {
		event.preventDefault();

		if (!reference || typeof reference === 'string') {
			const errorMessages = {
				references: 'Must select reference from dropdown',
			};

			dispatch({
				type: 'UPDATE_TABLE_ERROR_MESSAGES',
				entityType: 'references',
				errorMessages,
			});

			return;
		}

		const searchString = generateCrossRefSearchField(reference);

		const newReference = {
			...reference,
			shortCitation: reference.short_citation,
			crossReferencesFilter: searchString,
		};

		addDataKey(newReference);

		dispatch({
			type: 'ADD_ROW',
			row: newReference,
			entityType: 'references',
		});
		setReference(null);
		dispatch({
			type: 'UPDATE_TABLE_ERROR_MESSAGES',
			entityType: 'references',
			errorMessages: {},
		});
	};

	const referencesOnChangeHandler = (event) => {
		setReference(event.target.value);
	};
	return (
		<Splitter gutterSize="0" className="border-none surface-ground">
			<SplitterPanel size={20}>
				<SingleReferenceFormEditor
					reference={reference}
					onReferenceValueChange={referencesOnChangeHandler}
					errorMessages={state.entityStates.references.errorMessages}
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
