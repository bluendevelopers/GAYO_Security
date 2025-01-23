package bluen.homein.gayo_security.rest;

import com.google.gson.Gson;

public class MyGson {
    @Override
    public String toString() {
        return new Gson().toJson(this, this.getClass());
    }
}
