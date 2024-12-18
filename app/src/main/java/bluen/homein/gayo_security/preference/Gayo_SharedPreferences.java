package bluen.homein.gayo_security.preference;

import android.content.Context;
import android.content.SharedPreferences;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;



public class Gayo_SharedPreferences {

    private Context context = null;

    public SharedPreferences mPrefs = null;

    public Gayo_SharedPreferences(Context context, String info) {
        this.context = context;
        this.mPrefs = context.getSharedPreferences(info, Context.MODE_PRIVATE);
    }


    //----------------------------------------- auth

    public void setAuthorization(String _value) {
        putString(PrefKey.AUTHORIZATION, _value);
    }

    public String getAuthorization() {
        return getString(PrefKey.AUTHORIZATION, null);

    }

    //----------------------------------------- Data Get

    public String getString(String key, String _defValue) {
        return mPrefs.getString(key, _defValue);
    }

    public int getInteger(String key, Integer _defValue) {
        return mPrefs.getInt(key, _defValue);
    }

    public boolean getBoolean(String key, boolean _defValue) {
        return mPrefs.getBoolean(key, _defValue);
    }

    public long getLong(String key, long _defValue) {
        return mPrefs.getLong(key, _defValue);
    }

    public Map<Integer, Boolean> getBooleanList(String key, Map<Integer, Boolean> _defValue) {
        Map<Integer, Boolean> getMap = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(mPrefs.getString(key, _defValue.toString()));
            Iterator<String> keys = jsonObject.keys();
            int index = 0;
            while (keys.hasNext()) {
                getMap.put(index++, jsonObject.getBoolean(keys.next()));
            }
        } catch (ClassCastException | JSONException e) {
            e.printStackTrace();
        }

        return getMap;
    }

    public Map<Integer, Integer> getIntegerList(String key, Map<Integer, Integer> _defValue) {
        Map<Integer, Integer> getMap = new HashMap<>();
        try {
            JSONObject jsonObject = new JSONObject(mPrefs.getString(key, _defValue.toString()));
            Iterator<String> keys = jsonObject.keys();
            int index = 0;
            while (keys.hasNext()) {
                getMap.put(index++, jsonObject.getInt(keys.next()));
            }
        } catch (ClassCastException | JSONException e) {
            e.printStackTrace();
        }
        return getMap;
    }

    //----------------------------------------- Data Put

    public void putString(String key, String _defValue) {
        synchronized (context) {
            mPrefs.edit().putString(key, _defValue).commit();
        }
    }

    public void putInteger(String key, Integer _defValue) {
        synchronized (context) {
            mPrefs.edit().putInt(key, _defValue).commit();
        }
    }

    public void putBoolean(String key, boolean _defValue) {
        synchronized (context) {
            mPrefs.edit().putBoolean(key, _defValue).apply();
        }
    }

    public void putLong(String key, long _defValue) {
        synchronized (context) {
            mPrefs.edit().putLong(key, _defValue).commit();
        }
    }

    public void putBooleanList(String key, Map<Integer, Boolean> _defValue) {
        ObjectMapper mapper = new ObjectMapper();
        synchronized (context) {
            if (!_defValue.isEmpty()) {
                try {
                    String json = mapper.writeValueAsString(_defValue);
                    mPrefs.edit().putString(key, json).commit();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                mPrefs.edit().putString(key, null).commit();
            }
        }
    }

    public void putIntegerList(String key, Map<Integer, Integer> _defValue) {
        ObjectMapper mapper = new ObjectMapper();
        synchronized (context) {
            if (!_defValue.isEmpty()) {
                try {
                    String json = mapper.writeValueAsString(_defValue);
                    mPrefs.edit().putString(key, json).commit();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                mPrefs.edit().putString(key, null).commit();
            }
        }
    }

    public void removeAll() {
        synchronized (context) {
            mPrefs.edit().clear().commit();
        }
    }


    //--------------------------------------------------- Class Get & Put


    //---------------------------------------- User Info



    static private class PrefKey {

        static final private String AUTHORIZATION = "authorization";

    }

}
