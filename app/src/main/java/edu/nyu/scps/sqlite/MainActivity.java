package edu.nyu.scps.sqlite;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private Helper helper;  //Can't initialize these fields before onCreate.
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ListView listView = (ListView)findViewById(R.id.listView);

        helper = new Helper(this);
        Cursor cursor = helper.getCursor();

        //Plug cursor into an adapter, to put into view after view
        adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[] {"name",             "_id"},
                new int[]    {android.R.id.text1, android.R.id.text2},
                0	//don't need any flags
        );

        listView.setAdapter(adapter);

        //Display a message when the table contains no rows.

        LayoutInflater inflater = getLayoutInflater();
        TextView textView = (TextView)inflater.inflate(R.layout.empty, null);
        ViewGroup viewGroup = (ViewGroup)findViewById(android.R.id.content); //Get the RelativeLayout.
        viewGroup.addView(textView);
        listView.setEmptyView(textView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor)parent.getItemAtPosition(position); //downcast
                int indexDisplayName = cursor.getColumnIndex("name");
                String name = cursor.getString(indexDisplayName);
                String s = "Deleted " + name + ", position = " + position + ", id = " + id + ".";
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();

                SQLiteDatabase database = helper.getWritableDatabase();
                database.delete("people", "_id = ?", new String[] {Long.toString(id)}); //_id = ?, the array of values is the SQL command
                adapter.changeCursor(helper.getCursor());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                throw new RuntimeException();
            }
        };

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        String dialogTitle = getString(R.string.dialog_title);
        String dialogMessage = getString(R.string.dialog_message);
        dialogBuilder.setTitle(dialogTitle);
        dialogBuilder.setMessage(dialogMessage);
        //dialogBuilder.setPositiveButton("OK", null);
        AlertDialog addDialog = dialogBuilder.create();

        LayoutInflater dialogInflater = getLayoutInflater();
        final View dialogView = dialogInflater.inflate(R.layout.dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editText = (EditText) dialogView.findViewById(R.id.editText);

        if (id == R.id.action_append) {

            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN
                            && keyCode == KeyEvent.KEYCODE_ENTER) {

                        Editable editable = editText.getText();
                        String string = editable.toString();
                        //mResult = Double.parseDouble(string);

                        //Sending this message will break us out of the loop below.
                        Message message = handler.obtainMessage();
                        handler.sendMessage(message);
                    }
                    return false;
                }
            });

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();

            //Loop until the user presses the EditText's Done button.
            try {
                Looper.loop();
            }
            catch(RuntimeException runtimeException) {
            }

            alertDialog.dismiss();
            //return mResult;

            addDialog.show();

            ContentValues contentValues = new ContentValues();
            contentValues.put("name", "Joe");
            SQLiteDatabase database = helper.getWritableDatabase();
            database.insert("people", null, contentValues);
            adapter.changeCursor(helper.getCursor());
            return true;
        }

        if (id == R.id.action_delete_all) {
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete("people", null, null);  //Delete all records!
            adapter.changeCursor(helper.getCursor());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
