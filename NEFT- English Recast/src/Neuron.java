
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

// The nodes in my network, simple nodes that can hold a color value, and connect to other nodes via weighted channels
public class Neuron {
    // This neuron's color value
    private Color value;
    // When a neuron is first made, it has no value so the valueSet is false
    // This boolean is used in the recursive getValue function to make sure all of the neurons in a network have their
    // different values
    public boolean valueSet=false;
    // The input mapping with weights
    public HashMap<Neuron,Double> inputs=new HashMap<>(); //Map format: {NeuronA, weightA}
    // The "DNA" of this neuron is just a list of its weights, I tried to make a neuro-evolution program as I didn't
    // understand how to find the gradient of a function yet
    public double[] DNA;


    // I figured out about getters and setters before this project and halfway used them
    // Sets the neuron's value (color)
    public void setValue(Color newVal){
        valueSet=true;
        value=newVal;
    }

    // I made this while in calc 2 so I used a genetic algorithm to train as I did not understand
    // multi-variable calculus and partial derivatives and whatnot
    public double[] getDNA(){
        DNA=new double[inputs.size()];
        int i=0;
        for (Map.Entry<Neuron,Double> entry:inputs.entrySet()) {
            DNA[i]=entry.getValue();
            i++;
        }
        return DNA;
    }

    // The recursive get value algorithm (my first recursive algorithm)
    public Color getValue(){
        if(!valueSet && inputs.size()==0){
            valueSet=true;
            value=new Color(0,0,0);
            return value;
        }
        //For every input
            for (Map.Entry<Neuron, Double> entry : inputs.entrySet()) {
                //If its value hasn't been set yet
                if (!entry.getKey().valueSet) {
                    entry.getKey().getValue();
                }
            }

        if(!valueSet){
            value=process();
            valueSet=true;
            return value;

        }else{
            return value;
        }

    }

    public void addInput(Neuron n,double weight){
        inputs.put(n,weight);
    }

    // Different activation functions in case I wanted to use them
    // They ended up not being used because this example was with color and using them made the colors less nice
    public double sig(double num){
        return (1/(1+Math.exp(-num)));
    }

    public double relu(double num){
        if(num<0){
            return 0;
        }else{
            return num;
        }
    }

    // A special get input for the Color object, this makes is more visually pleasing in my opionion
    public Color process(){
        double newR=0;double newG=0;double newB=0;
        for (Map.Entry<Neuron, Double> entry :inputs.entrySet()) {
        //  newVal+=input color from entry*neuron connection weight
            newR+=(entry.getKey().getValue().getRed()*entry.getValue());
            newG+=(entry.getKey().getValue().getGreen()*entry.getValue());
            newB+=(entry.getKey().getValue().getBlue()*entry.getValue());
        }

        newR=Math.abs(newR/inputs.size());
        newG=Math.abs(newG/inputs.size());
        newB=Math.abs(newB/inputs.size());
        newR=frameStuff.clampRange((int)newR,0,255);
        newG=frameStuff.clampRange((int)newG,0,255);
        newB=frameStuff.clampRange((int)newB,0,255);

        return new Color((int)newR,(int)newG,(int)newB);
    }


}
