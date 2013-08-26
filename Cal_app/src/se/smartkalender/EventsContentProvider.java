package se.smartkalender;


import java.util.HashMap;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class EventsContentProvider extends ContentProvider {

    private static final String TAG = "EventsContentProvider";

    private static final String DATABASE_NAME = "events.db";

    private static final int DATABASE_VERSION = 1;

    private static final String EVENTS_TABLE_NAME = "events";

    public static final String AUTHORITY = "se.smartkalender.EventsContentProvider";
    
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/events");

	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.jwei512.notes";

	public static final String EVENT_ID = "_id";

	public static final String TITLE = "name";

    private static final UriMatcher sUriMatcher;

    private static final int EVENTS = 1;

	public static final String ICON_ID = "icon_id";

	public static final String COLOR = "color";

	public static final String DATE = "date";

	public static final String START_TIME = "start";

	public static final String FINISH_TIME = "end";

	public static final String DESCRIPTION = "descr";

	public static final String REPEAT_DAYS = "repeat";

	public static final String REMINDER = "remind";

	public static final String IS_YOUR_IMAGE = "is_your_image";

	public static final String YOUR_IMAGE_PATH = "your_image_path";
	

    private static HashMap<String, String> eventsProjectionMap;
    
    

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + EVENTS_TABLE_NAME + " (" + 
            		EVENT_ID + " INTEGER PRIMARY KEY," + 
            		ICON_ID  + " LONGTEXT," +
            		TITLE + " LONGTEXT," + 
            		DESCRIPTION  + " LONGTEXT," + 
                    COLOR  + " INTEGER," + 
            		DATE  + " INTEGER," + 
                    START_TIME  + " INTEGER," + 
            		FINISH_TIME  + " INTEGER," +
            		REPEAT_DAYS  + " LONGTEXT," + 
            		REMINDER  + " INTEGER," + 
            		IS_YOUR_IMAGE  + " INTEGER," + 
            		YOUR_IMAGE_PATH  + " LONGTEXT" + 
            		");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                    + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper dbHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case EVENTS:
                count = db.delete(EVENTS_TABLE_NAME, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case EVENTS:
                return CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != EVENTS) { throw new IllegalArgumentException("Unknown URI " + uri); }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();       
        //db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE_NAME);
       // dbHelper.onCreate(db);
        long rowId = db.insert(EVENTS_TABLE_NAME, ICON_ID, values);
        if (rowId > 0) {
            Uri eventUri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(eventUri, null);
            return eventUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case EVENTS:
                qb.setTables(EVENTS_TABLE_NAME);
                qb.setProjectionMap(eventsProjectionMap);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case EVENTS:
                count = db.update(EVENTS_TABLE_NAME, values, where, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, EVENTS_TABLE_NAME, EVENTS);

        eventsProjectionMap = new HashMap<String, String>();
        eventsProjectionMap.put(EVENT_ID, EVENT_ID);
        eventsProjectionMap.put(TITLE, TITLE);
        eventsProjectionMap.put(DESCRIPTION, DESCRIPTION);
        eventsProjectionMap.put(ICON_ID, ICON_ID);
        eventsProjectionMap.put(COLOR, COLOR);
        eventsProjectionMap.put(DATE, DATE);
        eventsProjectionMap.put(START_TIME, START_TIME);
        eventsProjectionMap.put(FINISH_TIME, FINISH_TIME);  
        eventsProjectionMap.put(REPEAT_DAYS, REPEAT_DAYS);  
        eventsProjectionMap.put(REMINDER, REMINDER);          
        eventsProjectionMap.put(IS_YOUR_IMAGE, IS_YOUR_IMAGE);          
        eventsProjectionMap.put(YOUR_IMAGE_PATH, YOUR_IMAGE_PATH);          

        
        
    }
}

