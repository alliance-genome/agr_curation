/**
 * FormField layout props for an entity's detail or create page: label, widget, then the field's additional data.
 */
export const DETAIL_FIELD_LAYOUT = {
	labelColumnSize: 'col-3',
	widgetColumnSize: 'col-4',
	fieldDetailsColumnSize: 'col-5',
};

/**
 * Field layout props for a detail page with no additional data column: the label, then a widget filling the rest of
 * the row.
 */
export const WIDE_DETAIL_FIELD_LAYOUT = {
	labelColumnSize: 'col-3',
	widgetColumnSize: 'col-9',
	showAdditionalData: false,
};

/**
 * FormField layout props for the New Annotation dialog: a narrow label at body text size, the widget, then the
 * field's additional data.
 */
export const NEW_ANNOTATION_FIELD_LAYOUT = {
	labelColumnSize: 'col-2',
	widgetColumnSize: 'col-4',
	fieldDetailsColumnSize: 'col-5',
	labelClassName: 'text-base font-semibold m-0',
};

/**
 * FormField layout props for a narrow dialog: the label above its widget at body text size, full width and close
 * together, with no additional data column.
 */
export const DIALOG_FIELD_LAYOUT = {
	labelColumnSize: 'col-12 pb-0',
	widgetColumnSize: 'col-12 pt-1',
	labelClassName: 'text-base font-semibold m-0',
	showAdditionalData: false,
};
