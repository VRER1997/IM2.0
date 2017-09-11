package gao.hzyc.com.im_c.db;

import android.content.ContentValues;
import android.content.Context;

import java.util.List;

/**
 * 邀请信息的方法类
 * Created by codeforce on 2017/5/6.
 */
public class InvitedMsgDao {

    public static final String TABLE_NAME = "new_friends_msgs";
    static final String COLUMN_NAME_ID = "id";
    static final String COLUMN_NAME_FROM = "username";
    static final String COLUMN_NAME_TIME = "time";
    static final String COLUMN_NAME_REASON = "reason";
    public static final String COLUMN_NAME_STATUS = "status";

    static final String COLUMN_NAME_UNREAD_MSG_COUNT = "unreadMsgCount";

    public InvitedMsgDao(Context context){

    }

    public Integer saveInvitedMsg(InviteMsg msg){
        return DBmanager.getInstance().saveInvitedMsg(msg);
    }

    public List<InviteMsg> getMessagesList(){
        return DBmanager.getInstance().getMessagesList();
    }

    public void updateMessage(int msgId,ContentValues values){
        DBmanager.getInstance().updateMessage(msgId,values);
    }

    public int getUnreadMessagesCount(){
        return DBmanager.getInstance().getUnreadNotifyCount();
    }

    public void saveUnreadMessageCount(int count){
        DBmanager.getInstance().setUnreadNotifyCount(count);
    }
}
