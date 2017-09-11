package gao.hzyc.com.im_c;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gao.hzyc.com.im_c.db.DBmanager;
import gao.hzyc.com.im_c.db.User;

public class LoginActivity extends AppCompatActivity {

    private EditText et_username, et_password;
    private Button  login;
    private boolean is_log = false;
    private String name, pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (EMClient.getInstance().isLoggedInBefore()){
            is_log = true;
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            return ;
        }
        setContentView(R.layout.activity_login);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
        name = et_username.getText().toString().trim();
        pwd = et_password.getText().toString().trim();

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });

        AppManager.addActivity(this);

    }

   /* private void Sign_up(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Log.i("message",name+"@@"+pwd);
                    //注册失败会抛出HyphenateException
                    EMClient.getInstance().createAccount(name, pwd);//同步方法
                    Log.i("message","注册成功");
                }catch (HyphenateException h){
                    h.printStackTrace();
                    Log.i("message","注册失败");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }*/

    private void Login(){
            name = et_username.getText().toString().trim();
            pwd = et_password.getText().toString().trim();
            //校验
            if (TextUtils.isEmpty(name)){
                Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(pwd)){
                Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return ;
            }
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("正在登录...");
            progressDialog.show();
            // close it before login to make sure DemoDB not overlap
            DBmanager.getInstance().closeDB();
            // reset current user name before login
            MyApplication.getInstance().setCurrentUserName(name);
            EMClient.getInstance().login(name, pwd, new EMCallBack() {
            @Override
            public void onSuccess() {

                Log.i("message","登录成功");
                //获取对话消息
                EMClient.getInstance().chatManager().loadAllConversations();
                //获取好友信息
                getFriends();
                //跳转到主页
                progressDialog.dismiss();
                //startActivity(new Intent(LoginActivity.this,MainActivity.class));
                startActivity(new Intent(LoginActivity.this,Fragment_main_Activity.class));
            }

            @Override
            public void onError(int i, final String s) {
                Log.i("message","登录失败");
                runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "登录失败:"+s, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    private  void  getFriends(){
        try {
            List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
            Map<String ,User> users=new HashMap<String ,User>();
            for(String username:usernames){
                User user=new User();
                user.setName(username);
                users.put(username, user);

            }
            MyApplication.getInstance().saveContactList(users);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (is_log) {
            return;
        }
    }
}
