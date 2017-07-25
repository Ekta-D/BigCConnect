package com.bigc.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.RecipientEntry;
import com.bigc.adapters.AutoCompleteTextViewAdapter;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc.interfaces.ConnectionExist;
import com.bigc.models.Users;
import com.bigc_connect.R;

import java.util.ArrayList;

import eu.janmuller.android.simplecropimage.Util;

public class AddTributeDialog extends Dialog implements
        android.view.View.OnClickListener {
    private Context context;
    private MultiAutoCompleteTextView shareUsers;
    private EditText ageView;
    private BaseFragment caller;
    private static com.android.ex.chips.Users targetUser = null;
    private static int userAge = -1;
    Users selectedUser = null;

    public static com.android.ex.chips.Users getTargetUser() {
        return targetUser;
    }

    public static int getTargetUserAge() {
        return userAge;
    }

    public AddTributeDialog(Context context, BaseFragment caller) {
        super(context, R.style.dlg_priority);
        this.context = context;
        this.caller = caller;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.layout_dialog_add_tribute);

        shareUsers = (MultiAutoCompleteTextView) findViewById(R.id.UsersInputView);

        shareUsers.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        shareUsers.setThreshold(1);

        final ArrayList<String> connectionNames = new ArrayList<>();
        final ArrayList<Users> active = Utils.loadConnectionChips(getContext());
        for(Users conn: active){
            connectionNames.add(conn.getName());
        }

        AutoCompleteTextViewAdapter autoCompleteTextViewAdapter = new AutoCompleteTextViewAdapter(context, R.layout.autotext_layout, R.id.auto_text, active);
        //ArrayAdapter<String> usersArrayAdapter = new ArrayAdapter<>(context, R.layout.single_textview_listitem, connectionNames);
        shareUsers.setAdapter(autoCompleteTextViewAdapter);
        shareUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUser = (Users) adapterView.getAdapter().getItem(i);
                /*shareUsers.setText(shareUsers.getText().toString().substring(0,shareUsers.getText().toString().length()-1));
                shareUsers.setText(connectionNames.get(i-1));
                shareUsers.setSelection(shareUsers.getText().length());*/

            }
        });

        findViewById(R.id.addButton).setOnClickListener(this);
        findViewById(R.id.cancelButton).setOnClickListener(this);
        ageView = (EditText) findViewById(R.id.AgeInputView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addButton:
                if(!shareUsers.getText().toString().equals("") && selectedUser!=null){
                    Utils.launchPostViewFromTribute(caller.getActivity(),
                            Constants.OPERATION_TRIBUTE, selectedUser);
                } else {
                    Utils.showToast(context, "Please select a valid connection.");
                    shareUsers.setText("");
                    return;
                }

                // Pass event to parent
//                targetUser = null;
//                for (RecipientEntry e : shareUsers.getChosenRecipients()) {
//                    targetUser = e.getUser();
//                }
//                if (targetUser == null) {
//                    Toast.makeText(getContext(), "Add user", Toast.LENGTH_LONG)
//                            .show();
//                    return;
//                } else if (ageView.getText().length() == 0) {
//                    Toast.makeText(getContext(), "Enter age", Toast.LENGTH_LONG)
//                            .show();
//                    return;
//                }
//
//                userAge = Integer.valueOf(ageView.getText().toString());
//                dismiss();
                //  Utils.launchPostView(caller.getActivity(),
//                        Constants.OPERATION_TRIBUTE);
                dismiss();
                break;
            case R.id.cancelButton:
                dismiss();
                break;
        }

    }

    public void shareTribute() {

    }


}
