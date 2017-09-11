package gao.hzyc.com.im_c;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;

import java.util.HashMap;
import java.util.Map;

import gao.hzyc.com.im_c.db.InviteMsg;
import gao.hzyc.com.im_c.db.InvitedMsgDao;
import gao.hzyc.com.im_c.db.User;
import gao.hzyc.com.im_c.db.UserDao;

public class MainActivity extends AppCompatActivity {

    private InvitedMsgDao invtedMsgDao;
    private UserDao userDao = new UserDao(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(final String s) {
                User user = new User();
                user.setName(s);
                Log.i("message","开始添加新的好友到数据库中");
                userDao.saveContact(user);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, s + "已经是您的好友，快开始聊天吧！！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onContactDeleted(String s) {

            }

            @Override
            public void onContactInvited(final String username, String reason) {

                InviteMsg msg = new InviteMsg();
                msg.setFrom(username);
                msg.setReason(reason);
                msg.setTime(System.currentTimeMillis());
                msg.setState(InviteMsg.MsgState.BEINVITEED);
                Log.i("message", username + "@@@@" + reason);
                notifyNewIviteMessage(msg);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "已经收到" + username + "的邀请", Toast.LENGTH_SHORT).show();
                    }
                });
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
        });
    }

    public void Check(View view) {
        switch (view.getId()) {
            case R.id.btn_conversation:
                startActivity(new Intent(MainActivity.this,ConversationActivity.class));
                break;
            case R.id.btn_contact:
                startActivity(new Intent(MainActivity.this,ContactActivity.class));
                break;
            case R.id.btn_newfriend:
                startActivity(new Intent(MainActivity.this, NewContactAskedActivity.class));
                break;
            case R.id.btn_logout:
                log_out();
                break;
        }
    }

    /**
     * 保存并提示消息的邀请消息
     *
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMsg msg) {
        if (invtedMsgDao == null) {
            invtedMsgDao = new InvitedMsgDao(MainActivity.this);
        }
        invtedMsgDao.saveInvitedMsg(msg);
        //保存未读数，这里没有精确计算
        //invtedMsgDao.saveUnreadMessageCount(1);
        // 提示有新消息
        //响铃或其他操作
        // 获取NotificationManager管理者对象
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建一个PendingIntent，和Intent类似，不同的是由于不是马上调用，需要在下拉状态条出发的Activity，所以采用的是PendingIntent,即点击Notification跳转启动到哪个Activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, NewContactAskedActivity.class), 0);
        // 通过Notification.Builder来创建通知，注意API Level 16之后才支持
        Notification notificationAPI_16p = new Notification.Builder(this)
                // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap icon)
                .setSmallIcon(R.drawable.login_default_avatar)
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
                // 需要注意build()是在API level 16及之后增加的，API11可以使用getNotificatin()来替代
                .build();
        // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
        notificationAPI_16p.flags |= Notification.FLAG_AUTO_CANCEL;
        // 通过通知管理器来发起通知
        manager.notify(1,notificationAPI_16p);
    }

    public void log_out(){
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("正在退出账号");
        progressDialog.show();
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));

                    }
                });

            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "解锁设备失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}