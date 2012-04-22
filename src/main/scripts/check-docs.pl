#!/usr/bin/perl

open(IN, "find . -name '*.java'|") || die "Failed to execute 'find' command!\n";

while( <IN> ){

   chomp;
   $author = "";
   $processor = 0;
   $file =$_;
   $cancel = 0;
   $lineAfterImplements = 10000000;
   open( F, "$file" ) || die "Failed to open file $_ for reading!\n";
   while( <F> ){
	if( $cancel == 1 ){
	   break;
	}

	if( /class/ && /abstract/ ){
	   break;
#	   $cancel = 1;
	   next;
	}
	if( /public\s+interface/ ){
	   break;
#	   $cancel = 1;
	   next;
        }

	if( /\@author\s+(.*)/ ){
	    $author = $1;
	}

	if( /implements/ && /Processor/ ){
	    $lineAfterImplements = 0;
	}
        if( $lineAfterImplements < 3 && /Processor/ ){
	    $processor = 1;
	}
   }
   
   close(F);

   if( $processor == 1 ){
      print "Found processor in file $file!\n";
#      print "   author is: $author\n";
   $doc = $file;
   $doc =~ s/\.java/\.md/;
   $doc =~ s/\/java\//\/resources\//;
#   print "doc file is $doc\n";
   if( ! -e $doc ){
      print "documentation $doc does not exist!\n";

   }
   }
}
