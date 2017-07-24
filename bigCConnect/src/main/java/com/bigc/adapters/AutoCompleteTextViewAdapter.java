package com.bigc.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.bigc.models.Users;
import com.bigc_connect.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ENTER on 24-07-2017.
 */
public class AutoCompleteTextViewAdapter extends ArrayAdapter<Users> {

    Context context;
    int resourse, textViewResourceid;
    List<Users> items, tempItems, suggestions;

    public AutoCompleteTextViewAdapter(Context context, int resource, int textViewResourceId, List<Users> items) {
        super(context, resource, textViewResourceId);
        this.context = context;
        this.resourse = resource;
        this.textViewResourceid = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<>(items);// this makes the difference.
        suggestions = new ArrayList<Users>(items);
    }

    class ViewHolder {
        TextView textView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        Users user=suggestions.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.autotext_layout, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.auto_text);
            convertView.setTag(holder);
        }
      else{
            holder=(ViewHolder)convertView.getTag();
        }
        holder.textView.setText(user.getName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((Users) resultValue).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Users user : tempItems) {
                    if (user.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(user);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            List<Users> filterList = (ArrayList<Users>) filterResults.values;
            if (filterResults != null && filterResults.count > 0) {
                clear();
                for (Users user : filterList) {
                    add(user);
                    notifyDataSetChanged();
                }
            } else {
                clear();
                notifyDataSetChanged();
            }
        }
    };


}
