import { useMemo, useRef } from 'react';
import { Button } from 'primereact/button';
import { Message } from 'primereact/message';
import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { useSubResource } from '../../../components/SubResourcesContext';
import { AlleleGeneAssociationsFormTable } from './AlleleGeneAssociationsFormTable';
import { addDataKey, generateCurieSearchField } from '../utils';
import { processOptionalField } from '../../../utils/utils';

/**
 * The allele detail and create pages' gene associations section.
 *
 * Its rows are saved by the page along with the allele. Gene associations are written through their own
 * sub-resource rather than in the allele payload, so the save makes that call itself.
 *
 * @param {Object} props
 * @param {number} [props.labelColumnSize]
 */
export const AlleleGeneAssociationsForm = ({ labelColumnSize }) => {
	const { alleleGeneAssociations, setAlleleGeneAssociations, errorMessages, setErrorMessages, loadError } =
		useSubResource('alleleGeneAssociations');
	const tableRef = useRef(null);

	const editingRows = useMemo(
		() => Object.fromEntries(alleleGeneAssociations.map((association) => [association.dataKey, true])),
		[alleleGeneAssociations]
	);

	const updateRow = (dataKey, changes) => {
		setAlleleGeneAssociations((previous) =>
			previous.map((association) => (association.dataKey === dataKey ? { ...association, ...changes } : association))
		);
	};

	const createNewGeneAssociationHandler = (event) => {
		event.preventDefault();
		const newAlleleGeneAssociation = {};
		addDataKey(newAlleleGeneAssociation);
		setAlleleGeneAssociations((previous) => [newAlleleGeneAssociation, ...previous]);
	};

	const onRowEditChange = () => {
		return null;
	};

	const alleleGeneRelationOnChangeHandler = (props, event) => {
		props.editorCallback(event.target.value);
		updateRow(props.rowData?.dataKey, { relation: event.target.value });
	};

	const relatedNoteOnChangeHandler = (value, props) => {
		props.editorCallback(value[0]);
		updateRow(props.rowData?.dataKey, { relatedNote: value[0] });
	};

	const geneOnChangeHandler = (event, setFieldValue, props) => {
		//updates value in table input box
		setFieldValue(event.target.value);
		updateRow(props.rowData?.dataKey, { alleleGeneAssociationObject: event.target.value });
	};

	const evidenceCodeOnChangeHandler = (event, setFieldValue, props) => {
		const value = processOptionalField(event.target.value);
		setFieldValue(value);
		updateRow(props.rowData?.dataKey, { evidenceCode: value });
	};

	const evidenceOnChangeHandler = (event, setFieldValue, props) => {
		const newEvidence = event.target.value;

		//updates value in table input box
		setFieldValue(newEvidence);
		updateRow(props.rowData?.dataKey, {
			evidence: newEvidence,
			evidenceCurieSearchFilter: generateCurieSearchField(newEvidence),
		});
	};

	const deletionHandler = (event, dataKey) => {
		event.preventDefault();
		setAlleleGeneAssociations((previous) => previous.filter((association) => association.dataKey !== dataKey));
		setErrorMessages((previous) => {
			const remaining = { ...previous };
			delete remaining[dataKey];
			return remaining;
		});
	};

	return (
		<FormTableWrapper
			labelColumnSize={labelColumnSize}
			table={
				<AlleleGeneAssociationsFormTable
					alleleGeneAssociations={alleleGeneAssociations}
					editingRows={editingRows}
					onRowEditChange={onRowEditChange}
					tableRef={tableRef}
					deletionHandler={deletionHandler}
					errorMessages={errorMessages}
					evidenceOnChangeHandler={evidenceOnChangeHandler}
					alleleGeneRelationOnChangeHandler={alleleGeneRelationOnChangeHandler}
					geneOnChangeHandler={geneOnChangeHandler}
					relatedNoteOnChangeHandler={relatedNoteOnChangeHandler}
					evidenceCodeOnChangeHandler={evidenceCodeOnChangeHandler}
					setErrorMessages={setErrorMessages}
				/>
			}
			tableName="Allele Gene Associations"
			showTable={alleleGeneAssociations.length > 0}
			button={
				<>
					<Button
						label="Add Gene Association"
						onClick={createNewGeneAssociationHandler}
						className="w-6 p-button-text"
					/>
					{loadError && (
						<Message
							severity="error"
							text="Could not load these gene associations, so they cannot be saved. Reload the page to try again."
						/>
					)}
				</>
			}
		/>
	);
};
