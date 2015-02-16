package joshie.harvestmoon.mining;

import joshie.harvestmoon.crops.WorldLocation;
import joshie.harvestmoon.util.IData;

public abstract class MineBlock extends WorldLocation implements IData {
    public MineBlock() {}
    public MineBlock(int dim, int x, int y, int z) {
        super(dim, x, y, z);
    }

    public void newDay(int level) {
        return;
    }

    public String getType() {
        return "";
    }
}
