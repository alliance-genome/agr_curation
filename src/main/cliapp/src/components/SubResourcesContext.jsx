import { createContext, useContext } from 'react';

/**
 * The sub-resources a detail or create page holds, keyed by name.
 *
 * A sub-resource is a field an entity's own endpoints cannot carry, so it is read and written
 * through a path of its own. Its section needs the state, and a create page needs it again to save
 * once the entity exists - but the form between them does not, so it passes through this rather than
 * through the form's props, which would otherwise grow a pair for every such field.
 */
const SubResourcesContext = createContext(null);

export const SubResourcesProvider = ({ value, children }) => (
	<SubResourcesContext.Provider value={value}>{children}</SubResourcesContext.Provider>
);

/**
 * @param {string} name the key the page provided this sub-resource under
 * @returns {Object} whatever the page put there, typically a hook's return value
 */
export const useSubResource = (name) => {
	const subResources = useContext(SubResourcesContext);
	const subResource = subResources?.[name];

	if (!subResource) {
		throw new Error(`No "${name}" sub-resource in context. Render this inside a SubResourcesProvider that holds one.`);
	}

	return subResource;
};
