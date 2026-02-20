import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * A class representing shared characteristics of animals.
 * 
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends Organism
{

    //whether the animal is a Female or male, True if female
    private boolean isFemale;
    //whether the animal is awake or asleep, True if awake
    private boolean isAwake; 
    //whether the animal is infected with a disease, True if infected
    private boolean isInfected;
    // The Animal's food level, which is increased by eating Prey.
    public int foodLevel;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param inected Whether the animal is infected or not
     */
    public Animal(Field field, Location location, boolean infected)
    {
        super(field,location);//pass field and location to Organism constructor
        if(infected == false){
            //if the infection is not passed down from parents then determine
            //whether it should be given an infection or not
            setInfection();
        }else{
            //if the one of its parents were infected then set this Animal to be infected
            isInfected = infected;
        }
        setGender();//generate a gender for the animal
        isAwake = true;//set the animal to be born awake
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     * @pram time To determine whther it should sleep or not
     * @param weather To determine how it's breeding or movement capibilities are affected
    */
    abstract public void act(List<Organism> newAnimals, String time, String weather);
    /**
     * Make this Animal more hungry. This could result in the Animal's death.
     */
    public void incrementHunger()
    {
        foodLevel--;//decrement this animal's hunger
        if(foodLevel <= 0) {
            //if its foodlevel has reached zero or less set it to dead
            setDead();
        }
    }
    /**
     * Generate the Animals Gender
     * 
     */
    private void setGender()
    {
        Random rand = Randomizer.getRandom();
        //generate a random number to determine gender
        double genderProb = rand.nextDouble();
        if(genderProb <=0.5){
            //set this animal to be male
            this.isFemale = false;
        }else{
            //set this animal to be female
            this.isFemale = true;
        }
    }
    /**
     * Return the animals gender
     * @return boolean The animal's gender, true if they are female.
     */
    public boolean getGender()
    {
        return this.isFemale;
    }
    
    /**
     * Set whether the animal is sleeping or not based on the time of day
     * @param timer used to retreive the current time of the simulator
     */
    public void setAwake(String time)
    {
        if(time.equals("Day")){
            //if ti is daytime then set the animal to be awake
            isAwake = true;
        }
        else{
            //if it is not day set the animal to be false
            isAwake = false;
        }
    }
    
    /**
     * Get whether the animal is awake or not
     * @return boolean True if the animal is awake
     */
    public boolean getAwake()
    {
        return isAwake; 
    }
    /**
     * Determine whether this animal should be infected or not at birth if 
     * their parents did not pass down the infection
     */
    private void setInfection()
    {
        Random rand = Randomizer.getRandom();
        //generate a random number to determine if the animal should be infected
        double infectionProb = rand.nextDouble();
        if(infectionProb <= 0.05){
            //make the animal infected
            makeInfected();
        }else{
            //make the animal not infected
            isInfected = false;
        }
    }
    /**
     * Get whether the animal is infected or not
     * @return boolean True if the animal is infected
     */
    public boolean getInfection()
    {
        return isInfected;
    }
    /**
     * Set the animal to be infected without generating a number
     */
    public void makeInfected(){
        isInfected = true;//set the animal to be infected
        age++;//increment the age so this animal dies quicker when infected
    }
    /**
     * Look for Animals adjacent to the current location.
     * to spread the infection to
     */
    public void spreadInfection()
    {
        if(getInfection()){
            //This animla has to be ifnected ot spread the infection
            if(getLocation() != null){
                //This animal has to be occupying a valid lcoaiton in the field
                Field field = getField();//get the field of this organism
                List<Location> adjacent = field.adjacentLocations(getLocation());//get adjacent lcoations
                Iterator<Location> it = adjacent.iterator();
                while(it.hasNext()) {
                        Location where = it.next();//get the next adjacent location
                        Object organism = field.getObjectAt(where);//get the object at that location
                        Random rand = Randomizer.getRandom();//get a Randomizer object
                        double infectionProb = rand.nextDouble();//generate a randomnumber if it should infect the nearby animal or not
                        if( infectionProb <= 0.05){
                            if(organism instanceof Animal){
                                //the organism has to be an inctance of animal as only animals can be infected
                                Animal nearbyAnimal = (Animal) organism;//cast the object to an animal
                                nearbyAnimal.makeInfected();//make the animal infected
                            }
                        }
                    }
                }
        }
    }
}
