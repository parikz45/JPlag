
//EXTRACT ELEMENTS OBFUSCATION
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 */
public abstract class Animal_extract_elements extends Organism {
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
    /*
     * ==============================
     * SINGLE-USE CONSTANT
     * (will be inlined later)
     * ==============================
     */
    private static final int SINGLE_OFFSET = 1;

    /**
     * Create a new animal at location in field.
     * 
     * @param field    The field currently occupied.
     * @param location The location within the field.\
     *                 other @params set the fields to their repspective values
     */
    public Animal_extract_elements(boolean randomAge, Field field, Location location, int maxAge, int maxFoodValue, int breedingAge,
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
        // incrementHunger();
        /*
         * ==============================
         * INLINE SINGLE-USE VARIABLE
         * Original:
         * int hungerDrop = 1;
         * foodLevel -= hungerDrop;
         * ==============================
         */
        if (--foodLevel <= 0) { // variable inlined
            setDead();
        }
        // Check if the animal has caught an infection
        /*
         * ==============================
         * INLINE OPTIONAL VALUE
         * Original:
         * Optional<Double> p = Optional.of(infectionProbability);
         * if(rand.nextDouble() <= p.orElse(0.0))
         * ==============================
         */
        if (rand.nextDouble() <= Optional.of(infectionProbability)
                .orElse(0.0) && !isInfected()) {
            infect();
        }
        if (isAlive()) {
            // if the animal is female and the weatrher is suitable for birth
            if (!male && canBreed(weather)) {
                // call the giveBirth method
                giveBirth(newAnimals);
            }
            // if the weather is suitable for moving
            if (canMove(weather)) {
                // Move towards a source of food if found.
                Location newLocation = findFood();
                /*
                 * ==============================
                 * INLINE OPTIONAL
                 * ==============================
                 */
                if (newLocation == null) {
                    newLocation = Optional
                            .ofNullable(newLocation)
                            .orElse(getField()
                                    .freeAdjacentLocation(getLocation()));
                }
                // See if it was possible to move.
                if (newLocation != null) {
                    setLocation(newLocation);
                } else {
                    // Overcrowding.
                    setDead();
                }
            }
        }
    }

    /**
     * Make this animal more hungry. This could result in the animal's death.
     */
    protected void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
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
        for (int i = 0; i < animalLocations.size(); i++) {
            // get the organism at the current field
            Organism organism = (Organism) field.getObjectAt(animalLocations.get(i));
            // if the organism is an animal
            if (organism instanceof Animal_extract_elements) {
                Animal_extract_elements animal = (Animal_extract_elements) organism;
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
                    // INLINE CONSTANT
                    births = rand.nextInt(litterSize) + SINGLE_OFFSET;
                    break;
                }
            }
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
        if (rand.nextDouble() <= probability && !isInfected()) {
            // infect the animal
            infected = true;
            return infected;
        }
        // otherwise do not infect the animal
        return false;
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
