package com.malikapps.trakerr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.malikapps.trakerr.DA.Group;
import com.malikapps.trakerr.R;

import java.util.ArrayList;

/**
 * Created by abdulmalik.khan on 23/09/15.
 */
public class GroupCustomAdapter extends ArrayAdapter<Group> {

    public ArrayList<Group> groupsList;

    public GroupCustomAdapter(Context context, int textViewResourceId,
                              ArrayList<Group> groupList) {
        super(context, textViewResourceId, groupList);
        this.groupsList = new ArrayList<Group>();
        this.groupsList.addAll(groupList);
    }

    private class ViewHolder {
        TextView groupName;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        final Context context = parent.getContext();
        //Log.v("ConvertView", String.valueOf(position));
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_view_groups, null);

            holder = new ViewHolder();
            holder.groupName = (TextView) convertView.findViewById(R.id.TextViewGroupName);
            convertView.setTag(holder);

            /*holder.groupName.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    TextView textView = (TextView) v ;
                    Group currentGroup = (Group) textView.getTag();
                    Toast.makeText(context,
                            "Clicked on Checkbox: " + textView.getText(),
                            Toast.LENGTH_LONG).show();
                    //TODO: open Map from here
                }
            });*/
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Group group = groupsList.get(position);
        holder.groupName.setText("" +  group.Name + "");
        holder.groupName.setTag(group);
        return convertView;

    }

    @Override
    public void add(Group group) {
        super.add(group);
        if(this.groupsList == null){
            this.groupsList = new ArrayList<Group>();
        }
        this.groupsList.add(group);
    }
}
