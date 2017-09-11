package gao.hzyc.com.im_c;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gao.hzyc.com.im_c.Heads_up.HeadsUp;
import gao.hzyc.com.im_c.Heads_up.HeadsUpManager;
import gao.hzyc.com.im_c.Utils.HttpUtils;
import gao.hzyc.com.im_c.db.Constant;
import gao.hzyc.com.im_c.db.DbOpenHelper;
import gao.hzyc.com.im_c.db.InviteMsg;
import gao.hzyc.com.im_c.db.InvitedMsgDao;
import gao.hzyc.com.im_c.db.Myinfo;
import gao.hzyc.com.im_c.db.User;
import gao.hzyc.com.im_c.db.UserDao;

/**
 * Created by codeforce on 2017/5/5.
 */
public class MyApplication extends Application {

    private UserDao userDao;
    private static Context applicationContext;
    private static MyApplication instance;
    private String username = "";
    private Map<String, User> contactList;
    private CallReceiver callReceiver;
    private LocalBroadcastManager broadcastManager;
    private InvitedMsgDao invtedMsgDao;
    private Context appcontext;
    private EMMessageListener messageListener;

    public static MyApplication getInstance(){
        return instance;
    }

    public void setCurrentUserName(String username) {
        this.username = username;
        Myinfo.getInstance(instance).setUserInfo(Constant.KEY_USERNAME, username);
    }

    public String getCurrentUserName() {
        if (TextUtils.isEmpty(username)) {
            username = Myinfo.getInstance(instance).getUserInfo(Constant.KEY_USERNAME);
        }
        return username;

    }

    //map ---> arrayList
    public void saveContactList(Map<String, User> contactList){
        userDao.saveContactList(new ArrayList<User>(contactList.values()));
    }

    public Map<String,User> getContactList(){
        return userDao.getContactList();
    }
    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        initApp();
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if(callReceiver == null){
            callReceiver = new CallReceiver();
        }
        //注册通话广播接收者
        this.registerReceiver(callReceiver, callFilter);
        broadcastManager = LocalBroadcastManager.getInstance(appcontext);
        registerContactListener();
        registerMessageListener();
    }

    private void initDbDao(Context context) {
        userDao = new UserDao(context);
        DbOpenHelper.getInstance(context);
        appcontext = context;
    }

    private void initApp() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);
        // 如果APP启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的APP会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回

        if (processAppName == null ||!processAppName.equalsIgnoreCase(this.getPackageName())) {
            Log.e("message", "enter the service process!");

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }
        //初始化
        EMClient.getInstance().init(this, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
        //初始化数据库
        initDbDao(this);
    }
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

    //注册好友添加listener
    public void registerContactListener(){

        EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
    }
    public class MyContactListener implements EMContactListener{

        @Override
        public void onContactAdded(String s) {
            Map<String, User> localUsers = getContactList();
            Map<String, User> toAddUsers = new HashMap<String, User>();
            User user = new User();
            user.setName(s);

            if (!localUsers.containsKey(s)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(s, user);
            localUsers.putAll(toAddUsers);

            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactDeleted(String s) {

        }

        @Override
        public void onContactInvited(String username, String reason) {
            InviteMsg msg = new InviteMsg();
            msg.setFrom(username);
            msg.setReason(reason);
            msg.setTime(System.currentTimeMillis());
            msg.setState(InviteMsg.MsgState.BEINVITEED);
            Log.i("message", username + "@@@@" + reason);
            notifyNewIviteMessage(msg);
            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "已经收到" + username + "的邀请", Toast.LENGTH_SHORT).show();
                }
            });*/
        }

        @Override
        public void onFriendRequestAccepted(String s) {
            User user = new User();
            user.setName(s);
            Log.i("message","开始添加新的好友到数据库中");
            userDao.saveContact(user);
        }

        @Override
        public void onFriendRequestDeclined(String s) {

        }
    }

    /**
     * 保存并提示消息的邀请消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMsg msg) {
        if (invtedMsgDao == null) {
            invtedMsgDao = new InvitedMsgDao(appcontext);
        }
        invtedMsgDao.saveInvitedMsg(msg);
        //保存未读数，这里没有精确计算
        invtedMsgDao.saveUnreadMessageCount(1);
        // 提示有新消息
        //响铃或其他操作
        // 获取NotificationManager管理者对象
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建一个PendingIntent，和Intent类似，不同的是由于不是马上调用，需要在下拉状态条出发的Activity，所以采用的是PendingIntent,即点击Notification跳转启动到哪个Activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, NewContactAskedActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level 16之后才支持
        Notification notificationAPI_16p = new Notification.Builder(this)
                // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap icon)
                .setSmallIcon(R.drawable.logo)
                // 设置在status bar上显示的提示文字
                .setTicker("TickerText:" + "您有新短消息，请注意查收！")
                // 设置在下拉status bar后显示的标题
                .setContentTitle("IM好友申请")
                // 设置在下拉status bar后显示的内容
                .setContentText(msg.getFrom()+"正添加您为好友")
                // 关联PendingIntent
                .setContentIntent(pendingIntent)
                // 设置在下拉status bar后显示的数字
                .setNumber(1)
                .setAutoCancel(true)
                // 需要注意build()是在API level 16及之后增加的，API11可以使用getNotificatin()来替代
                .build();
        // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        notificationAPI_16p.flags |= Notification.FLAG_AUTO_CANCEL;
        // 通过通知管理器来发起通知
        manager.notify(1,notificationAPI_16p);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            String from = data.getString("from");
            Log.i("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
            String mymsg = "";
            if (val.contains(gao.hzyc.com.im_c.Constant.ROBOT_FROM)){
                Log.i("message","发送ROBOT的镜像消息");
                String vals [] = val.split("from");
                mymsg = vals[0] + gao.hzyc.com.im_c.Constant.ROBOT_BY;
            }else {
                Log.i("message","对方收到向ROBOT回复的消息");
                mymsg = val + gao.hzyc.com.im_c.Constant.ROBOT_FROM;
            }
            EMMessage message = EMMessage.createTxtSendMessage(mymsg, from);
            // 发送消息
            EMClient.getInstance().chatManager().sendMessage(message);
        }
    };

    //消息监听器
    public void registerMessageListener(){
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                for (final EMMessage message : messages) {
                    if (message.getType() == EMMessage.Type.TXT){
                        EMLog.i("Message", "Messageid : " + message.getMsgId());
                        final EMTextMessageBody msgBody = (EMTextMessageBody)message.getBody();
                        if (msgBody.getMessage().contains(gao.hzyc.com.im_c.Constant.ROBOT_SUFFIX)){
                            Log.i("message","收到向ROBOT发送的消息");
                            String [] send_msgs = msgBody.getMessage().split("@");
                            String chatMessage = HttpUtils.sendMessage(send_msgs[0]);
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("value", chatMessage);
                            data.putString("from",message.getFrom());
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }else if (msgBody.getMessage().contains(gao.hzyc.com.im_c.Constant.ROBOT_FROM)){

                            String send_msg = msgBody.getMessage();
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("value", send_msg+"");
                            data.putString("from",message.getFrom());
                            msg.setData(data);
                            Log.i("message","我收到ROBOT消息"+send_msg);
                            handler.sendMessage(msg);
                        }else if (msgBody.getMessage().contains(gao.hzyc.com.im_c.Constant.ROBOT_BY)){
                            Log.i("message","我收到的ROBOT镜像消息");
                        }

                    }
                    notifyMessage(message);
                    // in background, do not refresh UI, notify it in notification bar
                    /*if(!easeUI.hasForegroundActivies()){
                        getNotifier().onNewMsg(message);
                    }*/
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
            }

            @Override
            public void onMessageRead(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDelivered(List<EMMessage> message) {
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                EMLog.i("message", "change:"+change);
            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    public void notifyMessage(EMMessage message){
        String mmss = "";
        if (message.getType() == EMMessage.Type.TXT){
            EMTextMessageBody msgBody = (EMTextMessageBody)message.getBody();
            mmss = msgBody.getMessage().toString();
        }
        if (message.getType() == EMMessage.Type.VOICE){
            mmss = "录音消息";
        }
        if (message.getType() == EMMessage.Type.IMAGE){
            mmss = "[图片]";
        }
        Bundle bundle = new Bundle();
        bundle.putString("name",message.getFrom());
        PendingIntent pendingIntent = PendingIntent.getActivity(appcontext,11,new Intent(appcontext,Chat_more_Activity.class).putExtras(bundle),
                PendingIntent.FLAG_UPDATE_CURRENT);
        HeadsUpManager manage = HeadsUpManager.getInstant(appcontext);
        HeadsUp.Builder builder = new HeadsUp.Builder(appcontext);
        builder.setContentTitle(message.getFrom())
                .setDefaults(Notification.DEFAULT_SOUND| Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL)
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent,false)
                .setAutoCancel(true)
                .setContentText(mmss);
        HeadsUp headsUp = builder.buildHeadUp();
        headsUp.setSticky(true);
        manage.notify(1,headsUp);
    }

}
