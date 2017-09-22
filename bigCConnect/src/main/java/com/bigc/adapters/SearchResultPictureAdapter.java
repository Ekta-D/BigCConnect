package com.bigc.adapters;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigc.datastorage.Preferences;
import com.bigc.fragments.CategorySurvivorsFragment;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.DbConstants;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.SearchResultBaseAdapter;
import com.bigc.models.Users;
import com.bigc_connect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class SearchResultPictureAdapter extends SearchResultBaseAdapter {

    private DisplayImageOptions options = new DisplayImageOptions.Builder()
            .cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(true)
            .build();
    int ribbon;

    public SearchResultPictureAdapter(Context context,
                                      List<Users> activeConnections,
                                      List<Users> pendingConnections, List<Users> data) {
        super(context, R.layout.listitem_search_result_with_picture,
                activeConnections, pendingConnections, data);

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final SurvivorSearchViewHolder holder;
        final Users user = data.get(position);

        if (view == null) {
            view = inflater
                    .inflate(R.layout.listitem_search_result_with_picture,
                            parent, false);
            holder = new SurvivorSearchViewHolder();
            holder.addOption = (ImageView) view.findViewById(R.id.addOption);
            holder.ribbonView = (ImageView) view.findViewById(R.id.ribbonView);
            holder.nameView = (TextView) view.findViewById(R.id.nameView);
            holder.descView = (TextView) view
                    .findViewById(R.id.descriptionView);
            view.setTag(holder);
        } else {
            holder = (SurvivorSearchViewHolder) view.getTag();
        }

        holder.indexInNewAddedConnections = Utils.getUserIndex(user,
                newAddedConnections);
        holder.indexInActiveConnections = Utils.getUserIndex(user,
                activeConnections);
        holder.indexInPendingConnections = Utils.getUserIndex(user,
                pendingConnections);
        holder.indexInRemovedConnections = Utils.getUserIndex(user,
                removedConnections);

        holder.nameView.setText(user.getName());
        String stage = user.getStage();
        stage = stage == null ? "" : stage;
        String loc = user.getLocation();

        String desc = (stage.length() > 0 ? stage.concat(", ") : "")
                .concat(loc == null ? "" : loc);
        holder.descView.setText(desc);
//		boolean supporter = user.getType() == Constants.USER_TYPE.SUPPORTER
//				.ordinal();

        int ribbon = user.getRibbon();
        if (ribbon >= 0) {

//                if (ProfileFragment.user.getInt(DbConstants.TYPE) == Constants.USER_TYPE.FIGHTER
//                        .ordinal())
            if (user.getType()==Constants.IS_FIGHTER) {
                holder.ribbonView.setImageResource(Utils.fighter_ribbons[ribbon]);
            } else {
                holder.ribbonView.setImageResource(Utils.survivor_ribbons[ribbon]);
            }
            // ribbonView.setImageResource(Utils.survivor_ribbons[ribbon]);
        } else {
            holder.ribbonView.setImageResource(R.drawable.ic_launcher);
        }
//		if (user.getProfile_picture() == null) {
//
//			holder.ribbonView.setImageResource(Utils.fighter_ribbons[ribbon]);
//
//		}
//
//		else {
//
//			String url = user.getProfile_picture();
//			if (url.length() > 0) {
//				ImageLoader.getInstance().displayImage(url, holder.ribbonView,
//						options);
//			} else {
//				holder.ribbonView.setImageResource( Utils.survivor_ribbons[CategorySurvivorsFragment.ribbon]);
//			}
//
//		}

//		if ((isSupporterUser && supporter)
        if ((isSupporterUser)
                || Preferences.getInstance(getContext().getApplicationContext()).getString(DbConstants.ID)
                .equals(user.getObjectId())) {
            holder.addOption.setVisibility(View.GONE);
        } else {
            holder.addOption.setVisibility(View.VISIBLE);
            if (holder.indexInNewAddedConnections >= 0) {
                holder.addOption
                        .setImageResource(R.drawable.ic_connect_pending);
                holder.addOption
                        .setContentDescription(SearchResultAdapter.TAG_WAITING);

            } else if (holder.indexInActiveConnections >= 0
                    && holder.indexInRemovedConnections < 0) {
                holder.addOption.setImageResource(R.drawable.ic_connected);
                holder.addOption
                        .setContentDescription(SearchResultAdapter.TAG_CONNECTED);

            } else if (holder.indexInPendingConnections >= 0
                    && holder.indexInRemovedConnections < 0) {
                holder.addOption
                        .setImageResource(R.drawable.ic_connect_pending);
                holder.addOption
                        .setContentDescription(SearchResultAdapter.TAG_WAITING);

            } else {
                holder.addOption.setImageResource(R.drawable.ic_connect);
                holder.addOption
                        .setContentDescription(SearchResultAdapter.TAG_NOT_CONNECTED);
            }

            holder.addOption.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (v.getContentDescription().toString()
                            .equals(SearchResultAdapter.TAG_NOT_CONNECTED)) {

                        if (holder.indexInRemovedConnections < 0) {
                            newAddedConnections.add(user);
                            holder.indexInNewAddedConnections = newAddedConnections
                                    .size() - 1;
                        } else {
                            removedConnections
                                    .remove(holder.indexInRemovedConnections);
                            holder.indexInRemovedConnections = -1;
                        }

                        holder.addOption
                                .setContentDescription(SearchResultAdapter.TAG_WAITING);
                        holder.addOption
                                .setImageResource(R.drawable.ic_connect_pending);
                    } else {
                        if (holder.indexInNewAddedConnections < 0) {
                            removedConnections.add(data.get(position));
                            holder.indexInRemovedConnections = removedConnections
                                    .size() - 1;
                        } else {
                            newAddedConnections
                                    .remove(holder.indexInNewAddedConnections);
                            holder.indexInNewAddedConnections = -1;
                        }

                        holder.addOption
                                .setContentDescription(SearchResultAdapter.TAG_NOT_CONNECTED);
                        holder.addOption
                                .setImageResource(R.drawable.ic_connect);
                    }
                }
            });
        }
        return view;
    }

    public static class SurvivorSearchViewHolder {
        public int indexInRemovedConnections;
        public int indexInActiveConnections;
        public int indexInPendingConnections;
        public int indexInNewAddedConnections;
        public ImageView ribbonView;
        public ImageView addOption;
        public TextView nameView;
        public TextView descView;
    }
}
