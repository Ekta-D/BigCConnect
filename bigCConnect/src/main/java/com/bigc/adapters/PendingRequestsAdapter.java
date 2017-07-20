package com.bigc.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigc.activities.LoginActivity;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.Utils;
import com.bigc.models.ConnectionsModel;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PendingRequestsAdapter extends ArrayAdapter<ConnectionsModel> {

    private List<ConnectionsModel> data;
    private LayoutInflater inflater;

    public PendingRequestsAdapter(Context context) {
        super(context, R.layout.item_request_layout);
        data = new ArrayList<ConnectionsModel>();
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<ConnectionsModel> requests) {
        data.clear();
        if (requests != null)
            data.addAll(requests);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;

        final ConnectionsModel object = data.get(position);

        if (view == null) {
            view = inflater
                    .inflate(R.layout.item_request_layout, parent, false);
            holder = new ViewHolder();
            holder.profileView = (ImageView) view
                    .findViewById(R.id.profilePictureView);
            holder.iconView = (ImageView) view.findViewById(R.id.iconView);
            holder.nameView = (TextView) view.findViewById(R.id.nameView);
            holder.locationView = (TextView) view
                    .findViewById(R.id.locationView);
            holder.confirmBtn = (TextView) view.findViewById(R.id.confirmBtn);
            holder.declineBtn = (TextView) view.findViewById(R.id.declineBtn);
            holder.progressParent = (LinearLayout) view
                    .findViewById(R.id.progressParent);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }


        Preferences.getInstance(getContext()).getString(DbConstants.NAME);
        final DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
        mReference.child(DbConstants.USERS).child(object.getFrom()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Users fromUser = dataSnapshot.getValue(Users.class);
                    holder.nameView.setText((fromUser.getName()) == null ? "" : fromUser.getName());
                    holder.locationView.setText((fromUser.getLocation() == null) ? "" : fromUser.getLocation());
                    if (fromUser.getType() == Constants.USER_TYPE.SUPPORTER.ordinal())
                        holder.iconView.setImageResource(R.drawable.ribbon_supporter);
                    else {
                        int ribbon = fromUser.getRibbon();
                        if (ribbon >= 0)
                            if (Preferences.getInstance(getContext()).getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
                                    .ordinal()) {
                                holder.iconView
                                        .setImageResource(Utils.fighter_ribbons[ribbon]);
                            } else {
                                holder.iconView
                                        .setImageResource(Utils.survivor_ribbons[ribbon]);
                            }
                    }
                    if (fromUser.getProfile_picture() != null) {
                        String url = fromUser.getProfile_picture();
                        if (url.length() > 0)
                            ImageLoader.getInstance().displayImage(url, holder.profileView,
                                    Utils.normalDisplayOptions);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        holder.confirmBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.progressParent.setVisibility(View.VISIBLE);
                Map<String, Object> postValues = new HashMap<String, Object>();
                postValues.put(DbConstants.STATUS, true);
                mReference.child(DbConstants.TABLE_CONNECTIONS).child(object.getObjectId()).updateChildren(postValues, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError==null) {
                            Toast.makeText(getContext(), "Update saved.", Toast.LENGTH_SHORT).show();
                            holder.progressParent.setVisibility(View.GONE);

                            data.remove(position);
                            notifyDataSetChanged();
                        }

                    }
                });

            }
        });

		holder.declineBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.progressParent.setVisibility(View.VISIBLE);
                Map<String, Object> postValues = new HashMap<String, Object>();
                postValues.put(DbConstants.STATUS, true);
                mReference.child(DbConstants.TABLE_CONNECTIONS).child(object.getObjectId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Update saved.", Toast.LENGTH_SHORT).show();
                        holder.progressParent.setVisibility(View.GONE);

                        data.remove(position);
                        notifyDataSetChanged();
                    }
                });
/*
                mReference.updateChildren(postValues, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError==null) {
                            Toast.makeText(getContext(), "Update saved.", Toast.LENGTH_SHORT).show();
                            holder.progressParent.setVisibility(View.GONE);

                                data.remove(position);
                                notifyDataSetChanged();
                        }

                    }
                });
*/

			}
		});

		/*final ParseObject object = data.get(position);
        if (view == null) {
			view = inflater
					.inflate(R.layout.item_request_layout, parent, false);
			holder = new ViewHolder();
			holder.profileView = (ImageView) view
					.findViewById(R.id.profilePictureView);
			holder.iconView = (ImageView) view.findViewById(R.id.iconView);
			holder.nameView = (TextView) view.findViewById(R.id.nameView);
			holder.locationView = (TextView) view
					.findViewById(R.id.locationView);
			holder.confirmBtn = (TextView) view.findViewById(R.id.confirmBtn);
			holder.declineBtn = (TextView) view.findViewById(R.id.declineBtn);
			holder.progressParent = (LinearLayout) view
					.findViewById(R.id.progressParent);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.nameView.setText(object.getParseUser(DbConstants.FROM)
				.getString(DbConstants.NAME));

		if (object.getParseUser(DbConstants.FROM).getString(
				DbConstants.LOCATION) != null)
			holder.locationView.setText(object.getParseUser(DbConstants.FROM)
					.getString(DbConstants.LOCATION));

		// Setting icon
		if (object.getParseUser(DbConstants.FROM).getInt(DbConstants.TYPE) == Constants.USER_TYPE.SUPPORTER
				.ordinal()) {
			holder.iconView.setImageResource(R.drawable.ribbon_supporter);
		} else {
			int ribbon = object.getParseUser(DbConstants.FROM).getInt(
					DbConstants.RIBBON);
			if (ribbon >= 0)
				if (ParseUser.getCurrentUser().getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
						.ordinal()) {
					holder.iconView
							.setImageResource(Utils.fighter_ribbons[ribbon]);
				} else {
					holder.iconView
							.setImageResource(Utils.survivor_ribbons[ribbon]);
				}

		}

		if (object.getParseUser(DbConstants.FROM).getParseFile(
				DbConstants.PROFILE_PICTURE) != null) {
			String url = object.getParseUser(DbConstants.FROM)
					.getParseFile(DbConstants.PROFILE_PICTURE).getUrl();
			if (url.length() > 0)
				ImageLoader.getInstance().displayImage(url, holder.profileView,
						Utils.normalDisplayOptions);
		}

		holder.confirmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.progressParent.setVisibility(View.VISIBLE);
				object.put(DbConstants.STATUS, true);
				object.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException e) {
						Log.e("Confirm", e + "-");
						holder.progressParent.setVisibility(View.GONE);
						if (e == null) {
							data.remove(position);
							notifyDataSetChanged();
						}
					}
				});
			}
		});

		holder.declineBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				holder.progressParent.setVisibility(View.VISIBLE);
				object.deleteInBackground(new DeleteCallback() {

					@Override
					public void done(ParseException e) {
						Log.e("Confirm", e + "-");
						holder.progressParent.setVisibility(View.GONE);
						if (e == null) {
							data.remove(position);
							notifyDataSetChanged();
						}
					}
				});
			}
		});*/

        return view;
    }

    public class ViewHolder {
        public ImageView profileView;
        public ImageView iconView;
        public TextView nameView;
        public TextView locationView;
        public TextView confirmBtn;
        public TextView declineBtn;
        public LinearLayout progressParent;
    }

}
