package bluen.homein.gayo_security.preference;

import android.content.Context;
import android.content.SharedPreferences;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bluen.homein.gayo_security.rest.RequestDataFormat;
import bluen.homein.gayo_security.rest.ResponseDataFormat;


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

    public void setPatrolMode(boolean _value) {
        putBoolean(PrefKey.PATROL_MODE, _value);
    }

    public boolean getPatrolMode() {
        return getBoolean(PrefKey.PATROL_MODE, false);

    }


    // 밝기 저장
    public void setBrightness(float brightness) {
        putFloat(PrefKey.KEY_BRIGHTNESS, brightness);
    }

    public float getBrightness() {
        return getFloat(PrefKey.KEY_BRIGHTNESS, 1.0f);
    }

    public void setPatrolDoorOpenType(int _value) {
        putInteger(PrefKey.PATROL_DOOR_OPEN_TYPE, _value);
    }

    public int getPatrolDoorOpenType() {
        return getInteger(PrefKey.PATROL_DOOR_OPEN_TYPE, 0);

    }

    public void setDevicePassword(String _value) {
        putString(PrefKey.DEVICE_PASSWORD, _value);
    }

    public String getDevicePassword() {
        return getString(PrefKey.DEVICE_PASSWORD, "0000");

    }

    public String getFirebaseToken() {
        return getString(PrefKey.FIREBASE_TOKEN, null);

    }

    public void setFirebaseToken(String _value) {
        putString(PrefKey.FIREBASE_TOKEN, _value);
    }

    //----------------------------------------- Data Get

    public String getString(String key, String _defValue) {
        return mPrefs.getString(key, _defValue);
    }

    public int getInteger(String key, Integer _defValue) {
        return mPrefs.getInt(key, _defValue);
    }

    public float getFloat(String key, Float _defValue) {
        return mPrefs.getFloat(key, _defValue);
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

    public void putFloat(String key, Float _defValue) {
        synchronized (context) {
            mPrefs.edit().putFloat(key, _defValue).commit();
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

    public void setContactsList(List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo> list) {

        // List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo> 직렬화하기
        Gson gson = new Gson();
        String serializedList = gson.toJson(list);

        putString(PrefKey.FACILITY_CONTACTS, serializedList);

    }

    public List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo> getContactsList() {
        Gson gson = new Gson();
        String serializedList = getString(PrefKey.FACILITY_CONTACTS, null);
        Type type = new TypeToken<List<ResponseDataFormat.FacilityContactListBody.FacilityContactInfo>>() {
        }.getType();
        return gson.fromJson(serializedList, type);

    }

    public void setAllDeviceList(List<RequestDataFormat.DeviceNetworkBody> list) {

        Gson gson = new Gson();
        String serializedList = gson.toJson(list);

        putString(PrefKey.ALL_DEVICE_LIST, serializedList);

    }

    public List<RequestDataFormat.DeviceNetworkBody> getAllDeviceList() {
        Gson gson = new Gson();
        String serializedList = getString(PrefKey.ALL_DEVICE_LIST, null);
        Type type = new TypeToken<List<RequestDataFormat.DeviceNetworkBody>>() {
        }.getType();
        return gson.fromJson(serializedList, type);

    }

    //--------------------------------------------------- Class Get & Put

//    public static class PrefDeviceData {
//        public static final String PREF_DEVICE_KEY = "device_data_key";
//        public static final String PREF_DEVICE_VALUE = "device_data_value";
//
//        public static RequestDataFormat.DeviceBody prefItem;
//
//        public static void setPrefDeviceData(Context _context, RequestDataFormat.DeviceBody _prefItem) {
//            _context.getSharedPreferences(PREF_DEVICE_KEY, Context.MODE_PRIVATE).edit()
//                    .putString(PREF_DEVICE_VALUE, new Gson().toJson(_prefItem, RequestDataFormat.DeviceBody.class)).commit();
//            prefItem = getDeviceData(_context);
//        }
//
//        public static RequestDataFormat.DeviceBody getDeviceData(Context _context) {
//            return new Gson().fromJson(_context.getSharedPreferences(PREF_DEVICE_KEY, Context.MODE_PRIVATE)
//                    .getString(PREF_DEVICE_VALUE, ""), RequestDataFormat.DeviceBody.class);
//        }
//
//    }

    public static class PrefDeviceData {
        public static final String PREF_DEVICE_KEY = "device_data_key";
        public static final String PREF_DEVICE_VALUE = "device_data_value";

        public static RequestDataFormat.DeviceBody prefItem;

        public static void setPrefDeviceData(Context _context, RequestDataFormat.DeviceBody _prefItem) {
            String json = new Gson().toJson(_prefItem, RequestDataFormat.DeviceBody.class);
            _context.getSharedPreferences(PREF_DEVICE_KEY, Context.MODE_PRIVATE).edit()
                    .putString(PREF_DEVICE_VALUE, json)
                    .apply();

            prefItem = _prefItem;
        }

        public static RequestDataFormat.DeviceBody getDeviceData(Context _context) {
            String json = _context.getSharedPreferences(PREF_DEVICE_KEY, Context.MODE_PRIVATE)
                    .getString(PREF_DEVICE_VALUE, null);

            if (json == null || json.isEmpty()) {
                return null;
            }

            try {
                return new Gson().fromJson(json, RequestDataFormat.DeviceBody.class);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        // 앱 시작 시 초기화 호출 (필수!)
        public static void initPrefDeviceData(Context context) {
            prefItem = getDeviceData(context);
        }
    }

    //---------------------------------------- User Info


    static private class PrefKey {

        private static final String AUTHORIZATION = "authorization";
        private static final String PATROL_MODE = "patrol_mode";
        private static final String PATROL_DOOR_OPEN_TYPE = "patrol_door_open_type";
        private static final String FIREBASE_TOKEN = "firebase_token";
        private static final String FACILITY_CONTACTS = "facility_contacts";
        private static final String ALL_DEVICE_LIST = "all_device_list";
        private static final String DEVICE_PASSWORD = "device_password";
        private static final String PREF_NAME = "app_brightness_pref";
        private static final String KEY_BRIGHTNESS = "key_brightness";

    }

}
