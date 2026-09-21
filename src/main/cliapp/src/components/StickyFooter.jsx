/**
 * Pins its children to the bottom of the content column, above the page content.
 *
 * `sticky` rather than `fixed` so the layout sizes and offsets it, keeping the actions reachable
 * whichever way the sidebar is set.
 *
 * @param {Object} props
 * @param {React.ReactNode} props.children
 */
export const StickyFooter = ({ children }) => {
	return <div className="sticky bottom-0 z-1 bg-primary-reverse">{children}</div>;
};
