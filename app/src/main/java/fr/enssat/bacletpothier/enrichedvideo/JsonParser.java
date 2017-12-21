/**
 * application EnrichedVideo
 * module Parser JSON
 * authors F. Pothier, A.C. Baclet
 */
package fr.enssat.bacletpothier.enrichedvideo;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;


public class JsonParser {

    public static LinkedHashMap<Integer,String> parseDatas(InputStream input, String name, String elt1, String elt2){
        Log.d("parseDatas","start - name="+name+" -elt1="+elt1+" -elt2="+elt2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        LinkedHashMap<Integer,String> datas;


        datas = new LinkedHashMap<>();
        int ctr;
        try {
            ctr = input.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = input.read();
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jObject = new JSONObject(byteArrayOutputStream.toString());
            JSONArray jArray = jObject.getJSONArray(name);
            int pos = 0;
            String str = "";
            for (int i = 0; i < jArray.length(); i++) {
                pos = jArray.getJSONObject(i).getInt(elt1);
                str = jArray.getJSONObject(i).getString(elt2);
                datas.put(pos,str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }


}

