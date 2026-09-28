// `sticky` keeps the header in the content column, so it is sized and offset by the layout and
// stays reachable whichever way the sidebar is set. The offset matches the topbar's own 5rem
// height, in rem so both track the component scale.
export const StickyHeader = ({ children }) => {
	return (
		<div className="sticky z-1" style={{ top: '5rem' }}>
			{children}
		</div>
	);
};
