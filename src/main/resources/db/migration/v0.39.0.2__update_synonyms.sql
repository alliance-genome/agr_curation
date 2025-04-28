  ALTER TABLE public.synonym
    ADD COLUMN hasbroadsynonym BOOLEAN DEFAULT false,
    ADD COLUMN hasexactsynonym BOOLEAN DEFAULT false,
    ADD COLUMN hasnarrowsynonym BOOLEAN DEFAULT false,
    ADD COLUMN hasrelatedsynonym BOOLEAN DEFAULT false,
    ADD COLUMN isdisplaysynonym BOOLEAN DEFAULT false;
