export const NumberTemplate = ({ number }) => {
    //still want to pass through falsy 0 values
    const num = new Intl.NumberFormat().format(number);
    console.log("totalRecordsDisplay type in component", typeof num);
    if (number === null || number === undefined) return;
    return new Intl.NumberFormat().format(number);
};