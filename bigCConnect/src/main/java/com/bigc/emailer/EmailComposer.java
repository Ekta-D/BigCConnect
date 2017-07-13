package com.bigc.emailer;

public class EmailComposer {

	public static String WELCOME_EMAIL_SUBJECT = "Welcome to BigC-Connect";
	public static String RECOVER_PASSWORD_SUBJECT = "BigC-Connect Password Recovery";

	public static String composeSupporterWelcomeEmail(String name) {

		String body = "Dear "
				+ name
				+ ","
				+ "<br /><br />Welcome to BigC-Connect. BigC-Connect has been designed as a unique platform to connect cancer survivors and supporters to one another in their time of need. "
				+ "<br /><br />Here are a few suggestions to get your started:\n\n"
				+ "<ul>"
				+ "<li>Add a profile picture</li>"
				+ "<li>Add supporters or survivors</li>"
				+ "<li>Add a survival story</li>"
				+ "<li>Update status as a survivor or a supporter (a survivors circle will see an update from a supporter)</li>"
				+ "<li>Find news, videos and information in the explore section</li>"
				+ "</ul>"
				+ "<br />FYI: Privacy of our users is very important to us. We will not sell, rent or give away your email address or information to anyone. If you have any suggestions for us, please send them to feedback@bigc-connect.com"
				+ "<br /><br />Sincerely" + "<br />Team BigC-Connect.";
		return body;
	}

	public static String ComposeForgotPasswordMail(String name, String password) {
		String body = "Dear " + name + ","
				+ "<br /><br />Your BigC-Connect's password is <b>" + password
				+ "</b>.<br /><br />Sincerely"
				+ "<br />Team BigC-Connect.";

		return body;
	}
}