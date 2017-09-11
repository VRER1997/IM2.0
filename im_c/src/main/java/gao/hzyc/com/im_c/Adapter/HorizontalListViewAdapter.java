package gao.hzyc.com.im_c.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gao.hzyc.com.im_c.R;
import gao.hzyc.com.im_c.Utils.BitmapUtil;

/**
 * 布局横向列表的Adaptec
 */
public class HorizontalListViewAdapter extends BaseAdapter{
	private Context context;
	private LayoutInflater mInflater;
	private List<String> list;
	private ImageView image;
	private int selectIndex = -1;
	private Set<Integer> set = new HashSet<Integer>();

	public HorizontalListViewAdapter(Context context, List<String> list){
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
	public View getView(final int position, View convertView, ViewGroup parent) {

		View view;
		if (convertView == null){
			view = LayoutInflater.from(context).inflate(R.layout.horizontal_list_item,null);
		}else{
			view = convertView;
		}

		image = (ImageView) view.findViewById(R.id.img_list_item);
		image.setImageURI(Uri.fromFile(new File(list.get(position))));
		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (!set.contains(position)){
					image.setBackgroundColor(android.graphics.Color.parseColor("#2584e4"));
					set.add(position);
				}else{
					image.setBackgroundColor(android.graphics.Color.parseColor("#ffffff"));
					set.remove(position);
				}
				Log.i("message","进入adapter_Photo"+set);
			}
		});

		return view;
		/*ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.horizontal_list_item, null);
			holder.mImage=(ImageView)convertView.findViewById(R.id.img_list_item);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		if(position == selectIndex){
			convertView.setSelected(true);
		}else{
			convertView.setSelected(false);
		}

		holder.mImage.setImageURI(Uri.fromFile(new File(mIconIDs[position]+"")));

		return convertView;*/
	}

	private static class ViewHolder {
		private ImageView mImage;
	}
	/*private Bitmap getPropThumnail(int id){
		Drawable d = mContext.getResources().getDrawable(id);
		Bitmap b = BitmapUtil.drawableToBitmap(d);
//		Bitmap bb = BitmapUtil.getRoundedCornerBitmap(b, 100);
		int w = mContext.getResources().getDimensionPixelOffset(R.dimen.thumnail_default_width);
		int h = mContext.getResources().getDimensionPixelSize(R.dimen.thumnail_default_height);
		
		Bitmap thumBitmap = ThumbnailUtils.extractThumbnail(b, w, h);
		
		return thumBitmap;
	}*/
	public void setSelectIndex(int i){
		selectIndex = i;
	}
}