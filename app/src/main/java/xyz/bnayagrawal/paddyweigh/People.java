package xyz.bnayagrawal.paddyweigh;

import java.util.ArrayList;

/**
 * Created by binay on 1/1/18.
 */

public class People {
    private String name;
    private long id;
    public ArrayList<Packet> packets;
    private int plasticPackets, jutePackets;
    private double totalWeight;

    public People(String name,long id) {
        this.name = name;
        this.id = id;
        packets = new ArrayList<>();
        totalWeight = plasticPackets = jutePackets = 0;
    }

    public void addPacket(double weight, Packet.Type type, long id) {
        packets.add(new Packet(id,type,weight));
        if(type == Packet.Type.Plastic)
            plasticPackets++;
        else
            jutePackets++;
        totalWeight += weight;
    }

    public long getId() {return this.id;}
    public String getName() {return this.name;}
    public int getPlasticPacketsCount() {return this.plasticPackets;}
    public int getJutePacketsCount() {return this.jutePackets;}
    public int getPacketCount() { return  plasticPackets + jutePackets;}
    public ArrayList<Packet> getPacketList() {return packets;}
}
