package xyz.bnayagrawal.paddyweigh;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

public class AddPaddyActivity extends AppCompatActivity implements DialogAddPerson.AddPersonDialogListener {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;

    private Date startDate;
    private TextView txtTotalPackets;
    private EditText txtPacketWeight;
    private Spinner spinnerPeopleList;
    private DialogAddPerson dialogAddPerson;

    private ArrayAdapter<String> arrayAdapter;
    private HashMap<Long, People> peoples;
    private People selectedPeople;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUserSelectedTheme();
        setContentView(R.layout.activity_add_paddy);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startDate = new Date();
        String dateTime = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(startDate);
        toolbar.setTitle("Weight List (" + dateTime + ")");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptAddPerson();
            }
        });

        txtPacketWeight = findViewById(R.id.txtPacketWeight);
        txtTotalPackets = findViewById(R.id.txtTotalPackets);

        txtPacketWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    txtPacketWeight.setSelection(txtPacketWeight.getEditableText().length());
                }
            }
        });

        txtPacketWeight.setFilters(
                new InputFilter[] {
                        new DecimalDigitsInputFilter(3,2)
                });

        spinnerPeopleList = findViewById(R.id.spinnerPeopleList);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeopleList.setAdapter(arrayAdapter);
        spinnerPeopleList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateSelectedPeople(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Do something...
            }
        });

        peoples = new HashMap<>();
        initRecyclerView();
        setAddButtonAction();
        setMakeBillButtonAction();
        promptAddPerson();
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

    protected void promptAddPerson() {
        dialogAddPerson = new DialogAddPerson();
        dialogAddPerson.show(getFragmentManager(), "DialogAddPerson");
    }

    protected void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_weight_view);
        FadeInUpAnimator animator = new FadeInUpAnimator();
        animator.setAddDuration(150);
        animator.setChangeDuration(150);
        animator.setRemoveDuration(150);
        animator.setMoveDuration(150);

        recyclerView.setItemAnimator(animator);
        layoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //only for initialization purpose
        selectedPeople = new People("",0);
        adapter = new RAdapter(getApplicationContext(), selectedPeople);

        recyclerView.setAdapter(adapter);
    }

    protected void setAddButtonAction() {
        (findViewById(R.id.btnAddPacket)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrayAdapter.getCount() == 0) {
                    Toast.makeText(getApplicationContext(), "Please add a person first!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String raw = txtPacketWeight.getEditableText().toString();
                if(raw.length() == 0) {
                    Toast.makeText(getApplicationContext(),"Please enter weight!",Toast.LENGTH_SHORT).show();
                    return;
                }

                float weight = Float.parseFloat(raw);
                DecimalFormat decimalFormat = new DecimalFormat("##.#");
                weight = Float.parseFloat(decimalFormat.format(weight));

                //check if value is greater than zero or not
                if (weight <= 0) {
                    Toast.makeText(getApplicationContext(), "Please enter a weight which is greater than zero", Toast.LENGTH_SHORT).show();
                    return;
                }

                Packet.Type type;
                if (R.id.rdoPacketJute == ((RadioGroup) findViewById(R.id.rdgPacketType)).getCheckedRadioButtonId())
                    type = Packet.Type.Jute;
                else
                    type = Packet.Type.Plastic;

                selectedPeople.addPacket(weight, type, 0);
                adapter.notifyItemInserted(selectedPeople.packets.size());
                txtTotalPackets.setText("Total Packets: " + selectedPeople.packets.size());
                txtPacketWeight.setText("");
            }
        });
    }

    protected void setMakeBillButtonAction() {
        (findViewById(R.id.btnMakeBill)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = saveWeightList();
                Intent intent = new Intent(getApplicationContext(),BillViewActivity.class);
                intent.putExtra("FILE_NAME",fileName);
                startActivity(intent);
            }
        });
    }

    //Dialog callback methods
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        EditText editText = dialog.getDialog().findViewById(R.id.editTextPersonName);
        String name = editText.getText().toString();

        arrayAdapter.add(name);
        arrayAdapter.notifyDataSetChanged();

        long id = arrayAdapter.getItemId(arrayAdapter.getCount() - 1);
        People people = new People(name, id);
        selectedPeople = people;
        peoples.put(id, people);

        spinnerPeopleList.setSelection(arrayAdapter.getCount() - 1, true);
        updateSelectedPeople(id);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if (arrayAdapter.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "You must add at least one person!", Toast.LENGTH_LONG).show();
        } else {
            //No use...
        }
    }

    protected void updateSelectedPeople(long id) {
        selectedPeople = peoples.get(id);
        txtTotalPackets.setText("Total Packets: " + selectedPeople.getPacketCount());
        ((RAdapter) adapter).updateCurrentPeople(selectedPeople);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(peoples.size() == 0) {
            AddPaddyActivity.super.onBackPressed();
            overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
        }
        else
            promptReturn();
    }

    protected void promptReturn() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                AddPaddyActivity.super.onBackPressed();
                overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int iad) {
                //Nothing to do...
            }
        });
        builder.setNeutralButton("Save and return", new
                DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveWeightList();
                        AddPaddyActivity.super.onBackPressed();
                        overridePendingTransition(R.anim.slide_in,R.anim.slide_out);
                    }
                });

        builder.setTitle("Return");
        builder.setMessage("Weight list is not saved. Are you sure you want to return?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    protected String saveWeightList() {
        JsonHandle jsonHandle = new JsonHandle();
        String jsonData = jsonHandle.toJson(peoples,startDate);
        FileHandle fileHandle = new FileHandle();

        String file_name = "WeightList_" + new SimpleDateFormat("dd_MMM_yyyy_HH_mm_ss").format(startDate) + ".txt";
        boolean result = fileHandle.writeToFile(getApplicationContext(),file_name,jsonData);

        if(result) {
            Toast.makeText(getApplicationContext(),"Weight List Saved",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Failed to save weight list!", Toast.LENGTH_SHORT).show();
            file_name = null;
        }
        return file_name;
    }

    protected void setUserSelectedTheme() {
        //get theme from shared pref.
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_theme_file), Context.MODE_PRIVATE);
        setTheme(sharedPref.getInt("APP_THEME",R.style.AppTheme));
    }
}
