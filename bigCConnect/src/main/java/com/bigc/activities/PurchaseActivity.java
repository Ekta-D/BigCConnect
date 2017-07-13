package com.bigc.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.billing.util.IabHelper;
import com.android.billing.util.IabResult;
import com.android.billing.util.Inventory;
import com.android.billing.util.Purchase;
import com.bigc.datastorage.Preferences;
import com.bigc.general.classes.Constants;

public class PurchaseActivity extends Activity {

	// Debug tag, for logging
	static final String TAG = "BigC-Connect";

	// Does the user have the premium upgrade?
	boolean mIsPremium = false;

	// SKUs for our products: the premium upgrade (non-consumable)
	static final String SKU_PREMIUM = "premium";

	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 10001;

	// The helper object
	IabHelper mHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/*
		 * Instead of just storing the entire literal string here embedded in
		 * the program, construct the key at runtime from pieces or use bit
		 * manipulation (for example, XOR with some other string) to hide the
		 * actual key. The key itself is not secret information, but we don't
		 * want to make it easy for an attacker to replace the public key with
		 * one of their own and then fake messages from the server.
		 */
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgX+F3lCDRtRR0PnYCKppdRDYQM9LSa+l8TJTDgFy3KjRUq2t6+OsGw7KTw41pM9GtPbL/+yEMknTnR4dil4QNhjR/f57MWHoMgs0BpCFX40M1TwFRd/6WVJVGvFd1Hmc2fBxkH1TzbkmY+QS5DHUwDW+1yyQdsGqTCuz9zO9Ev71LUa+509F5HadGwtKFQ7AL1xNFnMuMrlhkKiGJJoDcQZ534bOpA5Zm+xDTJ/AlKIipwdYL6rhbZFwypNCwpcKiBay1sGIMoVaZXv9hcXSu/tJk5ASTzlunhOxlGu3qLlGJx4K/sMJigiSMB/hrHKLP67ZyEyr2/MIkzfobKGGSQIDAQAB";

		// Create the helper, passing it our context and the public key to
		// verify signatures with
		Log.d(TAG, "Creating IAB helper.");
		mHelper = new IabHelper(this, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set
		// this to false).
		mHelper.enableDebugLogging(true);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		Log.d(TAG, "Starting setup.");
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				Log.d(TAG, "Setup finished.");

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain("Problem setting up in-app billing: " + result);
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				Log.e(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);

				onUpgradeAppButtonClicked(null);
			}
		});
	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			if (mHelper == null)
				return;

			// Is it a failure?
			if (result.isFailure()) {
				complain("Failed to query inventory: " + result);
				return;
			}

			Log.d(TAG, "Query inventory was successful.");

			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			// Purchase premium = inventory.getPurchase(SKU_PREMIUM);
			// if (premium != null && verifyDeveloperPayload(premium)) {
			//
			// mHelper.consumeAsync(inventory.getPurchase(SKU_PREMIUM),
			// mConsumeFinishedListener);
			// return;
			// }
		}
	};

	// User clicked the "Upgrade to Premium" button.
	public void onUpgradeAppButtonClicked(View view) {
		Log.d(TAG,
				"Upgrade button clicked; launching purchase flow for upgrade.");

		/*
		 * TODO: for security, generate your payload here for verification. See
		 * the comments on verifyDeveloperPayload() for more info. Since this is
		 * a SAMPLE, we just use an empty string, but on a production app you
		 * should carefully generate this.
		 */
		String payload = "";

		if (mHelper != null)
			mHelper.flagEndAsync();

		mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
				mPurchaseFinishedListener, payload);
	}

	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			Log.d(TAG, "Purchase finished: " + result + ", purchase: "
					+ purchase);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			if (result.isFailure()) {
				if (result.getResponse() == 7
						&& result.getMessage().contains("Already Owned")) {
					onPurchaseComplete();
				} else {
					complain("Error purchasing: " + result);
				}
				return;
			}

			if (!verifyDeveloperPayload(purchase)) {
				complain("Error purchasing. Authenticity verification failed.");
				return;
			}

			Log.d(TAG, "Purchase successful.");

			alert(purchase.getSku());
			if (purchase.getSku().equals(SKU_PREMIUM)) {
				// bought the premium upgrade!
				Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
				// mIsPremium = true;
				onPurchaseComplete();
				// mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}
		}
	};

	// Called when consumption is complete
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			Log.d(TAG, "Consumption finished. Purchase: " + purchase
					+ ", result: " + result);

			// if we were disposed of in the meantime, quit.
			if (mHelper == null)
				return;

			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in
				// our
				// game world's logic, which in our case means filling the gas
				// tank a bit
				onPurchaseComplete();
				Log.d(TAG, "Consumption successful. Provisioning.");
			} else {
				complain("Error while consuming: " + result);
			}
			Log.d(TAG, "End consumption flow.");
		}
	};

	private void onPurchaseComplete() {
		Preferences.getInstance(PurchaseActivity.this).save(Constants.PREMIUM,
				true);
		alert("Thanks for upgrading to Premium version.");
	}

	void complain(String message) {
		Log.e(TAG, "**** IAP Error: " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				PurchaseActivity.this.finish();
			}
		});
		Log.d(TAG, "Showing alert dialog: " + message);
		Dialog dialog = bld.create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	/** Verifies the developer payload of a purchase. */
	boolean verifyDeveloperPayload(Purchase p) {
		// String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mHelper == null)
			return;

		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		} else {
			// Log.e("Else", "onActivityResult handled by IABUtil.");
		}
	}
}
