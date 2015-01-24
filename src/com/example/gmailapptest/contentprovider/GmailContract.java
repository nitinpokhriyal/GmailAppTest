/*
 * Copyright 2012 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.gmailapptest.contentprovider;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.net.Uri;
import android.text.TextUtils;

/**
 * <p>Contract for use with the Gmail content provider.</p>
 *
 * <p>Developers can use this content provider to display label information to the user.
 * <br/>
 * The label information includes:
 * <ul>
 *     <li>Label name</li>
 *     <li>Total number of conversations</li>
 *     <li>Number of unread conversations</li>
 *     <li>Label text color</li>
 *     <li>Label background color</li>
 * </ul></p>
 *
 * <p>This content provider is available in Gmail version 2.3.6 or newer for Froyo/Gingerbread
 * and version 4.0.5 and newer for Honeycomb and Ice Cream Sandwich</p>
 * <p>An application can query the
 * <a href="http://developer.android.com/reference/android/content/ContentResolver.html">
 *     Content Resolver</a> directly
 * (or use a <a href="http://developer.android.com/guide/topics/fundamentals/loaders.html"
 * target="_blank">Loader</a>)
 * to obtain a Cursor with information for all labels on an account</p>
 * <code>Cursor labelsCursor = getContentResolver().query(GmailContract.Labels.getLabelsUri(
 * selectedAccount), null, null, null, null);</code>
 */
public final class GmailContract {
    private GmailContract() {}

    /**
     * Permission required to access this {@link android.content.ContentProvider}
     */
    public static final String PERMISSION =
            "com.google.android.gm.permission.READ_CONTENT_PROVIDER";

    /**
     * Authority for the Gmail content provider.
     */
    public static final String AUTHORITY = "com.google.android.gm";

    static final String LABELS_PARAM = "/labels";
    static final String LABEL_PARAM = "/label/";
    static final String BASE_URI_STRING = "content://" + AUTHORITY;
    static final String PACKAGE = "com.google.android.gm";
    static final String MESSAGE_PARAM = "/messages";
 
    /**
     * Check if the installed Gmail app supports querying for label information.
     *
     * @param c an application Context
     * @return true if it's safe to make label API queries
     */
    public static boolean canReadLabels(Context c) {
        boolean supported = false;

        try {
            final PackageInfo info = c.getPackageManager().getPackageInfo(PACKAGE,
                    PackageManager.GET_PROVIDERS | PackageManager.GET_PERMISSIONS);
            boolean allowRead = false;
            if (info.permissions != null) {
                for (int i = 0, len = info.permissions.length; i < len; i++) {
                    final PermissionInfo perm = info.permissions[i];
                    if (PERMISSION.equals(perm.name)
                            && perm.protectionLevel < PermissionInfo.PROTECTION_SIGNATURE) {
                        allowRead = true;
                        break;
                    }
                }
            }
            if (allowRead && info.providers != null) {
                for (int i = 0, len = info.providers.length; i < len; i++) {
                    final ProviderInfo provider = info.providers[i];
                    if (AUTHORITY.equals(provider.authority) &&
                            TextUtils.equals(PERMISSION, provider.readPermission)) {
                        supported = true;
                    }
                }
            }
        } catch (NameNotFoundException e) {
            // Gmail app not found
        }
        return supported;
    }


    
    public static final class MessageColumns {
    	
    	public static final String MESSAGE_ATTACHMENT_INFO_SEPARATOR = "\n";
        public static final String MESSAGE_LIST_TYPE =
                "vnd.android.cursor.dir/vnd.com.android.mail.message";
        public static final String MESSAGE_TYPE =
                "vnd.android.cursor.item/vnd.com.android.mail.message";
        /**
         * This string column contains a content provider URI that points to this single message.
         */
        public static final String URI = "messageUri";
        /**
         * This string column contains a server-assigned ID for this message.
         */
        public static final String SERVER_ID = "serverMessageId";
        public static final String CONVERSATION_ID = "conversationId";
        /**
         * This string column contains the subject of a message.
         */
        public static final String SUBJECT = "subject";
        /**
         * This string column contains a snippet of the message body.
         */
        public static final String SNIPPET = "snippet";
        /**
         * This string column contains the single email address (and optionally name) of the sender.
         */
        public static final String FROM = "fromAddress";
        /**
         * This string column contains a comma-delimited list of "To:" recipient email addresses.
         */
        public static final String TO = "toAddresses";
        /**
         * This string column contains a comma-delimited list of "CC:" recipient email addresses.
         */
        public static final String CC = "ccAddresses";
        /**
         * This string column contains a comma-delimited list of "BCC:" recipient email addresses.
         * This value will be null for incoming messages.
         */
        public static final String BCC = "bccAddresses";
        /**
         * This string column contains the single email address (and optionally name) of the
         * sender's reply-to address.
         */
        public static final String REPLY_TO = "replyToAddress";
        /**
         * This long column contains the timestamp (in millis) of receipt of the message.
         */
        public static final String DATE_RECEIVED_MS = "dateReceivedMs";
        /**
         * This string column contains the HTML form of the message body, if available. If not,
         * a provider must populate BODY_TEXT.
         */
        public static final String BODY_HTML = "bodyHtml";
        /**
         * This string column contains the plaintext form of the message body, if HTML is not
         * otherwise available. If HTML is available, this value should be left empty (null).
         */
        public static final String BODY_TEXT = "bodyText";
        public static final String EMBEDS_EXTERNAL_RESOURCES = "bodyEmbedsExternalResources";
        /**
         * This string column contains an opaque string used by the sendMessage api.
         */
        public static final String REF_MESSAGE_ID = "refMessageId";
        /**
         * This integer column contains the type of this draft, or zero (0) if this message is not a
         * draft. See {@link DraftType} for possible values.
         */
        public static final String DRAFT_TYPE = "draftType";
        /**
         * This boolean column indicates whether an outgoing message should trigger special quoted
         * text processing upon send. The value should default to zero (0) for protocols that do
         * not support or require this flag, and for all incoming messages.
         */
        public static final String APPEND_REF_MESSAGE_CONTENT = "appendRefMessageContent";
        /**
         * This boolean column indicates whether a message has attachments. The list of attachments
         * can be retrieved using the URI in {@link MessageColumns#ATTACHMENT_LIST_URI}.
         */
        public static final String HAS_ATTACHMENTS = "hasAttachments";
        /**
         * This string column contains the content provider URI for the list of
         * attachments associated with this message.
         */
        public static final String ATTACHMENT_LIST_URI = "attachmentListUri";
        /**
         * This long column is a bit field of flags defined in {@link MessageFlags}.
         */
        public static final String MESSAGE_FLAGS = "messageFlags";
        /**
         * This string column contains a specially formatted string representing all
         * attachments that we added to a message that is being sent or saved.
         */
        public static final String JOINED_ATTACHMENT_INFOS = "joinedAttachmentInfos";
        /**
         * This string column contains the content provider URI for saving this
         * message.
         */
        public static final String SAVE_MESSAGE_URI = "saveMessageUri";
        /**
         * This string column contains content provider URI for sending this
         * message.
         */
        public static final String SEND_MESSAGE_URI = "sendMessageUri";
        public static Uri getMessageUri(String account) {
            return Uri.parse(BASE_URI_STRING + "/" + account + MESSAGE_PARAM);
        }

        private MessageColumns() {}
    }
    
    
    
    

    /**
     * Table containing label information.
     */
    public static final class Labels {
        /**
         * Label canonical names for default Gmail system labels.
         */
        public static final class LabelCanonicalNames {
            /**
             * <p>Canonical name for the Primary inbox category</p>
             * <p><i>Note: This label may not exist, based on the user's inbox configuration</i></p>
             */
            public static final String CANONICAL_NAME_INBOX_CATEGORY_PRIMARY = "^sq_ig_i_personal";

            /**
             * Canonical name for the Social inbox category
             * <p><i>Note: This label may not exist, based on the user's inbox configuration</i></p>
             */
            public static final String CANONICAL_NAME_INBOX_CATEGORY_SOCIAL = "^sq_ig_i_social";

            /**
             * Canonical name for the Promotions inbox category
             * <p><i>Note: This label may not exist, based on the user's inbox configuration</i></p>
             */
            public static final String CANONICAL_NAME_INBOX_CATEGORY_PROMOTIONS = "^sq_ig_i_promo";

            /**
             * Canonical name for the Updates inbox category
             * <p><i>Note: This label may not exist, based on the user's inbox configuration</i></p>
             */
            public static final String CANONICAL_NAME_INBOX_CATEGORY_UPDATES =
                    "^sq_ig_i_notification";

            /**
             * Canonical name for the Forums inbox category
             * <p><i>Note: This label may not exist, based on the user's inbox configuration</i></p>
             */
            public static final String CANONICAL_NAME_INBOX_CATEGORY_FORUMS = "^sq_ig_i_group";

            /**
             * Canonical name for the Inbox label
             * <p><i>Note: This label may not exist, based on the user's inbox configuration</i></p>
             */
            public static final String CANONICAL_NAME_INBOX = "^i";

            /**
             * Canonical name for the Priority Inbox label
             * <p><i>Note: This label may not exist, based on the user's inbox configuration</i></p>
             */
            public static final String CANONICAL_NAME_PRIORITY_INBOX = "^iim";

            /**
             * Canonical name for the Starred label
             */
            public static final String CANONICAL_NAME_STARRED = "^t";

            /**
             * Canonical name for the Sent label
             */
            public static final String CANONICAL_NAME_SENT = "^f";

            /**
             * Canonical name for the Drafts label
             */
            public static final String CANONICAL_NAME_DRAFTS = "^r";

            /**
             * Canonical name for the All Mail label
             */
            public static final String CANONICAL_NAME_ALL_MAIL = "^all";

            /**
             * Canonical name for the Spam label
             */
            public static final String CANONICAL_NAME_SPAM = "^s";

            /**
             * Canonical name for the Trash label
             */
            public static final String CANONICAL_NAME_TRASH = "^k";

            private LabelCanonicalNames() {}
        }

        /**
         * The MIME-type of uri providing a directory of
         * label items.
         */
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.google.android.gm.label";

        /**
         * The MIME-type of a label item.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.com.google.android.gm.label";

        /**
         * This string value is the canonical name of a label. Canonical names are not localized and
         * are not user-facing.
         *
         * <p>Type: TEXT</p>
         */
        public static final String CANONICAL_NAME = "canonicalName";
        /**
         * This string value is the user-visible name of a label. Names of system labels
         * (Inbox, Sent, Drafts...) are localized.
         *
         * <p>Type: TEXT</p>
         */
        public static final String NAME = "name";
        /**
         * This integer value is the number of conversations in this label.
         *
         * <p>Type: INTEGER</p>
         */
        public static final String NUM_CONVERSATIONS = "numConversations";
        /**
         * This integer value is the number of unread conversations in this label.
         *
         * <p>Type: INTEGER</p>
         */
        public static final String NUM_UNREAD_CONVERSATIONS = "numUnreadConversations";
        /**
         * This integer value is the label's foreground text color in 32-bit 0xAARRGGBB format.
         *
         * <p>Type: INTEGER</p>
         */
        public static final String TEXT_COLOR = "text_color";
        /**
         * This integer value is the label's background color in 32-bit 0xAARRGGBB format.
         *
         * <p>Type: INTEGER</p>
         */
        public static final String BACKGROUND_COLOR = "background_color";
        /**
         * This string column value is the uri that can be used in subsequent calls to
         * {@link android.content.ContentProvider#query()} to query for information on the single
         * label represented by this row.
         *
         * <p>Type: TEXT</p>
         */
        public static final String URI = "labelUri";

        /**
         * Returns a URI that, when queried, will return the list of labels for an
         * account.
         * <p>
         * To use the Labels API, an app must first find the email address of a
         * valid Gmail account to query for label information. The <a href=
         * "http://developer.android.com/reference/android/accounts/AccountManager.html"
         * target="_blank">AccountManager</a> can return this information (<a
         * href="https://developers.google.com/gmail/android">example</a>).
         * </p>
         *
         * @param account Name of a valid Google account.
         * @return The URL that can be queried to retrieve the the label list.
         */
        public static Uri getLabelsUri(String account) {
            return Uri.parse(BASE_URI_STRING + "/" + account + LABELS_PARAM);
        }

        private Labels() {}
    }
}
