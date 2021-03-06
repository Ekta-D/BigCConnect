package com.bigc.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.activities.HomeScreen;
import com.bigc.datastorage.Preferences;
import com.bigc.fragments.NewsFeedFragment;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.PostManager;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.PopupOptionHandler;
import com.bigc.models.Post;
import com.bigc.models.Posts;
import com.bigc.models.Tributes;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import eu.janmuller.android.simplecropimage.Util;

/**
 * Created by ENTER on 11-07-2017.
 */
public class NewsFeedsAdapter extends BaseAdapter {

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    private static ImageLoaderConfiguration config;
    public BaseFragment context;
    LayoutInflater inflater;
    List<Posts> posts;
    private static PopupOptionHandler handler = null;
    private static DisplayImageOptions imgDisplayOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true).build();

    private static ImageLoader imageLoader = ImageLoader.getInstance();
    private boolean isClickable = true;

    public NewsFeedsAdapter(BaseFragment context, List<Posts> posts) {
        this.context = context;
        this.posts = new ArrayList<>();
        if (posts != null)
            this.posts.addAll(posts);


        inflater = (LayoutInflater) context.getActivity().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        config = new ImageLoaderConfiguration.Builder(context.getActivity())
                .memoryCacheSize(41943040).diskCacheSize(104857600)
                .threadPoolSize(10).build();


        imageLoader.init(config);
    }

    public void setClickable(boolean isClickable) {
        this.isClickable = isClickable;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (posts.size() == 0)
            return 1;
        else return posts.size();
    }

    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return posts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Log.i("getView", "getView called");
        final Posts user_post = posts.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.newsfeed_item_layout, parent,
                    false);
            holder = new ViewHolder();
            holder.headingOne = (TextView) convertView
                    .findViewById(R.id.newsFeedHeading1);
            holder.dateView = (TextView) convertView.findViewById(R.id.dateView);
            holder.ribbonView = (ImageView) convertView
                    .findViewById(R.id.newsFeedRibbonView);
            holder.statusView = (TextView) convertView
                    .findViewById(R.id.newsFeedMessageView);
            holder.commentCountView = (TextView) convertView
                    .findViewById(R.id.commentCountView);
            holder.commentIconView = (ImageView) convertView
                    .findViewById(R.id.commentImage);
            holder.loveCountView = (TextView) convertView.findViewById(R.id.loveCount);
            holder.loveIconView = (ImageView) convertView.findViewById(R.id.loveImage);
            holder.picView = (ImageView) convertView
                    .findViewById(R.id.newsFeedPicView);
            holder.optionView = (ImageView) convertView.findViewById(R.id.optionView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        ParseUser owner = post.getParseUser(DbConstants.USER);

        String user = posts.get(position).getUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).child(user);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {

                } else {

                    String key = dataSnapshot.getKey();
                    Map<Object, Object> user_values = (Map<Object, Object>) dataSnapshot.getValue();
                    setValues(context.getActivity(), user_values, holder, user_post);
                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                long count = dataSnapshot.getChildrenCount();
//
//                if (dataSnapshot.getValue() == null) {
//
//                } else {
//
//                    String key = dataSnapshot.getKey();
//                    Map<Object, Object> user_values = (Map<Object, Object>) dataSnapshot.getValue();
//                    setValues(context, user_values, holder, user_post);
//                }
//                Utils.hideProgress();
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });


//        holder.headingOne.setText(owner.getString(DbConstants.NAME));
//
//        if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                .ordinal()) {
//            holder.ribbonView.setImageResource(R.drawable.ribbon_supporter);
//        } else if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//                .ordinal()) {
//            holder.ribbonView
//                    .setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
//                            : Utils.fighter_ribbons[owner
//                            .getInt(DbConstants.RIBBON)]);
//        } else {
//            holder.ribbonView
//                    .setImageResource(owner.getInt(DbConstants.RIBBON) < 0 ? R.drawable.ic_launcher
//                            : Utils.survivor_ribbons[owner
//                            .getInt(DbConstants.RIBBON)]);
//        }
//
//        holder.statusView.setText(post.getString(DbConstants.MESSAGE));
//
//        holder.loveCountView.setText(String.valueOf(post
//                .getList(DbConstants.LIKES) == null ? 0 : post.getList(
//                DbConstants.LIKES).size()));
//
//        holder.commentCountView.setText(String.valueOf(post
//                .getInt(DbConstants.COMMENTS)));
//
//        if (post.getParseFile(DbConstants.MEDIA) == null) {
//            holder.picView.setVisibility(View.GONE);
//        } else {
//            holder.picView.setImageResource(R.drawable.loading_img);
//            holder.picView.setVisibility(View.VISIBLE);
//
//            imageLoader.displayImage(post.getParseFile(DbConstants.MEDIA)
//                            .getUrl(), holder.picView, imgDisplayOptions,
//                    new SimpleImageLoadingListener() {
//                        @Override
//                        public void onLoadingStarted(String imageUri, View view) {
//                            holder.picView
//                                    .setImageResource(R.drawable.loading_img);
//                            super.onLoadingStarted(imageUri, view);
//                        }
//                    });
//
//        }
//
//        holder.dateView.setText(Utils.getTimeStringForFeed(
//                context.getActivity(), post.getCreatedAt()));
//
//        holder.loveCountView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                onClickLove(post, holder.loveCountView);
//            }
//        });
//
//        holder.loveIconView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                onClickLove(post, holder.loveCountView);
//            }
//        });
//
//        // if () {
//        holder.optionView.setVisibility(View.VISIBLE);
        holder.optionView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Utils.showQuickActionMenu(
//                        NewsfeedAdapter.this,
//                        context.getActivity(),
//                        position,
//                        null,
//                        v,
//                        post.getParseUser(DbConstants.USER)
//                                .getObjectId()
//                                .equals(ParseUser.getCurrentUser()
//                                        .getObjectId()),
//                        DbConstants.Flags.NewsFeed);
                boolean isOwner = posts.get(position).getUser().equalsIgnoreCase
                        (Preferences.getInstance(context.getActivity()).getString(DbConstants.ID));
                Utils.showQuickActionMenu(
                        handler = new PopupOptionHandler() {
                            @Override
                            public void onDelete(int position, Object post) {
                                PostManager.getInstance().deletePost((Posts) post);
//                                if (handler != null)
//                                    handler.onDelete(position, post);
//                                ((HomeScreen) context.getActivity()).onBackPressed();
                                posts.remove(position);
                                Log.i("posts", posts.toString());
                            }

                            @Override
                            public void onEditClicked(int position, Object post) {
                                NewsFeedFragment.currentObject = (Posts) post;
                                Utils.launchEditView(
                                        context.getActivity(),
                                        ((Posts) (post)).getMedia() == null ? Constants.OPERATION_STATUS
                                                : Constants.OPERATION_PHOTO, true, position, (Posts) post);
                            }

                            @Override
                            public void onFlagClicked(int position, Object post) {

                            }

                        },
                        (Activity) context.getActivity(),
                        position,
                        posts.get(position),
                        v,
                        isOwner,
                        DbConstants.Flags.NewsFeed);
            }
        });

        holder.loveCountView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickLove(context.getActivity(), user_post, holder.loveCountView);
                //  Utils.showToast(context.getActivity(), "hey");
            }
        });
        return convertView;
    }

    public void setValues(final Context context, Map<Object, Object> user_values, final ViewHolder holder, final Posts posts) {
        holder.headingOne.setText(String.valueOf(user_values.get(DbConstants.NAME)));

        String user_id = String.valueOf(user_values.get(DbConstants.ID));
        String email = String.valueOf(user_values.get(DbConstants.EMAIL));


        Log.i("crashed_details", user_id + " " + email);

        String type_string = String.valueOf(user_values.get(DbConstants.TYPE));
        int type = 0;
        if (type_string.equalsIgnoreCase("null")) {
            type_string = "0";

        } else {
            type = Integer.parseInt(type_string);
        }
        String ribbon_string = String.valueOf(user_values.get(DbConstants.RIBBON));
        int ribbon = 0;

        if (ribbon_string.equalsIgnoreCase("null")) {
            ribbon_string = "0";

        } else {
            ribbon = Integer.parseInt(ribbon_string);
        }
        if (type == 1)//supporter
        {
            holder.ribbonView.setImageResource(R.drawable.ribbon_supporter);
        } else if (type == 2)//fighter
        {
            holder.ribbonView
                    .setImageResource(ribbon < 0 ? R.drawable.ic_launcher
                            : Utils.fighter_ribbons[ribbon]);
        } else {
            holder.ribbonView
                    .setImageResource(ribbon < 0 ? R.drawable.ic_launcher
                            : Utils.survivor_ribbons[ribbon]);
        }
        holder.statusView.setText(posts.getMessage());
        int size;
        size = posts.getLikes() == null ? 0 : posts.getLikes().size();

        holder.loveCountView.setText(String.valueOf(size));

//        holder.commentCountView.setText(String.valueOf(posts.getComments()));
        holder.commentCountView.setText(String.valueOf(posts.getComments()));
        // holder.commentCountView.setText(String.valueOf(Preferences.getInstance(context.getActivity()).getInt(DbConstants.COMMENT_COUNT)));
        if (posts.getMedia().equalsIgnoreCase("") || posts.getMedia().equals(null)) {
            holder.picView.setVisibility(View.GONE);
        } else {
            holder.picView.setImageResource(R.drawable.loading_img);
            holder.picView.setVisibility(View.VISIBLE);

            imageLoader.displayImage(posts.getMedia(), holder.picView, imgDisplayOptions);


        }


        holder.dateView.setText(Utils.getTimeStringForFeed(
                context, Utils.convertStringToDate(posts.getCreatedAt())));


        holder.optionView.setVisibility(View.VISIBLE);

    }

    private void onClickLove(Context context, Posts post, TextView countView) {
        if (!isClickable)
            return;

        if (!isLiked(context, post)) {
            countView.setText(String.valueOf(post.getLikes() == null ? 1
                    : post.getLikes().size() + 1));
            ArrayList<String> likes = new ArrayList<>();
            likes.add(Preferences.getInstance(context.getApplicationContext()).getString(DbConstants.ID));
            post.setLikes(likes);

            Log.e("Likes", post.getLikes().size() + "--");
            PostManager.getInstance().likePost(likes, post);
        }

    }

    private boolean isLiked(Context context, Posts post) {

        List<String> likes = post.getLikes();
        if (likes == null)
            return false;
        else
            return likes.contains(Preferences.getInstance(context).getString(DbConstants.ID));
    }

    public Date getLastItemDate() {
        if (posts.size() == 0)
            return null;
        return new Date(posts.get(posts.size() - 1).getCreatedAt());
    }

    public void setData(Collection<Posts> stories) {
        this.posts.clear();
        if (stories == null)
            return;
        this.posts.addAll(stories);
        notifyDataSetChanged();
    }

    public void addItems(Collection<Posts> posts, boolean atStart) {

        if (posts == null)
            return;

        if (atStart)
            this.posts.addAll(0, posts);
        else
            this.posts.addAll(posts);

        notifyDataSetChanged();
    }

    public void addItem(Posts post) {
        if (post == null)
            return;
        this.posts.add(0, post);
        notifyDataSetChanged();
    }


    public static class ViewHolder {

        public TextView headingOne;
        public TextView dateView;
        public ImageView ribbonView;
        public TextView statusView;
        public ImageView picView;
        public TextView commentCountView;
        public ImageView commentIconView;
        public ImageView loveIconView;
        public TextView loveCountView;
        public ImageView optionView;
    }

    public void updateItem(int position, Posts item) {
        if (position >= 0 && position < posts.size()) {
            posts.set(position, item);
            notifyDataSetChanged();
        }
    }


}
