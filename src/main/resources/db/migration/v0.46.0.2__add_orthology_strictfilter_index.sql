CREATE INDEX genetogeneorthologygenerated_strictfilter_index
	ON genetogeneorthologygenerated (id)
	WHERE strictfilter = true;
