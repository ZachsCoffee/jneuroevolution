/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package evolution_builder.population;

import maths.LinearValues;
import evolution_builder.Evolution;
import java.util.ArrayList;
import maths.MinMax;
import maths.SquareFunction;
import networks.multilayer_perceptron.BackpropagationMLP;


/**
 *
 * @author Zachs
 */
public class PersonMigration {
    private final ArrayList<Evolution> EVOLUTIONS = new ArrayList<>();
    private final int SAMPLE_RATE;//, epochs, migrationsCount = 0;
    
    private LinearValues dynamicLinearValues;
    private SquareFunction squareFunction;
    private Person[] migrationPersons;
    private int mlpEpoch = 0;
    
    /**
     * Needs percent of epochs in order to compute the sample rate of migration
     * @param percent A number between zero and one
     * @param epochs The epochs of evolution
     * @param populationsCount The number of total subpopulations
     */
    public PersonMigration(double percent, int epochs, int populationsCount){
        if (percent <= 0 || percent >= 1) throw new IllegalArgumentException("Percent must be a float number between the (0,1) without the zero and one. percent="+percent);

        migrationPersons = new Person[populationsCount];
        SAMPLE_RATE = (int)(epochs * percent);
        dynamicLinearValues = new LinearValues(new MinMax(0.00001, 0.3), SAMPLE_RATE, LinearValues.Order.DESC);
//        squareFunction = new SquareFunction(0.1, epochs);
    }
    
    public synchronized void addPopulation(Evolution evolution){
        EVOLUTIONS.add(evolution);
    }
    
    public void migrate(Population population, int currentEpoch) throws CloneNotSupportedException{
        if (currentEpoch % SAMPLE_RATE != 0) return;
        
        synchronized(population){
            population.sortPopulation();
        
            Person tempPerson;
            int size = EVOLUTIONS.size();
            for (int i=0; i<size; i++){
                if (EVOLUTIONS.get(i) == null) continue;

                tempPerson = EVOLUTIONS.get(i).getTotalBestPerson();
                
                if (tempPerson != null) {
                    tempPerson = (Person)tempPerson.clone();
                }
//                propagate((BackpropagationMLP) tempPerson.getGeneCode(), currentEpoch);

                population.setPersonAt(tempPerson, i);
            }
        }
    }
    
    /**
     * Sample rate is used in order to determine, how often the migration will execute, and how many epochs the propagate will run 
     * @return The sampleRate
     */
    public int getSampleRate(){
        return SAMPLE_RATE;
    }
    
    private void propagate(BackpropagationMLP mlp, int currentEpoch){
//        mlp.propagate();
//        double lastError = mlp.getError(), tempError;
//        double errorBefore = mlp.getError();
        int goodCount = 0;
        
        for (int i=0; i<SAMPLE_RATE; i++){
//            mlp.propagate();
            mlp.propagate(dynamicLinearValues.computeD(i));
//            mlp.propagate(squareFunction.compute(currentEpoch));
//            
//            tempError = mlp.getError();
//            if (tempError > lastError){
////                System.err.println(i);
//                break;
//            }
//            
//            lastError = tempError;
//
//            double newError = mlp.getError(), difError = errorBefore - newError;
//            if (difError > 0){
////                System.out.println("GOOD :"+difError+" epochs :"+SAMPLE_RATE);
//                goodCount++;
//            }
//            else{
////                System.out.println("BAD :"+difError+" epochs :"+SAMPLE_RATE);
//            }
//            
//            errorBefore = newError;
        }
//        System.out.println("Stats: "+goodCount+"/"+SAMPLE_RATE);
        
//        synchronized(TimeSeriesProblems.costCounter){
//            TimeSeriesProblems.costCounter += SAMPLE_RATE;
//        }
    }
}
