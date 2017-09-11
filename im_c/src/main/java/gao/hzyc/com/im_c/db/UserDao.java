package gao.hzyc.com.im_c.db;

import android.content.Context;

import java.util.List;
import java.util.Map;

/**
 * User的实体类
 * Created by codeforce on 2017/5/6.
 */
public class UserDao {

    public static final String TABLE_NAME = "uers";
    public static final String COLUMN_NAME_ID = "username";
    public static final String COLUMN_NAME_AVATAR = "avatar";

    public UserDao(Context context){

    }

    /**
     * 保存联系人
     * @param user
     */
    public void saveContact(User user){
        DBmanager.getInstance().saveContact(user);
    }

    /**
     * 保存联系人列表
     * @param contactList
     */
    public void saveContactList(List<User> contactList){
        DBmanager.getInstance().saveContactList(contactList);
    }

    /**
     * 获取联系人列表
     * @return
     */
    public Map<String, User> getContactList(){
        return DBmanager.getInstance().getContactList();
    }

}
