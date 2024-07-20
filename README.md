# Class-File-Searcher
Command-line tool for dumping Class-files & Strings from arbitrary data

## How To Install
+ [Download the latest release for your platform](https://github.com/Konloch/Class-File-Searcher/releases/latest)
+ Add the binary to your system-path
  + Or just call the absolute path to it in the command line

## How To Use - Count Class Files (Initial first look)
+ `cfs count input.bin` - Searches for classes inside the file input.bin
+ `cfs count input-p1.bin input-p2.bin` - Searches for classes inside the two files input-p1.bin & input-p2.bin
+ `cfs count *.bin` - Searches for classes in all files in the current path that end in .bin (non-recursive)
+ `cfs count dir/` - Searches for classes in the specified folder

## How To Use - Dump Strings From Found Class Files (Peak what's inside)
+ `cfs classes input.bin` - Searches for strings from classes inside the file input.bin
+ `cfs classes input-p1.bin input-p2.bin` - Searches for strings from classes inside the two files input-p1.bin & input-p2.bin
+ `cfs classes *.bin` - Searches for strings from classes in all files in the current path that end in .bin (non-recursive)
+ `cfs classes dir/` - Searches for strings from classes in the specified folder

## How To Use - Dump Class Files (Full dive with a decompiler)
+ `cfs classes input.bin` - Searches for classes inside the file input.bin
+ `cfs classes input-p1.bin input-p2.bin` - Searches for classes inside the two files input-p1.bin & input-p2.bin
+ `cfs classes *.bin` - Searches for classes in all files in the current path that end in .bin (non-recursive)
+ `cfs classes dir/` - Searches for classes in the specified folder

## How Does It Work?
+ We search for all instances of 'CAFEBABE' and store the data between each file, assuming the files are stored back to back.
+ We make the following assumptions:
  + It's uncompressed data
  + It's not lying to us when it says 'CAFEBABE'
  + The files are stored back to back, I.E. each chunk of 'CAFEBABE' represents another class once reached.