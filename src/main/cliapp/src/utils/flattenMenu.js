/**
 * Recursively flattens the hierarchical menu structure into a flat array
 * of navigable items with breadcrumb paths.
 */
export function flattenMenu(menuTree, parentPath = [], isRoot = true) {
	const results = [];

	for (const item of menuTree) {
		if (item.to || (item.url && item.url.length > 0 && !item.url.includes('undefined'))) {
			results.push({
				label: item.label,
				breadcrumb: parentPath.join(' > '),
				to: item.to || null,
				url: item.url || null,
				target: item.target || null,
				icon: item.icon || 'pi pi-fw pi-home',
			});
		}

		if (item.items) {
			const nestedPath = item.label && !isRoot ? [...parentPath, item.label] : parentPath;
			results.push(...flattenMenu(item.items, nestedPath, false));
		}
	}

	return results;
}
