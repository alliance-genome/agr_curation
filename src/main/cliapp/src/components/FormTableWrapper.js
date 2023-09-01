export const FormTableWrapper = ({
  labelColumnSize,
  table,
  tableName,
  showTable,
  button,
}) => {
  return (
    <div className="grid">
      <div className="col-12">
        <div className="mb-3">
          <label>{tableName}</label>
        </div>
        {showTable && table}
        <div className={`${showTable ? "pt-3" : ""} p-field p-col col-4`}>
          {button}
        </div>
      </div>
    </div>
  );
};