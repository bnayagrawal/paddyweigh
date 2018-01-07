package xyz.bnayagrawal.paddyweigh;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements DialogWeightListOptions.WeightListOptionsDialogListener,DialogAppTheme.AppThemeDialogListener {

    private ArrayList<TextView> textViews;
    private int userSelectedTheme;
    LinearLayout parentLayout;
    TextView txtDashDate,
            txtDashPeoples,
            txtDashPeoplesCount,
            txtDashPacketsCount,
            txtDashCirclePacketsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUserSelectedTheme();
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.activity_main_toolbar));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addWeightList);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AddPaddyActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_left);
            }
        });

        parentLayout = findViewById(R.id.parentLayout);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(),"Refreshing weight lists...",Toast.LENGTH_SHORT).show();
        parentLayout.removeAllViews();
        readSavedWeightList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_app_theme: {
                DialogAppTheme dialogAppTheme = new DialogAppTheme();
                Bundle bundle = new Bundle();
                bundle.putInt("APP_THEME",userSelectedTheme);
                dialogAppTheme.setArguments(bundle); //IMPORTANT
                dialogAppTheme.show(getFragmentManager(),"DialogAppTheme");
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected void readSavedWeightList() {
        JsonHandle jsonHandle = new JsonHandle();
        FileHandle fileHandle = new FileHandle();
        WeightList weightList;

        File filesDir = getApplicationContext().getFilesDir();
        for(File file: filesDir.listFiles()) {
            if(file.isFile()) {
                if(file.getName().contains("WeightList")) {
                    String fileJsonData = fileHandle.readFromFile(getApplicationContext(),file.getName());
                    if(null == fileJsonData)
                        continue;
                    weightList = jsonHandle.toWeightList(fileJsonData);
                    if(null != weightList) {
                        inflateWeightListView(weightList.getPeoples(),weightList.getDate(),file.getName());
                    }
                }
            }
        }
    }

    protected void inflateWeightListView(HashMap<Long,People> peoples, String date, final String fileName) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.template_dash_item,null,false);

        txtDashPeoples = view.findViewById(R.id.txtDashPeoples);
        txtDashPeoplesCount = view.findViewById(R.id.txtDashPeoplesCount);
        txtDashDate = view.findViewById(R.id.txtDashDate);
        txtDashPacketsCount = view.findViewById(R.id.txtDashPacketsCount);
        txtDashCirclePacketsCount = view.findViewById(R.id.txtDashCirclePacketsCount);

        String peopleNames = "";
        int totalPackets = 0;
        int plasticPackets = 0;
        int peoplesCount = peoples.size();

        for(Map.Entry<Long,People> entry: peoples.entrySet()) {
            peopleNames += entry.getValue().getName() + ", ";
            totalPackets += entry.getValue().getPacketCount();
            plasticPackets += entry.getValue().getPlasticPacketsCount();
        }

        txtDashPeoples.setText(peopleNames.substring(0,peopleNames.lastIndexOf(',')));
        txtDashPacketsCount.setText(totalPackets + " Packets (" + plasticPackets + " Plastic)");
        txtDashDate.setText(date);
        txtDashPeoplesCount.setText(peoplesCount + " Peoples");
        txtDashCirclePacketsCount.setText(totalPackets + "");

        //set bottom margin
        /*LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        params.setMargins(0,0,0,margin);
        view.setLayoutParams(params);*/

        //show dialog on click
        ((TableRow) view.findViewById(R.id.dash_item)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                DialogWeightListOptions dialogWeightListOptions = new DialogWeightListOptions();
                Bundle bundle = new Bundle();
                bundle.putCharSequence("FILE_NAME",fileName);
                bundle.putInt("VIEW_ID",view.getId());
                dialogWeightListOptions.setArguments(bundle);
                dialogWeightListOptions.show(getFragmentManager(),"DialogWeightListOptions");
                return false;
            }
        });

        parentLayout.addView(view);
    }

    void getTextViewReferences(ViewGroup viewGroup) {
        for(int i=0; i < viewGroup.getChildCount(); i++) {
            Object childView = viewGroup.getChildAt(i);
            if(childView instanceof TextView)
                textViews.add((TextView)childView);
            else if(childView instanceof ViewGroup)
                getTextViewReferences((ViewGroup)childView);
        }
    }

    void setCustomFont() {
        textViews = new ArrayList<>();
        getTextViewReferences((ViewGroup)findViewById(R.id.parentLayout));
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/opensans_regular.ttf");
        for(TextView textView: textViews)
            textView.setTypeface(typeface);
    }

    //Dialog callback methods
    @Override
    public void onDialogViewBillButtonClick(int ViewId, String fileName) {
        Intent intent = new Intent(getApplicationContext(),BillViewActivity.class);
        intent.putExtra("FILE_NAME",fileName);
        startActivity(intent);
    }

    @Override
    public void onDialogDeleteWeightListButtonClick(final int ViewId, final String fileName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Boolean result = getApplicationContext().deleteFile(fileName);
                if(result) {
                    Toast.makeText(getApplicationContext(), "Weight List deleted!", Toast.LENGTH_LONG).show();
                    parentLayout.removeAllViews(); //cause removeView ain't working
                    readSavedWeightList();
                }
                else
                    Toast.makeText(getApplicationContext(),"Error deleting weight list!",Toast.LENGTH_LONG).show();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int iad) {
                //Nothing to do...
            }
        });

        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //AppTheme Dialog callback methods
    @Override
    public void onDialogPositiveClick(int selectedTheme) {
        //update sharedPreference
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_theme_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("APP_THEME",selectedTheme);
        editor.apply();

        //setTheme
        userSelectedTheme = selectedTheme;
        setTheme(selectedTheme);
        //recreate activity
        recreate();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        //Nothing to do...
    }

    protected void setUserSelectedTheme() {
        //get theme from shared pref.
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_theme_file), Context.MODE_PRIVATE);
        userSelectedTheme = sharedPref.getInt("APP_THEME",R.style.AppTheme);
        setTheme(userSelectedTheme);
    }
}
