import { createContext, useContext } from 'react';

const TableEditorContext = createContext(null);

/**
 * Supplies the table editing strategy to every TableField beneath it, so column
 * definitions do not each have to thread it through.
 *
 * @param {object} strategy - see the strategies directory
 * @param {React.ReactNode} children
 * @returns {JSX.Element}
 */
export const TableEditorProvider = ({ strategy, children }) => (
	<TableEditorContext.Provider value={strategy}>{children}</TableEditorContext.Provider>
);

/**
 * Reads the current table editing strategy.
 *
 * @returns {object|null} null when there is no provider above
 */
export const useTableStrategy = () => useContext(TableEditorContext);
