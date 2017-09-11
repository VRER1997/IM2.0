package gao.hzyc.com.im_c.Adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;

import java.util.List;

import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.db.InviteMsg;
import gao.hzyc.com.im_c.db.InvitedMsgDao;

/**
 * 布局申请通知的Adapter
 * Created by codeforce on 2017/5/9.
 */
public class MyAdapter extends BaseAdapter {

    private List<InviteMsg> list;
    private Context context;

    public MyAdapter(List<InviteMsg> list,Context context){
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null){
            v = LayoutInflater.from(context).inflate(R.layout.massage_invited,null);
        }else{
            v = convertView;
        }

        final InviteMsg msg = list.get(position);
        Log.i("message",msg.getFrom()+""+msg.getReason());
        TextView name = (TextView) v.findViewById(R.id.tv_name);
        TextView reason = (TextView) v.findViewById(R.id.tv_reason);
        final Button btn_agree = (Button) v.findViewById(R.id.btn_agree);

        name.setText(msg.getFrom());
        reason.setText(msg.getReason());

         if (msg.getState() == InviteMsg.MsgState.BEINVITEED){
            btn_agree.setEnabled(true);
             btn_agree.setEnabled(true);
        }else if (msg.getState() == InviteMsg.MsgState.BEREFUSED){
            btn_agree.setVisibility(Button.GONE);
             btn_agree.setText("被拒绝");
             btn_agree.setEnabled(false);
        }else if (msg.getState() == InviteMsg.MsgState.BEAGREED){
            btn_agree.setText("已同意");
            btn_agree.setEnabled(false);
        }

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            EMClient.getInstance().contactManager().acceptInvitation(msg.getFrom());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        //更新信息的状态
                        msg.setState(InviteMsg.MsgState.BEAGREED);

                        ContentValues values = new ContentValues();
                        values.put(InvitedMsgDao.COLUMN_NAME_STATUS, InviteMsg.MsgState.BEAGREED.ordinal());
                        InvitedMsgDao invitedMsgDao = new InvitedMsgDao(context);
                        invitedMsgDao.updateMessage(msg.getId(),values);
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_agree.setText("已同意");

                           }
                        });
                    }
                }).start();
            }
        });

        //先简化为  只有同意的按钮
        /*if (msg.getState() == InviteMsg.MsgState.BEINVITEED){
            btn_agree.setEnabled(true);
            btn_refuse.setEnabled(true);
        }else if (msg.getState() == InviteMsg.MsgState.BEREFUSED){
            btn_agree.setVisibility(Button.GONE);
            btn_refuse.setText("被拒绝");
            btn_refuse.setEnabled(false);
        }else if (msg.getState() == InviteMsg.MsgState.BEAGREED){
            btn_refuse.setVisibility(Button.GONE);
            btn_agree.setText("已同意");
            btn_agree.setEnabled(false);
        }*/
        return v;
    }

}
