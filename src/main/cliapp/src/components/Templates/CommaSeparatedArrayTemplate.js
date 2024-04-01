export const CommaSeparatedArrayTemplate = ({ array }) => {
    if (Array.isArray(array) && array.length > 0) {
        return array.join(', ');
    }
    return null;
};