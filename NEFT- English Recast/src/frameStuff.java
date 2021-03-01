import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.concurrent.*;

// The first way I found out how to get a working display in java was with this method of extending
// JPanel and adding a canvas to the screen
public class frameStuff extends JPanel {
    // The array of output colors
    public static Color[] targetColors=new Color[Config.outputCount];
    // Frame height and width
    public static final int width=800;public static final int height=800;
    // Set the fitness to be very high so the first generation's best immediately takes over
    public static double bestFitness=99999999999.;
    // A variable to store the last generation's best fitness, used to see if the fitness has changed for panic mode
    public static double lastGenBestFit;
    // The array of networks used, has size Config.populationSize
    public static Network[] population=new Network[Config.populationSize];
    // The array of color inputs (Full red, full green, full blue)
    public static Color[] inputs;
    // The variable to hold the current best overall network as a reference
    public static Network best;
    // Create an array to store the next generation of networks (used when switching generations)
    public static Network[] nextGen=new Network[Config.populationSize];
    // Generation counter
    public static int generation=0;
    // The average generation fitness for displaying in window
    public static double avgFit;
    // Set mutation and weight change chance to the values in the config file
    public static double mChance=Config.defaultMutationChance;
    public static double wChangeChance=Config.defaultWeightAdjustChance;
    // Create a buffered image object to hold the display frame
    private static BufferedImage bi = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
    // Create a graphics object for the buffered image to allow drawing on the image
    public static Graphics2D c=bi.createGraphics();

    // Updates the visuals
    public void paint(Graphics g) {
        // set g2 to the parameter g but cast to a Graphics2D object
        Graphics2D g2=(Graphics2D) g;
        // Set the color of the Graphics2D object c to the background
        c.setColor(g2.getBackground());
        // Fill the entire frame with that color
        c.fillRect(0,0,getWidth(),getHeight());
        // If there hasn't been a generation tested yet
        if(best!=null) {
            // Go through each of the inputs and draw their circles with the correct colors
            for (int i = 0; i < inputs.length; i++) {
                c.setColor(inputs[i]);
                c.fillOval(0, (i * (Config.size + Config.size/2)) + Config.size, Config.size, Config.size);
                c.setColor(Color.BLACK);
                c.drawOval(0, (i * (Config.size + Config.size/2)) + Config.size, Config.size, Config.size);
            }
            // Write the info about the current generation to the frame
            c.drawString("Best Fitness: "+best.fitness,100,height-80);
            c.drawString("Best Fitness of Generation: "+population[0].fitness,100,height-60);
            c.drawString("Average Fitness: "+avgFit,100,height-40);
            c.drawString("Generation "+generation,width/2,20);
            // Draw the best network's hidden layer
            for (int i = 0; i < best.hidden.length; i++) {
                // xVal is static because the neurons are shown in a column, the yVal changes depending on i
                int yVal = (i * (Config.size + Config.size/2)) + Config.size;
                int xVal = width / 2 - (Config.size / 2);
                // j is a counter because this for loop isn't indexed, it is looping through the neurons
                int j = 0;
                for (Map.Entry<Neuron, Double> entry : best.hidden[i].inputs.entrySet()) {
                    // new X and new Y for the weight line
                    int nX = 0;
                    int nY = (j * (Config.size + Config.size/2) + Config.size);
                    // get the weight from the neuron
                    double w = entry.getValue();
                    // figure out the sign of the weight (positive/negative)
                    int wSign = (int) Math.signum(w);
                    // If the weight is positive, make the color red, if it's not then make it blue
                    if (wSign > 0) {
                        c.setColor(new Color(255,0,0,70));
                    } else c.setColor(new Color(0,0,255,70));
                    // Set the stroke width to 5*the weight itself
                    c.setStroke(new BasicStroke((int) (Math.abs(w * 5))));
                    // Draw a line from the previous neuron to the hidden neuron
                    c.drawLine(width / 2, yVal + (Config.size / 2), nX + (Config.size), nY + Config.size / 2);
                    j++;
                }
                // Reset the stroke width to normal
                c.setStroke(new BasicStroke(1));
                // get the color for the inside of each hidden neuron
                c.setColor(best.hidden[i].getValue());
                // Color the inside of each hidden neuron
                c.fillOval(xVal, yVal, Config.size, Config.size);
                // Color a border around each hidden neuron in black
                c.setColor(Color.BLACK);
                c.drawOval(xVal, yVal, Config.size, Config.size);
            }
            // This loop colors the output layer's neurons and their weights
            for (int i = 0; i < best.outputs.length; i++) {
                // set xVal and yVal similar to the previous loop but further right
                int xVal = (width - (Config.size) - (Config.size / 2));
                int yVal = (i * (Config.size+Config.size/2)) + Config.size;
                // Same as the previous loop, use j as a counter and set the color to red if positive and blue otherwise
                int j = 0;
                for (Map.Entry<Neuron, Double> entry : best.outputs[i].inputs.entrySet()) {
                    int nY = (j * (Config.size + Config.size/2)) + Config.size;
                    double w = entry.getValue();
                    int wSign = (int) Math.signum(w);
                    if (wSign > 0) {
                        c.setColor(new Color(255,0,0,70));
                    } else c.setColor(new Color(0,0,255,70));
                    // Set the stroke width to the weight's value*5 and draw a line with that width connecting the
                    // output neuron to the hidden neuron
                    c.setStroke(new BasicStroke((int) (Math.abs(w * 5))));
                    c.drawLine(xVal, yVal + (Config.size / 2), width / 2 + (Config.size / 2), nY + Config.size / 2);
                    j++;
                }
                // Create a circle behind each output that shows the actual target color value (used for judging by eye)
                c.setColor(targetColors[i]);
                c.fillOval(xVal - (int) (Config.size * 0.15), yVal - (int) (Config.size * 0.15), (int) (Config.size * 1.3), (int) (Config.size * 1.3));
                // Reset the stroke then color the inside of each output neuron to what their actual colors are
                c.setStroke(new BasicStroke(1));
                c.setColor(best.outputs[i].getValue());
                c.fillOval(xVal, yVal, Config.size, Config.size);
                // Draw a black border around the output neuron to visually separate the target vs the actual
                c.setColor(Color.BLACK);
                c.drawOval(xVal, yVal, Config.size, Config.size);
            }
            // draw all of the stuff we just did on the bufferedImage object
            g2.drawImage(bi, 0, 0, getWidth(), getHeight(), Color.white, this);
        }
    }

    // A function to keep a value within a certain range
    public static int clampRange(int num,int min,int max){
        if(num<min){
            return min;
        }else if(num>max){
            return max;
        }
        return num;
    }
    // A class that is used to sort the population by fitness
    public static class sortByFitness implements Comparator<Network>{
        public int compare(Network a, Network b){
            return Double.compare(a.fitness,b.fitness);
        }
    }

    // The setup function, ran once on startup
    private static void setup(){
        // Set each of the target output colors to random colors
        for (int i = 0; i < targetColors.length; i++) {
            targetColors[i]=new Color((int)(Math.random()*256),(int)(Math.random()*256),(int)(Math.random()*256));
        }
        // Set the input colors to their constant red, green, and blue
        inputs=new Color[]{new Color(255,0,0),new Color(0,255,0),new Color(0,0,255)};
        // Create a network with 3 inputs for every index in the population array
        for (int i = 0; i < population.length; i++) {
            population[i]=new Network(3,frameStuff.targetColors.length,Config.hiddenNeurons);
        }
    }

    // This function finds the best network of the current generation and sets the 'best' variable to that network
    public static void findBest(){
        //  Temporary variable used to calculate the average fitness
        double temp=0;
        for (Network n:population) {
            n.evaluateFitness();
            temp+=n.fitness;
        }
        // Set this generation's average fitness to a variable used in displaying it
        avgFit=(temp/population.length);
        // Sort the population array by fitness
        Arrays.sort(population,new sortByFitness());
        // If you are viewing the best of each generation instead of best overall, the best network is set to the best
        // of the generation according to the sorting. Otherwise, it is whichever network performed best overall
        if(Config.viewBestOfGen){
            best=population[0];
        }
        if(population[0].fitness<bestFitness){
            best=population[0];
        }
        // output statement used in debugging, the second one shows the variable that determines when the program stops
//        System.out.println("Best fitness for this generation: "+bestOf[0].fitness+" fitChange= "+Config.fitChange);
        System.out.println("Generations since the last change in fitness: "+Config.trueFitChange);
    }

    // A function to choose a network with a bias towards higher fitness levels
    private static Network chooseParent() {
        // Pick a random number with upper bound of highest fitness/2+lowest fitness
        // and a lower bound of the lowest fitness of this generation
        // Pick a random organism, if that organism's fitness is below the random number (fitness is supposed to be low)
        // then it is chosen as a parent
        double highestFit=population[population.length - 1].fitness;
        double lowestFit=population[0].fitness;
        // choose a random member of the population
        Network chosen = population[(int) (Math.random() * population.length)];
        // Choose the random fitness level required to be chosen
        double r = (Math.random()*(highestFit/2))+lowestFit;
        // Count how many times to try for a parent, to avoid an infinite loop
        int counter=0;
        while (chosen.fitness > r && counter<population.length*2.5) {
            // Choose a random member of the population until they fit the description
            chosen = population[(int)(Math.random() * population.length)];
            counter++;
        }
        return chosen;
    }
    // The first way I found online to update the main frame was this class
    public static class SimpleScheduledExecutorExample {
        public void main(JFrame f) {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            Runnable task = new Runnable() {
                public void run() {
                    // Put the main function in a try catch loop because there's a LOT of stuff happening
                    try {
                        // For every network in the population, set it's inputs
                        for (Network network : population) {
                            network.setInputs(inputs);
                        }
                        // Find the best network in the population and repaint the frame
                        // This function also gets all of the values for each network and the fitness of each one,
                        // in other words, this function evaluates all of the networks in the population and finds the best one
                        findBest();
                        f.repaint();
                        // For every spot in the population for the next generation, choose the parents
                        // for that network and perform crossover to create a new network in that spot
                        for (int i = 0; i < population.length; i++) {
                            Network parentA = chooseParent();
                            Network parentB = chooseParent();
                            nextGen[i] = Network.Crossover(parentA, parentB);
                        }
                        // Set the population's array equal to the next generation's array and reset nextGen
                        for (int i = 0; i < nextGen.length; i++) {
                            population[i] = nextGen[i];
                            nextGen[i] = null;
                        }
                        // Find the best fitness overall
                        bestFitness=Math.min(population[0].fitness,best.fitness);
                        // Increment the fitChange counter if the fitness didn't change
                        if(lastGenBestFit== best.fitness)
                        {Config.fitChange++;Config.trueFitChange++;}
                        // If the fitness changed, disable panic mode and
                        else{
                            Config.panicModeEnabled=false;
                            Config.trueFitChange=0;
                            Config.fitChange=0;
                        }
                        // Go into panic mode if we haven't changed the best fitness
                        // in Config.panicModeThreshold generations
                        if(Config.fitChange>Config.panicModeThreshold){
                            Config.panicModeEnabled=true;
                        }
                        // Only go into panic mode for 50 generations to spread out the actual gene pool, then optimize
                        if(Config.fitChange>Config.panicModeThreshold+50){
                            Config.trueFitChange=Config.fitChange;
                            Config.fitChange=0;
                            Config.panicModeEnabled=false;
                        }
                        // Carry over the best network from the last generation to the next generation
                        population[0]=best;
                        // Set the lastGenBestFit variable to the current generation's best fitness
                        lastGenBestFit=best.fitness;
                        // increment the generation counter
                        generation++;
                        // if the best fitness hasn't changed for more
                        // than Config.executorStopThreshold, stop the program
                        if(Config.trueFitChange>Config.executorStopThreshold){
                            scheduler.shutdown();
                        }
                        // Garbage collection
                        System.gc();
                    // If there's an exception (e), output it
                    }catch(Throwable e){
                        System.out.println(e);
                    }
                    }
                };
            // Wait Config.delay milliseconds between executions of the above code
            scheduler.scheduleAtFixedRate(task,0,Config.delay,TimeUnit.MILLISECONDS);
            }
        }

    // Make sure the frame is the size we want it by overriding the getPreferredSize method
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width,height);
    }

    // The main function
    public static void main(String[] args){
        // Run all of the setup commands
        setup();
        // Create and title the frame window, add a new frameStuff() object to it, set the size and visibility
        JFrame frame= new JFrame("Neuroevolution- Fixed Topologies");
        frameStuff f=new frameStuff();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(f);
        frame.setSize(width,height);
        frame.setVisible(true);
        // Create a new SimpleScheduledExecutorExample object (t) to run the program repeatedly
        SimpleScheduledExecutorExample t= new SimpleScheduledExecutorExample();
        t.main(frame);
    }
}

