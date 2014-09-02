<%@ page import="java.awt.*,org.jCharts.*,org.jCharts.chartData.*"%>
<%@ page import="org.jCharts.properties.*"%>
<%@ page import="org.jCharts.types.ChartType,org.jCharts.axisChart.*"%>
<%@ page import="org.jCharts.test.TestDataGenerator"%>
<%@ page import="org.jCharts.encoders.ServletEncoderHelper"%>

<%@ page import="java.util.*"%>
<%@ page import="org.ei.struts.framework.util.StringUtils"%>
<%@ page import="org.ei.struts.emetrics.businessobjects.reports.*"%>
<%@ page import="org.ei.struts.emetrics.Constants"%>

<%@ page import="org.apache.commons.logging.Log"%>
<%@ page import="org.apache.commons.logging.LogFactory"%>
<%@ page import="org.ei.struts.emetrics.businessobjects.reports.*"%>
<%@ page import="org.ei.struts.emetrics.Constants"%>
<%@ page import="org.apache.commons.beanutils.*"%>

<%
/**************************************************************************************
* Copyright 2002 (C) Nathaniel G. Auvil. All Rights Reserved.
*
* Redistribution and use of this software and associated documentation
* ("Software"), with or without modification, are permitted provided
* that the following conditions are met:
*
* 1. Redistributions of source code must retain copyright
*    statements and notices.  Redistributions must also contain a
*    copy of this document.
*
* 2. Redistributions in binary form must reproduce the
*    above copyright notice, this list of conditions and the
*    following disclaimer in the documentation and/or other
*    materials provided with the distribution.
*
* 3. The name "jCharts" or "Nathaniel G. Auvil" must not be used to
* 	  endorse or promote products derived from this Software without
* 	  prior written permission of Nathaniel G. Auvil.  For written
*    permission, please contact nathaniel_auvil@users.sourceforge.net
*
* 4. Products derived from this Software may not be called "jCharts"
*    nor may "jCharts" appear in their names without prior written
*    permission of Nathaniel G. Auvil. jCharts is a registered
*    trademark of Nathaniel G. Auvil.
*
* 5. Due credit should be given to the jCharts Project
*    (http://jcharts.sourceforge.net/).
*
* THIS SOFTWARE IS PROVIDED BY Nathaniel G. Auvil AND CONTRIBUTORS
* ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
* NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
* FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
* jCharts OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
* INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
* (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
* HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
* STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
* ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
* OF THE POSSIBILITY OF SUCH DAMAGE.
**********************************************************************************/

%>


<%
	try
	{
		//HttpSession session = request.getSession(false);

		Report report = (Report)session.getAttribute(Constants.REPORTS_KEY);
		ArrayList rawData = (ArrayList)session.getAttribute(Constants.REPORT_DATA_KEY);

		int dataCount = rawData.size();

		Vector charts = report.getCharts();
		
		Iterator chartIt = charts.iterator();
		
		org.ei.struts.emetrics.businessobjects.reports.Chart chart = null;
		Vector chartRows = null;
		Vector legendLables = null;
		XAxisLabels xAxisLabels = null;
		double[][] rowData = null;
		ChartRow row = null;
		while (chartIt.hasNext()) {
			chart = (org.ei.struts.emetrics.businessobjects.reports.Chart)chartIt.next();

			String title = chart.getTitle();
			String xAxisTitle = chart.getxAxisTitle();
			String yAxisTitle = chart.getyAxisTitle();
			String chartType = chart.getType();
			
			chartRows = chart.getChartRows();
			int rowCount = chartRows.size();
			
			rowData = new double[rowCount][dataCount];
			
			Iterator rowIt = chartRows.iterator();
			int i = 0;
			int j = 0;
			String column = null;
			Iterator dataIt = null;
			while (rowIt.hasNext()) {
				row = (ChartRow)rowIt.next();
				column = ((Result)((ResultsSet)report.findResultsSet(row.getResultsSetId())).findResult(row.getResultId())).getColumn();
				dataIt = rawData.iterator();
				j = 0;
				while (dataIt.hasNext()) {
					Object obj = dataIt.next();
					rowData[i][j] = Double.parseDouble(((DynaBean)obj).get(column).toString());
					j++;
				}
				i++;
			}

			if ((i==0)||(j==0)) {
				response.sendRedirect(request.getContextPath()+"/images/nodata.gif");
			} else {
				xAxisLabels = chart.getXAxisLabels();

				column = ((Result)((ResultsSet)report.findResultsSet(xAxisLabels.getResultsSetId())).findResult(xAxisLabels.getResultId())).getColumn();
				dataIt = rawData.iterator();
				j = 0;
				String[] xLabels = new String[dataCount];
				while (dataIt.hasNext()) {
					Object obj = dataIt.next();
					xLabels[j] = ((DynaBean) obj).get(column).toString();
					j++;
				}

				int[][] colors = new int[10][3];
				colors[0] = new int[] {102,153,204};
				colors[1] = new int[] {153,204,255};
				colors[2] = new int[] {0,102,204};
				colors[3] = new int[] {204,204,204};
				colors[4] = new int[] {153,153,153};
				colors[5] = new int[] {204,102,0};
				colors[6] = new int[] {102,153,204};
				colors[7] = new int[] {153,204,255};
				colors[8] = new int[] {255,153,51};
				colors[9] = new int[] {204,102,0};


				Paint[] paints = new Paint[rowData.length];

				for ( int pi = 0; pi < rowData.length ; pi++) {

					paints[pi] = new GradientPaint( 0, 0, new Color(colors[pi][0],colors[pi][1],colors[pi][2]), 
										10, 10, new Color(colors[pi][0],colors[pi][1],colors[pi][2]) );

				}

				//From AxisChartServlet.java:init()
				Iterator legendIt = chartRows.iterator();
				LegendLabel legendItem = null;
				String[] labels = new String[rowCount];
				j= 0;
				while (legendIt.hasNext()) {
					legendItem = (LegendLabel)legendIt.next();
					labels[j] = ((Result)((ResultsSet)report.findResultsSet(legendItem.getResultsSetId())).findResult(legendItem.getResultId())).getName();
					j++;
				}
				LegendProperties legendProperties = new LegendProperties();
				legendProperties.setPlacement( LegendAreaProperties.RIGHT );
				legendProperties.setNumColumns( 1 );
				ChartProperties chartProperties = new ChartProperties();
				AxisProperties axisProperties = new AxisProperties();
				//axisProperties.setScaleFont( new Font( "Georgia Negreta cursiva", Font.PLAIN,13 ) );
				//axisProperties.setAxisTitleFont( new Font( "Arial Narrow", Font.PLAIN, 14) );
				//axisProperties.setYAxisRoundValuesToNearest( 0 ); 

				//From LineChartServlet.java:init()
				Stroke[] strokes= { LineChartProperties.DEFAULT_LINE_STROKE, LineChartProperties.DEFAULT_LINE_STROKE, LineChartProperties.DEFAULT_LINE_STROKE };
				Shape[] shapes= { PointChartProperties.SHAPE_TRIANGLE,PointChartProperties.SHAPE_DIAMOND, PointChartProperties.SHAPE_CIRCLE };
				LineChartProperties lineChartProperties = new LineChartProperties(strokes,shapes);

				ClusteredBarChartProperties clusteredBarChartProperties= new ClusteredBarChartProperties();

				DataSeries dataSeries = new DataSeries( xLabels, xAxisTitle, yAxisTitle,title );

				AxisChartDataSet acds = new AxisChartDataSet(rowData, labels, paints,ChartType.BAR_CLUSTERED, clusteredBarChartProperties );
				dataSeries.addIAxisPlotDataSet(acds);
				AxisChart axisChart = new AxisChart(dataSeries, chartProperties, axisProperties,legendProperties, 600, 400);

				ServletEncoderHelper.encodeJPEG13(axisChart, 1.0f, response);
			}
		}
	}
	catch(Exception e)
	{
		System.out.println(e);
	}

%>