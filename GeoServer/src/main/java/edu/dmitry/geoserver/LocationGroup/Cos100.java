package edu.dmitry.geoserver.LocationGroup;

public class Cos100
{
    private final int original_bits = Consts.GPWIDTH - 1;
    private final int bits = 12;
    private final byte[] table;

    public Cos100() {
        table = new byte[(1 << bits) + 1];

        for (int i = 0; i <= (1 << bits); ++i)
            table[i] = (byte)Calculate(Widen(i));
    }

    public int Guess(int intDegree) {
        int res = table[Narrow(intDegree)];
        return res;
    }
    public int Calculate(int intDegree)
    {
        return (int)(Math.cos(GeoUtls.ToDegree(intDegree) / 180 * Consts.pi) * 100);
    }
    public int Narrow(int intDegree)
    {
        return (Math.abs(intDegree) & ((1 << original_bits) - 1)) >> (original_bits - bits);
    }

    public int Widen(int intNarrow)
    {
        return intNarrow << (original_bits - bits);
    }
}