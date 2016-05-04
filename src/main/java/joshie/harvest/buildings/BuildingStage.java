package joshie.harvest.buildings;

import joshie.harvest.buildings.placeable.Placeable;
import joshie.harvest.buildings.placeable.Placeable.ConstructionStage;
import joshie.harvest.core.handlers.HFTrackers;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

/** This data is used by the BuilderNPC, 
 * to know their current progress through a building project **/
public class BuildingStage {
    public UUID owner;
    public Building building;
    public Mirror mirror;
    public Rotation rotation;
    public ConstructionStage stage;
    public int index;
    public BlockPos pos;

    public BuildingStage() {
    }

    public BuildingStage(UUID uuid, Building building, BlockPos pos, Mirror mirror, Rotation rotation) {
        this.owner = uuid;
        this.building = building;
        this.mirror = mirror;
        this.rotation = rotation;
        this.stage = ConstructionStage.BUILD;
        this.index = 0;
        this.pos = pos.add(0, building.getOffsetY(), 0);
    }

    public BuildingStage build(World world) {
        if (index >= building.getSize()) {
            if (stage == ConstructionStage.BUILD) {
                stage = ConstructionStage.DECORATE;
                index = 0;
            } else if (stage == ConstructionStage.DECORATE) {
                stage = ConstructionStage.PAINT;
                index = 0;
            } else if (stage == ConstructionStage.PAINT) {
                stage = ConstructionStage.MOVEIN;
                index = 0;
            } else if (stage == ConstructionStage.MOVEIN) {
                stage = ConstructionStage.FINISHED;
                index = 0;

                HFTrackers.getPlayerTracker(owner).getTown().addBuilding(world, this);
            }
        } else {
            while (index < building.getSize()) {
                Placeable block = building.get(index);
                if (block.place(owner, world, pos, mirror, rotation, stage)) {
                    index++;
                    return this;
                }

                index++;
            }
        }

        return this;
    }

    public long getTickTime() {
        return building.getTickTime();
    }

    public boolean isFinished() {
        return stage == ConstructionStage.FINISHED;
    }

    public void readFromNBT(NBTTagCompound nbt) {
        building = Building.getGroup(nbt.getString("CurrentlyBuilding"));
        mirror = Mirror.valueOf(nbt.getString("Mirror"));
        rotation = Rotation.valueOf(nbt.getString("Rotation"));
        pos = new BlockPos(nbt.getInteger("BuildingX"), nbt.getInteger("BuildingY"), nbt.getInteger("BuildingZ"));

        if (nbt.hasKey("Owner-UUIDMost")) {
            index = nbt.getInteger("Index");
            stage = ConstructionStage.values()[nbt.getInteger("Stage")];
            owner = new UUID(nbt.getLong("Owner-UUIDMost"), nbt.getLong("Owner-UUIDLeast"));
        }
    }

    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setString("CurrentlyBuilding", building.getName());
        nbt.setString("Mirror", mirror.name());
        nbt.setString("Rotation", rotation.name());
        nbt.setInteger("BuildingX", pos.getX());
        nbt.setInteger("BuildingY", pos.getY());
        nbt.setInteger("BuildingZ", pos.getZ());

        if (owner != null) {
            nbt.setInteger("Stage", stage.ordinal());
            nbt.setInteger("Index", index);
            nbt.setLong("Owner-UUIDMost", owner.getMostSignificantBits());
            nbt.setLong("Owner-UUIDLeast", owner.getLeastSignificantBits());
        }
    }
}