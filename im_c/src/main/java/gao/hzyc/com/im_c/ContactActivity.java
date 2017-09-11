package gao.hzyc.com.im_c;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gao.hzyc.com.im_c.Adapter.ContactAdapter;
import gao.hzyc.com.im_c.db.User;

public class ContactActivity extends AppCompatActivity {

    private Button btn_add_friend;
    protected List<User> contactList = new ArrayList<User>();
    protected ListView listView;
    private Map<String, User> contactsMap;
    private ContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        listView = (ListView) findViewById(R.id.listView);
        btn_add_friend = (Button) findViewById(R.id.btn_add);

        contactsMap = MyApplication.getInstance().getContactList();
        Iterator<Map.Entry<String, User>> iterator = contactsMap.entrySet().iterator();

        while (iterator.hasNext()){
            Map.Entry<String,User> it = iterator.next();
            User user = it.getValue();
            contactList.add(user);
        }
        adapter = new ContactAdapter(ContactActivity.this,contactList);
        /*User user = contactList.get(0);
        Log.i("message","啦啦啦啦啦啦啦啦啦@@@"+user.getName()+"@@@"+contactList.size());*/
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = (User) adapter.getItem(position);
                startActivity(new Intent(ContactActivity.this,ChatActivity.class).putExtra("name",user.getName()));
            }
        });
        btn_add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ContactActivity.this,AddContactActivity.class));
            }
        });
    }
}
