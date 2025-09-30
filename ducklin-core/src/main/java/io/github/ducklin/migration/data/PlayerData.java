package io.github.ducklin.migration.data;

import com.google.common.annotations.Beta;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;

import io.github.ducklin.migration.gps.Waypoint;
import io.github.ducklin.migration.player.PlayerBackpack;
import io.github.ducklin.migration.researches.Research;
import org.apache.commons.lang.Validate;

@Beta
public class PlayerData {

    private final Set<Research> researches = new HashSet<>();
    private final Map<Integer, PlayerBackpack> backpacks = new HashMap<>();
    private final Set<Waypoint> waypoints = new HashSet<>();

    public PlayerData(Set<Research> researches, Map<Integer, PlayerBackpack> backpacks, Set<Waypoint> waypoints) {
        this.researches.addAll(researches);
        this.backpacks.putAll(backpacks);
        this.waypoints.addAll(waypoints);
    }

    public Set<Research> getResearches() {
        return researches;
    }

    public void addResearch(@Nonnull Research research) {
        Validate.notNull(research, "Cannot add a 'null' research!");
        researches.add(research);
    }

    public void removeResearch(@Nonnull Research research) {
        Validate.notNull(research, "Cannot remove a 'null' research!");
        researches.remove(research);
    }

    @Nonnull
    public Map<Integer, PlayerBackpack> getBackpacks() {
        return backpacks;
    }

    @Nonnull
    public PlayerBackpack getBackpack(int id) {
        return backpacks.get(id);
    }

    public void addBackpack(@Nonnull PlayerBackpack backpack) {
        Validate.notNull(backpack, "Cannot add a 'null' backpack!");
        backpacks.put(backpack.getId(), backpack);
    }

    public void removeBackpack(@Nonnull PlayerBackpack backpack) {
        Validate.notNull(backpack, "Cannot remove a 'null' backpack!");
        backpacks.remove(backpack.getId());
    }

    public Set<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void addWaypoint(@Nonnull Waypoint waypoint) {
        Validate.notNull(waypoint, "Cannot add a 'null' waypoint!");

        for (Waypoint wp : waypoints) {
            if (wp.getId().equals(waypoint.getId())) {
                throw new IllegalArgumentException("A Waypoint with that id already exists for this Player");
            }
        }

        // Limited to 21 due to limited UI space and no pagination
        if (waypoints.size() >= 21) {
            return; // not sure why this doesn't throw but the one above does...
        }

        waypoints.add(waypoint);
    }

    public void removeWaypoint(@Nonnull Waypoint waypoint) {
        Validate.notNull(waypoint, "Cannot remove a 'null' waypoint!");
        waypoints.remove(waypoint);
    }
}
