package com.example.gmailapptest.contentprovider;

public class GmailApi {

	public void testAPI(){
/*		GoogleCredential credential = new GoogleCredential().setAccessToken(token);
		JsonFactory jsonFactory = new JacksonFactory();
		HttpTransport httpTransport = new NetHttpTransport();

		service = new Gmail.Builder(httpTransport, jsonFactory, credential).setApplicationName("GmailApiTP").build();
		ListMessagesResponse messagesRespose;
		List<Message> m = null;

		ArrayList<String> ids = new ArrayList<String>();
		ids.add("INBOX");
		try {
		    messagesRespose = service.users().messages().list("me").setLabelIds(ids).setQ("From: something")
		            .execute();
		    m = messagesRespose.getMessages();

		} catch (IOException e) {
		    e.printStackTrace();
		}
*/	}
}
