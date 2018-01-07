package xyz.bnayagrawal.paddyweigh;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Map;

public class BillViewActivity extends AppCompatActivity {

    private int jutePacketDeduction,plasticPacketDeduction;
    private double pricePerKg;
    private String fileName;
    private WeightList weightList;

    private LinearLayout layoutPersonBillContainer;
    private TextView txtBillPersonName;
    private TextView txtBillTotalPacket;
    private TextView txtBillPacketList;
    private TextView txtBillPacketWeight;
    private TextView txtBillPacketBill;

    private TextView txtBillPlasticPD;
    private TextView txtBillJutePD;
    private TextView txtBillPricePerKg;
    private TextView txtBillTotalPeopleCount;
    private TextView txtBillTotalPacketCount;
    private TextView txtBillTotalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUserSelectedTheme();
        setContentView(R.layout.activity_bill_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        layoutPersonBillContainer = findViewById(R.id.layoutPersonBillContainer);
        txtBillPlasticPD = findViewById(R.id.txtBillPlasticWD);
        txtBillJutePD = findViewById(R.id.txtBillJuteWD);
        txtBillPricePerKg = findViewById(R.id.txtBillPricePerKg);
        txtBillTotalPeopleCount = findViewById(R.id.txtBillTotalPeopleCount);
        txtBillTotalPacketCount = findViewById(R.id.txtBillTotalPacketCount);
        txtBillTotalAmount = findViewById(R.id.txtBillTotalAmount);

        Bundle bundle = getIntent().getExtras();
        fileName = bundle.getString("FILE_NAME");
        prepareBill();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void prepareBill() {
        String jsonData = (new FileHandle()).readFromFile(getApplicationContext(),fileName);
        weightList = (new JsonHandle()).toWeightList(jsonData);

        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        jutePacketDeduction = sharedPref.getInt("JUTE_PACKET_DEDUCTION",700);
        plasticPacketDeduction = sharedPref.getInt("PLASTIC_PACKET_DEDUCTION",500);
        pricePerKg = sharedPref.getFloat("PRICE_PER_KG",13);

        //txtBillPlasticPD.setText(plasticPacketDeduction);
        //txtBillJutePD.setText(jutePacketDeduction);
        txtBillPricePerKg.setText("₹" + String.format("%.2f",pricePerKg));

        View view;
        People people;
        String packetWeightList;
        int totalPacketCount = 0;
        float totalAmount = 0;
        int jutePacketCount, plasticPacketCount,packetCount;
        float totalPacketWeight,finalPacketWeight,totalWeightDeduction,personBill;
        DecimalFormat decimalFormat = new DecimalFormat("##.##");

        //Set bottom margin
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        params.setMargins(0,0,0,margin);

        for(Map.Entry<Long,People> entry: weightList.getPeoples().entrySet()) {
            //reset values
            jutePacketCount = plasticPacketCount = packetCount = 0;
            totalPacketWeight = finalPacketWeight = personBill = totalWeightDeduction = 0;
            packetWeightList = "";

            people = entry.getValue();
            if(people.getPacketCount() == 0)
                continue;
            view = getLayoutInflater().inflate(R.layout.template_person_bill,null,false);

            txtBillPersonName = view.findViewById(R.id.txtBillPersonName);
            txtBillPacketBill = view.findViewById(R.id.txtBillPacketBill);
            txtBillPacketWeight = view.findViewById(R.id.txtBillPacketWeight);
            txtBillPacketList = view.findViewById(R.id.txtBillPacketList);
            txtBillTotalPacket = view.findViewById(R.id.txtBillTotalPacket);

            jutePacketCount = people.getJutePacketsCount();
            plasticPacketCount = people.getPlasticPacketsCount();
            packetCount = people.getPacketCount();

            txtBillPersonName.setText(people.getName());
            txtBillTotalPacket.setText("Total Packets: " +
                    packetCount + " (" +
                    plasticPacketCount + " Plastic, " +
                    jutePacketCount + " Jute)"
            );

            totalPacketCount += packetCount;
            totalPacketWeight = 0;
            for(Packet packet: people.getPacketList()) {
                totalPacketWeight += packet.getWeight();
                packetWeightList += String.format("%.2f",packet.getWeight()) + ", ";
            }

            txtBillPacketList.setText(packetWeightList.substring(0,packetWeightList.lastIndexOf(',')));
            String packetWeight = "";
            packetWeight += "Total Weight: " + (int)totalPacketWeight + "Kg\n";
            totalWeightDeduction = (float)((plasticPacketCount * plasticPacketDeduction) + (jutePacketCount * jutePacketDeduction))/1000;
            finalPacketWeight = totalPacketWeight - totalWeightDeduction;
            packetWeight += "(" + plasticPacketCount + "x" +
                    plasticPacketDeduction + "gm + " +
                    jutePacketCount + "x" +
                    jutePacketDeduction + "gm) -" + String.format("%.2f",totalWeightDeduction) + "Kg\n";
            packetWeight += "Final Weight: " + String.format("%.2f",finalPacketWeight) + "Kg";
            txtBillPacketWeight.setText(packetWeight);

            personBill = (float)(pricePerKg * finalPacketWeight);
            txtBillPacketBill.setText("Total Amount: ₹" + String.format("%.2f",personBill));
            totalAmount += personBill;
            view.setLayoutParams(params);
            layoutPersonBillContainer.addView(view);
        }

        txtBillTotalPeopleCount.setText("Peoples: " + weightList.getPeoples().size());
        txtBillTotalPacketCount.setText("Total Packets: " + totalPacketCount);
        txtBillTotalAmount.setText("Total Amount: ₹" + String.format("%.2f",totalAmount));
    }

    protected void setUserSelectedTheme() {
        //get theme from shared pref.
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_theme_file), Context.MODE_PRIVATE);
        setTheme(sharedPref.getInt("APP_THEME",R.style.AppTheme));
    }
}
