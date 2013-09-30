package dif.clogic.druzic;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.*;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created with IntelliJ IDEA.
 * User: choeseung-il
 * Date: 13. 7. 25.
 * Time: 오전 9:35
 * To change this template use File | Settings | File Templates.
 */
public class StartActivity extends Activity {

    private ImageButton drawButton;
    private ImageButton listenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        drawButton = (ImageButton)findViewById(R.id.drawButton);
        listenButton = (ImageButton)findViewById(R.id.listenButton);

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To change body of implemented methods use File | Settings | File Templates.
                Intent intent = new Intent(StartActivity.this, HandlerView.ColorActivity.class);
                startActivity(intent);
            }
        });

        listenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //To change body of implemented methods use File | Settings | File Templates.
                Intent intent = new Intent(StartActivity.this, ListenListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event){
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                String alertTitle = getResources().getString(R.string.app_name);

                new AlertDialog.Builder(StartActivity.this)
                        .setTitle("DRUZIC")
                        .setMessage("종료하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
// TODO Auto-generated method stub
                                moveTaskToBack(true);
                                System.exit(0);
                                android.os.Process.killProcess(android.os.Process.myPid());
                            }
                        })
                        .setNegativeButton("아니오", null)
                        .show();
        }
        return true;
    }
}