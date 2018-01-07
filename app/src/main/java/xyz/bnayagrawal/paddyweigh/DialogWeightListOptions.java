package xyz.bnayagrawal.paddyweigh;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by binay on 6/1/18.
 */

public class DialogWeightListOptions extends DialogFragment {

    private WeightListOptionsDialogListener mListener;
    private String fileName;
    private int viewId;

    public interface WeightListOptionsDialogListener {
        public void onDialogViewBillButtonClick(int ViewId, String fileName);
        public void onDialogDeleteWeightListButtonClick(int ViewId, String fileName);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (WeightListOptionsDialogListener) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString()
                    + " must implement WeightListOptionsDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        fileName = bundle.getString("FILE_NAME");
        viewId = bundle.getInt("VIEW_ID");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle("Weight List");
        View view = inflater.inflate(R.layout.dialog_weight_list_options,null);

        //View Bill button click listener
        ((Button) view.findViewById(R.id.btnDialogViewBill)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onDialogViewBillButtonClick(viewId, fileName);
                dismiss();
            }
        });

        //Delete Weight List button click listener
        ((Button) view.findViewById(R.id.btnDialogDeleteList)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mListener.onDialogDeleteWeightListButtonClick(viewId, fileName);
                dismiss();
            }
        });
        
        builder.setView(view);
        return builder.create();
    }
}
