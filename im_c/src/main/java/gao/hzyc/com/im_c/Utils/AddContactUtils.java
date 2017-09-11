package gao.hzyc.com.im_c.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by codeforce on 2017/5/28.
 */
public class AddContactUtils {
    private Context context;
    private ProgressDialog progressDialog;
    public AddContactUtils(Context context){
        this.context = context;
    }
    //添加朋友
    public void addContact(final String name){

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在发送请求...");
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String reason = "我好想认识你呀!!!";
                try{
                    EMClient.getInstance().contactManager().addContact(name, reason);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                new Thread().sleep(1000);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch (HyphenateException h){
                    h.printStackTrace();
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(context, "添加好友失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }
}
