package gao.hzyc.com.im_c.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;

import java.util.List;

import gao.hzyc.com.im_c.Adapter.HorizontalListViewAdapter;
import gao.hzyc.com.im_c.HorizontalListView;
import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.Utils.FindRecentPhoto;

/**
 * Created by codeforce on 2017/5/28.
 */
public class Fragment_photo extends Fragment {

    private Context context;
    private View view;
    private FindRecentPhoto finder;
    private HorizontalListView hListView;
    private HorizontalListViewAdapter hListViewAdapter;
    private TextView sys_photos;
    private String name;

    private static final int IMAGE = 1;
    private ImageSender sender;

    public interface ImageSender{
        void sendimage(String path);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        sender = (ImageSender) activity;
    }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        view = inflater.inflate(R.layout.fragment_photo,container,false);
        //获取最近的照片
        finder = new FindRecentPhoto(context);
        final List<String> img_path = finder.getList();
        Log.i("message","进入Fragment_Photo"+img_path.size());

        hListView = (HorizontalListView) view.findViewById(R.id.horizon_listview);
        hListViewAdapter = new HorizontalListViewAdapter(context,img_path);
        hListView.setAdapter(hListViewAdapter);

        sys_photos = (TextView) view.findViewById(R.id.sys_photos);
        sys_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用系统相册
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = context.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            //callBackValue.SendMessageValue("image@"+imagePath);
            Log.i("message","image@"+imagePath);
            sender.sendimage(imagePath);
            c.close();
        }
    }

    private void showImage(String imaePath){
        Bitmap bm = BitmapFactory.decodeFile(imaePath);
        //myavatar.setImageBitmap(bm);
    }
}
