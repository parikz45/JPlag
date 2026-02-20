import java.util.List;
import java.util.Random;

//REORDERING OBFUSCATION
/**
 * A class representing shared characteristics of animals.
 */
public abstract class Animal_reordered extends Organism {
    // Infection probability
    protected double infectionProbability;

    // Random generator
    Random rand;

    // State flags
    private boolean infected = false;
    private boolean hasBred = false;
    private boolean male;

    // Behaviour parameters
    private int breedingAge;
    private double breedingProbability;
    private int litterSize;

    // Resource state
    private int foodLevel;

    /**
     * Constructor
     */
    public Animal_reordered(boolean randomAge, Field field, Location location,
            int maxAge, int maxFoodValue, int breedingAge,
            double breedingProbability, int litterSize,
            double infectionProbability, Random rand) {
        super(randomAge, field, location, maxAge, rand);

        this.rand = rand;
        this.breedingAge = breedingAge;
        this.breedingProbability = breedingProbability;
        this.litterSize = litterSize;
        this.infectionProbability = infectionProbability;

        male = rand.nextBoolean();

        if (randomAge) {
            foodLevel = rand.nextInt(maxFoodValue);
        } else {
            foodLevel = maxFoodValue;
        }
    }

    /**
     * Main simulation behaviour
     */
    public void act(List<Organism> newAnimals, Weather weather) {
        affectInfection(weather);

        incrementAge();
        incrementHunger();

        hasBred = false;

        if (checkInfected(infectionProbability)) {
            infect();
        }

        if (!isAlive()) {
            return;
        }

        if (!male && canBreed(weather)) {
            giveBirth(newAnimals);
        }

        if (canMove(weather)) {
            Location newLocation = findFood();

            if (newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            if (newLocation != null) {
                setLocation(newLocation);
            } else {
                setDead();
            }
        }
    }

    /**
     * Hunger logic
     */
    protected void incrementHunger() {
        foodLevel--;

        if (foodLevel <= 0) {
            setDead();
        }
    }

    protected void setFoodValue(int value) {
        foodLevel = value;
    }

    /**
     * Breeding logic
     */
    protected int breed(Class c) {
        Field field = getField();
        int births = 0;

        List<Location> animalLocations = field.adjacentLocations(getLocation());

        for (int i = 0; i < animalLocations.size(); i++) {
            // get the organism at the current field
            Organism organism = (Organism) field.getObjectAt(animalLocations.get(i));
            // if the organism is an animal
            if (organism instanceof Animal_original) {
                Animal_original animal = (Animal_original) organism;
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
        }

        return births;
    }

    /**
     * Infection logic
     */
    protected boolean checkInfected(double probability) {
        Random rand = new Random();

        if (rand.nextDouble() <= probability && !isInfected()) {
            infected = true;
            return infected;
        }

        return false;
    }

    protected void infect() {
        int scale = rand.nextInt(4) + 1;
        int currentAge = scale * getAge();
        setAge(currentAge);
        foodLevel = (int) foodLevel / scale;
    }

    protected void scaleInfectionProbability(int scale) {
        infectionProbability = scale * infectionProbability;
    }

    protected void setInfectionProbability(double probability) {
        infectionProbability = probability;
    }

    protected boolean isInfected() {
        return infected;
    }

    /**
     * Status methods
     */
    protected boolean hasBred() {
        return hasBred;
    }

    protected void setBred(boolean bred) {
        hasBred = bred;
    }

    protected boolean isMale() {
        return male;
    }

    /**
     * Abstract behaviours
     */
    abstract protected Location findFood();

    abstract protected void giveBirth(List<Organism> newOrganisms);

    abstract public boolean isDayActive();

    abstract protected boolean canMove(Weather weather);

    abstract protected boolean canBreed(Weather weather);

    abstract protected void affectInfection(Weather weather);
}
