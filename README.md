# ClassFile-File-Search
Search & dump class files stored arbitrarily in files.

I've had to use this countless times - so I figured I would release a proper tool.

## How To Install
+ Download the latest release for your platform
+ Add the binary to your system-path
  + Or just call the absolute path to it in the command line

## How To Use - Count Class Files
`cffs count input.bin`
`cffs count input-p1.bin input-p2.bin`
`cffs count *.bin`
`cffs count dir/`

## How To Use - Dump Class Files
`cffs classes input.bin`
`cffs classes input-p1.bin input-p2.bin`
`cffs classes *.bin`
`cffs classes dir/`

## How To Use - Dump Strings From Found Class Files
`cffs strings input.bin`
`cffs strings input-p1.bin input-p2.bin`
`cffs strings *.bin`
`cffs strings dir/`

## How Does It Work?
+ We search for all instances of 'CAFEBABE' and store the data between each file, assuming the files are stored back to back.
+ We make the following assumptions:
  + It's uncompressed data
  + It's not lying to us when it says 'CAFEBABE'
  + The files are stored back to back, I.E. each chunk of 'CAFEBABE' represents another class once reached.