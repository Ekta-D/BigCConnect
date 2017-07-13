package com.bigc.general.classes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bigc.interfaces.MessageObservable;
import com.bigc.interfaces.MessageObserver;
import com.bigc.interfaces.ProgressHandler;
import com.bigc.interfaces.UploadPostObservable;
import com.bigc.interfaces.UploadPostObserver;
import com.bigc.interfaces.UploadStoryObserver;
import com.parse.DeleteCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

public class PostManager implements UploadPostObservable, MessageObservable {

	private static PostManager manager = new PostManager();

	private UploadPostObserver postObserver;
	private UploadPostObserver tributeObserver;
	private UploadStoryObserver storyObserver;
	private MessageObserver messageObserver;

	private PostManager() {

	}

	public static PostManager getInstance() {
		return manager;
	}

	public void addStory(final ParseObject story,
			final ProgressHandler progressHandler) {

		progressHandler.switchToProgressMode();
		if (story.getParseFile(DbConstants.MEDIA) != null) {
			ParseFile file = story.getParseFile(DbConstants.MEDIA);
			file.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					progressHandler.updateLabelToProcessingMessage();
					saveStory(story, progressHandler);
				}
			}, new ProgressCallback() {

				@Override
				public void done(Integer percentDone) {
					progressHandler.updateProgress(percentDone);
				}
			});
		} else {
			progressHandler.updateProgress(95);
			saveStory(story, progressHandler);
		}
	}

	private void saveStory(final ParseObject storyObject,
			final ProgressHandler progressHandler) {
		storyObject.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				progressHandler.onUploadComplete();
				if (e != null) {
					storyObject.saveEventually();
				}
				notifyStoryObservers(storyObject);
			}
		});
	}

	public void deletePost(final ParseObject post) {
		post.deleteInBackground(new DeleteCallback() {

			@Override
			public void done(ParseException e) {
				if (e != null) {
					post.deleteEventually();
				}
			}
		});
	}

	public void addPost(final ParseObject post,
			final ProgressHandler progressHandler) {

		progressHandler.switchToProgressMode();
		if (post.getParseFile(DbConstants.MEDIA) != null) {
			ParseFile file = post.getParseFile(DbConstants.MEDIA);
			file.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					progressHandler.updateLabelToProcessingMessage();
					savePost(post, progressHandler);
				}
			}, new ProgressCallback() {

				@Override
				public void done(Integer percentDone) {
					progressHandler.updateProgress(percentDone);
				}
			});
		} else {
			progressHandler.updateProgress(95);
			savePost(post, progressHandler);
		}
	}

	private void savePost(final ParseObject post,
			final ProgressHandler progressHandler) {
		progressHandler.updateLabelToProcessingMessage();
		post.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				progressHandler.onUploadComplete();
				if (e == null) {
					notifyFeedObservers(post);
					notifyPostUsers(
							post,
							ParseUser.getCurrentUser().getString(
									DbConstants.NAME)
									+ " posted a status.");
				} else {
					notifyFeedObservers(null);
				}
			}
		});
	}

	public void addTribute(final ParseObject tribute,
			final ProgressHandler progressHandler) {

		progressHandler.switchToProgressMode();
		if (tribute.getParseFile(DbConstants.MEDIA) != null) {
			ParseFile file = tribute.getParseFile(DbConstants.MEDIA);
			file.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					progressHandler.updateLabelToProcessingMessage();
					saveTribute(tribute, progressHandler);
				}
			}, new ProgressCallback() {

				@Override
				public void done(Integer percentDone) {
					progressHandler.updateProgress(percentDone);
				}
			});
		} else {
			progressHandler.updateProgress(95);
			saveTribute(tribute, progressHandler);
		}
	}

	private void saveTribute(final ParseObject tribute,
			final ProgressHandler progressHandler) {
		tribute.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				progressHandler.onUploadComplete();
				if (e == null) {
					notifyTributeObservers(tribute);
					notifyUser(tribute.getParseUser(DbConstants.TO), tribute
							.getObjectId(), ParseUser.getCurrentUser()
							.getString(DbConstants.NAME)
							+ " added a tribute for you.");
				} else {
					notifyTributeObservers(null);
				}
			}
		});
	}

	public ParseObject commentOnPost(String comment, final ParseObject post) {
		final ParseObject obj = new ParseObject(DbConstants.TABLE_COMMENT);
		obj.put(DbConstants.POST, post);
		obj.put(DbConstants.MESSAGE, comment);
		obj.put(DbConstants.USER, ParseUser.getCurrentUser());
		obj.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				Log.e("Comment", "callback----" + e);
				notifyPostUsers(post,
						ParseUser.getCurrentUser().getString(DbConstants.NAME)
								+ " commented on a post.");
				post.fetchInBackground(new GetCallback<ParseObject>() {

					@Override
					public void done(ParseObject object, ParseException e) {

					}
				});

			}
		});
		return obj;
	}

	public ParseObject commentOnStory(String comment, final ParseObject story) {
		final ParseObject obj = new ParseObject(DbConstants.TABLE_STORY_COMMENT);
		obj.put(DbConstants.POST, story);
		obj.put(DbConstants.MESSAGE, comment);
		obj.put(DbConstants.USER, ParseUser.getCurrentUser());
		obj.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				Log.e("Comment", "callback----" + e);
				story.fetchInBackground(new GetCallback<ParseObject>() {

					@Override
					public void done(ParseObject object, ParseException e) {

					}
				});

			}
		});
		return obj;
	}

	public ParseObject commentOnTribute(String comment,
			final ParseObject tribute) {
		final ParseObject obj = new ParseObject(
				DbConstants.TABLE_TRIBUTE_COMMENT);
		obj.put(DbConstants.POST, tribute);
		obj.put(DbConstants.MESSAGE, comment);
		obj.put(DbConstants.USER, ParseUser.getCurrentUser());
		obj.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				Log.e("Comment", "callback----" + e);
				tribute.fetchInBackground(new GetCallback<ParseObject>() {

					@Override
					public void done(ParseObject object, ParseException e) {

					}
				});
			}
		});
		return obj;
	}

	public void likePost(ParseObject post) {
		post.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {

			}
		});

	}

	public void likeStory(ParseObject story) {
		story.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {

			}
		});

	}

	public void likeTribute(ParseObject story) {
		story.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {

			}
		});

	}

	public void reportProblem(ParseObject problem) {
		problem.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {

			}
		});
	}

	private void notifyPostUsers(ParseObject post, String message) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("post", post.getObjectId());
		params.put(DbConstants.MESSAGE, message);

		ParseCloud.callFunctionInBackground("notifyPostUsers", params,
				new FunctionCallback<Boolean>() {

					@Override
					public void done(Boolean b, ParseException e) {
						Log.e("notifyPostUser", b + " - " + e);
					}
				});

	}

	private void notifyUser(ParseUser user, String tribute, String message) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("user", user.getObjectId());
		params.put("object", tribute);
		params.put(DbConstants.MESSAGE, message);

		ParseCloud.callFunctionInBackground("sendPushNotification", params,
				new FunctionCallback<Boolean>() {

					@Override
					public void done(Boolean b, ParseException e) {

					}
				});

	}

	@Override
	public void addObserver(UploadPostObserver o) {
		this.postObserver = o;
	}

	@Override
	public void removePostObserver() {
		this.postObserver = null;
	}

	@Override
	public void addObserver(UploadStoryObserver o) {
		this.storyObserver = o;
	}

	@Override
	public void removeStoryObserver() {
		this.postObserver = null;
	}

	@Override
	public void notifyFeedObservers(ParseObject post) {
		if (this.postObserver != null)
			postObserver.onNotify(post);
	}

	@Override
	public void notifyStoryObservers(ParseObject story) {
		if (this.storyObserver != null)
			storyObserver.onNotify(story);
	}

	public void sendMessage(final ParseObject message, final ParseUser user) {
		message.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e == null) {
					List<String> users = new ArrayList<String>();
					users.add(user.getObjectId());

					notifyObserver(message, user);
					sendMessageNotification(
							users,
							message.getString(DbConstants.MESSAGE),
							ParseUser.getCurrentUser().getString(
									DbConstants.NAME)
									+ " sent you a message.");
				}
			}
		});
	}

	public void sendMessage(final String message, Bitmap image,
			final List<ParseUser> users) {
		new sendMessageTask(message, image, users).execute();
	}

	private class sendMessageTask extends AsyncTask<Void, Void, List<String>> {
		private List<ParseUser> users = new ArrayList<ParseUser>();
		private String message;
		private Bitmap image;

		public sendMessageTask(String message, Bitmap image,
				List<ParseUser> users) {
			this.message = message;
			this.image = image;
			this.users.addAll(users);
		}

		@Override
		protected List<String> doInBackground(Void... params) {
			ParseFile imgFile = null;
			if (image != null) {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				byte[] bitmapdata = outStream.toByteArray();
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				imgFile = new ParseFile("media.png", bitmapdata);
			}

			List<String> usersID = new ArrayList<String>();
			ParseUser user = ParseUser.getCurrentUser();
			for (ParseUser u : users) {
				ParseObject obj = new ParseObject(DbConstants.TABLE_MESSAGE);
				obj.put(DbConstants.USER1, user);
				obj.put(DbConstants.USER2, u);
				usersID.add(u.getObjectId());
				obj.put(DbConstants.MESSAGE, message);
				if (imgFile != null)
					obj.put(DbConstants.MEDIA, imgFile);
				obj.saveInBackground();
				notifyObserver(obj, u);
			}
			return usersID;
		}

		@Override
		public void onPostExecute(List<String> usersID) {
			sendMessageNotification(usersID, message, ParseUser
					.getCurrentUser().getString(DbConstants.NAME)
					+ " sent you a message.");
		}
	}

	private static void sendMessageNotification(List<String> users,
			String body, String alert) {
		Log.e("Notifying", "Message");
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("users", users);
		params.put(DbConstants.MESSAGE, body);
		params.put(DbConstants.ALERT, alert);

		ParseCloud.callFunctionInBackground("notifyMessageUsers", params,
				new FunctionCallback<Boolean>() {

					@Override
					public void done(Boolean b, ParseException e) {
						Log.e("Notify Post", "Executed--" + e);
					}
				});

	}

	@Override
	public void bindObserver(MessageObserver ob) {
		this.messageObserver = ob;
	}

	@Override
	public void freeObserver() {
		this.messageObserver = null;
	}

	@Override
	public boolean notifyObserver(ParseObject message, ParseUser sender) {
		if (this.messageObserver != null)
			return this.messageObserver.onMessageReceive(message, sender);

		return false;
	}

	@Override
	public void addTributeObserver(UploadPostObserver o) {
		this.tributeObserver = o;
	}

	@Override
	public void removeTributeObserver() {
		this.tributeObserver = null;
	}

	@Override
	public void notifyTributeObservers(ParseObject tribute) {
		if (this.tributeObserver != null)
			tributeObserver.onNotify(tribute);
	}

	@Override
	public void notifyEditFeedObservers(int position, ParseObject post) {
		if (this.postObserver != null)
			postObserver.onEditDone(position, post);
	}

	@Override
	public void notifyEditStoryObservers(int position, ParseObject story) {
		if (this.storyObserver != null)
			storyObserver.onEditDone(position, story);

	}

	@Override
	public void notifyEditTributeObservers(int position, ParseObject tribute) {
		if (this.tributeObserver != null)
			tributeObserver.onEditDone(position, tribute);
	}

	public void editPost(int position, final ParseObject post) {
		post.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e != null)
					post.saveEventually();
			}
		});
		notifyEditFeedObservers(position, post);
	}

	public void editStory(int position, final ParseObject story) {
		story.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e != null)
					story.saveEventually();
			}
		});
		notifyEditStoryObservers(position, story);
	}

	public void editTribute(int position, final ParseObject tribute) {
		tribute.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				if (e != null)
					tribute.saveEventually();
			}
		});
		notifyEditTributeObservers(position, tribute);
	}
}
