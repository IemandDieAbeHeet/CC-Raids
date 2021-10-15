package com.abevriens;

import org.bukkit.util.Vector;

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
}
