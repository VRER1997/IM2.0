package gao.hzyc.com.im_c.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.xys.libzxing.zxing.activity.CaptureActivity;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import gao.hzyc.com.im_c.Adapter.ConversationAdapter;
import gao.hzyc.com.im_c.AddContactActivity;
import gao.hzyc.com.im_c.Chat_more_Activity;
import gao.hzyc.com.im_c.Constant;
import gao.hzyc.com.im_c.MyApplication;
import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.Utils.AddContactUtils;

/**
 * Created by codeforce on 2017/5/21.
 */
public class Fragment_news extends Fragment {
    private ListView listView;
    private View baseview;
    private Button add;
    private Context context;
    private PullToRefreshView mPullToRefreshView;
    private ConversationAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("message","@@@@@@进入Fragment");
        context = getActivity();
        baseview = inflater.inflate(R.layout.fragment_news,null);
        listView = (ListView) baseview.findViewById(R.id.news_listview);
        adapter = new ConversationAdapter(context,getConversationList());
        listView.setAdapter(adapter);
        mPullToRefreshView = (PullToRefreshView) baseview.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        Log.i("message","@@@@@@@@@@@@@get");
                        adapter = new ConversationAdapter(context,getConversationList());
                        listView.setAdapter(adapter);
                    }
                }, 1000);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = (EMConversation) adapter.getItem(position);
                String name = "";
                String curUser = MyApplication.getInstance().getCurrentUserName();
                String from = conversation.getLastMessage().getFrom();
                if(from.equals(curUser)){
                    name=conversation.getLastMessage().getTo();
                }else{
                    name=from;
                }

                //Intent intent = new Intent(context, ChatActivity.class);
                Intent intent = new Intent(context, Chat_more_Activity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });

        add = (Button) baseview.findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context,add);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.menu_main, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch(id){
                            case R.id.add_friends:
                                startActivity(new Intent(context, AddContactActivity.class));
                                break;
                            case R.id.add_friends_cap:
                                startActivityForResult(new Intent(context, CaptureActivity.class),0);
                                break;
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });
        return baseview;
    }

    //获取会话列表
    private List<EMConversation> getConversationList(){
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        Log.i("message","@@@@@@正在获取会话列表");
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        for (EMConversation conversation: conversations.values()){
            if (conversation.getAllMessages().size() != 0){
                sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(),conversation));
            }
        }

        //按照最后的时间进行排序
        Collections.sort(sortList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(Pair<Long, EMConversation> lhs, Pair<Long, EMConversation> rhs) {
                if (lhs.first == rhs.first){
                    return 0;
                } else if(rhs.first > lhs.first){
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        List<EMConversation> conversations1 = new ArrayList<EMConversation>();
        for (Pair<Long,EMConversation> p : sortList){
            conversations1.add(p.second);
        }

        return conversations1;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            String result = data.getExtras().getString("result");
            String [] name = result.split(":");
            if (name[0].equals(Constant.QR_PREFIX)){
                Toast.makeText(context, "添加"+result+"成功", Toast.LENGTH_SHORT).show();
                AddContactUtils utils = new AddContactUtils(context);
                utils.addContact(name[1]);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


