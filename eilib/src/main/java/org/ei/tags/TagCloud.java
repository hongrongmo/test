package org.ei.tags;

/** project specific imports*/
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * TagCloud class which gives the xml file with top count number of tags after doing scalling
 * 
 */

public class TagCloud {
    private Tag[] tags;
    private double max;
    private double min;
    private double buckets;

    /**
     * Constructor
     * 
     * @param tags
     * @param buckets
     * @param comp
     */
    public TagCloud(Tag[] tags, int buckets, Comparator<Tag> comp) {
        if (tags != null && tags.length > 0) {
            this.max = (double) tags[0].getCount();
            this.min = (double) tags[tags.length - 1].getCount();
            this.buckets = (double) buckets;
            Arrays.sort(tags, comp);
            this.tags = tags;
        }
    }

    /**
     * Return list of Tags for display
     * 
     * @return tag list
     */
    public List<Tag> getTagsDisplay() {
        List<Tag> taglist = new ArrayList<Tag>();
        if (this.tags != null) {
            double intervals[] = new double[(int) buckets];
            double dif = max - min;
            double bucketsize = 0.0;
            if (dif < buckets) {
                bucketsize = max + 1;
            } else {
                bucketsize = dif / buckets;
            }

            for (int i = 0; i < buckets; i++) {
                double intervalMax = (min + (bucketsize * (i + 1)));
                intervals[i] = intervalMax;
            }

            for (int i = 0; i < tags.length; i++) {
                Tag tag = tags[i];
                int count = tag.getCount();

                for (int j = 0; j < buckets; j++) {
                    if (count <= intervals[j]) {
                        tag.setSize("size" + Integer.toString(j + 1));
                        break;
                    }
                }
                taglist.add(tag);
            }
        }

        return taglist;
    }

    /**
     * Return XML version of Tags
     * 
     * @param out
     * @throws Exception
     */
    public void toXML(Writer out) throws Exception {
        if (this.tags != null) {
            out.write("<CLOUD>");

            double intervals[] = new double[(int) buckets];
            double dif = max - min;
            double bucketsize = 0.0;
            if (dif < buckets) {
                bucketsize = max + 1;
            } else {
                bucketsize = dif / buckets;
            }

            for (int i = 0; i < buckets; i++) {
                double intervalMax = (min + (bucketsize * (i + 1)));
                // System.out.println("Interval:"+intervalMax);
                intervals[i] = intervalMax;
            }

            for (int i = 0; i < tags.length; i++) {
                Tag tag = tags[i];
                int count = tag.getCount();
                out.write("<CTAG");

                for (int j = 0; j < buckets; j++) {
                    if (count <= intervals[j]) {
                        out.write(" class=\"");
                        out.write("size");
                        out.write(Integer.toString(j + 1));
                        out.write("\">");
                        out.write("<![CDATA[");
                        out.write(tag.getTag());
                        out.write("]]>");
                        break;
                    }
                }
                out.write("<CSCOPE>");
                out.write(Integer.toString(tag.getScope()));
                out.write("</CSCOPE>");
                TagGroup group = tag.getGroup();
                if (group != null) {
                    group.toXML(out);
                }
                out.write("</CTAG>");

            }

            out.write("</CLOUD>");
        } else {
            out.write("<NOTAGS/>");
        }
    }
}
