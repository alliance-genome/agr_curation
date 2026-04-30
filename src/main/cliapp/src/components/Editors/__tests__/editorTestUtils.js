export const makeEditorOptions = (rowData, rowIndex = 0) => ({
	rowData,
	rowIndex,
	editorCallback: vi.fn(),
});

export const emptyErrorMessagesRef = { current: {} };
