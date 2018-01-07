package xyz.bnayagrawal.paddyweigh;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by binay on 5/1/18.
 */

public class JsonHandle {
    public String toJson(HashMap<Long,People> peoples,Date date) {
        String jsonData = "";
        char newLine = '\n', quote = '\"';

        jsonData += "{" + newLine;
        jsonData += getKeyValuePair("date",new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss").format(date));

        jsonData += quote + "data" + quote + ":" + "{";
        jsonData += quote + "peoples" + quote + ":" + "[";

        People people;
        int peopleCount = 0,packetCount = 0;
        ArrayList<Packet> packets;

        for(Map.Entry<Long,People> entry: peoples.entrySet()) {
            jsonData += "{" + newLine;

            peopleCount++; packetCount = 0;
            people = (People)entry.getValue();
            jsonData += getKeyValuePair("id",people.getId());
            jsonData += getKeyValuePair("name",people.getName());
            jsonData += getKeyValuePair("packetCount",people.getPacketCount());

            packets = people.getPacketList();
            jsonData += quote + "packets" + quote + ": [" + newLine;
            for(Packet packet: packets) {
                packetCount++;
                jsonData += "{" + newLine;
                jsonData += getKeyValuePair("id",packet.getId());
                jsonData += getKeyValuePair("weight",packet.getWeight());
                jsonData += getEndKeyValuePair("type",(Packet.Type.Plastic == packet.getType()) ? "Plastic" : "Jute");
                jsonData += "}" + newLine;
                if(packetCount < packets.size())
                    jsonData += "," + newLine;
            }
            jsonData += "]" + newLine;
            jsonData += "}" + newLine;
            if(peopleCount < peoples.size())
                jsonData += "," + newLine;
        }

        jsonData += "]}}";
        return jsonData;
    }

    public WeightList toWeightList(String jsonData) {
        WeightList weightList;

        try {
            HashMap<Long,People> peoples = new HashMap<>();
            JSONObject jsonObject = new JSONObject(jsonData);
            String date = jsonObject.getString("date");
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray jsonArrayPeoples = data.getJSONArray("peoples");

            JSONObject jsonObjectPeople;
            JSONObject jsonObjectPacket;
            JSONArray jsonArrayPackets;
            People people;

            //Loop through each peoples
            for(int i=0; i < jsonArrayPeoples.length(); i++) {
                jsonObjectPeople = jsonArrayPeoples.getJSONObject(i);
                people = new People(jsonObjectPeople.getString("name"),jsonObjectPeople.getInt("id"));
                jsonArrayPackets = jsonObjectPeople.getJSONArray("packets");
                //Loop through each packets
                for(int j=0; j < jsonArrayPackets.length(); j++) {
                    jsonObjectPacket = jsonArrayPackets.getJSONObject(j);
                    people.addPacket(
                            jsonObjectPacket.getDouble("weight"),
                            Packet.getType(jsonObjectPacket.getString("type")),
                            jsonObjectPacket.getInt("id")
                    );
                }
                peoples.put(people.getId(),people);
            }
            weightList = new WeightList(peoples,date);
        }
        catch (JSONException je) {
            //Parse error
            Log.e("json to hashmap",je.getMessage());
            weightList = null;
        }
        return weightList;
    }

    private String getKeyValuePair(String key,String value) {
        return "\"" + key + "\":\"" + value + "\",\n";
    }

    private String getKeyValuePair(String key,long value) {
        return "\"" + key + "\":" + value + ",\n";
    }

    private String getKeyValuePair(String key,double value) {
        return "\"" + key + "\":" + value + ",\n";
    }

    /* returns a key value pair which doesn't contain comma at the end*/
    private String getEndKeyValuePair(String key,String value) {
        return "\"" + key + "\":\"" + value + "\"\n";
    }

    private String getEndKeyValuePair(String key,long value) {
        return "\"" + key + "\":" + value + "\n";
    }

    private String getEndKeyValuePair(String key,double value) {
        return "\"" + key + "\":" + value + "\n";
    }
}
