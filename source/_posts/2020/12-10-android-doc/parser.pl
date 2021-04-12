#!/usr/bin/env perl -l

# BSD 0-clause license
#
# Copyright (C) 2020 by Julian Chu
#
# Permission to use, copy, modify, and/or distribute this software for any purpose
# with or without fee is hereby granted.
#
# THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
# REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND
# FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
# INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS
# OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
# TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
# THIS SOFTWARE.

my $pwd = `pwd`;
chomp $pwd;
my @files = `find . -regex ".*.html" | sort -t '/' `;
print "window.rawData = [";
foreach my $line(@files) {
    chomp $line;
    substr $line, 0, 1, ""; # remove leading '.'
    my $full = $pwd.$line;
    @match = $full=~ /.*\/reference\/(.*)\.html/;
    @match[0] =~ tr/\//\./;
    my $class = @match[0] =~ s/^.*\.//r;
    if ($class) {
        print "{";
        print "name: \"", @match[0], "\",";
        print "class: \"", $class, "\",";
        print "key: \"", lc @match[0], "\",";
        print "url: \"$full\"";
        print "},";
    }
}
print "];";
