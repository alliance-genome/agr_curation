/**
 * Covers the page while a request is in flight.
 *
 * @param {Object} props
 * @param {boolean} props.isLoading - true while the request is in flight
 * @param {string} [props.message] - what the page is waiting on
 */
export const LoadingOverlay = ({ isLoading, message = 'Saving in progress...' }) => {
	if (!isLoading) return null;
	return (
		<div className="top-0 left-0 w-full h-full opacity-90 fixed p-5 surface-overlay z-2 origin-top">
			<div className="flex align-items-center justify-content-center h-screen">
				<h1>{message}</h1>
			</div>
		</div>
	);
};
