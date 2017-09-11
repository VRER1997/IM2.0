package gao.hzyc.com.im_c.db;

import android.content.ContentValues;
import android.content.pm.LabeledIntent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.IvParameterSpec;

import gao.hzyc.com.im_c.MyApplication;

/**
 * Created by codeforce on 2017/5/6.
 */
public class DBmanager {

    private DbOpenHelper helper;
    private static DBmanager manager;

    //获取 单例模式
    public DBmanager(){
        helper = DbOpenHelper.getInstance(MyApplication.getInstance().getApplicationContext());
    }
    public static DBmanager getInstance(){
        if (manager == null){
            manager = new DBmanager();
        }
        return manager;
    }

    /**
     *  联系人操作
     */

    //保存联系人
    synchronized public void saveContact(User user){
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserDao.COLUMN_NAME_ID,user.getName());
        if (user.getAvast()!=null){
            values.put(UserDao.COLUMN_NAME_AVATAR,user.getAvast());
        }else{
            values.put(UserDao.COLUMN_NAME_AVATAR,"");
        }
        if(database.isOpen()){
            database.replace(UserDao.TABLE_NAME, null, values);
        }
    }

    // 保存联系人列表
    synchronized public void saveContactList(List<User> contactList){
        SQLiteDatabase database = helper.getWritableDatabase();
        //ContentValues values = new ContentValues();
        database.delete(UserDao.TABLE_NAME, null, null);
        for(User user : contactList){
            saveContact(user);
        }
        database.close();
    }

    //获取联系人列表
    synchronized public Map<String, User> getContactList(){
        SQLiteDatabase database = helper.getWritableDatabase();
        Map<String, User> users = new Hashtable<String, User>();
        if (database.isOpen()){
            Cursor cursor = database.rawQuery("select * from "+UserDao.TABLE_NAME, null);
            while(cursor.moveToNext()){
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                //String avast = cursor.getColumnName(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String avast = "";
                User user = new User();
                user.setName(username);
                user.setAvast(avast);
                Log.i("message",user.toString());
                users.put(username,user);
            }
            cursor.close();
            database.close();
        }
        return users;
    }

    /**
     *  信息操作
     */
    //保存信息 并且返回储存的id
    synchronized public Integer saveInvitedMsg(InviteMsg msg){
        SQLiteDatabase database = helper.getWritableDatabase();
        int result = -1;
        if (database.isOpen()){
            ContentValues values = new ContentValues();
            values.put(InvitedMsgDao.COLUMN_NAME_FROM,msg.getFrom());
            values.put(InvitedMsgDao.COLUMN_NAME_ID,msg.getId());
            values.put(InvitedMsgDao.COLUMN_NAME_REASON,msg.getReason());
            values.put(InvitedMsgDao.COLUMN_NAME_STATUS,msg.getState().ordinal());
            values.put(InvitedMsgDao.COLUMN_NAME_TIME,msg.getTime());


            database.insert(InvitedMsgDao.TABLE_NAME,null,values);

            Cursor cursor = database.rawQuery("select last_insert_rowid() from " + InvitedMsgDao.TABLE_NAME,null);
            if(cursor.moveToFirst()){
                result = cursor.getInt(0);
            }
            cursor.close();
        }
        return result;
    }

    //获取信息列表
    synchronized public List<InviteMsg> getMessagesList(){
        SQLiteDatabase database = helper.getWritableDatabase();
        List<InviteMsg> MsgList = new ArrayList<InviteMsg>();
        if (database.isOpen()){

            Cursor cursor = database.rawQuery("select * from " + InvitedMsgDao.TABLE_NAME,null);

            while (cursor.moveToNext()){
                int id = cursor.getInt(cursor.getColumnIndex(InvitedMsgDao.COLUMN_NAME_ID));
                String from = cursor.getString(cursor.getColumnIndex(InvitedMsgDao.COLUMN_NAME_FROM));
                long time = cursor.getLong(cursor.getColumnIndex(InvitedMsgDao.COLUMN_NAME_TIME));
                String reason = cursor.getString(cursor.getColumnIndex(InvitedMsgDao.COLUMN_NAME_REASON));
                int state = cursor.getInt(cursor.getColumnIndex(InvitedMsgDao.COLUMN_NAME_STATUS));

                InviteMsg msg = new InviteMsg();
                msg.setTime(time);
                msg.setReason(reason);
                msg.setFrom(from);
                msg.setId(id);
                if (state == InviteMsg.MsgState.BEINVITEED.ordinal()){
                    msg.setState(InviteMsg.MsgState.BEINVITEED);
                }
                if (state == InviteMsg.MsgState.BEREFUSED.ordinal()){
                    msg.setState(InviteMsg.MsgState.BEREFUSED);
                }
                if (state == InviteMsg.MsgState.BEAGREED.ordinal()){
                    msg.setState(InviteMsg.MsgState.BEAGREED);
                }
                MsgList.add(msg);
                /*switch (state){
                    case InviteMsg.MsgState.BEINVITEED.ordinal():
                        break;
                    case InviteMsg.MsgState.BEREFUSED.ordinal():
                        break;
                    case InviteMsg.MsgState.BEAGREED.ordinal():
                        break;
                }*/
            }
            cursor.close();
        }
        return MsgList;
    }

    //更信息的状态
    synchronized public void updateMessage(int msgId,ContentValues values){
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()){
            db.update(InvitedMsgDao.TABLE_NAME, values, InvitedMsgDao.COLUMN_NAME_ID + " = ?", new String[]{String.valueOf(msgId)});
        }
    }

    //获取未读的信息数
    synchronized int getUnreadNotifyCount(){
        int count = 0;
        SQLiteDatabase db = helper.getReadableDatabase();
        if(db.isOpen()){
            Cursor cursor = db.rawQuery("select " + InvitedMsgDao.COLUMN_NAME_UNREAD_MSG_COUNT + " from " + InvitedMsgDao.TABLE_NAME, null);
            if(cursor.moveToFirst()){
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }
    //设置未读信息数
    synchronized void setUnreadNotifyCount(int count){
        SQLiteDatabase db = helper.getWritableDatabase();
        if(db.isOpen()){
            ContentValues values = new ContentValues();
            values.put(InvitedMsgDao.COLUMN_NAME_UNREAD_MSG_COUNT, count);

            db.update(InvitedMsgDao.TABLE_NAME, values, null,null);
        }
    }

    //关闭资源
    synchronized public void closeDB(){
        helper.closeDB();
    }
}
