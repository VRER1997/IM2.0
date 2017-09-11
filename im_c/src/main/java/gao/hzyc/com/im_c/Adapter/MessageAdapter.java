package gao.hzyc.com.im_c.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.util.ImageUtils;

import net.tsz.afinal.FinalBitmap;

import java.io.File;
import java.util.List;

import gao.hzyc.com.im_c.MyApplication;
import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.Utils.PopWindow_image;

/**
 * Created by codeforce on 2017/5/10.
 */
public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<EMMessage> list;
    private MediaPlayer mPlayer;
    private ImageView imageView;
    private TextView chatcontent;

    private String playMsgId;
    private boolean isPlaying;
    private FinalBitmap fb;//显示图片

    public MessageAdapter(Context context, List<EMMessage> list){
        this.context = context;
        this.list = list;
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
    public int getItemViewType(int position) {
        EMMessage message = (EMMessage) getItem(position);
        return message.direct() == EMMessage.Direct.RECEIVE ? 0 : 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EMMessage message = (EMMessage) getItem(position);
        //获取发送的类型
        int viewType = getItemViewType(position);
        View view = null;
        if (convertView == null){
                if (viewType == 0){
                    view = LayoutInflater.from(context).inflate(R.layout.item_mic_get,null);
                }else{
                    view = LayoutInflater.from(context).inflate(R.layout.item_mic_send,null);

                }
        }else{
            view = convertView;
        }

        fb = FinalBitmap.create(context);
        fb.configLoadfailImage(R.drawable.skin_qzone_title_progress);
        fb.configLoadingImage(R.drawable.skin_qzone_title_progress);

        chatcontent = (TextView) view.findViewById(R.id.tv_chatcontent);
        imageView = (ImageView) view.findViewById(R.id.imv_voice);

        if (message.getType() == EMMessage.Type.TXT){
            Log.i("message","@@@正在布局文字界面_Messge");
            EMTextMessageBody msgBody = (EMTextMessageBody)message.getBody();
            chatcontent.setText(msgBody.getMessage());
            imageView.setVisibility(View.GONE);
            chatcontent.setVisibility(View.VISIBLE);
        }
        if (message.getType() == EMMessage.Type.VOICE){
            imageView.setVisibility(View.VISIBLE);
            chatcontent.setVisibility(View.GONE);
            Log.i("message","@@@正在布局语音界面_Message");
            if (message.getFrom().equals(MyApplication.getInstance().getCurrentUserName())){
                imageView.setImageResource(R.drawable.aurora_sendvoice_send_3);
            }else{
                imageView.setImageResource(R.drawable.aurora_receivevoice_receive_3);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startPlay(message);
                    Log.i("message","@@@@正在播放录音");
                }
            });
        }
        if (message.getType() == EMMessage.Type.IMAGE){
            imageView.setVisibility(View.VISIBLE);
            chatcontent.setVisibility(View.GONE);
            Log.i("message","@@@正在布局图片界面_MessageAdapter");
            if (!message.getFrom().equals(MyApplication.getInstance().getCurrentUserName())) { // 对方发的消息
                String ThumbnailUrl = ((EMImageMessageBody) message.getBody()).getThumbnailUrl(); // 获取缩略图片地址
                String thumbnailPath = ImageUtils.getScaledImage(context, ThumbnailUrl);
                String imageRemoteUrl = ((EMImageMessageBody) message.getBody()).getRemoteUrl();// 获取远程原图片地址

                fb.display(imageView, thumbnailPath);//显示图片
                imageClick(imageView, imageRemoteUrl);//图片添加监听
            } else {
                // 自己发的消息
                String LocalUrl = ((EMImageMessageBody) message.getBody()).getLocalUrl(); // 获取本地图片地址
                Bitmap bm = ImageUtils.decodeScaleImage(LocalUrl, 160, 160);//获取缩略图
                imageView.setImageBitmap(bm);//显示图片
                Log.e("message:", "bm=" + bm + "==LocalUrl=" + LocalUrl);

                imageClick(imageView, LocalUrl);//图片添加监听
            }
        }
        return view;
    }

    // 图片点击监听
    private void imageClick(ImageView image, final String imageUrl) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                //new PopWindow_Image(Chat.this, imageUrl).showAtLocation(arg0, 0, 0, 0);
                new PopWindow_image(fb, (Activity)context, imageUrl).showAtLocation(arg0, 0, 0, 0);
            }
        });
    }

    // 开始播放
    private void startPlay(final EMMessage message) {

        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) message.getBody();

        if (message.direct() == EMMessage.Direct.SEND) {
            // for sent msg, we will try to play the voice file directly
            playVoice(voiceBody.getLocalUrl(), message);
        } else {
            if (message.status() == EMMessage.Status.SUCCESS) {
                playVoice(voiceBody.getLocalUrl(), message);
                //voiceBody.getRemoteUrl();
            } else if (message.status() == EMMessage.Status.INPROGRESS) {
                //toast("信息还在发送中");
                Toast.makeText(context, "信息还在发送中", Toast.LENGTH_SHORT).show();
            } else if (message.status() == EMMessage.Status.FAIL) {
                // toast("接收失败");
                Toast.makeText(context, "接收失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //录音的播放
    public void playVoice(String filePath, final EMMessage message) {

        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            System.err.println("file not exist");
            // toast("语音文件不存在");
            return;
        }
        playMsgId = message.getMsgId();
        mPlayer = new MediaPlayer();

        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    Log.i("message","@@@@@播放完成");
                    if (mPlayer == null) // 表示因为要播放其他语音时已经被停止了,所以不需要再次调用停止
                        return;
                    stopPlayVoice(message); // stop animation
                }
            });
            isPlaying = true;
            mPlayer.start();

        } catch (Exception e) {
        }
    }

    /**
     * 停止语音
     *
     * @param message
     */
    public void stopPlayVoice(final EMMessage message) {
        //voiceAnimation.stop();
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            imageView.setImageResource(R.drawable.aurora_receivevoice_receive_3);
        } else {
            imageView.setImageResource(R.drawable.aurora_sendvoice_send_3);
        }
        // stop play voice
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
        isPlaying = false;
        playMsgId = null;
    }

}
