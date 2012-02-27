package be.ugent.zeus.resto.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 *
 * @author Thomas Meire
 */
public class Heracles extends Activity {

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setContentView(R.layout.heracles);

    findViewById(R.id.home_btn_locations).setOnClickListener(
            new View.OnClickListener() {

              public void onClick(View view) {
                startActivity(new Intent(Heracles.this, RestoMap.class));
              }
            });

    findViewById(R.id.home_btn_menu).setOnClickListener(
            new View.OnClickListener() {

              public void onClick(View view) {
                startActivity(new Intent(Heracles.this, RestoMenu.class));
              }
            });
  }
}
