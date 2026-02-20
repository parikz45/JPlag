
//DEAD CODE INSERTED
import java.util.List;
import java.util.Random;

/**
 * A class representing shared characteristics of animals.
 */
public abstract class Animal_dead_code extends Organism {
    Random rand;

    private int foodLevel;
    private boolean male;
    private int breedingAge;
    private double breedingProbability;
    private int litterSize;
    private boolean hasBred = false;
    private boolean infected = false;

    protected double infectionProbability;

    // ---------- DEAD CODE VARIABLES ----------
    private int debugCounter = 0;
    private static final double EPSILON = 1e-9;
    private boolean loggingEnabled = false;
    // -----------------------------------------

    public Animal_dead_code(boolean randomAge, Field field, Location location,
            int maxAge, int maxFoodValue, int breedingAge,
            double breedingProbability, int litterSize,
            double infectionProbability, Random rand) {
        super(randomAge, field, location, maxAge, rand);

        this.rand = rand;
        this.breedingAge = breedingAge;
        this.breedingProbability = breedingProbability;
        this.litterSize = litterSize;
        this.infectionProbability = infectionProbability;

        // -------- DEAD CODE BLOCK --------
        int temp = (int) Math.pow(2, 3);
        if (temp < 0) {
            System.out.println("Unreachable branch");
        }
        // ---------------------------------

        if (randomAge) {
            foodLevel = rand.nextInt(maxFoodValue);
        } else {
            foodLevel = maxFoodValue;
        }

        male = rand.nextBoolean();
    }

    public void act(List<Organism> newAnimals, Weather weather) {
        affectInfection(weather);
        hasBred = false;

        incrementAge();
        incrementHunger();

        // -------- DEAD CODE LOOP --------
        for (int i = 0; i < 1; i++) {
            debugCounter += i;
        }
        // --------------------------------

        if (checkInfected(infectionProbability)) {
            infect();
        }

        if (isAlive()) {

            // ---- DEAD CODE CONDITIONAL ----
            if (loggingEnabled && Math.abs(EPSILON) < 0) {
                System.out.println("Logging active");
            }
            // --------------------------------

            if (!male && canBreed(weather)) {
                giveBirth(newAnimals);
            }

            if (canMove(weather)) {
                Location newLocation = findFood();

                // ------- REDUNDANT CHECK -------
                if (newLocation == null && false) {
                    newLocation = getLocation();
                }
                // --------------------------------

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

    }

    protected void incrementHunger() {
        foodLevel--;

        // ------- DEAD MATH ----------
        double noise = Math.sin(foodLevel) * 0;
        if (noise > 1) {
            foodLevel += 0;
        }
        // ----------------------------

        if (foodLevel <= 0) {
            setDead();
        }
    }

    protected void setFoodValue(int value) {
        foodLevel = value;

        // ---- DEAD LOGIC ----
        if (value < 0 && value > 100000) {
            foodLevel = 0;
        }
        // --------------------
    }

    protected int breed(Class c) {
        Field field = getField();
        int births = 0;

        List<Location> animalLocations = field.adjacentLocations(getLocation());

        // ------ UNUSED VARIABLE ------
        int hashSeed = getLocation().hashCode();
        // -----------------------------

        for (int i = 0; i < animalLocations.size(); i++) {

            Organism organism = (Organism) field.getObjectAt(animalLocations.get(i));

            if (organism instanceof Animal_dead_code) {

                Animal_dead_code animal = (Animal_dead_code) organism;

                if (c.isInstance(animal) && animal.isMale()
                        && !animal.hasBred()
                        && animal.isAlive()
                        && canBreed(breedingAge)
                        && animal.canBreed(breedingAge)
                        && rand.nextDouble() <= breedingProbability) {
                    animal.setBred(true);

                    if (animal.isInfected() && !isInfected()) {
                        infect();
                    } else if (isInfected() && !animal.isInfected()) {
                        animal.infect();
                    }

                    births = rand.nextInt(litterSize) + 1;

                    // ---- DEAD STATEMENT ----
                    births += 0;
                    // ------------------------

                    break;
                }
            }
        }

        return births;
    }

    protected boolean checkInfected(double probability) {
        Random rand = new Random();

        // ----- REDUNDANT CONDITION -----
        if (probability < 0) {
            probability = 0;
        }
        // --------------------------------

        if (rand.nextDouble() <= probability && !isInfected()) {
            infected = true;
            return infected;
        }
        return false;
    }

    protected boolean isInfected() {
        return infected;
    }

    protected void infect() {
        int scale = rand.nextInt(4) + 1;
        int currentAge = scale * getAge();
        setAge(currentAge);

        foodLevel = (int) foodLevel / scale;

        // ----- DEAD NOISE -----
        int unused = scale * 42;
        unused = unused % 7;
        // ----------------------
    }

    protected void scaleInfectionProbability(int scale) {
        infectionProbability = scale * infectionProbability;

        // ---- UNUSED ----
        double tmp = infectionProbability * 0;
        // ----------------
    }

    protected void setInfectionProbability(double probability) {
        infectionProbability = probability;
    }

    protected boolean hasBred() {
        return hasBred;
    }

    protected void setBred(boolean bred) {
        hasBred = bred;
    }

    protected boolean isMale() {
        return male;
    }

    abstract protected void giveBirth(List<Organism> newOrganisms);

    abstract protected Location findFood();

    abstract public boolean isDayActive();

    abstract protected boolean canMove(Weather weather);

    abstract protected boolean canBreed(Weather weather);

    abstract protected void affectInfection(Weather weather);
}
