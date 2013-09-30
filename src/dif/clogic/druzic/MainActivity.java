package dif.clogic.druzic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.Menu;
import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private HandlerView handlerView;
    private DbOpenHelper mDbOpenHelper;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        handlerView = new HandlerView(this, intent.getExtras().getString("color"));
        setContentView(handlerView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                String alertTitle = getResources().getString(R.string.app_name);

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("DRUZIC")
                        .setMessage("저장하고 종료하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub

                                SimpleDateFormat fomatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
                                Date currentTime = new Date();
                                String filename = fomatter.format(currentTime);

                                mDbOpenHelper = new DbOpenHelper(MainActivity.this);
                                try {
                                    mDbOpenHelper.open();
                                } catch (SQLException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }

                                String fileMelodySequence = HandlerView.melodySequenceList.replaceAll(" ", "&");
                                fileMelodySequence = fileMelodySequence.replaceAll("_", "");
                                fileMelodySequence = fileMelodySequence.substring(1);
                                long result = mDbOpenHelper.insertColumn(filename, fileMelodySequence);

                                String query = HandlerView.melodySequenceList;
                                query = query.replaceAll("_", "");
                                String strlist[] = query.split(" ");

                                MidiTrack tempoTrack = new MidiTrack();
                                MidiTrack noteTrack = new MidiTrack();

                                TimeSignature ts = new TimeSignature();
                                ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);

                                Tempo t = new Tempo();
                                t.setBpm(125);

                                tempoTrack.insertEvent(ts);
                                tempoTrack.insertEvent(t);

                                for (int idx = 1; idx < strlist.length; idx++) {
                                    int channel = 0;
                                    int pitch = 0;
                                    int velocity = 100;

                                    String str = strlist[idx];
                                    switch (str.charAt(0)) {

                                        case 'C':
                                            pitch = 0;
                                            break;
                                        case 'D':
                                            pitch = 2;
                                            break;
                                        case 'E':
                                            pitch = 4;
                                            break;
                                        case 'F':
                                            pitch = 5;
                                            break;
                                        case 'G':
                                            pitch = 7;
                                            break;
                                        case 'A':
                                            pitch = 9;
                                            break;
                                        case 'B':
                                            pitch = 11;
                                            break;
                                        case 'R':
                                            pitch = 0;
                                        default:
                                            break;
                                    }

                                    if (str.length() > 1) {
                                        if (str.charAt(1) == 'S') {
                                            pitch++;
                                        }
                                        pitch = pitch + 12 * (str.charAt(2) - '0' + 1) - 4;
                                    }

                                    noteTrack.insertNote(channel, pitch, velocity, idx * 240, 240);
                                }

                                ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
                                tracks.add(tempoTrack);
                                tracks.add(noteTrack);

                                MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);

                                File output = new File(Environment.getExternalStorageDirectory() + File.separator + filename + ".mid");
                                System.out.println(output.getAbsolutePath());
                                try {
                                    midi.writeToFile(output);
                                } catch (IOException e) {
                                    System.err.println(e);
                                }

                                HandlerView.timer.cancel();
                                handlerView.timer.purge();

                                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNeutralButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //To change body of implemented methods use File | Settings | File Templates.
                                HandlerView.timer.cancel();
                                handlerView.timer.purge();

                                //mDbOpenHelper.deleteTable();

                                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
        }
        return true;
    }


}