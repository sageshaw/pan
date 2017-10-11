# pan (PointANalysis)
ImageJ plugin for 3D point analysis.

####Build Instructions (Maven must be installed first)
```
git clone https://github.com/sageshaw/pan
mvn clean package
```
Drag jars in `target` folder into `jars` folder in ImageJ.

####Getting Started

######Importing a 3D point set from Nikon Elements
* In ImageJ, select PAN>Add point set from text file...
* Browse for text file export from Nikon Elements
* Select "Crop Image to fit dataset" to move absolute positioning to relative positioning.
* Select "Show Image render..." to generate image of imported dataset.
Note: this plugin was originally developed to round all decimal numbers to the nearest integer (the analysis did not need)
to be that precise for the original use-case).

######Displaying image from loaded point set
* Select PAN>Render Image from dataset
* Select point marker size (radius in pixels)
* Select channels to render

######Removing dataset from plugin
* Select PAN>Remove channel set...
* Specify dataset (denoted by imported file name) to remove

######Single-Channel Analysis
* Select Pan>Analysis>Single-Channel Analysis
* To export text file, select "Export Text File", to display a histogram, select "Show Histogram"
* Select desired analysis method, and fill appropriate fields

######Cross-Channel Analysis
* Select Pan>Analysis>Cross-Channel Analysis
* To export text file, select "Export Text File", to display a histogram, select "Show Histogram"
* Select desired analysis method, and fill appropriate fields

A note on "single-channel" vs. "double-channel" :
'Single-channel' is the analysis of a single channel performing the operation on itself, while
'cross-channel' is the analysis of one channel operation on another channel.
Ex: In a nearest neighbor algorithm, cross-channel analysis would find the nearest neighbors of channel A located in channel B.
Single-Channel would find nearest neighbors of channel A in channel A.

######Importable Text File Format (in case you are not using Nikon Elements)
The plugin will compare the first line to the line below to confirm the file is importable. This
format is the same format used by Nikon Elements when exporting a point set as a .txt file.
```
Channel Name	X	Y	Xc	Yc	Height	Area	Width	Phi	Ax	BG	I	Frame	Length	Link	Valid	Z	Zc	Photons	Lateral Localization Accuracy	Xw	Yw	Xwc	Ywc	Zw	Zwc
```
All columns are separated by a tab character, rows separated by a carriage return.
Rows represent an individual point, columns represent attributes of that specific point.

Relevant columns (used by the plugin):
* Channel Name (1st column): Channel number point is associated with
* X (2nd column): x position (no units should be present, plugin assumes all coordinates in same unit) 
* Y (3rd column): y position
* Z (17th column): z position
Note: decimals numbers are allowed, but will be rounded to the nearest integer.

Example of a "manual" row:
```
Channel Name	X	Y	Xc	Yc	Height	Area	Width	Phi	Ax	BG	I	Frame	Length	Link	Valid	Z	Zc	Photons	Lateral Localization Accuracy	Xw	Yw	Xwc	Ywc	Zw	Zwc
999	123	456	0	0	0	0	0	0	0	0	0	0	0	0	0	789	0	0	0	0	0	0	0	0	0

```
The first row describes a point associated with channel 999 with an X coordinate of 123, Y coordinate of 456, and Z coordinate of 789.

After a few rows, manual point entry can be cumbersome, so use of a spreadsheet program will make this much easier.