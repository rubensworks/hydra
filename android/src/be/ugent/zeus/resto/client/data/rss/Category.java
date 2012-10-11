package be.ugent.zeus.resto.client.data.rss;

import java.io.Serializable;

/**
 *
 * @author Thomas Meire
 */
public class Category implements Serializable {

  public String domain;
  public String value;

  @Override
  public String toString() {
    return domain + " -> " + value;
  }
}
