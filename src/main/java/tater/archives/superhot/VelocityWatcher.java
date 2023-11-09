package tater.archives.superhot;

import net.minecraft.entity.Entity;

public class VelocityWatcher {
    public final Entity entity;

    private double prevX = 0;

    private double prevY = 0;
    private double prevZ = 0;

    public VelocityWatcher(Entity entity) {
        this.entity = entity;
    }

    public double getEntityVelocity() {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        double dx = x - prevX;
        double dy = y - prevY;
        double dz = z - prevZ;
        prevX = x;
        prevY = y;
        prevZ = z;
        return Math.sqrt(dy * dy + dx * dx + dz * dz);
    }
}
