
use strict;

my $ready = 0;

while (<>) {
	if (/<email id="([0-9]*)">/) {
		open( FILE, ">data/split/$1.txt" );
		$ready = 1;
		print FILE;
	} elsif (/<\/email>/) {
		print FILE;
		$ready = 0;
		close( FILE );
	} else {
		if ( $ready != 0 ) {
			print FILE;
		}
	}
}

