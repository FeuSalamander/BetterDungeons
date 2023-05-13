package me.feusalamander.betterdungeons;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

public class VoidWorld extends ChunkGenerator {
    public byte[] generate(World w, Random rand, int x, int z)
    {
        return new byte[32768];
    }

    private Integer xyz(int x, int y, int z)
    {
        return (x * 16 + z)*128+y;
    }
}
