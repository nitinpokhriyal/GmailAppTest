package com.example.gmailapptest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.example.gmailapptest.contentprovider.GmailContract;
import com.example.gmailapptest.contentprovidertest.LabelDetailsActivity;
import com.example.gmailapptest.contentprovidertest.LabelListFragment;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.Thread;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;



public class MainActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        LabelListFragment.ItemClickedListener {
    static final String TAG = "TestApp";
    LabelListFragment mFragment = null;
    SimpleCursorAdapter mAdapter;
    String account =null;
    private static final String ACCOUNT_TYPE_GOOGLE = "com.google";
    private static final String[] FEATURES_MAIL = {"service_mail"};

    static final String[] COLUMNS_TO_SHOW = new String[] {
            GmailContract.Labels.NAME,
            GmailContract.Labels.NUM_CONVERSATIONS,
            GmailContract.Labels.NUM_UNREAD_CONVERSATIONS };

    static final int[] LAYOUT_ITEMS = new int[] {
            R.id.name_entry,
            R.id.number_entry,
            R.id.unread_count_number_entry };

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_main);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        // There is only one fragment
        mFragment = (LabelListFragment)fragment;
        mFragment.setItemClickedListener(this);

        mAdapter = new SimpleCursorAdapter(this, R.layout.label_list_item, null,
                COLUMNS_TO_SHOW, LAYOUT_ITEMS);
        mFragment.setListAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Get the account list, and pick the first one
        AccountManager.get(this).getAccountsByTypeAndFeatures(ACCOUNT_TYPE_GOOGLE, FEATURES_MAIL,
                new AccountManagerCallback<Account[]>() {
                    @Override
                    public void run(AccountManagerFuture<Account[]> future) {
                        Account[] accounts = null;
                        try {
                            accounts = future.getResult();
                        //    getAuthToken();
                        } catch (OperationCanceledException oce) {
                            Log.e(TAG, "Got OperationCanceledException", oce);
                        } catch (IOException ioe) {
                            Log.e(TAG, "Got OperationCanceledException", ioe);
                        } catch (AuthenticatorException ae) {
                            Log.e(TAG, "Got OperationCanceledException", ae);
                        }
                        onAccountResults(accounts);
                    }
                }, null /* handler */);
    }

    private void onAccountResults(Account[] accounts) {
        Log.i(TAG, "received accounts: " + Arrays.toString(accounts));
        if (accounts != null && accounts.length > 0) {
            // Pick the first one, and display a list of labels
            account = accounts[0].name;
            new getAuthToken().execute();
            Log.i(TAG, "Starting loader for labels of account: " + account);
            final Bundle args = new Bundle();
            args.putString("account", account);
            getSupportLoaderManager().restartLoader(0, args, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String account = args.getString("account");
      //  queryGmailConversations(account);
        final Uri labelsUri = GmailContract.Labels.getLabelsUri(account);
      Loader<Cursor> loaderCursor= new CursorLoader(this, labelsUri, null, null, null, null);
       // final Uri messageUri=GmailContract.MessageColumns.getMessageUri(account);
      //Loader<Cursor> loaderCursor= new CursorLoader(this, messageUri, null, null, null, null);
      Log.i(TAG, "loader info " + loaderCursor.toString());
      return loaderCursor;
    }
    
   

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            Log.i(TAG, "Received cursor with # rows: " + data.getCount());
        }
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemClicked(int position) {
        // Get the cursor from the adapter
        final Cursor cursor = mAdapter.getCursor();

        cursor.moveToPosition(position);

        // get the uri
        final Uri labelUri = Uri.parse(
                cursor.getString(cursor.getColumnIndex(GmailContract.Labels.URI)));

        Log.i(TAG, "got label uri: " + labelUri);
        final Intent intent = new Intent(this, LabelDetailsActivity.class);
        intent.putExtra(LabelDetailsActivity.LABEL_URI_EXTRA, labelUri);
        startActivity(intent);
    }
    

/*	public void getGmailAuthToken(String account){
		String token=null;
		try {
			token = GoogleAuthUtil.getToken(this,
					account, "https://www.googleapis.com/auth/gmail.readonly");
			Log.i(TAG,"token received" +token);
		} catch (UserRecoverableAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GoogleAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GoogleCredential credential = new GoogleCredential()
				.setAccessToken(token);
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		Gmail service = new Gmail.Builder(httpTransport, jsonFactory,
				credential).setApplicationName("GmailApiTest").build();
		
		ListThreadsResponse threadsResponse;
		
		try {
			threadsResponse = service.users().threads().list("me").execute();
			  List<Thread> threads = threadsResponse.getThreads();
			    ArrayList<String> ids = new ArrayList<String>();
			    ids.add("INBOX");
			    ListMessagesResponse messagesRespose= service.users().messages().list("me").setLabelIds(ids).setQ("From: sanjaygu@gmail.com")
			            .execute();
			    List<Message> m = messagesRespose.getMessages();
			    for (Message ms : m) {
			    	 //getBareMessageDetails(ms.getId(),service);
			    	Log.i(TAG,"Message reading" + ms.toPrettyString());
			    	 break;
			    }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  
	}*/
	
    private final static String G_PLUS_SCOPE = 
		      "oauth2:https://www.googleapis.com/auth/gmail.readonly";
		  private final static String USERINFO_SCOPE =   
		      "https://www.googleapis.com/auth/userinfo.profile";
		  private final static String SCOPES = G_PLUS_SCOPE;
	public class getAuthToken extends AsyncTask<Void, Void, List<Thread>> {

		protected List<Thread> doInBackground(Void... params) {
			String token=null;
			try {
				
				token = GoogleAuthUtil.getToken(MainActivity.this,
						account, SCOPES);
				Log.i(TAG,"token received" +token);
			} catch (UserRecoverableAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GoogleAuthException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GoogleCredential credential = new GoogleCredential()
					.setAccessToken(token);
			HttpTransport httpTransport = new NetHttpTransport();
			JsonFactory jsonFactory = new JacksonFactory();
			Gmail service = new Gmail.Builder(httpTransport, jsonFactory,
					credential).setApplicationName("GmailApiTest").build();
			
			ListThreadsResponse threadsResponse;
			
			try {
				threadsResponse = service.users().threads().list("me").execute();
				  List<Thread> threads = threadsResponse.getThreads();
				    ArrayList<String> ids = new ArrayList<String>();
				    ids.add("INBOX");
				    ListMessagesResponse messagesRespose= service.users().messages().list("me").setLabelIds(ids).setQ("From: sanjaygu@gmail.com")
				            .execute();
				    List<Message> m = messagesRespose.getMessages();
				    for (Message ms : m) {
				    	 //getBareMessageDetails(ms.getId(),service);
				    	Log.i(TAG,"Message reading" + ms.toPrettyString());
				    	 break;
				    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		  
		}

		protected void onPostExecute(List<Thread> result) {
			if (result != null) {
				//generateBatch(result);
			}
		}

	}

}