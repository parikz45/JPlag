//identifiers renamed


import java.util.List;
import java.util.Iterator;
import java.util.Random;
/**
 * A class representing the shared characteristics of all animals
 *
 * @version 2019.02.20
 */
public abstract class Creature extends Actor {

    private int reproductionAge;
    private int lifespan;
    private double reproductionChance;
    private int maxOffspring;

    protected int energy;
    private int years;
    private boolean male;
    private boolean infected;

    private static final Random randomGen = Randomizer.getRandom();

    public Creature(boolean randomYears, Field field, Location location,
                    int reproductionAge, int lifespan,
                    double reproductionChance, int maxOffspring,
                    int energyValue, boolean randomInfection, boolean infected) {

        super(field, location);

        this.reproductionAge = reproductionAge;
        this.lifespan = lifespan;
        this.reproductionChance = reproductionChance;
        this.maxOffspring = maxOffspring;

        male = randomGen.nextBoolean();

        if (randomYears) {
            years = randomGen.nextInt(80);
            energy = randomGen.nextInt(energyValue);
        } else {
            years = 0;
            energy = energyValue;
        }

        this.infected = infected;
        if (randomInfection) {
            this.infected = randomGen.nextDouble() <= 0.05;
        }
    }

    protected void growOlder() {
        years++;
        if (infected) {
            if (years > lifespan / 2) {
                setDead();
            }
        } else if (years > lifespan) {
            setDead();
        }
    }

    protected boolean isMale() {
        return male;
    }

    protected void decreaseEnergy() {
        if (infected) {
            energy--;
        }
        energy--;
        if (energy <= 0) {
            setDead();
        }
    }

    protected double getReproductionChance() {
        return reproductionChance;
    }

    protected int getMaxOffspring() {
        return maxOffspring;
    }

    protected boolean readyToReproduce() {
        return years >= reproductionAge;
    }

    protected boolean isInfected() {
        return infected;
    }

    protected int reproduce() {
        int babies = 0;
        if (readyToReproduce() && randomGen.nextDouble() <= reproductionChance) {
            babies = randomGen.nextInt(maxOffspring) + 1;
        }
        return babies;
    }
}
