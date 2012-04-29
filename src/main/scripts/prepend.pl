#!/usr/bin/perl

$file = $ARGV[0];
$license = $ARGV[1];

print "Prepending preamble $license to file $file\n";


open( L, $license ) || die "Could not open license-file $license!\n";

$preamble = "";
while( <L> ){
    $preamble = $preamble . $_;
}
chomp($preamble);
close( L );

#print "Preamble to add:\n$preamble";

$tmp = "$file.$$";
open( OUT, ">$tmp" ) || die "Could not open temporary output file $tmp for writing!\n";
open( F, $file ) || die "Could not open file $file for reading!\n";

print OUT $preamble . "\n";
$foundPackage = 0;

while( <F> ){
  if( $foundPackage == 1 || /^package.*/ ){
     $foundPackage = 1;
  }
  print OUT $_ if $foundPackage == 1; 
}

close( F );
close( OUT );

rename ($tmp, $file) || die "Failed to replace original file $file !\n";

#print "Prepended preamble to file $tmp\n";
