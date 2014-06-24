/***********************************************************************************************
 * File Info: $Id: BarChartServlet.java,v 1.5 2003/04/19 01:41:27 nathaniel_auvil Exp $
 * Copyright (C) 2002
 * Author: Nathaniel G. Auvil
 * Contributor(s):
 *
 * Copyright 2002 (C) Nathaniel G. Auvil. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation ("Software"), with or
 * without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and notices.
 * 	Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * 	conditions and the following disclaimer in the documentation and/or other materials
 * 	provided with the distribution.
 *
 * 3. The name "jCharts" or "Nathaniel G. Auvil" must not be used to endorse or promote
 * 	products derived from this Software without prior written permission of Nathaniel G.
 * 	Auvil.  For written permission, please contact nathaniel_auvil@users.sourceforge.net
 *
 * 4. Products derived from this Software may not be called "jCharts" nor may "jCharts" appear
 * 	in their names without prior written permission of Nathaniel G. Auvil. jCharts is a
 * 	registered trademark of Nathaniel G. Auvil.
 *
 * 5. Due credit should be given to the jCharts Project (http://jcharts.sourceforge.net/).
 *
 * THIS SOFTWARE IS PROVIDED BY Nathaniel G. Auvil AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * jCharts OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 ************************************************************************************************/

package org.ei.controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.ei.domain.navigators.EiModifier;
import org.ei.domain.navigators.EiNavigator;
import org.ei.util.Base64Coder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Align;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

public class BarChart extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String YAXISTITLE = "Records";

    public static final int BAR_LIMIT = 40;
    public static final int LABEL_LENGTH_LIMIT = 30;
    private static final int CHART_WIDTH = 550;
    private static final int CHART_HEIGHT = 500;
    private static final Color BAR_COLOR = new Color(20, 140, 117); 
    private static final Font BAR_LABEL_FONT = new Font("Arial", Font.PLAIN, 9);
    private Image bkgndImage = null;

    public void init() throws ServletException {
        System.setProperty("java.awt.headless", "true");
    }

    /**********************************************************************************************
	 *
	 **********************************************************************************************/
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            
            // Get chart data from navigator
            String navdata = request.getParameter("analyzedata");
            navdata = unZipText(navdata);
            EiNavigator anav = EiNavigator.parseSimpleNavigator(navdata);

            // create the chart...
            JFreeChart chart = buildChart(anav, this.bkgndImage);

            // Write to response
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-store"); // HTTP 1.1
            response.setDateHeader("Expires", 0);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Content-Disposition", "inline; filename=chart.jpg");
            response.setContentType("image/jpeg");

            ChartUtilities.writeChartAsJPEG(response.getOutputStream(), chart, BarChart.CHART_WIDTH, BarChart.CHART_HEIGHT);

        } catch (Throwable throwable) {
            // HACK do your error handling here...
            throwable.printStackTrace();
        }

    }

    /**
     * Build the dataset for the chart from EiNavigator
     * 
     * @param anav
     * @return
     */
    private static DefaultCategoryDataset buildDataset(EiNavigator anav) {
        List<EiModifier> mods = anav.getModifiers();
        int maxxaxislabel = (mods.size() > BarChart.BAR_LIMIT) ? BarChart.BAR_LIMIT : mods.size();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Iterator<EiModifier> moditr = mods.iterator();
        Map<String, Integer> rowKeys = new Hashtable<String, Integer>();

        for (int i = 0; i < maxxaxislabel; i++) {
            if (moditr.hasNext()) {
                EiModifier amod = (EiModifier) moditr.next();
                String xAxisLabel = amod.getLabel();
                int data = amod.getCount();

                if ((xAxisLabel.length() > BarChart.LABEL_LENGTH_LIMIT)) {
                    xAxisLabel = xAxisLabel.substring(0, BarChart.LABEL_LENGTH_LIMIT - 1).concat(".");
                }
                if (rowKeys.containsKey(xAxisLabel)) {
                    int count = ((Integer) rowKeys.get(xAxisLabel)).intValue();
                    rowKeys.put(xAxisLabel, new Integer(count++));
                    xAxisLabel = xAxisLabel.concat(" (" + count + ")");
                } else {
                    rowKeys.put(xAxisLabel, new Integer(1));
                }
                dataset.addValue(data, BarChart.YAXISTITLE, xAxisLabel);
            }
        }
        return dataset;
    }
    
    /**
     * Build a JFreeChart Bar chart for navigator.
     *  
     * @param anav
     * @param background
     * @return
     */
    private static JFreeChart buildChart(EiNavigator anav, Image background) {
        JFreeChart chart = ChartFactory.createBarChart(
            anav.getDisplayname(),         // chart title
            "",               // domain axis label
            BarChart.YAXISTITLE,                  // range axis label
            buildDataset(anav),                  // data
            PlotOrientation.VERTICAL, // orientation
            false,                     // include legend
            false,                     // tooltips?
            false                     // URLs?
            );

        TextTitle source = new TextTitle("© " + Calendar.getInstance().get(Calendar.YEAR) + " Elsevier Inc.");
        source.setFont(new Font("SansSerif", Font.PLAIN, 10));
        source.setPosition(RectangleEdge.BOTTOM);
        source.setHorizontalAlignment(HorizontalAlignment.LEFT);
        chart.addSubtitle(source);

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderStroke(new BasicStroke(0.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] {1.0f,0.5f},1.0f));

        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
        if (background != null) {
            chart.setBackgroundImage(background);
            chart.setBackgroundImageAlignment(Align.BOTTOM_RIGHT);
        }

        // get a reference to the plot for further customisation...
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setRangeGridlinePaint(Color.black);
        plot.setRangeGridlinesVisible(true);
        
        // disable bar outlines...
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setBaseFillPaint(Color.white);

        // set label font
        renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setItemLabelFont(BarChart.BAR_LABEL_FONT);
        renderer.setItemLabelsVisible(true);

        renderer.setSeriesPaint(0, BarChart.BAR_COLOR);

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        
        return chart;
    }
    
    /**
     * Text Unzip utility
     * 
     * @param text
     * @return
     */
    private static String unZipText(String text) {
        StringBuffer buf = new StringBuffer();

        try {
            byte[] decodedStr = Base64Coder.decode(text.toCharArray());
            ByteArrayInputStream bytesIn = new ByteArrayInputStream(decodedStr);
            GZIPInputStream in = null;

            in = new GZIPInputStream(bytesIn);
            byte[] bytes = new byte[1024];
            int i = -1;
            buf = new StringBuffer();

            while ((i = in.read(bytes, 0, 1024)) != -1) {
                String a = new String(bytes, 0, i);
                buf.append(a);
            }

        } catch (Exception ex) {
            // Upon failure - return input string
            buf = new StringBuffer(new String(text));
            ex.printStackTrace();
        }

        return buf.toString();
    }
}
