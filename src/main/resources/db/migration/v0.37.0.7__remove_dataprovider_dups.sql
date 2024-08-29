-- Update the null resourcedescriptorpage_id to the XB home page because Xenbase != XB

update crossreference 
	set resourcedescriptorpage_id = (
		SELECT rp.id 
		FROM resourcedescriptorpage rp, resourcedescriptor rd 
		WHERE rp.resourcedescriptor_id=rd.id and rd.prefix = 'Xenbase' and rp.name = 'homepage'
)
WHERE resourcedescriptorpage_id is null and referencedCurie = 'XB';

CREATE INDEX organization_abbreviation_index ON organization USING btree (abbreviation);
