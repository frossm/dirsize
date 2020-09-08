<p align="center"> <img width="120" height = "120" src ="https://github.com/frossm/dirsize/blob/master/graphics/PostIt-200x200.jpg"> </p> 

<p align="center"> <b><i>DirSize: The Command Line Directory Reporting Tool</i></b></p>

# DirSize
DirSize is a directory reporting tool.  It recursively scans the provided (or current) directory, and reports the number of files and director sizes of each subdirectory present.

### Command-Line Options
**Usage: `java -jar dirsize.jar [-D] [-v] [-h|?] [-e] [-d b|k|m|g|h] [-w width] [-x <filename>] [Directory]`**

|Option|Description|
|-------|-----------|
|-D | Run program in debug mode.  This will display quite a bit of information on the program as it's running.  I usually use this as I debug the program, but if you wish to get a bit more insight into what's going on, go for it.|
|-v| Simply display the version and exit|
|-h or -?| Display the program help information and exit|
|-e| Hide errors|
|-x FileName|Export the results as a CSV to the file provided|
|-c width| DirSize will use the width of the terminal in columns, but you can specify a different number of columns|

### Examples
