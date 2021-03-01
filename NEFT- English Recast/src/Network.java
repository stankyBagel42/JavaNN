import java.awt.*;
import java.util.Map;

// A class to hold the neurons and layers, also it supports the genetic algorithm type of learning used in this example
public class Network {

    // Array of neurons which will be the inputs of the network
    public Neuron[] inputs;
    // Array of hidden neurons (the network only has 1 hidden layer for visual simplicity)
    public Neuron[] hidden;
    // Array of neurons which stores the outputs of the network
    public Neuron[] outputs;
    // The fitness of each network is used to evaluate the performance of that network, and the higher the fitness,
    // the more likely the network is to become part of the next generation during crossover
    public double fitness;
    // List of weights, determined by going thru weights of each neuron's input weight(s).
    public double[] DNA;

    // The constructor function without DNA is used to create the first generation of
    // networks as they won't have DNA to be created from
    public Network(int numInputs, int numOutputs, int numHidden){
        inputs=new Neuron[numInputs];
        outputs=new Neuron[numOutputs];
        hidden=new Neuron[numHidden];
        // Function which fills the network with neurons and connects them all to each other with random weights
        fillAndConnect();
    }

    // The constructor function used for the second generation onward to create the networks, this one uses DNA as an
    // argument to make the networks
    public Network(int numInputs, int numOutputs, int numHidden, double[] newDNA){
        inputs=new Neuron[numInputs];
        outputs=new Neuron[numOutputs];
        hidden=new Neuron[numHidden];
        // This is the same function as above except it uses the weights described in the DNA instead of random weights.
        fillAndConnect(newDNA);
    }

    // The function used to evaluate the fitness of a network
    public void evaluateFitness(){
        // A temporary color array that stores the outputs of the network, used to see how close
        // the outputs are to the target outputs
        Color[] temp=this.getOutputs();
        // Variable used to sum up the absolute errors
        double sum=0;
        int i=0;
        // I learned how to use for each n in array and wanted to use it here so I used a separate count variable i.
        for (Color c:temp) {
            // Sum up the absolute errors of the network's outputs vs the target output
            sum+= Math.abs(c.getRed()-frameStuff.targetColors[i].getRed());
            sum+= Math.abs(c.getGreen()-frameStuff.targetColors[i].getGreen());
            sum+= Math.abs(c.getBlue()-frameStuff.targetColors[i].getBlue());
            i++;
        }
        // Assign the network's fitness to the sum of the absolute errors
        fitness=sum;
    }

    // Get the DNA of the network
    public void getDNA(){
        // 'temp' counts the amount of weights in the network
        int temp=0;
        for(Neuron n:hidden) {
            for (Map.Entry<Neuron,Double> entry:n.inputs.entrySet()) {
                temp++;
            }
        }
        for(Neuron n:outputs) {
            for (Map.Entry<Neuron,Double> entry:n.inputs.entrySet()) {
                temp++;
            }
        }
        // Set the network DNA to an empty double array the length of what was counted above
        DNA=new double[temp];
        // An index counter variable used to place the correct weights in the correct place in the DNA vector
        int index=0;
        // The two for loops loop through the hidden layer and output layer to get all
        // of the network weights and place them in the DNA array
        for (int i = 0; i < hidden.length; i++) {
            for (Map.Entry<Neuron,Double>entry:hidden[i].inputs.entrySet()) {
                DNA[index]=entry.getValue();
                index+=1;
            }
        }
        for (int i = 0; i < outputs.length; i++) {
            for (Map.Entry<Neuron,Double>entry:outputs[i].inputs.entrySet()) {
                DNA[index]=entry.getValue();
                index+=1;
            }
        }
    }

    // Gets the outputs for a network
    private Color[] getOutputs(){
        // Create a new color[] to store the outputs, used to return the value
        Color[] out=new Color[outputs.length];
        // Loops through each output neuron and uses the recursive getValue function to
        // get the value of each neuron in the network
        for (int i = 0; i < outputs.length; i++) {
            out[i]=outputs[i].getValue();
        }
        return out;
    }

    // Function used to set the inputs of a network
    public void setInputs(Color[] input){
        for (int i=0;i<inputs.length;i++) {
            inputs[i].setValue(input[i]);
        }
    }

    // This function fills out a new network's layers with neurons and connects them all together
    private void fillAndConnect(){
        // These for loops just loop thru the input array, hidden array, and output array
        // and fills each of them with neurons
        for (int i = 0; i < inputs.length; i++) {
            inputs[i]=new Neuron();
        }
        for (int i = 0; i < hidden.length; i++) {
            hidden[i]=new Neuron();
        }
        for (int i = 0; i < outputs.length; i++) {
            outputs[i]=new Neuron();
        }
        // Loop through each layer and set the weights for each neuron to a random value on (-weightMax,weightMax)
        for (Neuron hidden:hidden) {
            for (Neuron input:inputs) {
                hidden.addInput(input,(Math.random()*(2*Config.weightMax))-Config.weightMax);
            }
        }
        for (Neuron output:outputs) {
            for (Neuron hidden:hidden) {
                output.addInput(hidden,(Math.random()*(2*Config.weightMax))-Config.weightMax);
            }
        }

    }

    // The same thing as the other fillAndConnect function but this one uses a DNA array for
    // the weights instead of randomly assigning weights
    private void fillAndConnect(double[] genes){
        for (int i = 0; i < inputs.length; i++) {
            inputs[i]=new Neuron();
        }
        for (int i = 0; i < hidden.length; i++) {
            hidden[i]=new Neuron();
        }
        for (int i = 0; i < outputs.length; i++) {
            outputs[i]=new Neuron();
        }
        // Loops through each layer's neurons and sets the weights according to the DNA provided to the function
        int index=0;
        for (Neuron hidden:hidden) {
            for (Neuron input:inputs) {
                hidden.addInput(input,genes[index]);
                index++;
            }
        }
        for (Neuron output:outputs) {
            for (Neuron hidden:hidden) {
                output.addInput(hidden,genes[index]);
                index++;
            }
        }

    }

    // This is the function that governs the 'reproduction' of two networks and creates
    // the child according to their DNA values and mutations
    public static Network Crossover(Network a,Network b){
        // Make sure the DNA of each parent is set in their DNA variable
        a.getDNA();b.getDNA();
        // The "child"'s DNA array
        double[]newDNA=new double[a.DNA.length];
        // Pull from the 2nd and 3rd quarters when choosing the point in the DNA that
        // specifies the place that switches taking from parent a to parent b
        int choice=(int)(Math.random()*(a.DNA.length/2))+a.DNA.length/4;
        // Variable to store the random choice to determine if the mutations happen or not
        double r;
        // Loop through the DNA of the parents
        for (int i = 0; i < a.DNA.length; i++) {
            // If the current index is before the chosen point, take from parent A
            if(i<choice){
                newDNA[i]=a.DNA[i];
                // Else, take from parent B
            }else{
                newDNA[i]=b.DNA[i];
            }
            // Choose a random value between 0 and 1
            r=Math.random();
            // If the random value is less than the mutation chance and there's no panic mode on
            if(r<frameStuff.mChance && !Config.panicModeEnabled){
                // If it does mutate, you can either adjust the weight slightly OR you can make a whole new weight
                // So we set the r value to a random number again and repeat the process to decide from the above options
                r=Math.random();
                if(r<frameStuff.wChangeChance){
                    // Change weight by a number in range (-0.5,0.5)
                    newDNA[i]+=((Math.random()*2)-1)/2;
                }else{
                    // New random weight between -weightMax/2 and weightMax/2
                    newDNA[i]=(Math.random()*Config.weightMax)-Config.weightMax;
                }
                // If the mutation happens and theres panic mode enabled, the chances are different so there's a separate set of
            }else if(r<Config.panicMutationChance &&Config.panicModeEnabled){
                // Set the random value again to choose between weight adjusting vs weight replacing
                r=Math.random();
                // Choose between the two options with an if statement
                if(r<Config.panicWeightAdjustChance){
                    newDNA[i]+=(((Math.random())*2)-1)/2;
                }else{
                    newDNA[i]=(Math.random()*(2*Config.weightMax))-Config.weightMax;
                }
            }
        }
        // Return a new network with the DNA created above
        return new Network(3,frameStuff.targetColors.length,Config.hiddenNeurons,newDNA);
    }

}
