package com.junglee.app;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.jungleeclick.R;

/**
 * Created by shrankur on 1/9/14.
 */
public class NavigationListAdaptor extends BaseExpandableListAdapter {

    private NavigationDrawerFragment context;
    private List<String> categories;
    private Map<String, List<String>> categoryCollections;
    private Map<String, String> urlMap;

    public NavigationListAdaptor(NavigationDrawerFragment context, List<String> categories, Map<String, List<String>> categoryCollections, Map<String, String> urlMap){
        this.context = context;
        this.categories = categories;
        this.categoryCollections = categoryCollections;
        this.urlMap = urlMap;
    }

    @Override
    public int getGroupCount() {
        return categoryCollections.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return categoryCollections.get(categories.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return categories.get(i);
    }

    @Override
    public Object getChild(int i, int i2) {
        return categoryCollections.get(categories.get(i)).get(i2);
    }

    public String getUrl(int i, int i2) {
        return urlMap.get(this.getChild(i, i2));
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i2) {
        return i2;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String catName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.nav_entry);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(catName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String itemName = (String) getChild(groupPosition, childPosition);
        LayoutInflater inflater = context.getActivity().getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.child_item, null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.nav_entry);
        item.setText(itemName);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2) {
        return true;
    }
}
