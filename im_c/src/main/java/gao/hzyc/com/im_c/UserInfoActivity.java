package gao.hzyc.com.im_c;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoActivity extends AppCompatActivity {

    private TextView username;
    private LinearLayout dialog_layout;
    private ImageView myavatar;

    private String picPath;
    private Intent lastIntent;
    private Uri photoUri;

    /** 使用照相机拍照获取图片 */
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;

    private static final int IMAGE = 1;

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        username = (TextView) findViewById(R.id.username);
        dialog_layout = (LinearLayout) findViewById(R.id.dialog_layout);
        myavatar = (ImageView) findViewById(R.id.avatar);

        dialog_layout.setVisibility(View.INVISIBLE);
        myavatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    public void check(View v) {
        switch (v.getId()) {
            case R.id.btn_take_photo : // 开启相机
                takePhoto();
                break;
            case R.id.btn_pick_photo : // 开启图册
                //调用系统相册
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE);
                break;
            case R.id.btn_cancel : // 取消操作
                dialog_layout.setVisibility(View.INVISIBLE);
                break;
            default :
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //获取图片路径
        if (requestCode == IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumns = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePathColumns[0]);
            String imagePath = c.getString(columnIndex);
            showImage(imagePath);
            dialog_layout.setVisibility(View.INVISIBLE);
            c.close();
        }
    }
    /**
     * 拍照获取图片
     */
    private void takePhoto() {
    }

    private void showImage(String imaePath){
        Bitmap bm = BitmapFactory.decodeFile(imaePath);
        myavatar.setImageBitmap(bm);
    }

}
