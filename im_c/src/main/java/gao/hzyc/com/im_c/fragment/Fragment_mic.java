package gao.hzyc.com.im_c.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.Utils.AudioRecoderUtils;
import gao.hzyc.com.im_c.Utils.TimeUtils;

/**
 * 录音的Fragment
 * Created by codeforce on 2017/5/23.
 */
public class Fragment_mic extends Fragment{

    //请求返回码
    static final int VOICE_REQUEST_CODE = 66;

    private MediaPlayer mediaPlayer = null;
    private Context context;
    private View view;
    private ImageView play, touch_btn, del, send;
    private TextView mic_text;
    private AudioRecoderUtils audioRecoderUtils;
    private String Path = "";
    private boolean first = true;
    private long time = 0;
    CallBackValue callBackValue;

    //定义一个回调接口 向Activity传值
    public interface CallBackValue{
        public void SendMessageValue(String strValue);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callBackValue = (CallBackValue) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 获取Activity的控件
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        send = (ImageView) getActivity().findViewById(R.id.send);
        Log.i("Message","已经获取到send控件");
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_mic,container,false);

        play = (ImageView) view.findViewById(R.id.play);
        touch_btn = (ImageView) view.findViewById(R.id.touch_btn);
        del = (ImageView) view.findViewById(R.id.del);
        mic_text = (TextView) view.findViewById(R.id.mic_text);
        //setMicImageType(0);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //播放录音
                Log.i("Message","正在播放");
                if (mediaPlayer == null)
                    mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if (first){
                    try {
                        if (Path.equals("")){
                            Toast.makeText(context , "文件不存在", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.i("message","@@@@@@"+Path);
                        mediaPlayer.setDataSource(context,Uri.parse(Path));
                        //准备一下(内存卡)
                        mediaPlayer.prepare();
                        first = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                mediaPlayer.start();
                Toast.makeText(context, "正在播放录音", Toast.LENGTH_SHORT).show();
            }
        });

        //删除录音信息
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Path.equals("")){
                    Toast.makeText(context , "文件不存在", Toast.LENGTH_SHORT).show();
                    return;
                }
                File file = new File(Path);
                if(file.isFile()){
                    file.delete();
                    Toast.makeText(context , "已经删除", Toast.LENGTH_SHORT).show();
                    callBackValue.SendMessageValue(Path+"@0");
                }
            }
        });
        audioRecoderUtils = new AudioRecoderUtils();
        audioRecoderUtils.setOnAudioStatusUpdateListener(new AudioRecoderUtils.OnAudioStatusUpdateListener() {
            @Override
            public void onUpdate(double db, long time) {
                mic_text.setText(TimeUtils.long2String(time));
            }

            @Override
            public void onStop(String filePath) {
                Path = filePath;
                first = true;
                //Toast.makeText(context, "录音保存在：" + filePath, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "录音已保存", Toast.LENGTH_SHORT).show();
                mic_text.setText(TimeUtils.long2String(0));
                if (mediaPlayer == null)
                    mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if (first){
                    try {
                        if (Path.equals("")){
                            Toast.makeText(context , "文件不存在", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        mediaPlayer.setDataSource(context,Uri.parse(Path));
                        //准备一下(内存卡)
                        mediaPlayer.prepare();

                        first = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    time = mediaPlayer.getDuration();
                    int sec = (int) time / 1000 ;
                    Log.i("message","@@@@@@"+Path+time);
                    callBackValue.SendMessageValue(Path+"@"+sec);
                }
            }
        });

        requestPermissions();

        return view;
    }

    /**
     * 开启扫描之前判断权限是否打开
     */
    private void requestPermissions() {
        //判断是否开启摄像头权限
        if ((ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
                ) {
            StartListener();

            //判断是否开启语音权限
        } else {
            //请求获取摄像头权限
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, VOICE_REQUEST_CODE);
        }

    }
    /**
     * 请求权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == VOICE_REQUEST_CODE) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED) ) {
                StartListener();
            } else {
                Toast.makeText(context, "已拒绝权限！", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void StartListener(){
        //Button的touch监听
        touch_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){

                    case MotionEvent.ACTION_DOWN:

                        /*mPop.showAtLocation(rl, Gravity.CENTER, 0, 0);*/

//                        mButton.setText("松开保存");
                        audioRecoderUtils.startRecord();
                        Log.i("message","@@@@正在录音");

                        break;

                    case MotionEvent.ACTION_UP:

                        audioRecoderUtils.stopRecord();          //结束录音（保存录音文件）
//                        audioRecoderUtils.cancelRecord();    //取消录音（不保存录音文件）
                        /*mPop.dismiss();*/
                        mic_text.setText("按住说话");
                        setMicImageType(1);
                        break;
                }
                return true;
            }
        });
    }

    //设置录音前后的图片样式
    public void setMicImageType(int type){
        if (type == 0){
            play.setImageResource(R.drawable.aurora_recordvoice_preview_play);
            del.setImageResource(R.drawable.aurora_recordvoice_cancel_record);
            send.setImageResource(R.drawable.aurora_menuitem_send);
        }else{
            play.setImageResource(R.drawable.aurora_recordvoice_play);
            del.setImageResource(R.drawable.aurora_recordvoice_cancel_record);
            send.setImageResource(R.drawable.aurora_menuitem_send_pres);
        }
    }

}
