package gao.hzyc.com.im_c;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import gao.hzyc.com.im_c.Utils.ChatMessage;
import gao.hzyc.com.im_c.Utils.HttpUtils;
import gao.hzyc.com.im_c.fragment.Fragment_contact;
import gao.hzyc.com.im_c.fragment.Fragment_news;
import gao.hzyc.com.im_c.fragment.Fragment_setting;

public class Fragment_main_Activity extends AppCompatActivity {

    private ImageView news, contact, setting;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_main_);

        news = (ImageView) findViewById(R.id.iv_news);
        contact = (ImageView) findViewById(R.id.iv_contact);
        setting = (ImageView) findViewById(R.id.iv_setting);
        //new Thread(networkTask).start();
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        Fragment_news fragment = new Fragment_news();
        ft.replace(R.id.linearLayout,fragment);
        ft.commit();

        AppManager.finishAllActivity();
        AppManager.addActivity(this);
    }

    public void Check(View view){
        int id = view.getId();
        ft = fm.beginTransaction();
        switch (id){
            case R.id.iv_news:
                Fragment_news fragment = new Fragment_news();
                ft.replace(R.id.linearLayout,fragment);
                ft.addToBackStack("news");

                //按钮样式改变
                news.setImageResource(R.drawable.skin_tab_icon_conversation_selected);
                contact.setImageResource(R.drawable.skin_tab_icon_contact_normal);
                setting.setImageResource(R.drawable.skin_tab_icon_setup_normal);
                break;
            case R.id.iv_contact:
                Fragment_contact fragment_contact = new Fragment_contact();
                ft.replace(R.id.linearLayout,fragment_contact);
                ft.addToBackStack("contact");

                news.setImageResource(R.drawable.skin_tab_icon_conversation_normal);
                contact.setImageResource(R.drawable.skin_tab_icon_contact_selected);
                setting.setImageResource(R.drawable.skin_tab_icon_setup_normal);
                break;
            case R.id.iv_setting:
                Fragment_setting fragment_setting = new Fragment_setting();
                ft.replace(R.id.linearLayout,fragment_setting);
                ft.addToBackStack("setting");

                news.setImageResource(R.drawable.skin_tab_icon_conversation_normal);
                contact.setImageResource(R.drawable.skin_tab_icon_contact_normal);
                setting.setImageResource(R.drawable.skin_tab_icon_setup_selected);
                break;
        }
        ft.commit();
    }
}
