package gao.hzyc.com.im_c;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import gao.hzyc.com.im_c.Adapter.MyAdapter;
import gao.hzyc.com.im_c.db.InviteMsg;
import gao.hzyc.com.im_c.db.InvitedMsgDao;

public class NewContactAskedActivity extends AppCompatActivity {

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact_asked);

        //Toast.makeText(NewContactAskedActivity.this, "@@@@进入添加联系人的页面中", Toast.LENGTH_SHORT).show();
        listView = (ListView) findViewById(R.id.list);
        InvitedMsgDao invitedMsgDao = new InvitedMsgDao(NewContactAskedActivity.this);
        List<InviteMsg> msgs = invitedMsgDao.getMessagesList();
        Log.i("message","@@@@"+msgs.size());
        //Toast.makeText(NewContactAskedActivity.this, "@@@@"+msgs.size(), Toast.LENGTH_SHORT).show();
        listView.setAdapter(new MyAdapter(msgs,this));
    }
}
