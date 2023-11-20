export const StickyHeader = ({ children }) => {
  return (
    <div className="fixed top-0 left-0 mt-8 z-1 w-full bg-primary-reverse">
      {children}
    </div>
  );
};