package com.bigc.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.android.ex.chips.BaseRecipientAdapter;
import com.android.ex.chips.RecipientEditTextView;
import com.android.ex.chips.RecipientEntry;
import com.bigc.general.classes.Constants;
import com.bigc.general.classes.Utils;
import com.bigc.interfaces.BaseFragment;
import com.bigc_connect.R;

public class AddTributeDialog extends Dialog implements
		android.view.View.OnClickListener {
	private Context context;
	private RecipientEditTextView shareUsers;
	private EditText ageView;
	private BaseFragment caller;
	private static com.android.ex.chips.Users targetUser = null;
	private static int userAge = -1;

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

		shareUsers = (RecipientEditTextView) findViewById(R.id.UsersInputView);

		shareUsers.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		shareUsers.setThreshold(1);
		shareUsers.setAdapter(new BaseRecipientAdapter(context, 4, Utils
				.loadConnectionChips()) {
		});

		findViewById(R.id.addButton).setOnClickListener(this);
		findViewById(R.id.cancelButton).setOnClickListener(this);
		ageView = (EditText) findViewById(R.id.AgeInputView);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.addButton:
			// Pass event to parent
			targetUser = null;
			for (RecipientEntry e : shareUsers.getChosenRecipients()) {
				targetUser = e.getUser();
			}
			if (targetUser == null) {
				Toast.makeText(getContext(), "Add user", Toast.LENGTH_LONG)
						.show();
				return;
			} else if (ageView.getText().length() == 0) {
				Toast.makeText(getContext(), "Enter age", Toast.LENGTH_LONG)
						.show();
				return;
			}
			userAge = Integer.valueOf(ageView.getText().toString());
			dismiss();
			Utils.launchPostView(caller.getActivity(),
					Constants.OPERATION_TRIBUTE);
			break;
		case R.id.cancelButton:
			dismiss();
			break;
		}

	}
}
