package gao.hzyc.com.im_c.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gao.hzyc.com.im_c.Adapter.ContactAdapter;
import gao.hzyc.com.im_c.AddContactActivity;
import gao.hzyc.com.im_c.ChatActivity;
import gao.hzyc.com.im_c.Chat_more_Activity;
import gao.hzyc.com.im_c.MyApplication;
import gao.hzyc.com.im_c.NewContactAskedActivity;
import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.db.User;

/**
 * 联系人的Fragment
 * Created by codeforce on 2017/5/21.
 */
public class Fragment_contact extends Fragment {

    private Button btn_add_friend;
    protected List<User> contactList = new ArrayList<User>();
    protected ListView listView;
    private Map<String, User> contactsMap;
    private ContactAdapter adapter;
    private PullToRefreshView mPullToRefreshView;

    private Context context;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_contact,null);
        listView = (ListView) view.findViewById(R.id.contact_listview);
        //btn_add_friend = (Button) view.findViewById(R.id.btn_add);
        //获取联系人列表
        getList();
        adapter = new ContactAdapter(context,contactList);
        /*User user = contactList.get(0);
        Log.i("message","啦啦啦啦啦啦啦啦啦@@@"+user.getName()+"@@@"+contactList.size());*/
        listView.setAdapter(adapter);
        mPullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullToRefreshView.setRefreshing(false);
                        Log.i("message","@@@@@@@@@@@@@get");
                        //刷新列表
                        getList();
                        adapter = new ContactAdapter(context,contactList);
                        listView.setAdapter(adapter);
                    }
                }, 1000);
            }
        });

        /**
         * 向聊天界面进行跳转
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) adapter.getItem(position);
                startActivity(new Intent(context,Chat_more_Activity.class).putExtra("name",user.getName()));
            }
        });
        /*btn_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, AddContactActivity.class));
            }
        });*/

        /**
         * 向申请消息界面进行跳转
         */
        view.findViewById(R.id.linearLayout_newfrd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, NewContactAskedActivity.class));
            }
        });

        return view;
    }

    //Map ---> ArrayList
    public List<User> getList(){

        contactList = new ArrayList<User>();
        contactsMap = MyApplication.getInstance().getContactList();
        Iterator<Map.Entry<String, User>> iterator = contactsMap.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String,User> it = iterator.next();
            User user = it.getValue();
            contactList.add(user);
        }
        return contactList;
    }
}
