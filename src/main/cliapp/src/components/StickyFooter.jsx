/**
 * Pins its children to the bottom of the viewport, above the page content.
 *
 * @param {Object} props
 * @param {React.ReactNode} props.children
 */
export const StickyFooter = ({ children }) => {
	return <div className="fixed bottom-0 z-1 w-12 bg-primary-reverse">{children}</div>;
};
