package gao.hzyc.com.im_c;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.xys.libzxing.zxing.encoding.EncodingUtils;

/**
 * Created by codeforce on 2017/5/28.
 */
public class Qr_Activity extends AppCompatActivity {

    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        String name = Constant.QR_PREFIX+":"+getIntent().getStringExtra("name");
        //String name = getIntent().getStringExtra("name");
        image = (ImageView) findViewById(R.id.image);
        //image.setImageResource(R.drawable.logo);
        if (name.equals("")) {
            Toast.makeText(Qr_Activity.this, "不能为空", Toast.LENGTH_SHORT).show();
        } else {
            // 位图
            try {
                /**
                 * 参数：1.文本 2 3.二维码的宽高 4.二维码中间的那个logo
                 */
                Bitmap bitmap = EncodingUtils.createQRCode(name, 500, 500, null);
                // 设置图片
                Log.i("message",""+(image == null));
                image.setImageBitmap(bitmap);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
