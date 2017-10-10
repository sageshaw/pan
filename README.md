# pan (PointANalysis)
ImageJ plugin for 3D point analysis.

####Build Instructions (Maven must be installed first)
```$xslt
git clone https://github.com/sageshaw/pan
mvn clean package
```
Drag jars in `target` folder into `jars` folder in ImageJ.

####Getting Started

######Importing a 3D point set from Nikon Elements
* In ImageJ, select PAN>Add point set from text file...
* Browse for text file export from Nikon Elements
* Select "Crop Image to fit dataset" to move absolute positioning to relative positioning.
* Select "Show Image render..." to generate image of your dataset.

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

######Cross-Channel Analysis
* Select Pan>Analysis>Cross-Channel Analysis
* To export text file, select "Export Text File", to display a histogram, select "Show Histogram"

A note on "single-channel" vs. "double-channel" :
'Single-channel' is the analysis of a single channel performing the operation on itself, while
'cross-channel' is the analysis of one channel operation on another channel.
Ex: In a nearest neighbor algorithm, cross-channel analysis would find the nearest neighbors of channel A located in channel B.
Single-Channel would find nearest neighbors of channel A in channel A.
