package com.abevriens;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Objects;

public class POJO_Vector {
    public int x;
    public int y;
    public int z;

    public POJO_Vector() {
    }

    public POJO_Vector(Vector vector) {
        x = vector.getBlockX();
        y = vector.getBlockY();
        z = vector.getBlockZ();
    }

    public Location pojoVectorToLocation() {
        return new Vector(x, y, z).toLocation(Objects.requireNonNull(Bukkit.getWorld("world")));
    }
}
