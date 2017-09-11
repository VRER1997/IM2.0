package gao.hzyc.com.im_c;

import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import gao.hzyc.com.im_c.Adapter.ConversationAdapter;

public class ConversationActivity extends AppCompatActivity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        listView = (ListView) findViewById(R.id.listView);
        final ConversationAdapter adapter = new ConversationAdapter(this,getConversationList());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = (EMConversation) adapter.getItem(position);
                String name = conversation.getLastMessage().getFrom();

                Intent intent = new Intent(ConversationActivity.this, ChatActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
            }
        });
    }

    //获取会话列表
    private List<EMConversation> getConversationList(){
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();

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
}
