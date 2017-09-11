package gao.hzyc.com.im_c.Utils;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import gao.hzyc.com.im_c.MyApplication;

/**
 * Created by codeforce on 2017/5/24.
 */
public class MessageUtils {

    public String getFrom(EMConversation conversation){
        String curUser = MyApplication.getInstance().getCurrentUserName();
        List<EMMessage> msgs = conversation.getAllMessages();
        for (EMMessage m: msgs  ) {
            if (!curUser.equals(m.getFrom()))
                return m.getFrom();
        }
        return "";
    }
}
