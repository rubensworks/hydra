package be.ugent.zeus.hydra;

import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.TextView;
import be.ugent.zeus.hydra.data.NewsItem;
import com.google.analytics.tracking.android.EasyTracker;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 *
 * @author blackskad
 */
public class NewsItemActivity extends AbstractSherlockActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setTitle(R.string.title_news);
        setContentView(R.layout.news_item);

        NewsItem item = (NewsItem) getIntent().getSerializableExtra("item");

        EasyTracker.getTracker().sendView("/News/" + item.title);

        TextView title = (TextView) findViewById(R.id.news_item_title);
        title.setText(item.title);

        String postedBy = getResources().getString(R.string.posted_by);
        TextView association = (TextView) findViewById(R.id.news_item_info);
        try {
            association.setText(String.format(postedBy, Html.fromHtml(item.association.display_name),
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Hydra.LOCALE).parse(item.date)));
        } catch (ParseException ex) {
            Log.w("Parse error", "");
            ex.printStackTrace();
        }

        TextView content = (TextView) findViewById(R.id.news_item_content);
        content.setText(Html.fromHtml(item.content.replace("\n", "<br>")));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(content, Linkify.ALL);
    }
}
