import { Message } from 'primereact/message';
import { RequiredFieldMarker } from './RequiredFieldMarker';

/**
 * A detail page field row: its label, editor, errors and additional data.
 *
 * @param {Object} props
 * @param {boolean} [props.isPending] - highlights the row and says it has pending edits
 * @param {string} [props.labelClassName] - classes for the label heading, such as a smaller size in a dialog
 */
export const DetailPageFieldWrapper = ({
	formField,
	errorField,
	additionalDataField,
	labelColumnSize,
	widgetColumnSize,
	fieldDetailsColumnSize,
	fieldName,
	showAdditionalData = true,
	required = false,
	isPending = false,
	labelClassName,
}) => {
	return (
		<div className={isPending ? 'grid border-left-3 border-yellow-500' : 'grid'}>
			<div className={labelColumnSize}>
				<h2 htmlFor={fieldName?.toLowerCase()} className={labelClassName}>
					{required && <RequiredFieldMarker />}
					{fieldName}
				</h2>
			</div>
			<div className={widgetColumnSize}>
				{formField}
				{errorField}
				{isPending && <Message severity="warn" text="Pending Edits!" />}
			</div>
			{showAdditionalData && <div className={fieldDetailsColumnSize}>{additionalDataField}</div>}
		</div>
	);
};
