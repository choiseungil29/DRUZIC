package dif.clogic.druzic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: choeseung-il
 * Date: 13. 7. 25.
 * Time: 오전 11:22
 * To change this template use File | Settings | File Templates.
 */
public class ListenListActivity extends Activity {

    private MediaPlayer mPlayer = null;
    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private ArrayList<Song> songList;

    private Handler mHandler = new Handler();

    private ListView listenListView;

    private Context mContext;

    private DbOpenHelper mDbOpenHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listenlist);

        songList = new ArrayList<Song>();

        mContext = this.getApplicationContext();

        mDbOpenHelper = new DbOpenHelper(ListenListActivity.this);
        try {
            mDbOpenHelper.open();
        } catch (SQLException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        listenListView = (ListView)findViewById(R.id.listenList);
        listenListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int which, long l) {
                //To change body of implemented methods use File | Settings | File Templates.

                AlertDialog.Builder builder = new AlertDialog.Builder(ListenListActivity.this);
                builder.setTitle("리스트");

                builder.setItems(R.array.data, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //To change body of implemented methods use File | Settings | File Templates.

                        String[] data = getResources().getStringArray(R.array.data);

                        if (data[i].equals("음악 듣기")) {
                            if (mPlayer != null) {
                                mPlayer.reset();
                                mPlayer.release();
                                mPlayer = null;
                            }

                            mPlayer = new MediaPlayer();
                            try {
                                String str = Environment.getExternalStorageDirectory().getAbsolutePath();
                                str += File.separator;
                                str += songList.get(which).Name;
                                str += ".mid";
                                mPlayer.setDataSource(str);
                                mPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                            mPlayer.start();
                        }

                        if (data[i].equals("악보 보기")) {

                            //Cursor cursor = mDbOpenHelper.getMatchName("example");
                            String params = songList.get(which).MelodySequence;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://druzicofclogic.appspot.com/melody/view/" + params)); // CN5 같은 쿼리 보내주기~~
                            startActivity(intent);
                        }

                    }
                });
                builder.show();
            }
        });

        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)) {
            findFolder();
        } else {

        }

    }

    private void findFolder(){
        Cursor cursor = mDbOpenHelper.getAllColumns();

        while(cursor.moveToNext()) {
            Toast.makeText(ListenListActivity.this, "text : " + cursor.getExtras().getString("name"), Toast.LENGTH_LONG);
            songList.add(new Song(cursor.getInt(cursor.getColumnIndex("_id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("melodysequence"))));
        }
        SongAdapter songAdapter = new SongAdapter(this, R.layout.row, songList);
        listenListView.setAdapter(songAdapter);
    }



    private class SongAdapter extends ArrayAdapter<Song> {

        private ArrayList<Song> items;

        public SongAdapter(Context context, int textViewResourceId, ArrayList<Song> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if(v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row, null);
            }
            Song s = items.get(position);
            if( s != null) {
                TextView text = (TextView)v.findViewById(R.id.filename);

                if(text != null) {
                    text.setText(s.Name);
                }
            }
            return v;
        }
    }
}
