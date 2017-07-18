package com.bigc.adapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.Utils;
import com.bigc.models.Comments;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class CommentsAdapter extends ArrayAdapter<Comments> {

    private List<Comments> comments;
    private LayoutInflater inflater;
    private Context context;

    //	public CommentsAdapter(Context context) {
//		super(context, R.layout.list_item_comment);
//		this.comments = new ArrayList<ParseObject>();
//		this.context = context;
//
//		inflater = (LayoutInflater) context
//				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//	}
    public CommentsAdapter(Context context, List<Comments> comments) {
        super(context, R.layout.list_item_comment);
        this.comments = comments;
        this.context = context;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public void setData(List<Comments> comments) {
   //     this.comments.clear();
        if (comments == null)
            return;
        this.comments.addAll(comments);
        notifyDataSetChanged();
    }

    public void addItem(Comments comment) {
        this.comments.add(0, comment);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item_comment, parent, false);
            holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.nameView);
            holder.ribbonView = (ImageView) view.findViewById(R.id.ribbonView);
            holder.commentView = (TextView) view.findViewById(R.id.commentView);
            holder.dateView = (TextView) view.findViewById(R.id.dateView);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        //       ParseObject comment = comments.get(position);
        final Comments comment = comments.get(position);
        // ParseUser owner = comment.getParseUser(DbConstants.USER);
        String owner = comment.getUser();

        FirebaseDatabase.getInstance().getReference().child(DbConstants.USERS).child(owner)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (dataSnapshot.getValue() == null) {

                } else {
                    String key = dataSnapshot.getKey();
                    Users users=dataSnapshot.getValue(Users.class);
                  //  Map<Object, Object> user_values = (Map<Object, Object>) dataSnapshot.getValue();
                    setValues(users,holder,comment);
                }


                Utils.hideProgress();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    public void setValues(Users users,ViewHolder holder,Comments comment)
    {
//        if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
//                .ordinal())
            if (users.getType()==1)
        {
            holder.ribbonView.setImageResource(R.drawable.ribbon_supporter);
        }
//            else if (owner.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//                .ordinal())
            else if (users.getType() ==2)
            {
            holder.ribbonView
                    .setImageResource(users.getRibbon() < 0 ? R.drawable.ic_launcher
                            : Utils.fighter_ribbons[users.getRibbon()]);
        } else {
            holder.ribbonView
                    .setImageResource(users.getRibbon() < 0 ? R.drawable.ic_launcher
                            : Utils.survivor_ribbons[users.getRibbon()]);
        }


        String date = users.getCreatedAt();
        SimpleDateFormat format = new SimpleDateFormat(DbConstants.DATE_FORMAT, Locale.getDefault());
        Date createdDate = null;

        try {
            if (date.contains("T") && date.contains("Z")) {
                date.replace("T", "");
                date.replace("Z", "");
            }
            createdDate = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.dateView.setText(Utils.getTimeStringForFeed(context,
                createdDate));
        holder.nameView.setText(users.getName());
        holder.commentView.setText(comment.getMessage());
    }

    private static class ViewHolder {
        public TextView nameView;
        public TextView dateView;
        public TextView commentView;
        public ImageView ribbonView;
    }
}
