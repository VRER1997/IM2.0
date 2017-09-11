package gao.hzyc.com.im_c;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import gao.hzyc.com.im_c.Adapter.ConversationAdapter;
import gao.hzyc.com.im_c.Adapter.MessageAdapter;

public class ChatActivity extends AppCompatActivity{

    private String toChatUsername;
    private TextView toUsername;
    private EditText et_content;
    private ListView listView;
    private Button btn_send;

    private EMConversation conversation;
    protected int pagesize = 20;
    private List<EMMessage> msgs;
    private MessageAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toChatUsername = this.getIntent().getStringExtra("name");
        toUsername = (TextView) findViewById(R.id.tv_toUsername);
        listView = (ListView) findViewById(R.id.listView);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_content = (EditText) findViewById(R.id.et_content);

        toUsername.setText(toChatUsername);

        getMessage();

        msgs = conversation.getAllMessages();
        adapter = new MessageAdapter(ChatActivity.this,msgs);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send_msg = et_content.getText().toString().trim();
                if (TextUtils.isEmpty(send_msg)) return;
                // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
                EMMessage message = EMMessage.createTxtSendMessage(send_msg, toChatUsername);
                // 发送消息
                EMClient.getInstance().chatManager().sendMessage(message);
                msgs.add(message);
                //notifyDataSetChanged方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
                adapter.notifyDataSetChanged();
                if (msgs.size() > 0) {
                    listView.setSelection(listView.getCount() - 1);
                }
                et_content.setText("");
                //et_content.clearFocus();
            }
        });
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                for (EMMessage message : list) {
                    String username = message.getFrom();
                    // 如果是当前会话的消息，刷新聊天页面
                    if (username.equals(toChatUsername)) {
                        msgs.addAll(list);
                        //msgs = conversation.getAllMessages();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        if (msgs.size() > 0) {
                            et_content.setSelection(listView.getCount() - 1);
                        }
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> list) {

            }

            @Override
            public void onMessageRead(List<EMMessage> list) {

            }

            @Override
            public void onMessageDelivered(List<EMMessage> list) {

            }

            @Override
            public void onMessageChanged(EMMessage emMessage, Object o) {

            }
        });
    }

    public void getMessage(){
        // 获取当前conversation对象
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
                EMConversation.EMConversationType.Chat, true);
        // 把此会话的未读数置为0
        conversation.markAllMessagesAsRead();
        // 初始化db时，每个conversation加载数目是getChatOptions().getNumberOfMessagesLoaded
        // 这个数目如果比用户期望进入会话界面时显示的个数不一样，就多加载一些
        final List<EMMessage> msgs = conversation.getAllMessages();
        int msgCount = msgs != null ? msgs.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
            String msgId = null;
            if (msgs != null && msgs.size() > 0) {
                msgId = msgs.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
        }
    }
}
