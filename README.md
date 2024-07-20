# ClassFile-Searcher
Search & dump class files stored arbitrarily in files.

I've had to use this countless times - so I figured I would release a proper tool.

## How To Install
+ Download the latest release for your platform
+ Add the binary to your system-path
  + Or just call the absolute path to it in the command line

## How To Use - Count Class Files (Initial first look)
+ `cffs count input.bin` - Searches for classes inside the file input.bin
+ `cffs count input-p1.bin input-p2.bin` - Searches for classes inside the two files input-p1.bin & input-p2.bin
+ `cffs count *.bin` - Searches for classes in all files in the current path that end in .bin (non-recursive)
+ `cffs count dir/` - Searches for classes in the specified folder

## How To Use - Dump Strings From Found Class Files (Peak what's inside)
+ `cffs classes input.bin` - Searches for strings from classes inside the file input.bin
+ `cffs classes input-p1.bin input-p2.bin` - Searches for strings from classes inside the two files input-p1.bin & input-p2.bin
+ `cffs classes *.bin` - Searches for strings from classes in all files in the current path that end in .bin (non-recursive)
+ `cffs classes dir/` - Searches for strings from classes in the specified folder

## How To Use - Dump Class Files (Full dive with a decompiler)
+ `cffs classes input.bin` - Searches for classes inside the file input.bin
+ `cffs classes input-p1.bin input-p2.bin` - Searches for classes inside the two files input-p1.bin & input-p2.bin
+ `cffs classes *.bin` - Searches for classes in all files in the current path that end in .bin (non-recursive)
+ `cffs classes dir/` - Searches for classes in the specified folder

## How Does It Work?
+ We search for all instances of 'CAFEBABE' and store the data between each file, assuming the files are stored back to back.
+ We make the following assumptions:
  + It's uncompressed data
  + It's not lying to us when it says 'CAFEBABE'
  + The files are stored back to back, I.E. each chunk of 'CAFEBABE' represents another class once reached.