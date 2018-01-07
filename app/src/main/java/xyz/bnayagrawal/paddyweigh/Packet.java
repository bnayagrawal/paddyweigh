package xyz.bnayagrawal.paddyweigh;

/**
 * Created by binay on 1/1/18.
 */

public class Packet {
    private long id;
    private Packet.Type type;
    private double weight;

    public Packet(long id, Packet.Type type, double weight) {
        this.id = id;
        this.type = type;
        this.weight = weight;
    }

    public long getId(){return this.id;}
    public double getWeight() {return this.weight;}
    public Packet.Type getType() {return this.type;}

    public static Packet.Type getType(String type) {
        Packet.Type pType;
        if(type.equalsIgnoreCase("Plastic"))
            pType = Type.Plastic;
        else
            pType = Type.Jute;
        return pType;
    }

    public enum Type {
        Plastic, Jute;
    }
}
