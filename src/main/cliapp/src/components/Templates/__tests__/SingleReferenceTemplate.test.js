import { render } from '@testing-library/react';
import { SingleReferenceTemplate } from '../reference/SingleReferenceTemplate';
import '../../../tools/jest/setupTests';

describe("SingleReferenceTemplate", () => {

  it("should return null when singleReference is falsy", () => {
    const singleReference = null;
    const { container } = render(<SingleReferenceTemplate singleReference={singleReference} />);

    expect(container.firstChild).toBeNull();
  });

});

