package com.bigc.receivers;

public class NotificationReceiver /*extends ParsePushBroadcastReceiver implements
		MessageObservable*/ {

	/*private static MessageObserver observer;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction() != null) {
			JSONObject json;
			try {
				json = new JSONObject(intent.getExtras().getString(
						"com.parse.Data"));
				String action = json.getString("action");
				if (Constants.ACTION_FRIEND_REQUEST.equals(action)
						|| Constants.ACTION_NEWS_FEED.equals(action)
						|| Constants.ACTION_TRIBUTE.equals(action)) {

					String objId = "";
					if (!json.isNull("object")) {
						objId = json.getString("object");
					}

					Utils.buildRequestNotification(context, objId,
							json.getString(DbConstants.ALERT), action);

				} else if (Constants.ACTION_MESSAGE.equals(action)) {

					ParseObject obj = new ParseObject(DbConstants.TABLE_MESSAGE);
					ParseQuery<ParseUser> uQuery = ParseUser.getQuery();
					ParseUser u;
					try {
						u = uQuery.get(json.getString(DbConstants.SENDER));
					} catch (ParseException e) {
						e.printStackTrace();
						u = new ParseUser();
						u.setObjectId(json.getString(DbConstants.SENDER));
					}
					obj.put(DbConstants.SENDER, u);
					obj.put(DbConstants.MESSAGE, json.getString("body"));
					obj.put(DbConstants.USER1, u);
					obj.put(DbConstants.USER2, ParseUser.getCurrentUser());

					if (!notifyObserver(obj, null))
						Utils.buildRequestNotification(context, null,
								json.getString(DbConstants.ALERT), action);

					((Vibrator) context
							.getSystemService(Context.VIBRATOR_SERVICE))
							.vibrate(500);

				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void bindObserver(MessageObserver ob) {
		observer = ob;
	}

	@Override
	public void freeObserver() {
		observer = null;
	}

	@Override
	public boolean notifyObserver(ParseObject message, ParseUser user) {
		if (observer != null)
			return observer.onMessageReceive(message, user);

		return false;
	}*/
}
