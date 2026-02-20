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
    private boolean isAlive;
    // The animal's field.
    private Field habitat;
    // The plant's field.
    private Field plantHabitat;
    // The animal's position in the field.
    private Location position;
    // A shared random generator.
    private static final Random randomGen = Randomizer.getRandom();
    // The animal's gender.
    private boolean isMale;
    // The animal's age.
    private int currentAge;
    // A timer to keep track of day and night.
    private Timer dayNightTimer;
    // Number of animals infected.
    private int infectedCount = 0;

    // The animal's food level, increased when eating prey or plants.
    private int energyLevel;
    // Whether the animal is infected.
    private boolean isInfected;
    // An instance of the ChronicDisease class
    private ChronicDisease illness;

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location, Timer timer)
    {
        isAlive = true;
        isMale = randomGen.nextBoolean();

        illness = new ChronicDisease();
        isInfected = illness.randInfected();

        this.habitat = field;
        setLocation(location);
        this.dayNightTimer = timer;
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
        if(isDay() || randomGen.nextDouble() <= getMovementProbability()){
            if(isActive()) {
                if(isInfected){
                    spreadDisease();
                    illness.act();
                    if (illness.diseased()){
                        // Killed by disease.
                        setDead();
                        return;
                    }
                }
                giveBirth(newActors);
                // Move towards a source of food if found.
                Location newPosition = findFood();
                if(newPosition == null) {
                    // No food found - try to move to a free location.
                    newPosition = getField().freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if(newPosition != null) {
                    setLocation(newPosition);
                }
                else {
                    // Overcrowding.
                    setDead();
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
        return isAlive;
    }

    /**
     * Spread the disease to the animals of the same species in the
     * neighboring locations.
     */
    private void spreadDisease()
    {
        if(randomGen.nextDouble() <= illness.getSpreadProbability()){
            Field currentHabitat = getField();
            List<Location> nearbyLocations = currentHabitat.perimeterLocations(getLocation(), -1, 1);
            Iterator<Location> locationIterator = nearbyLocations.iterator();
            while(locationIterator.hasNext()) {
                Location nearbySpot = locationIterator.next();
                Object nearbyActor = currentHabitat.getObjectAt(nearbySpot);
                // Check that the object is an animal.
                if(nearbyActor instanceof Animal){
                    Animal nearbyAnimal = (Animal) nearbyActor;
                    // Check that the animal is of the same species.
                    if(getClass().equals(nearbyAnimal.getClass())){
                        // Spread the disease.
                        nearbyAnimal.setInfected();
                    }
                }
            }
        }
    }

    /**
     * Set variable isInfected to true.
     */
    private void setInfected()
    {
        isInfected = true;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
    protected void giveBirth(List<Actor> newAnimals)
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field currentHabitat = getField();
        List<Location> freeSpots = currentHabitat.getFreeAdjacentLocations(getLocation());
        int offspringCount = breed();
        for(int b = 0; b < offspringCount && freeSpots.size() > 0; b++) {
            Location birthSpot = freeSpots.remove(0);
            newAnimals.add(createAnimal(false, currentHabitat, birthSpot));
        }
    }

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int offspringCount = 0;
        if(canBreed() && oppositeGender() &&
        randomGen.nextDouble() <= getBreedingProb()) {
            offspringCount = randomGen.nextInt(getMaxLitterSz()) + 1;
        }
        return offspringCount;
    }

    /**
     * An animal can breed if it has reached the breeding age and it is
     * not infected by the disease.
     *
     * @return true if the animal can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return (currentAge >= getBreedingAge() && !isInfected);
    }

    /**
     * An animal can breed only if it has an animal of opposite gender
     * nearby.
     */
    protected boolean oppositeGender()
    {
        Field currentHabitat = getField();
        List<Location> nearbyLocations = currentHabitat.perimeterLocations(getLocation(), -1, 1);
        Iterator<Location> locationIterator = nearbyLocations.iterator();
        while(locationIterator.hasNext()) {
            Location nearbySpot = locationIterator.next();
            Object nearbyActor = currentHabitat.getObjectAt(nearbySpot);
            if(nearbyActor instanceof Animal){
                Animal nearbyAnimal = (Animal) nearbyActor;
                if(getClass().equals(nearbyAnimal.getClass())){
                    return isMale() != nearbyAnimal.isMale();
                }
            }
        }
        return false;
    }

    /**
     * @return The animal's field.
     */
    protected Field getField()
    {
        return habitat;
    }

    /**
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return position;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        isAlive = false;
        if(position != null) {
            habitat.clear(position);
            position = null;
            habitat = null;
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
        if(position != null) {
            habitat.clear(position);
        }
        position = newLocation;
        habitat.place(this, newLocation);
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
        currentAge++;
        if(currentAge > getMaxAge()) {
            setDead();
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
        energyLevel--;
        if(energyLevel <= 0) {
            setDead();
        }
    }

    protected boolean isDay()
    {
        return dayNightTimer.isDay();
    }

    /**
     * @return Common timer.
     */
    protected Timer getTimer()
    {
        return dayNightTimer;
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
        return currentAge;
    }

    /**
     * Set the age of the animal.
     * @param age The age to be set to.
     */
    protected void setAge(int age)
    {
        this.currentAge = age;
    }

    /**
     * @return The animal's food level.
     */
    protected int getFoodLevel()
    {
        return energyLevel;
    }

    /**
     * Set the food level of the animal.
     * @param foodLevel The food level to be set to.
     */
    protected void setFoodLevel(int foodLevel)
    {
        this.energyLevel = foodLevel;
    }
}