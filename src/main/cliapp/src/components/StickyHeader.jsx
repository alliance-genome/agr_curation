// `sticky` keeps the header in the content column, so it is sized and offset by the layout and
// stays reachable whichever way the sidebar is set. The offset matches the topbar's own 5rem
// height, in rem so both track the component scale. z-3 (rather than PrimeReact's default z-1)
// keeps it above a scrollable DataTable's own sticky thead (App.scss sets that to z-index 2), so
// an embedded table's header can't paint over this one once it scrolls underneath.
export const StickyHeader = ({ children }) => {
	return (
		<div className="sticky z-3" style={{ top: '5rem' }}>
			{children}
		</div>
	);
};
