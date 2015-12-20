package com.malikapps.trakerr.DA;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by abdulmalik_khan on 29/07/15.
 */
public class MyPreferances {
    public static final String KeyName = "KeyName";
    public static final String KeyPhone = "KeyPhone";
    public static final String KeyId = "KeyId";
    private static final String MyPREFERENCES = "MyPreferances" ;

    public static String getValue(Context context, String key)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String value = sharedpreferences.getString(key, "");
        return value;
    }

    public static void setValue(Context context, String key, String value)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        // temp code start
        editor.remove(key);
        editor.commit();
        // temp code end
        editor.putString(key, value);
        editor.commit();
        sharedpreferences.getAll();
    }
    public static void setNamePhone(Context context, String name, String phone, String id)
    {
        MyPreferances.setValue(context, MyPreferances.KeyName, name);
        MyPreferances.setValue(context, MyPreferances.KeyPhone, phone);
        MyPreferances.setValue(context, MyPreferances.KeyId, id);
    }
}
