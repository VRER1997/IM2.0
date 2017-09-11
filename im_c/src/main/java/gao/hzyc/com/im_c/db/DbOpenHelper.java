package gao.hzyc.com.im_c.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import gao.hzyc.com.im_c.MyApplication;

/**
 * Created by codeforce on 2017/5/6.
 */
public class DbOpenHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 6;
    private static DbOpenHelper instance;


    public DbOpenHelper(Context context){
         super(context,getUserDatabaseName(),null,DATABASE_VERSION);
    }

    //单例模式
    public static DbOpenHelper getInstance(Context context){
        if (instance == null){
            instance = new DbOpenHelper(context.getApplicationContext());
        }
        Log.i("message","DbHelper_state"+(instance == null));
        return instance;
    }

    //获取数据库的名字
    private static String getUserDatabaseName() {
        return  MyApplication.getInstance().getCurrentUserName() + "_demo.db";
    }

    //拼写User 的数据库 sql语句
    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            //+ UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";
    //拼写 INIVTE_MESSAG 的数据库 sql语句
    private static final String INIVTE_MESSAGE_TABLE_CREATE = "CREATE TABLE "
            + InvitedMsgDao.TABLE_NAME + " ("
            + InvitedMsgDao.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + InvitedMsgDao.COLUMN_NAME_FROM + " TEXT, "
            + InvitedMsgDao.COLUMN_NAME_REASON + " TEXT, "
            + InvitedMsgDao.COLUMN_NAME_STATUS + " INTEGER, "
            + InvitedMsgDao.COLUMN_NAME_UNREAD_MSG_COUNT + " INTEGER, "
            + InvitedMsgDao.COLUMN_NAME_TIME +  " TEXT); ";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(INIVTE_MESSAGE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void closeDB(){
        if (instance != null){
            SQLiteDatabase sqLiteDatabase = instance.getWritableDatabase();
            sqLiteDatabase.close();
        }
    }
}
