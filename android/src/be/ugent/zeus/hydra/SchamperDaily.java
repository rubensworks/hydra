package be.ugent.zeus.hydra;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import be.ugent.zeus.hydra.data.caches.ChannelCache;
import be.ugent.zeus.hydra.data.rss.Channel;
import be.ugent.zeus.hydra.data.rss.Item;
import be.ugent.zeus.hydra.data.services.HTTPIntentService;
import be.ugent.zeus.hydra.data.services.SchamperDailyService;
import be.ugent.zeus.hydra.ui.schamper.ChannelAdapter;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

/**
 * TODO: add spinner while loading the feed similar to menu's
 *
 * @author Thomas Meire
 */
public class SchamperDaily extends AbstractSherlockListActivity {

    private ChannelCache cache;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setTitle(R.string.title_schamper);
        getListView().setCacheColorHint(0);

        // add a button to the end of the list to read more online.
        View footer = getLayoutInflater().inflate(R.layout.schamper_footer, null);
        Button visitOnline = (Button) footer.findViewById(R.id.schamper_visit_online);
        visitOnline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://www.schamper.ugent.be/editie/2012-online"));
                startActivity(i);
            }
        });
        getListView().addFooterView(footer);

        cache = ChannelCache.getInstance(SchamperDaily.this);

        if (!cache.exists(ChannelCache.SCHAMPER)
            || System.currentTimeMillis() - cache.lastModified(ChannelCache.SCHAMPER) > SchamperDailyService.REFRESH_TIME) {
            refresh(false);
        } else {
            refresh(true);
        }
    }

    private void refresh(boolean synced) {
        if (!synced) {

            Intent intent = new Intent(this, SchamperDailyService.class);
            intent.putExtra(HTTPIntentService.RESULT_RECEIVER_EXTRA, new SchamperResultReceiver());

            startService(intent);

        } else {

            Channel channel = cache.get(ChannelCache.SCHAMPER);

            if (channel != null) {
                setTitle(channel.title);
                setListAdapter(new ChannelAdapter(SchamperDaily.this, channel));
            } else {
                Toast.makeText(SchamperDaily.this, R.string.no_schamper_articles, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        // Get the item that was clicked
        Item item = (Item) getListAdapter().getItem(position);

        // Launch a new activity
        Intent intent = new Intent(this, SchamperDailyItem.class);
        intent.putExtra("class", this.getClass().getCanonicalName());
        intent.putExtra("item", item);
        startActivity(intent);
    }

    private class SchamperResultReceiver extends ResultReceiver {

        private ProgressDialog progressDialog;

        public SchamperResultReceiver() {
            super(null);

            SchamperDaily.this.runOnUiThread(new Runnable() {
                public void run() {
                    progressDialog = ProgressDialog.show(SchamperDaily.this,
                        getResources().getString(R.string.title_schamper),
                        getResources().getString(R.string.loading));
                }
            });
        }

        @Override
        public void onReceiveResult(int code, Bundle icicle) {
            SchamperDaily.this.runOnUiThread(new Runnable() {
                public void run() {
                    progressDialog.dismiss();
                }
            });

            switch (code) {
                case HTTPIntentService.STATUS_FINISHED:
                    SchamperDaily.this.runOnUiThread(new Runnable() {
                        public void run() {
                            refresh(true);
                        }
                    });
                    break;
                case HTTPIntentService.STATUS_ERROR:
                    Toast.makeText(SchamperDaily.this, R.string.schamper_update_failed, Toast.LENGTH_SHORT).show();

                    SchamperDaily.this.runOnUiThread(new Runnable() {
                        public void run() {
                            refresh(true);
                        }
                    });
                    break;
            }
        }
    }
}
