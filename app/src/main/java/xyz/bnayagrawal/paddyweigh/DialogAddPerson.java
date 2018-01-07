package xyz.bnayagrawal.paddyweigh;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by binay on 1/1/18.
 */

public class DialogAddPerson extends DialogFragment {

    private AddPersonDialogListener mListener;

    public interface AddPersonDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (AddPersonDialogListener) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddPersonDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle("Add Person");
        View view = inflater.inflate(R.layout.dialog_add_person,null);
        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(DialogAddPerson.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(DialogAddPerson.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        final AlertDialog alertDialog = (AlertDialog) getDialog();
        if(null != alertDialog) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //Prevent closing of dialog before entering a invalid name
                            EditText editText = getDialog().findViewById(R.id.editTextPersonName);
                            String name = editText.getText().toString();
                            if(name.length() < 1) {
                                editText.setError("Please enter a name!");
                            }
                            else {
                                alertDialog.dismiss();
                                mListener.onDialogPositiveClick(DialogAddPerson.this);
                            }
                        }
                    }
            );
        }
    }

    //No use so far...
    public void setViewMargin(View view,int margin) {
        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, margin, getResources()
                        .getDisplayMetrics());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(marginInDp, marginInDp, marginInDp,marginInDp);
        view.setLayoutParams(params);
    }
}
