
// VARIABLES RENAMED
import java.util.List;
import java.util.Random;

import com.sun.jdi.Location;

/**
 * Abstract class representing a generic intelligent agent in the simulation.
 */
public abstract class Animal_renamed extends Organism {
    Random rng;

    // Resource level of the agent
    private int resourceLevel;

    // Role flag (leader/follower or producer/consumer depending on model)
    private boolean primaryRole;

    // Replication parameters
    private int activationAge;
    private double replicationRate;
    private int maxReplicationCount;

    // Replication status
    private boolean hasReplicated = false;

    // Contamination / anomaly state
    private boolean compromised = false;

    // Probability of contamination
    protected double anomalyProbability;

    /**
     * Constructor.
     */
    public Animal_renamed(boolean randomAge, Field environment, Location position,
            int maxLifetime, int maxResource, int activationAge,
            double replicationRate, int maxReplicationCount,
            double anomalyProbability, Random rng) {
        super(randomAge, environment, position, maxLifetime, rng);

        this.rng = rng;
        this.activationAge = activationAge;
        this.replicationRate = replicationRate;
        this.maxReplicationCount = maxReplicationCount;
        this.anomalyProbability = anomalyProbability;

        if (randomAge) {
            resourceLevel = rng.nextInt(maxResource);
        } else {
            resourceLevel = maxResource;
        }

        primaryRole = rng.nextBoolean();
    }

    /**
     * Core simulation step.
     */
    public void executeStep(List<Organism> generatedAgents, EnvironmentState state) {
        adjustAnomalyRisk(state);

        hasReplicated = false;

        incrementAge();
        consumeResource();

        if (triggerAnomaly(anomalyProbability)) {
            applyAnomalyImpact();
        }

        if (isAlive()) {

            if (!primaryRole && canReplicate(state)) {
                generateOffspring(generatedAgents);
            }

            if (canRelocate(state)) {
                Location next = locateResource();

                if (next == null) {
                    next = getField().freeAdjacentLocation(getLocation());
                }

                if (next != null) {
                    setLocation(next);
                } else {
                    setDead();
                }
            }
        }
    }

    /**
     * Resource consumption.
     */
    protected void consumeResource() {
        resourceLevel--;
        if (resourceLevel <= 0) {
            setDead();
        }
    }

    protected void updateResource(int value) {
        resourceLevel = value;
    }

    /**
     * Replication logic.
     */
    protected int computeReplication(Class agentType) {
        Field field = getField();
        int offspring = 0;

        List<Location> neighbors = field.adjacentLocations(getLocation());

        // for (Location loc : neighbors)
        for (int i = 0; i < neighbors.size(); i++) {

            Organism obj = (Organism) field.getObjectAt(neighbors.get(i));

            if (obj instanceof Animal_renamed) {

                Animal_renamed partner = (Animal_renamed) obj;

                if (agentType.isInstance(partner) &&
                        partner.isPrimaryRole() &&
                        !partner.hasReplicated() &&
                        partner.isAlive() &&
                        isActive(activationAge) &&
                        partner.isActive(activationAge) &&
                        rng.nextDouble() <= replicationRate) {
                    partner.setReplicated(true);

                    // anomaly propagation
                    if (partner.isCompromised() && !isCompromised()) {
                        applyAnomalyImpact();
                    } else if (isCompromised() && !partner.isCompromised()) {
                        partner.applyAnomalyImpact();
                    }

                    offspring = rng.nextInt(maxReplicationCount) + 1;
                    break;
                }
            }
        }
        return offspring;
    }

    /**
     * Random anomaly occurrence.
     */
    protected boolean triggerAnomaly(double probability) {
        Random rng = new Random();
        if (rng.nextDouble() <= probability && !isCompromised()) {
            compromised = true;
            return true;
        }
        return false;
    }

    protected boolean isCompromised() {
        return compromised;
    }

    /**
     * Apply anomaly effect.
     */
    protected void applyAnomalyImpact() {
        int severity = rng.nextInt(4) + 1;

        int adjustedAge = severity * getAge();
        setAge(adjustedAge);

        resourceLevel = (int) resourceLevel / severity;
    }

    protected void scaleAnomalyProbability(int factor) {
        anomalyProbability *= factor;
    }

    protected void setAnomalyProbability(double probability) {
        anomalyProbability = probability;
    }

    protected boolean hasReplicated() {
        return hasReplicated;
    }

    protected void setReplicated(boolean status) {
        hasReplicated = status;
    }

    protected boolean isPrimaryRole() {
        return primaryRole;
    }

    /**
     * Abstract behaviours.
     */
    protected abstract void generateOffspring(List<Organism> newAgents);

    protected abstract Location locateResource();

    public abstract boolean isActivePhase();

    protected abstract boolean canRelocate(EnvironmentState state);

    protected abstract boolean canReplicate(EnvironmentState state);

    protected abstract void adjustAnomalyRisk(EnvironmentState state);
}
