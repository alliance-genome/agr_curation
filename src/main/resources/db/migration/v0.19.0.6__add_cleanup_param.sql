ALTER TABLE public.bulkloadfile_aud
   ADD bulkloadcleanup character varying(255);
ALTER TABLE public.bulkloadfile
   ADD bulkloadcleanup character varying(255);

UPDATE public.bulkloadfile_aud SET bulkloadcleanup = 'YES';
UPDATE public.bulkloadfile SET bulkloadcleanup = 'YES';
