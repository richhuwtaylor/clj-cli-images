# clj-cli-images

A simple interactive graphical editor for the command line, written in Clojure.

## User Guide

#### Running the Editor
Ensure [Leiningen](https://leiningen.org) is installed, then `lein run` in the project directory.

#### Pixel Co-ordinates
Pixel co-ordinates are represented by a pair of integers: 
1) a column number between 1 and M, and 
2) a row number between 1 and N, where 1 <= M, N <= 250. 

The origin sits in the upper-left of the table. Colours are specified by 
capital letters.

#### Commands
The editor supports 9 commands:
1) `I M N`​. Create a new M x N image with all pixels coloured white (O).
2) `C`​. Clears the table, setting all pixels to white (O).
3) `L X Y C`​. Colours the pixel (X,Y) with colour C.
4) `V X Y1 Y2 C`​. Draw a vertical segment of colour C in column X between rows Y1 and Y2
(inclusive).
5) `H X1 X2 Y C`​. Draw a horizontal segment of colour C in row Y between columns X1 and X2
(inclusive).
6) `F X Y C`​. Fill the region R with the colour C. R is defined as: Pixel (X,Y) belongs to R. Any other
pixel which is the same colour as (X,Y) and shares a common side with any pixel in R also
belongs to this region.
7) `R X Y C1 C2 ...` Fill concentric 'rings' of pixels centered on (X,Y) with the supplied colours C,
from innermost to outermost.
8) `S`​. Show the contents of the current image.
9) `X`​. Terminate the session.

#### Example Usage
Create a new 5 x 6 white image, colour pixel (2,3) with colour A, then show the image:  
```
I 5 6
L 2 3 A
S
```
Result:
```
OOOOO
OOOOO
OAOOO
OOOOO
OOOOO
OOOOO
```
Fill the region at (3,3) with colour J, draw a vertical segment of colour W in column 2 between rows 3 and 4, draw a 
horizontal segment of colour Z in row 2 between columns 3 and 4, then show the image:   
```
F 3 3 J
V 2 3 4 W
H 3 4 2 Z
S
```
Result:
```
JJJJJ
JJZZJ
JWJJJ
JWJJJ
JJJJJ
JJJJJ
```

#### Testing
To run tests of core functionality, `lein test`.
