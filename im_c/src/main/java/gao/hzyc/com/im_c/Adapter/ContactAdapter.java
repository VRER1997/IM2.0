package gao.hzyc.com.im_c.Adapter;

import android.content.Context;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.db.User;

/**
 * 布局联系人界面的Adaptec
 * Created by codeforce on 2017/5/10.
 */
public class ContactAdapter extends BaseAdapter{

    private List<User> users;
    private Context context;
    public ContactAdapter(Context context , List<User> users){
        this.context = context;
        this.users = users;
    }
    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        if (convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.item_contact,null);
        }else{
            view = convertView;
        }
        User user = users.get(position);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_name.setText(user.getName());

        return view;
    }
}
