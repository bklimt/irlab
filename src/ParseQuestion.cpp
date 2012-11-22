
#include <stdio.h>
#include <stdlib.h>
#include <string>
using namespace std;

void usage() {
	fprintf( stderr, "usage: ParseQuestion <question> <output-file>\n" );
	exit(-1);
}

int main(int argc, char** argv) {
	if ( argc != 3 ) {
		usage();
	}
	string question = argv[1];
	string output = argv[2];
	string cmd = "gcl -load lisp/qa.lisp \"("+question+")\" >"+output;
	//printf( "%s", cmd.c_str() );
	system( cmd.c_str() );
	return 0;
}

