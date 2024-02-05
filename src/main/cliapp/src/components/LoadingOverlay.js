export const LoadingOverlay = ({ isLoading }) => {
  if (!isLoading) return null;
  return (
    <div className="top-0 left-0 w-full h-full opacity-90 fixed p-5 surface-overlay z-2 origin-top">
      <div className='flex align-items-center justify-content-center h-screen'>
        <h1>Saving in progress...</h1>
      </div>
    </div>
  );
};