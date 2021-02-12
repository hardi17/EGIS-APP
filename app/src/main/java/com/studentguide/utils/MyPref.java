package com.studentguide.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Sachin on 26/4/19.
 * Prismetric Technology, Gandhinagar, Gujarat
 */
public class MyPref {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public MyPref(Context context) {
        preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        editor = preferences.edit();
    }

  /*  public void setUserData(User userData) {
        editor.putString(Keys.UserData.name(), new Gson().toJson(userData));
        editor.commit();
    }


    public User getUserData() {
        String userData = preferences.getString(Keys.UserData.name(), "");
        if (!StringUtils.isNotEmpty(userData))
            return new User();
        User user = new Gson().fromJson(userData, User.class);
        if (user == null) return new User();
        return user;
    }
*/

    public void setData(Keys keys, boolean isData) {
        editor.putBoolean(keys.name(), isData);
        editor.commit();
    }

    public void setData(Keys keys, String isData) {
        editor.putString(keys.name(), isData);
        editor.commit();
    }

    public void setData(Keys keys, Integer isData) {
        editor.putInt(keys.name(), isData);
        editor.commit();
    }

    public void clearPrefs() {
        editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public String getData(Keys keys) {
        return preferences.getString(keys.name(), "");
    }

    public boolean getData(Keys keys, boolean defaults) {
        return preferences.getBoolean(keys.name(), defaults);
    }

    public Integer getData(Keys keys, Integer defaults) {
        return preferences.getInt(keys.name(), defaults);
    }

    public enum Keys {
        Lat,
        Lng,
        Location,
        Score
    }
}