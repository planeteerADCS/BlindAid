package com.planeteers.blindaid.util;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;

/**
 * Created by Jose on 11/9/15.
 */
public class ContactsUtil {

    /**
     * Returns an ArrayList with the profile image's {@link Uri}s for every contact.
     * CHECK FOR CONTACT ACCESS PERMISSION BEFORE CALLING THIS METHOD
     * @param context
     * @return ArrayList with profile image's uri
     */
    public static ArrayList<Uri> getContactsImageUri(Context context){

        ArrayList<Uri> uris = new ArrayList<>();

        // Gets the URI of the db
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        // What to grab from the db
        String[] projection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID
        };

        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, sortOrder);
        if(cursor != null) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                long contactId = cursor.getLong(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID));

                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

                uris.add(photoUri);
            }
            cursor.close();
        }

        return uris;
    }
}
