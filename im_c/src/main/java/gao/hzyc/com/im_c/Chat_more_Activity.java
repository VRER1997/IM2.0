package gao.hzyc.com.im_c;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import gao.hzyc.com.im_c.Adapter.MessageAdapter;
import gao.hzyc.com.im_c.Utils.RobotFilter;
import gao.hzyc.com.im_c.fragment.Fragment_mic;
import gao.hzyc.com.im_c.fragment.Fragment_mic.CallBackValue;
import gao.hzyc.com.im_c.fragment.Fragment_more;
import gao.hzyc.com.im_c.fragment.Fragment_photo;

public class Chat_more_Activity extends AppCompatActivity implements CallBackValue, Fragment_photo.ImageSender {

    private String toChatUsername;
    private TextView toUsername;
    private ListView listView;

    private EMConversation conversation;
    protected int pagesize = 20;
    private List<EMMessage> msgs;
    private MessageAdapter adapter;

    private LinearLayout mLinearLayout;
    private EditText input;
    private ImageView send, mic, photo, camera, back;

    private FragmentManager fm;
    private FragmentTransaction ft;
    private Context context;

    private String Micmsg = "";
    private String OldText = "";
    private String imagePath = "";

    /*public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
            //发送ROBOT消息
            EMMessage message = EMMessage.createTxtSendMessage(val+Constant.ROBOT_FROM, toChatUsername);
            // 发送消息
            EMClient.getInstance().chatManager().sendMessage(message);
        }
    };*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_more_);

        AppManager.addActivity(this);

        toChatUsername = this.getIntent().getStringExtra("name");
        toUsername = (TextView) findViewById(R.id.tv_toUsername);
        listView = (ListView) findViewById(R.id.chat_list);

        context = this;
        mLinearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        input = (EditText) findViewById(R.id.aurora_et_chat_input);
        mic = (ImageView) findViewById(R.id.mic);
        photo = (ImageView) findViewById(R.id.photo);
        camera = (ImageView) findViewById(R.id.camera);
        send = (ImageView) findViewById(R.id.send);
        back = (ImageView) findViewById(R.id.back);

        fm = getFragmentManager();

        toUsername.setText(toChatUsername);
        getMessage();
        msgs = conversation.getAllMessages();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppManager.finishCurrentActivity();
            }
        });

        RobotFilter filter = new RobotFilter(msgs, MyApplication.getInstance().getCurrentUserName());
        msgs = filter.getMsgs();
        adapter = new MessageAdapter(Chat_more_Activity.this, msgs);
        listView.setAdapter(adapter);
        listView.setSelection(listView.getCount() - 1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("message", "@@@@@@@@@@@单击空白处");
                //单击空白处 收起键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
            }
        });
        input.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                setmLinearLayout(0);
                Log.i("Message", "@@@@正在点击输入框");
                input.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {

                            String text = input.getText().toString();
                            Log.i("Message", "正在监听.." + text);
                            if (text.length() > 0) {
                                if (text.charAt(text.length() - 1) == '@' && OldText.charAt(OldText.length() - 1) != '小') {
                                    text += "小飒";
                                    input.setText(text);
                                    input.setSelection(text.length());
                                }
                            }

                            if (!"".equals(text)) {
                                send.setImageResource(R.drawable.aurora_menuitem_send_pres);
                            } else {
                                send.setImageResource(R.drawable.aurora_menuitem_send);
                            }
                            OldText = text;
                            Log.i("message", "OldText" + OldText);
                        }
                        return false;
                    }
                });
                return false;
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String send_msg = input.getText().toString().trim();
                if (!TextUtils.isEmpty(send_msg)) {
                    // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户
                    EMMessage message = EMMessage.createTxtSendMessage(send_msg, toChatUsername);
                    // 发送消息
                    EMClient.getInstance().chatManager().sendMessage(message);
                    msgs.add(message);
                    //notifyDataSetChanged方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
                    adapter.notifyDataSetChanged();
                    if (msgs.size() > 0) {
                        listView.setSelection(listView.getCount() - 1);
                    }
                    input.setText("");
                    Log.i("message", "@@@@正在发送文字消息");
                    //et_content.clearFocus();
                } else if (!"".equals(Micmsg)) {
                    Log.i("Message", "@@@@得到回传值" + Micmsg);
                    String msgs1[] = Micmsg.split("@");
                    if (!msgs1[1].equals("0")) {
                        EMMessage message = EMMessage.createVoiceSendMessage(msgs1[0], Integer.parseInt(msgs1[1]), toChatUsername);
                        EMClient.getInstance().chatManager().sendMessage(message);
                        msgs.add(message);
                        adapter.notifyDataSetChanged();
                        if (msgs.size() > 0) {
                            listView.setSelection(listView.getCount() - 1);
                        }
                    }
                }
            }
        });
        EMClient.getInstance().chatManager().addMessageListener(new EMMessageListener() {
            @Override
            public void onMessageReceived(List<EMMessage> list) {
                for (EMMessage message : list) {
                    String username = message.getFrom();
                    // 如果是当前会话的消息，刷新聊天页面
                    if (username.equals(toChatUsername)) {
                        msgs.add(message);
                        //msgs = conversation.getAllMessages();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });
                        if (msgs.size() > 0) {
                            //input.setSelection(listView.getCount() - 1);
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

    public void getMessage() {
        // 获取当前conversation对象
        conversation = EMClient.getInstance().chatManager().getConversation(toChatUsername,
                EMConversation.EMConversationType.Chat, true);
        // 把此会话的未读数置为0
        //conversation.markAllMessagesAsRead();
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

    public void selecter(View view) {
        int id = view.getId();
        ft = fm.beginTransaction();
        InputMethodManager imm;
        switch (id) {
            case R.id.aurora_et_chat_input:
                /*setmLinearLayout(0);
                Log.i("Message","@@@@正在点击输入框");
                input.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP){

                            String text = input.getText().toString();
                            Log.i("Message","正在监听.."+text);
                            if (text.length() > 0){
                                if (text.charAt(text.length()-1) == '@' && OldText.charAt(OldText.length()-1)!='小'){
                                    text += "小飒";
                                    input.setText(text);
                                    input.setSelection(text.length());
                                }
                            }

                            if (!"".equals(text)){
                                send.setImageResource(R.drawable.aurora_menuitem_send_pres);
                            }else{
                                send.setImageResource(R.drawable.aurora_menuitem_send);
                            }
                            OldText = text;
                            Log.i("message","OldText"+OldText);
                        }
                        return false;
                    }
                });*/
                break;
            case R.id.mic:
                Log.i("message", "进入录音的界面");
                //设置上浮高度
                setmLinearLayout(600);
                //收起软键盘
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                Fragment_mic fragment_mic = new Fragment_mic();
                ft.replace(R.id.linearLayout, fragment_mic);
                break;
            case R.id.photo:
                Log.i("message", "进入发送图片的界面");
                //设置上浮高度
                setmLinearLayout(600);
                //收起软键盘
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                Fragment_photo fragment_photo = new Fragment_photo();
                ft.replace(R.id.linearLayout, fragment_photo);
                break;
            case R.id.camera:
                break;
            case R.id.send:
                break;
            case R.id.more:
                Log.i("message", "进入MORE的界面");
                //设置上浮高度
                setmLinearLayout(600);
                //收起软键盘
                imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                Fragment_more fragment_more = new Fragment_more();
                ft.replace(R.id.linearLayout, fragment_more);
                break;
        }
        ft.commit();
        listView.setSelection(listView.getCount() - 1);
    }

    public void setmLinearLayout(int h) {
        ViewGroup.LayoutParams lp;
        lp = mLinearLayout.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = h;
        mLinearLayout.setLayoutParams(lp);
    }

    public void getImagePaths() {

    }

    @Override
    public void SendMessageValue(String strValue) {
        Micmsg = strValue;
    }

    @Override
    public void sendimage(String path) {
        imagePath = path;
        Toast.makeText(Chat_more_Activity.this, imagePath, Toast.LENGTH_SHORT).show();
        Log.i("message", "chat_more@@@@@@@image");
        EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
        if (message == null) {
            return;
        }
        EMClient.getInstance().chatManager().sendMessage(message);
        msgs.add(message);
        adapter.notifyDataSetChanged();
        if (msgs.size() > 0) {
            listView.setSelection(listView.getCount() - 1);
        }
    }
}
