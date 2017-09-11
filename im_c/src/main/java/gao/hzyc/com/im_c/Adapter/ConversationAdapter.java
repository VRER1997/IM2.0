package gao.hzyc.com.im_c.Adapter;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;

import gao.hzyc.com.im_c.MyApplication;
import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.Utils.TimeUtils;

/**
 * 布局会话界面的Adaptec
 * Created by codeforce on 2017/5/9.
 */
public class ConversationAdapter extends BaseAdapter {

    private List<EMConversation> conversations;
    private Context context;

    public ConversationAdapter(Context context,List<EMConversation> conversations){

        this.context = context;
        this.conversations = conversations;

    }
    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public Object getItem(int position) {
        return conversations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_conversation,null);
        }else{
            view = convertView;
        }

        ImageView avast = (ImageView) view.findViewById(R.id.avatar);
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView message = (TextView) view.findViewById(R.id.message);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView unread_msg_number = (TextView) view.findViewById(R.id.unread_msg_number);

        EMConversation conversation = (EMConversation) getItem(position);
        avast.setImageResource(R.drawable.logo);
        String curUser = MyApplication.getInstance().getCurrentUserName();
        String from = conversation.getLastMessage().getFrom();
        if(from.equals(curUser)){
            name.setText(conversation.getLastMessage().getTo());
        }else{
            name.setText(from);
        }
        if (conversation.getAllMessages().size() > 0){
            EMMessage LastM = conversation.getLastMessage();
            if (LastM.getType() == EMMessage.Type.TXT){
                EMTextMessageBody body = (EMTextMessageBody) LastM.getBody();
                message.setText(body.getMessage());
            }
            if (LastM.getType() == EMMessage.Type.VOICE){
                message.setText("录音消息");
            }
            if (LastM.getType() == EMMessage.Type.IMAGE){
                message.setText("[图片]");
            }
            String temtime = DateUtils.getTimestampString(new Date(conversation.getLastMessage().getMsgTime()));
            time.setText(TimeUtils.progressTiem(temtime));
        }else{
            time.setVisibility(View.INVISIBLE);
        }
        /*if (conversation.getUnreadMsgCount() > 0){
            unread_msg_number.setText(conversation.getUnreadMsgCount());
        }else {
            unread_msg_number.setVisibility(View.INVISIBLE);
        }*/
        unread_msg_number.setVisibility(View.GONE);
        return view;
    }
}
