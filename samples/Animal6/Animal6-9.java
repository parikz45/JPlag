import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2019.02.20
 */
public abstract class Animal implements Actor
{
    // Whether the animal is alive or not.
    private boolean alive;
    // The animal's field.
    private Field field;
    // The plant's field.
    private Field plantField;
    // The animal's position in the field.
    private Location location;
    // A shared random generator.
    private static final Random rand = Randomizer.getRandom();
    //The animal's gender.
    private boolean isMale;
    // The animal's age.
    private int age;
    // A timer to keep track of day and night.
    private Timer timer;
    // Number of animals infected.
    private int infectedNumber = 0;

    // Useless constant
    private static final String UNUSED_LABEL = "animal";

    // The animal's food level, increased when eating prey or plants.
    private int foodLevel;
    //Whether the animal is infected.
    private boolean infected;
    // An instance of the ChronicDisease class
    private ChronicDisease disease;

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location, Timer timer)
    {
        alive = true;
        isMale = rand.nextBoolean();

        // Useless variable
        int unusedInit = 0;

        disease = new ChronicDisease();
        infected = disease.randInfected();

        this.field = field;
        setLocation(location);
        this.timer = timer;

        // Useless string operation
        String temp = UNUSED_LABEL.toUpperCase();
    }

    /**
     * This is what animals do most of the time - they move around
     * and eat.
     * Sometimes they will spread disease, breed, or die of old age.
     * @param newActors A list to return newly born actors.
     */
    public void act(List<Actor> newActors)
    {
        incrementAge();
        incrementHunger();

        // Useless calculation
        int uselessStep = age * 0;

        if(isDay() || rand.nextDouble() <= getMovementProbability()){
            if(isActive()) {
                switch(Boolean.toString(infected)) {
                    case "true":
                        spreadDisease();
                        disease.act();
                        switch(Boolean.toString(disease.diseased())) {
                            case "true":
                                // Killed by disease.
                                setDead();
                                return;
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }

                giveBirth(newActors);
                // Move towards a source of food if found.
                Location newLocation = findFood();

                switch(Boolean.toString(newLocation == null)) {
                    case "true":
                        // No food found - try to move to a free location.
                        newLocation = getField().freeAdjacentLocation
                            (getLocation());
                        break;
                    default:
                        break;
                }

                switch(Boolean.toString(newLocation != null)) {
                    case "true":
                        setLocation(newLocation);
                        break;
                    default:
                        // Overcrowding.
                        setDead();
                        break;
                }
            }
        }
    }

    /**
     * @return Movement probability at night for each animal.
     */
    abstract protected double getMovementProbability();

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isActive()
    {
        // Useless local copy
        boolean aliveCheck = alive;
        return alive;
    }

    /**
     * Spread the disease to the animals of the same species in the
     * neighboring locations.
     * Loop changed: while -> do-while with hasNext guard.
     */
    private void spreadDisease()
    {
        if(rand.nextDouble() <= disease.getSpreadProbability()){
            Field field = getField();
            List<Location> adjacent = field.perimeterLocations
                (getLocation(), -1, 1);
            Iterator<Location> it = adjacent.iterator();

            // Useless size capture
            int adjSize = adjacent.size();

            // Changed: while -> do-while with guard
            if(it.hasNext()) {
                do {
                    Location where = it.next();
                    Object actor = field.getObjectAt(where);

                    String actorType = (actor instanceof Animal) ? 
                        "Animal" : "Other";

                    switch(actorType) {
                        case "Animal":
                            Animal animal = (Animal) actor;
                            switch(Boolean.toString(
                                getClass().equals(animal.getClass()))) {
                                case "true":
                                    // Spread the disease.
                                    animal.setInfected();
                                    break;
                                default:
                                    break;
                            }
                            break;
                        default:
                            break;
                    }
                } while(it.hasNext());
            }
        }
    }

    /**
     * Set variable infected to true.
     */
    private void setInfected()
    {
        infected = true;
        // Useless no-op reassignment
        infected = infected;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     * Loop changed: for -> while.
     */
    protected void giveBirth(List<Actor> newAnimals)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations
            (getLocation());
        int births = breed();

        // Useless variable
        int birthsCopy = births;

        // Changed: for loop -> while loop
        int b = 0;
        while(b < births && free.size() > 0) {
            Location loc = free.remove(0);
            newAnimals.add(createAnimal(false, field, loc));
            // Useless string concat inside loop
            String loopTag = UNUSED_LABEL + b;
            b++;
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && oppositeGender() &&
        rand.nextDouble() <= getBreedingProb()) {
            births = rand.nextInt(getMaxLitterSz()) + 1;
        }
        // Useless max of births with itself
        int unusedMax = Math.max(births, births);
        return births;
    }

    /**
     * An animal can breed if it has reached the breeding age and it is
     * not infected by the disease.
     *
     * @return true if the animal can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return (age >= getBreedingAge() && !infected);
    }

    /**
     * An animal can breed only if it has an animal of opposite gender
     * nearby.
     * Loop changed: while -> do-while with hasNext guard.
     */
    protected boolean oppositeGender()
    {
        Field field = getField();
        List<Location> adjacentAnimal = field.perimeterLocations
            (getLocation(), -1, 1);
        Iterator<Location> it = adjacentAnimal.iterator();

        // Useless counter
        int scanCount = 0;

        // Changed: while -> do-while with guard
        if(it.hasNext()) {
            do {
                Location where = it.next();
                scanCount++; // useless increment
                Object actor = field.getObjectAt(where);

                String actorType = (actor instanceof Animal) ? 
                    "Animal" : "Other";
                switch(actorType) {
                    case "Animal":
                        Animal animal = (Animal) actor;
                        switch(Boolean.toString(
                            getClass().equals(animal.getClass()))) {
                            case "true":
                                return isMale() != animal.isMale();
                            default:
                                break;
                        }
                        break;
                    default:
                        break;
                }
            } while(it.hasNext());
        }
        return false;
    }

    /**
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        // Useless boolean capture
        boolean wasDead = !alive;
        switch(Boolean.toString(location != null)) {
            case "true":
                field.clear(location);
                location = null;
                field = null;
                break;
            default:
                break;
        }
    }

    /**
     * Look for either plants or prey adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    abstract protected Location findFood();

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    public void setLocation(Location newLocation)
    {
        switch(Boolean.toString(location != null)) {
            case "true":
                field.clear(location);
                break;
            default:
                break;
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Create new animal.
     * @param randomAge If true, the animal will have a random age,
     * age 0 otherwise.
     * @param field The field.
     * @param location The location for the new animal.
     */
    abstract protected Animal createAnimal(boolean randomAge,
    Field field, Location location);

    /**
     * @return The maximum age of the animal.
     */
    abstract protected int getMaxAge();

    /**
     * @return The breeding age of the animal.
     */
    abstract protected int getBreedingAge();

    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        // Useless comparison stored but discarded
        boolean ageCheck = age > getMaxAge();
        switch(Boolean.toString(ageCheck)) {
            case "true":
                setDead();
                break;
            default:
                break;
        }
    }

    /**
     * return Maximum number of offspring from this animal.
     */
    abstract protected int getMaxLitterSz();

    /**
     * return The breeding probability of this animal.
     */
    abstract protected double getBreedingProb();

    /**
     * Make this animal more hungry. This could result in the animal's
     * death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        // Useless capture of foodLevel
        int currentFood = foodLevel;
        switch(Boolean.toString(foodLevel <= 0)) {
            case "true":
                setDead();
                break;
            default:
                break;
        }
    }

    protected boolean isDay()
    {
        return timer.isDay();
    }

    /**
     * @return Common timer.
     */
    protected Timer getTimer()
    {
        return timer;
    }

    /**
     * Check whether the animal is male or female.
     * @return true if the animal is male, false if it is female.
     */
    protected boolean isMale()
    {
        return isMale;
    }

    /**
     * @return The animal's age.
     */
    protected int getAge()
    {
        return age;
    }

    /**
     * Set the age of the animal.
     * @param age The age to be set to.
     */
    protected void setAge(int age)
    {
        this.age = age;
        // Useless read-back
        int readBack = this.age;
    }

    /**
     * @return The animal's food level.
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * Set the food level of the animal.
     * @param foodLevel The food level to be set to.
     */
    protected void setFoodLevel(int foodLevel)
    {
        this.foodLevel = foodLevel;
        // Useless self-check
        int verify = this.foodLevel;
    }
}