UPDATE bulkloadfileexception
SET exception = jsonb_set(
    exception::jsonb - 'messages',
    '{message}',
    to_jsonb(array_to_string(
        ARRAY(SELECT jsonb_array_elements_text(exception::jsonb -> 'messages')),
        ' | '
    ))
)
WHERE jsonb_typeof(exception::jsonb -> 'messages') = 'array';
