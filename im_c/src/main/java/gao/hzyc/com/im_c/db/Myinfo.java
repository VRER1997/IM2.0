package gao.hzyc.com.im_c.db;

import android.content.Context;
import android.content.SharedPreferences;

public class Myinfo {

    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "local_userinfo";
    private static SharedPreferences mSharedPreferences;
    private static Myinfo mPreferenceUtils;
    private static SharedPreferences.Editor editor;

    private Myinfo(Context context) {
        mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    }

    /**
     * 单例模式，获取instance实例
     *
     */
    public static Myinfo getInstance(Context context) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new Myinfo(context);
        }
        editor = mSharedPreferences.edit();
        return mPreferenceUtils;
    }

    //数据库 保存键值对
    public void setUserInfo(String str_name, String str_value) {

        editor.putString(str_name, str_value);
        editor.commit();
    }
    //数据库 查询
    public String getUserInfo(String str_name) {

        return mSharedPreferences.getString(str_name, "");

    }

}
