package gao.hzyc.com.im_c.Utils;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.util.ArrayList;
import java.util.List;

import gao.hzyc.com.im_c.Constant;

/**
 * Created by codeforce on 2017/5/29.
 */
public class RobotFilter  {

    private List<EMMessage> msgs;
    private String curUser;
    public RobotFilter(List<EMMessage> msgs,String curUser){
        this.msgs = msgs;
        this.curUser = curUser;
    }

    public List<EMMessage> getMsgs(){
        List<EMMessage> filtmsgs = new ArrayList<EMMessage>();
        for (int i = 0; i < msgs.size(); i++) {
            EMMessage m = msgs.get(i);
            if (m.getType() == EMMessage.Type.TXT){
                EMTextMessageBody body = (EMTextMessageBody) m.getBody();
                String msgText = body.getMessage().toString();
                if (msgText.contains(Constant.ROBOT_SUFFIX) ){
                    filtmsgs.add(m);
                    if (m.getFrom().equals(curUser) && msgs.size()-1>=i+1)
                        filtmsgs.add(msgs.get(i+1));
                    else if (!m.getFrom().equals(curUser) && msgs.size()-1>=i+2)
                        filtmsgs.add(msgs.get(i+2));
                }else if (msgText.contains(Constant.ROBOT_BY) || msgText.contains(Constant.ROBOT_FROM)){

                }else {
                    filtmsgs.add(m);
                }
            }else{
                filtmsgs.add(m);
            }
        }
        return filtmsgs;
    }

}
