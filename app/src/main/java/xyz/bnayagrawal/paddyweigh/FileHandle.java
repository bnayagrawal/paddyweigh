package xyz.bnayagrawal.paddyweigh;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by binay on 6/1/18.
 */

public class FileHandle {
    public boolean writeToFile(Context context,String file_name,String data) {
        boolean res = false;
        try {
            FileOutputStream fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
            res = true;
        }
        catch (IOException ioe) {
            Log.e("File write error",ioe.getMessage());
        }
        return res;
    }

    public String readFromFile(Context context,String file_name) {
        String data = "";
        try {
            FileInputStream fis = context.openFileInput(file_name);
            int bytesToRead = fis.available();
            byte[] bytes = new byte[bytesToRead];

            fis.read(bytes);
            for(byte b:bytes) {
                data += (char)b;
            }
            fis.close();
            Log.i("Data read from file",data);
        }
        catch (IOException ioe) {
            data = null;
            Log.e("File read error",ioe.getMessage());
        }
        return data;
    }
}
