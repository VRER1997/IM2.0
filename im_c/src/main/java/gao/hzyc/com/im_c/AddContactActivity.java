package gao.hzyc.com.im_c;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class AddContactActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private EditText et_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        et_name = (EditText) findViewById(R.id.et_username);
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = et_name.getText().toString().trim();
                if ("".equals(name)){
                    Toast.makeText(AddContactActivity.this, "请输入要添加的账号", Toast.LENGTH_SHORT).show();
                }else{
                    addContact(name);
                }

            }
        });
    }

    //添加朋友
    public void addContact(final String name){

        progressDialog = new ProgressDialog(AddContactActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在发送请求...");
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String reason = "我好想认识你呀!!!";
                try{
                    EMClient.getInstance().contactManager().addContact(name, reason);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                new Thread().sleep(1000);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            Toast.makeText(AddContactActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (HyphenateException h){
                    h.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(AddContactActivity.this, "添加好友失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
}
