export const FormTableWrapper = ({
  table,
  tableName,
  showTable,
  button,
  includeField=false,
}) => {
  return (
    <div className="grid">
      <div className="col-12">
        <div className="mb-3 grid">
          <div><h2>{tableName}</h2></div>
          <div className={`${showTable ? "pt-3" : ""} p-field p-col ${includeField ? "col-12" : "col-4"} col-4`}>
            {button}
          </div>
        </div>
        {showTable && table}
      </div>
    </div>
  );
};