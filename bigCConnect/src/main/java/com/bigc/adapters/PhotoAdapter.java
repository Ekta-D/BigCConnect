package com.bigc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bigc.models.Posts;
import com.bigc_connect.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by ENTER on 11-07-2017.
 */
public class PhotoAdapter extends BaseAdapter {

    private ArrayList<Posts> data;
    private LayoutInflater inflater;
    private static ImageLoaderConfiguration config;
    Context context;
    String image_url;
    private static DisplayImageOptions imgDisplayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true).build();
    private static ImageLoader imageLoader = ImageLoader.getInstance();

    public PhotoAdapter(ArrayList<Posts> data, Context context) {
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.data = data;
        config = new ImageLoaderConfiguration.Builder(context)
                .memoryCacheSize(41943040).diskCacheSize(104857600)
                .threadPoolSize(10).build();

        imageLoader.init(config);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final ViewHolder holder;

        if (row == null) {
            row = inflater.inflate(R.layout.row_grid, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        image_url = data.get(position).getMedia();
        ImageLoader.getInstance().displayImage(image_url, holder.image,
                imgDisplayOptions, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.image.setImageResource(R.drawable.loading_img);
                        super.onLoadingStarted(imageUri, view);
                    }
                });

//        imageLoader.displayImage(
//                data.get(position).getParseFile(DbConstants.MEDIA).getUrl(),
//                holder.image, imgDisplayOptions,
//                new SimpleImageLoadingListener() {
//                    @Override
//                    public void onLoadingStarted(String imageUri, View view) {
//                        holder.image.setImageResource(R.drawable.loading_img);
//                        super.onLoadingStarted(imageUri, view);
//                    }
//                });

        return row;
    }
}
