<p align="center"> <img width="300" height = "300" src ="https://github.com/frossm/dirsize/blob/master/graphics/PostIt-512x512.jpg"> </p> 

<p align="center"> <b><i>DirSize: The Command Line Directory Reporting Tool</i></b></p>

# DirSize
DirSize is a directory reporting tool.  It recursively scans the provided (or current) directory, and reports the number of files and director sizes of each subdirectory present.  It also defaults to sorting by by directory size, but there are other options.

Lastly, it's written in Java and packaged into a single .JAR file.  All of the dependencies built in so it should run anywhere.  I've only tested it on Windows 10 and Ubuntu.

## Maps
One of the interesting things about DirSize is it shows a visual representation of the file sizes of counts.  I call this a SizeMap or FilesMap and it's the area to the right of the output.  If you sort by size (default or -ss) or directories (-sd) is will show the SizeMap.  If sorted by file counts (-sf) the map will change to show a visual representation of the number of files per subdirectory.

<p align="center"> <img width="800" src ="https://github.com/frossm/dirsize/blob/master/graphics/ScreenShot.jpg"> </p> 

## Command-Line Options
**Usage:**

`java -jar dirsize.jar [-D] [-x <filename>] [-ss|-sf|-sd] [-c width] [-v] [-h|?] [Directory]`

|Option|Description|
|-------|-----------|
|-D | Run program in debug mode.  This will display quite a bit of information on the program as it's running.  I usually use this as I debug the program, but if you wish to get a bit more insight into what's going on, go for it.|
|-x FileName|Export the results as a CSV to the file provided|
|-ss| Sort output by the directory sizes.  This is the default|
|-sf| Sort output by the number of files|
|-sd| Sort output by directory name|
|-e| Error display.  With this switch enabled, show a list of files and directories that had errors during the scanning process|
|-c Width|Set By default, DirSize uses the full console width.  However, you can change this.  Useful if you have a very wide console display|
|-v| Simply display the program version and exit|
|-h \| -?| Display the program help|

## Examples
**``java -jar dirsize.jar``**

Display a size sorted report from the current directory

**``java -jar dirsize.jar -sd C:\Apps``**

Display a directoyr name sorted report from the C:\Apps directory

**``java -jar dirsize.jar -c 80 -sf /home/jimbob``**

Display a file number sorted report from the jimbob's home dir using 100 columns

**``java -jar dirsize.jar -x $HOME/usr-output.csv /usr``**

Output the directory report of `/usr` into a CSV file in my home directory

## SNAP
I would encourage anyone with a supported Linux platform to use snap. See Snapcraft Homepage for more information. You can download, install, and keep DirSize up to date automatically by installing the snap. You don't even have to have java as it's bundled within the snap virtual machine. Install via:

sudo snap install dirsize (Assuming snapd is installed)

This will install the application into a sandbox where it is separate from other applications. I do want to look at packaging it via Flatpak as well, but my understanding is that Maven is not well supported. However, I need to do more investigation.

[![Get it from the Snap Store](https://snapcraft.io/static/images/badges/en/snap-store-black.svg)](https://snapcraft.io/dirsize)

## Wrapup
I'm making this freely available in the hope that others may find this useful. Please let me know if you have any issues, thoughts or suggestions for enhancements by mailing dirsize@fross.org.

## License
The MIT License

Copyright (C) 2011-2020 by Michael Fross

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
