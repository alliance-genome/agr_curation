DO
$$
DECLARE
    r RECORD;
BEGIN
    FOR r IN
        SELECT table_schema,
               table_name,
               column_name
        FROM information_schema.columns
        WHERE column_name IN ('internal', 'obsolete')
          AND is_nullable = 'NO'
          AND table_schema NOT IN ('pg_catalog', 'information_schema')
    LOOP
        RAISE NOTICE 'Altering %.% column % to DROP NOT NULL',
            r.table_schema, r.table_name, r.column_name;

        EXECUTE format(
            'ALTER TABLE %I.%I ALTER COLUMN %I DROP NOT NULL;',
            r.table_schema,
            r.table_name,
            r.column_name
        );
    END LOOP;
END;
$$;
