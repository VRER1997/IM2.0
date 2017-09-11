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
import android.widget.TextView;

import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.VideoCallActivity;
import gao.hzyc.com.im_c.VoiceCallActivity;

/**
 * 加载更多信息的Fragment
 * Created by codeforce on 2017/5/24.
 */
public class Fragment_more extends Fragment {

    private Context context;
    private View view;
    private String name = "";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i("Message","正在获取发送人的控件信息");
        TextView textView = (TextView) getActivity().findViewById(R.id.tv_toUsername);
        name = textView.getText().toString();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_more,container,false);
        view.findViewById(R.id.voice_communicate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Message","正在进行语音通话");
                Intent intent = new Intent(context, VoiceCallActivity.class);
                intent.putExtra("username",name);
                intent.putExtra("isComingCall", false);
                startActivity(intent);
            }
        });
        view.findViewById(R.id.vedio_communicate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Message","正在进行视频通话");
                Intent intent = new Intent(context, VideoCallActivity.class);
                intent.putExtra("username",name);
                intent.putExtra("isComingCall", false);
                startActivity(intent);
            }
        });
        return view;
    }
}
