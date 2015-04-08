package edu.nyu.scps.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class Helper extends SQLiteOpenHelper {
    public Helper(Context context) {
        super(context, "stooges.db", null, 1);	//1 is version number
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //a Java String containing five SQLite statements.
        String[] statements = {
            "CREATE TABLE people ("
                + "	_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "	name TEXT"
                + ");",

            "INSERT INTO people (_id, name) VALUES (NULL, 'Moe');",
            "INSERT INTO people (_id, name) VALUES (NULL, 'Larry');",
            "INSERT INTO people (_id, name) VALUES (NULL, 'Curly');",
            "INSERT INTO people (_id, name) VALUES (NULL, 'Shemp');"
        };

        for (String statement: statements) {
            db.execSQL(statement);
        }
    }

    public Cursor getCursor() {
        SQLiteDatabase db = getReadableDatabase(); // the db passed to onCreate
        //can say "_id, name" instead of "*", but _id must be included.
        Cursor cursor = db.rawQuery("SELECT * FROM people;", null);
        cursor.moveToFirst();
        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
