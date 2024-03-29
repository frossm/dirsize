<p align="center"> <img width="900" src ="https://github.com/frossm/dirsize/raw/master/graphics/ScreenShot.jpg"> </p> 

<p align="center"> <b><i>DirSize: The Command Line Directory Reporting Tool</i></b></p>

# DirSize
<img align="right" width="200" src="https://github.com/frossm/dirsize/raw/master/graphics/PostIt-512x512.jpg">DirSize is a directory reporting tool.  It recursively scans the provided (or current) directory, and reports the number of files and director sizes of each subdirectory present.  It also defaults to sorting by by directory size, but there are other options.

Lastly, it's written in Java and packaged into a single executable JAR file.  All of the dependencies built in so it should run anywhere.  Howver, I've only tested it on Windows 10/11 and Ubuntu.

## Maps
One of the interesting things about DirSize is it shows a visual representation of the file sizes of counts.  I call this a SizeMap or FilesMap and it's the area to the right of the output.  If you sort by size (default or -ss) or directories (-sd) is will show the SizeMap.  If sorted by file counts (-sf) the map will change to show a visual representation of the number of files per subdirectory, sorted from high to low.

## Command-Line Options
**Usage:**

`java -jar dirsize.jar [-D] [-x <filename>] [-ss|-sf|-sd] [-r] [-e] [-c width] [-v] [-z] [-h|?] [Directory]`

|Option|Description|
|-------|-----------|
|-D | Run program in debug mode.  This will display quite a bit of information on the program as it's running.  I usually use this as I debug the program, but if you wish to get a bit more insight into what's going on, go for it.|
|-x FileName|Export the results as a CSV to the file provided|
|-ss| Sort output by the directory sizes.  This is the default|
|-sf| Sort output by the number of files|
|-sd| Sort output by directory name|
|-r| Reverse the sort order.  Ascending or descending will depend on the sorting type selected. File and Size sorting will be displayed in ascending order.  Directory sorting will be displayed alphabetically in reverse order|
|-e| Suppress error display.  Normally, issues with scanning are display at the end of the output.  With this switch enabled, this list will be suppressed.  Scanning errors usually happen when DirSize does not have permission to a file or folder although there can be other reasons|
|-c Width|Set By default, DirSize uses a 90 character console width.  However, you can change this|
|-v| Simply display the program version and exit.  `-v` will also query GitHub and show the latest program release|
|-z| Disable colorized output.  DirSize colors are made for a dark terminal background.  You may need to do this to remove the colors which will then show correctly on light backgrounds|
|-h \| -?| Display the program help|

## Symbolic Links
DirSize will detect if a directory is a symbolic link and display it in a different color with `[LINK]` appended to the name.  However, this functionality does not seem to work with Windows.  I've tested this in Linux (Ubuntu) and it works well, but does not seem to work on Windows.  Java can't determine if a directory is a link.  I have not been able to test on a MAC, so if someone has a MAC, drop me a note and I'll update this README.

## Examples
**``java -jar dirsize.jar``**

Display a size sorted report from the current directory

**``java -jar dirsize.jar -sd C:\Apps``**

Display a directory name sorted report from the C:\Apps directory

**``java -jar dirsize.jar -c 80 -sf /home/jimbob``**

Display a file number sorted report from the jimbob's home dir using 100 columns

**``java -jar dirsize.jar -x $HOME/usr-output.csv /usr``**

Output the directory report of `/usr` into a CSV file in my home directory

## SNAP
[![dirsize](https://snapcraft.io//dirsize/badge.svg)](https://snapcraft.io/dirsize)

I would encourage anyone with a supported Linux platform to use snap. See Snapcraft Homepage for more information. You can download, install, and keep DirSize up to date automatically by installing the snap. You don't even have to have java as it's bundled within the snap virtual machine. Install via:

sudo snap install dirsize (Assuming snapd is installed)

[![Get it from the Snap Store](https://snapcraft.io/static/images/badges/en/snap-store-black.svg)](https://snapcraft.io/dirsize)

**Note:**

Snap applications run in a container and by default do not have rights to see files on the filesystem outside of this 'sandbox.'  In order to use DirSize, it needs to be able see file names and sizes.  The interface ``system-backup`` gives it that right.  Therefore, to use dirsize , you need to allow it to read file information by executing:

``sudo snap connect dirsize:system-backup``

If you change your mind, you can remove this permission by executing:

``sudo snap disconnect dirsize:system-backup``

If you use the `-x` export or `-i` import capabilities, be sure to assign the needed permissions via `snap connect`

``sudo snap connect dirsize:home``

## Wrapup
I'm making this freely available in the hope that others may find this useful. Please let me know if you have any issues, thoughts or suggestions for enhancements by mailing dirsize@fross.org.

## License
The MIT License

Copyright (C) 2011-2024 by Michael Fross

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
