package gao.hzyc.com.im_c.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import gao.hzyc.com.im_c.AppManager;
import gao.hzyc.com.im_c.EditInfoActivity;
import gao.hzyc.com.im_c.LoginActivity;
import gao.hzyc.com.im_c.MyApplication;
import gao.hzyc.com.im_c.OprationActivity;
import gao.hzyc.com.im_c.Qr_Activity;
import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.UserInfoActivity;

/**
 * Created by codeforce on 2017/5/21.
 */
public class Fragment_setting extends Fragment {

    private Context context;
    private View view;
    private LinearLayout logout, lversion, qrl, user_inof_edit;
    private TextView tv_name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();

        AppManager.addActivity((Activity)context);

        view = inflater.inflate(R.layout.fragment_setting,null);
        logout = (LinearLayout) view.findViewById(R.id.logout);
        lversion = (LinearLayout) view.findViewById(R.id.lversion);
        tv_name = (TextView) view.findViewById(R.id.tv_name);
        qrl = (LinearLayout) view.findViewById(R.id.qr);
        user_inof_edit = (LinearLayout) view.findViewById(R.id.user_inof_edit);

        final String username = MyApplication.getInstance().getCurrentUserName();
        user_inof_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(context,UserInfoActivity.class);
                intent.putExtra("name",username);
                startActivity(intent);
            }
        });
        qrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(context,Qr_Activity.class);
                intent.putExtra("name",username);
                startActivity(intent);
            }
        });

        lversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "界面优化，实现下拉刷新", Toast.LENGTH_SHORT).show();
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                log_out();
            }
        });
        tv_name.setText(username);
        return view;
    }

    public void log_out(){
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("正在退出账号");
        progressDialog.show();
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                ((Activity)context).runOnUiThread(new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        startActivity(new Intent(context, OprationActivity.class));

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
                Toast.makeText(context, "解锁设备失败", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
