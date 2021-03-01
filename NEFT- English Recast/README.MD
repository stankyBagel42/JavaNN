##Setup
The file "Config.java" has a bunch of different config settings for the program 
along with explanations as to what each setting does. To start the network run "frameStuff.java"

## Explanation
The network that is shown upon running "frameStuff.java" is a network with 3 input colors 
(full red, full green, and full blue) and it tries to approximate N output colors 
(specified in the configuration file). The large rectangles in between nodes represent the weights
of the network, with the color being its sign (red is positive, blue is negative) and the width of the 
rectangle representing the magnitude of that weight. Each hidden node also is colored to represent whatever
color value it is outputting.

Also, this project was made without using libraries like tensorflow so I used a genetic
algorithm to fit the network, which is why there is a generation count and fitness
number, you can think of the fitness number as a cost function to be minimized.