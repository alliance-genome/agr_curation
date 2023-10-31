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
        <div className="mb-3">
          <label>{tableName}</label>
        </div>
        {showTable && table}
        <div className={`${showTable ? "pt-3" : ""} p-field p-col ${includeField ? "col-12" : "col-4"} col-4`}>
          {button}
        </div>
      </div>
    </div>
  );
};