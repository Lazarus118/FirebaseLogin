package co.aulatech.firebaselogin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "data.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "user_details";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USERS_NAME = "users_name";

    /****************************************************************
     * DATABASE INITIALIZATION W/ NAME AND VERSION NUMBER
     ***************************************************************/
    DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /****************************************************************
     * CREATE TABLE ON START
     ***************************************************************/
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_USERS_NAME + " TEXT)");
    }


    /****************************************************************
     * UPGRADE RECORD
     ***************************************************************/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /****************************************************************
     * INSERT
     ***************************************************************/
    boolean insert(String users_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERS_NAME, users_name);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }


    /****************************************************************
     * UPDATE
     ***************************************************************/
    boolean update(Integer id, String users_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERS_NAME, users_name);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    /****************************************************************
     * DELETE
     ***************************************************************/
    Integer delete(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
    }

    /****************************************************************
     * GET RECORD
     ***************************************************************/
    Cursor getRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "= ?", new String[]{Integer.toString(id)});
        return res;
    }

    /****************************************************************
     * GET ALL RECORD
     ***************************************************************/
    Cursor getAllRecords() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return res;
    }
}