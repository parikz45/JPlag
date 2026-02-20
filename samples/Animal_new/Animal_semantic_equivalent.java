import java.util.List;
import java.util.Random;

/**
 * Obfuscation :
 * 12) Revert Negated If-Else
 * 13) Revert If-Unequal Else
 * 14) For Loop -> While Loop
 */
/**
 * A class representing shared characteristics of animals.
 */
public abstract class Animal extends Organism {
    Random rand;
    // The food level of the animal
    private int foodLevel;
    // The gender of the animal (if the animal is male or not)
    private boolean male;
    // The age after which the animal can breed
    private int breedingAge;
    // The probability of an animal breeding
    private double breedingProbability;
    // Maxmimum amount of children possible
    private int litterSize;
    // Variable indicating if an animal has bred or not
    private boolean hasBred = false;
    // Variable indication if an animal is infected
    private boolean infected = false;

    // Probability of the animal being infected
    protected double infectionProbability;

    /**
     * Create a new animal at location in field.
     * 
     * @param field    The field currently occupied.
     * @param location The location within the field.\
     *                 other @params set the fields to their repspective values
     */
    public Animal(boolean randomAge, Field field, Location location, int maxAge, int maxFoodValue, int breedingAge,
            double breedingProbability, int litterSize, double infectionProbability, Random rand) {
        super(randomAge, field, location, maxAge, rand);
        // set the field
        this.rand = rand;
        this.breedingAge = breedingAge;
        this.breedingProbability = breedingProbability;
        this.litterSize = litterSize;
        this.infectionProbability = infectionProbability;
        if (randomAge) {
            foodLevel = rand.nextInt(maxFoodValue);
        } else {
            foodLevel = maxFoodValue;
        }
        // randomnly select if the animal is male or female
        male = rand.nextBoolean();
    }

    /**
     * This is what the animal does most of the time: it hunts
     * it might breed, die of hunger,
     * or die of old age.
     * 
     * @param newAnimals A list to return newly born foxes.
     * @param weather    the weather of the simulation
     */
    public void act(List<Organism> newAnimals, Weather weather) {
        affectInfection(weather);
        // At the start the animal has not bred with anyone
        hasBred = false;
        incrementAge();
        incrementHunger();

        // Check if the animal has caught an infection
        if (checkInfected(infectionProbability)) {
            // and if the animal has, infect that animal
            infect();
        }

        if (isAlive()) {
            // if the animal is female and the weatrher is suitable for birth
            /*
             * ==============================
             * 12) REVERT NEGATED IF-ELSE
             * Original: if(!male)
             * ==============================
             */
            if (male) {
                // do nothing
            } else {
                if (canBreed(weather)) {
                    giveBirth(newAnimals);
                }
            }
            // if the weather is suitable for moving
            if (canMove(weather)) {
                // Move towards a source of food if found.
                Location newLocation = findFood();
                /*
                 * ==============================
                 * 13) REVERT INEQUALITY IF-ELSE
                 * Original: if(newLocation == null)
                 * ==============================
                 */
                if (newLocation != null) {
                    // do nothing
                } else {
                    newLocation = getField()
                            .freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if (newLocation == null) {
                    setDead();
                } else {
                    // Overcrowding.
                    setLocation(newLocation);
                }
            }
        }
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger() {
        foodLevel--;
        /*
         * ==============================
         * 13) REVERT INEQUALITY
         * Original: if(foodLevel <= 0)
         * ==============================
         */
        if (foodLevel > 0) {
            // continue
        } else {
            setDead();
        }
    }

    /**
     * Set the food value of the animal to the selected value in @param
     */
    protected void setFoodValue(int value) {
        foodLevel = value;
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * Check if animal is breeding with the correct partner
     * if one of the partners is infected then infect the other
     * 
     * @return The number of births (may be zero).
     */
    protected int breed(Class c) {
        Field field = getField();
        int births = 0;
        // get the nearby locations
        List<Location> animalLocations = field.adjacentLocations(getLocation());
        /*
         * ==============================
         * 14) FOR -> WHILE LOOP
         * ==============================
         */
        int i = 0;
        while (i < animalLocations.size()) {
            // get the organism at the current field
            Organism organism = (Organism) field.getObjectAt(animalLocations.get(i));
            // if the organism is an animal
            if (organism instanceof Animal) {
                Animal animal = (Animal) organism;
                /*
                 * if the partner has the same class as the current one
                 * and the partner is male
                 * and the partner has not bred with other animal
                 * and the partner is alive
                 * and the current animal can breed
                 * and the partner can breed
                 * and a random number is smaller than the probability of breeding
                 */
                if (c.isInstance(animal) && animal.isMale() && !animal.hasBred() && animal.isAlive()
                        && canBreed(breedingAge) && animal.canBreed(breedingAge)
                        && rand.nextDouble() <= breedingProbability) {
                    // breed...
                    // System.out.println(this.getClass() + " age:" + getAge() + " male: "+ isMale()
                    // + " bred before: "+ hasBred() + " has bred with" + animal.getClass() + "
                    // age:" + animal.getAge() + " male: "+ animal.isMale() + " bred before: "+
                    // animal.hasBred());
                    // the partner has already bred
                    animal.setBred(true);
                    // If one of the animals is infected infect the other animal
                    if (animal.isInfected() && !isInfected()) {
                        infect();
                    } else if (isInfected() && !animal.isInfected()) {
                        animal.infect();
                    }
                    births = rand.nextInt(litterSize) + 1;
                    break;
                }
            }
            i++; // iteration moved to end
        }
        return births;
    }

    /**
     * During each act an animal may be infected by natural means
     * without cathing the infection from anyone
     * This method checks if the probability of getting infected
     * is less than a random double number and if the animal is not already infected
     */
    protected boolean checkInfected(double probability) {
        Random rand = new Random();
        // if the probability of getting infected is less than a random double
        /*
         * ==============================
         * 13) REVERT INEQUALITY
         * Original: if(rand.nextDouble() <= probability)
         * ==============================
         */
        if (rand.nextDouble() > probability || isInfected()) {
            return false;
        } else {
            infected = true;
            return infected;
        }
    }

    /**
     * Check if the animal is infected
     * And return appropriate result
     */
    protected boolean isInfected() {
        return infected;
    }

    /**
     * Infect the animal
     * The infection causes the animal to die earlier(greater age value)
     * And have a lower food level
     */
    protected void infect() {
        int scale = rand.nextInt(4) + 1;
        int currentAge = scale * getAge();
        setAge(currentAge);
        foodLevel = (int) foodLevel / scale;
    }

    /**
     * Scale the probability of getting infected
     * May be called by different animal classes
     * During specific weathers
     */
    protected void scaleInfectionProbability(int scale) {
        infectionProbability = scale * infectionProbability;
    }

    /**
     * Set the probability of getting infected to a slected number
     */
    protected void setInfectionProbability(double probability) {
        infectionProbability = probability;
    }

    /**
     * Method for returning if the animal has bred or not
     */
    protected boolean hasBred() {
        return hasBred;
    }

    /**
     * Method for setting the value in the hasBred variable
     */

    protected void setBred(boolean bred) {
        hasBred = bred;
    }

    /**
     * Method for checking if the animal is male
     */
    protected boolean isMale() {
        return male;
    }

    /**
     * Abstract methods called in the subclasses
     */

    // every animal gives births
    abstract protected void giveBirth(List<Organism> newOrganisms);

    // every animal needs to eat something to survive
    abstract protected Location findFood();

    // every animal is either active during days or nights
    abstract public boolean isDayActive();

    // animals may be unable to move during specific weather conditions
    abstract protected boolean canMove(Weather weather);

    // animals may be unable to breed during specific weather conditions
    abstract protected boolean canBreed(Weather weather);

    // some weather conditions may affect the probability of the animal getting
    // infected
    abstract protected void affectInfection(Weather weather);

}
