/*
 * Decompiled with CFR 0_122.
 */
package javazoom.jl.decoder;

public final class Crc16 {
    private static short polynomial = -32763;
    private short crc = -1;

    public void add_bits(int bitstring, int length) {
        int bitmask = 1 << length - 1;
        do {
            if ((this.crc & 32768) == 0 ^ (bitstring & bitmask) == 0) {
                this.crc = (short)(this.crc << 1);
                this.crc = (short)(this.crc ^ polynomial);
                continue;
            }
            this.crc = (short)(this.crc << 1);
        } while ((bitmask >>>= 1) != 0);
    }

    public short checksum() {
        short sum = this.crc;
        this.crc = -1;
        return sum;
    }
}
