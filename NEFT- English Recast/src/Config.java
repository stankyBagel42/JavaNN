// This program will show the user a neural network whose job is to optimize the combination of 3 colors to match a
// number of output colors. It does this optimization through neuroevolution, a process similar to natural selection
// in which the best performers are more likely to 'reproduce' for the next generation. It also keeps count of how many
// generations have passed since the overall population has improved, if this count gets to 300 generations,
// the program will stop running.

// NOTE: The fitness in this program represents the absolute error of the network's outputs vs the target outputs,
// so the program actually optimizes for the lowest fitness but the ideas of neuroevolution still apply.

// Also, the neurons are displayed with whatever color value they have in the current network, the outputs are displayed
// with their current color value on the inside and the target output color value on the outside. The lines in between
// neurons are the weights, the width of the line is the strength of the weight and the
// color is the sign; red being positive, and blue being negative.

// NORMAL VALUES FOR THE DIFFERENT CONFIG VARIABLES:
// weightMax=10
// populationSize=[100-1000)
// size = 50
// delay = (5,20)
// hiddenNeurons = (outputCount/2,outputCount)
// outputCount = (3,8)
// panicMutationChance = (0.5,0.9)
// panicWeightAdjustChance = (0.2,0.5)
// defaultMutationChance = (0.05-0.2)
// defaultWeightAdjustChance = (0.7-0.9)

// Header config file for ease of use
public class Config {
    // The maximum possible weight, weights can be generated from -weightMax to weightMax
    public static int weightMax=10;
    // The size of the population of networks
    public static int populationSize=500;
    // Keeps track of how long it has been since the fitness has changed
    // If it has been too long since the fitness has changed then we turn up the mutation chances to try and increase
    //  the search range. See "panic mode"
    public static int fitChange=0;
    // The true value of the generations since the last change it fitness
    // (The above value is used to determine when panic mode happens, this one determines when the program ends)
    public static int trueFitChange=0;
    // The size of the circles in the display
    public static final int size=50;
    // How often to update the frame and run the evaluations
    public static final long delay=5;
    // If set to false, changes the displayed network to be the best overall. If true, it will show you the best network
    // of the current generation
    public static boolean viewBestOfGen=true;
    // Panic mode is when the system sees little to no change in fitness over a few generations so it turns up the
    // mutation chance during crossover. We start out not in panic mode. The point of panic mode is to increase the
    // search range of the population by spreading out the gene pool
    public static boolean panicModeEnabled=false;
    // There's one hidden layer in this example for simplicity, this parameter adjusts the amount of neurons it contains
    public static int hiddenNeurons=4;
    // The amount of outputs to try and optimize the colors for (each output is a randomly chosen color)
    public static int outputCount=6;
    // The mutation settings for panic mode, the mutation chance is the chance for a mutation to occur and the weight
    // adjust chance is the chance for the mutation to either just adjust a weight or replace it entirely
    public static double panicMutationChance=0.6;
    public static double panicWeightAdjustChance=0.5;
    // The non-panic mode mutation settings
    public static double defaultMutationChance=0.1;
    public static double defaultWeightAdjustChance=0.8;
    // After this many generations without an improvement of the best fitness, enable panic mode
    public static int panicModeThreshold=300;
    // After this many generations without an improvement of the best fitness, stop running the program
    public static int executorStopThreshold=500;
}
