
while (<stdin>) {
	if ( /^([0-9]+\.txt)/ ) {
		$id = $1;
	}
	if ( /^false .: $ARGV[0]/ ) {
		print "$id\t";
		print;
	}
}

