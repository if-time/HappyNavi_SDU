package com.trackersurvey.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackersurvey.happynavi.R;

import java.util.List;
import java.util.Map;

public class SportTypeChangeAdapter extends BaseAdapter implements OnItemClickListener {

    private Context                   context;
    private List<Map<String, Object>> listItems;
    private int pos;

//    int[] imageId = new int[]{R.mipmap.ic_walking,
//            R.mipmap.ic_cycling,
//            R.mipmap.ic_rollerblading,
//            R.mipmap.ic_driving,
//            R.mipmap.ic_train,
//            R.mipmap.others,
//    };
//    String[] title = context.getResources().getStringArray(R.array.sporttype);

    public SportTypeChangeAdapter(Context context, List<Map<String, Object>> listItems, int position) {
        this.context = context;
        this.listItems = listItems;
        this.pos = position;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = LayoutInflater.from(context).inflate(R.layout.sport_dialog_items, null);
            holder.tv_name = convertView.findViewById(R.id.sporttype_title);
            holder.iv_thumb = convertView.findViewById(R.id.sporttype_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Map<String, Object> listItem = listItems.get(position);

        holder.tv_name.setText((CharSequence) listItem.get("title"));
        holder.iv_thumb.setImageResource((Integer) listItem.get("image"));

        Log.i("dongiyuanAga", "getView: posrion" + position + " pos : " + pos);

        if (position == pos - 1) {
//            holder.iv_thumb.setBackgroundColor(Color.parseColor("#99cc33"));
            convertView.setBackgroundColor(Color.parseColor("#99cc33"));
        }

        return convertView;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }


    // 定义一个视图持有者，以便重用列表项的视图资源
    public final class ViewHolder {
        public TextView  tv_name;
        public ImageView iv_thumb;
    }
}
