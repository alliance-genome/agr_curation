export const FormTableWrapper = ({
  labelColumnSize,
  table,
  tableName,
  showTable,
  button,
}) => {
  return (
    <div className="grid">
      <div className={labelColumnSize}>
        <label>{tableName}</label>
      </div>
      <div className="col-6">
        {showTable && table}
        <div className={`${showTable ? "pt-3" : ""} p-field p-col`}>
          {button}
        </div>
      </div>
    </div>
  );
};