package com.malikapps.trakerr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.malikapps.trakerr.DA.User;

import java.util.ArrayList;

/**
 * Created by abdulmalik_khan on 14/09/15.
 */
public class UserCustomAdapter extends ArrayAdapter<User> {

    public ArrayList<User> usersList;

    public UserCustomAdapter(Context context, int textViewResourceId,
                           ArrayList<User> userList) {
        super(context, textViewResourceId, userList);
        this.usersList = new ArrayList<User>();
        this.usersList.addAll(userList);
    }

    private class ViewHolder {
        TextView code;
        CheckBox name;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        final Context context = parent.getContext();
        //Log.v("ConvertView", String.valueOf(position));
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_view_users, null);

            holder = new ViewHolder();
            holder.code = (TextView) convertView.findViewById(R.id.TextViewContactName);
            holder.name = (CheckBox) convertView.findViewById(R.id.checkBoxSelectContact);
            convertView.setTag(holder);

            holder.name.setOnClickListener( new View.OnClickListener() {
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v ;
                    User user = (User) cb.getTag();
                    Toast.makeText(context,
                            "Clicked on Checkbox: " + cb.getText() +
                                    " is " + cb.isChecked(),
                            Toast.LENGTH_LONG).show();
                    user.setSelected(cb.isChecked());
                }
            });
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        User user = usersList.get(position);
        holder.code.setText(" (" +  user.Name + ")");
        holder.name.setText(user.Name );
        holder.name.setChecked(user.getSelected());
        holder.name.setTag(user);

        return convertView;

    }

    @Override
    public void add(User user) {
        super.add(user);
        if(this.usersList == null){
            this.usersList = new ArrayList<User>();
        }
        this.usersList.add(user);
    }
}

