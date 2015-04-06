package iainsmart.carcontroller;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;
import android.view.MotionEvent;
import android.graphics.drawable.shapes.OvalShape;

// TODO: Joysticks
// TODO: Bluetooth things
// TODO: Make an app that actually works?

public class MainActivity extends ActionBarActivity {
    private static TextView txt1;
    private static TextView txt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt1 = (TextView)findViewById(R.id.txt1);
        txt2 = (TextView)findViewById(R.id.txt2);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent( MotionEvent event) {
        String xTouch = Float.toString(event.getX());
        String yTouch = Float.toString(event.getY());
        txt1.setText(yTouch);
        txt2.setText(xTouch);
        return true;
    }
}
