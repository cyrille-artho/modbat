#!/usr/bin/perl

# partially sorts input by sorting only parts that match a given regexp

my $regexp = shift;
my @buffer = ();

while (<>) {
  if (/$regexp/) {
    push @buffer, $_;
  } else {
    print sort @buffer;
    @buffer = ();
    print;
  }
}
print sort @buffer;
