package gao.hzyc.com.im_c.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import net.tsz.afinal.FinalBitmap;

import gao.hzyc.com.im_c.R;

/**
 * Created by apple on 16/12/24.
 */

public class PopWindow_image extends PopupWindow {
   // private FinalBitmap fb;

    public PopWindow_image(FinalBitmap fb ,final Activity context, String imageUrl) {
        LayoutInflater layout = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layout.inflate(R.layout.chat_pic, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv);
        fb.display(iv, imageUrl);
        // iv.setImageURI(Uri.parse(imageUrl));
        // 设置SelectPicPopupWindow的View
        setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable drawable = new ColorDrawable(00000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(drawable);

        this.setAnimationStyle(R.style.AppTheme_PopupOverlay);

    }
}
