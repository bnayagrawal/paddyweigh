package xyz.bnayagrawal.paddyweigh;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by binay on 7/1/18.
 */

public class DialogAppTheme extends DialogFragment {

    private AppThemeDialogListener mListener;
    private ArrayList<ImageView> imageViews;
    private HashMap<Integer,Integer> imageThemeMap;
    private LinearLayout layoutColorListContainer;
    private int selectedTheme;
    private int checkedImageViewId;

    public interface AppThemeDialogListener {
        public void onDialogPositiveClick(int selectedTheme);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    void loadImageThemeMap() {
        imageThemeMap = new HashMap<>();
        imageThemeMap.put(R.id.imgColorChocolate,R.style.AppTheme);
        imageThemeMap.put(R.id.imgColorLightBlue,R.style.AppThemeLightBlue);
        imageThemeMap.put(R.id.imgColorGreen,R.style.AppThemeGreen);
        imageThemeMap.put(R.id.imgColorOrange,R.style.AppThemeOrange);
        imageThemeMap.put(R.id.imgColorBlueGrey,R.style.AppThemeBlueGrey);
        imageThemeMap.put(R.id.imgColorDarkIndigo,R.style.AppThemeDarkIndigo);
    }

    int getCheckedImageViewId(int selectedTheme) {
        int viewId = R.id.imgColorChocolate;
        if(imageThemeMap.containsValue(selectedTheme)) {
            //Not suitable, But we know that resource id's are unique.
            for(Map.Entry<Integer,Integer> entry: imageThemeMap.entrySet())
                if(entry.getValue() == selectedTheme) {
                    viewId = entry.getKey();
                    break;
                }
        }
        return viewId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        imageViews = new ArrayList<>();
        try {
            mListener = (AppThemeDialogListener) activity;
        } catch (ClassCastException cce) {
            throw new ClassCastException(activity.toString()
                    + " must implement AppThemeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //get reference of currently applied theme
        Bundle bundle = getArguments();
        selectedTheme = bundle.getInt("APP_THEME");
        loadImageThemeMap();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle("Select a theme");
        View view = inflater.inflate(R.layout.dialog_app_theme,null);
        layoutColorListContainer = view.findViewById(R.id.layoutColorListContainer);
        getViewReferences(layoutColorListContainer);

        for(ImageView imageView: imageViews) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(imageThemeMap.containsKey(view.getId())) {
                        selectedTheme = imageThemeMap.get(view.getId());
                        toggleCheckedImageView(view.getId());
                    }
                }
            });
        }

        toggleCheckedImageView(getCheckedImageViewId(selectedTheme));
        builder.setView(view)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(selectedTheme);
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(DialogAppTheme.this);
                    }
                });

        return builder.create();
    }

    void getViewReferences(ViewGroup viewGroup) {
        for(int i=0; i < viewGroup.getChildCount(); i++) {
            Object childView = viewGroup.getChildAt(i);
            if(childView instanceof ImageView)
                imageViews.add((ImageView) childView);
            else if(childView instanceof ViewGroup)
                getViewReferences((ViewGroup)childView);
        }
    }


    void toggleCheckedImageView(int uncheckedImageViewId) {
        //Hide check mark of previously selected theme
        ImageView imageView = layoutColorListContainer.findViewById(checkedImageViewId);
        if(null != imageView)
            imageView.setImageDrawable(null);

        //Show check mark of currently selected theme
        imageView = layoutColorListContainer.findViewById(uncheckedImageViewId);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white_24dp));
        this.checkedImageViewId = uncheckedImageViewId;
    }
}
