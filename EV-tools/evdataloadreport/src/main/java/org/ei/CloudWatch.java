package org.ei;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.aws.DescribeEc2;
import org.aws.AwsCloudWatchMetricsINFO;

import com.google.gson.Gson;

/**
 * Servlet implementation class CloudWatch
 */
public class CloudWatch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CloudWatch() {
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		HashMap <String,String> instanceIds = new HashMap<String, String>();
		HashMap <String,String> metrics = new HashMap<String, String>();
		HashMap <String,String> statistics = new HashMap<String, String>();
		HashMap <String,String> tests = new HashMap<String, String>();
		
		HashMap<String, String> metricsData = new HashMap<String, String>();

		String selectedNameSpace = request.getParameter("namespace");
		String InstanceID = request.getParameter("instances");
		String metricName = request.getParameter("metrics");
		String statisticName = request.getParameter("statistic");
		String test = request.getParameter("test");
		
		ServletContext servletContext = request.getServletContext();
		int SelectedIndex = 0;
		int selectedStatisticIndex = 0;

		
		if(session.getAttribute("SELECTEDNAMESPACE") !=null && !(session.getAttribute("SELECTEDNAMESPACE").toString().equalsIgnoreCase(selectedNameSpace)))
		{
			InstanceID = "default";
			metricName = "default";
			statisticName = "default";
			
			session.removeAttribute("METRICDATA");
			
		}
		
		//remove statisc session
		if(session.getAttribute("STATISTICSELECTEDINDEX") !=null)
		{
			session.removeAttribute("STATISTICSELECTEDINDEX");
		}
		session.setAttribute("SELECTEDNAMESPACE", selectedNameSpace);
		session.setAttribute("SELECTEDINSTANCEID", InstanceID);
		

			if(selectedNameSpace !=null)
			{
				if(selectedNameSpace.equalsIgnoreCase("default"))
				{
					SelectedIndex = 0;
				}
				else if(selectedNameSpace.equalsIgnoreCase("AWS/RDS"))
				{
					SelectedIndex = 1;
						instanceIds.put("eid", "eid");
						instanceIds.put("eib", "eib");
						instanceIds.put("eia", "eia");
				}
				else if (selectedNameSpace.equalsIgnoreCase("AWS/EC2"))
				{
					SelectedIndex = 2;
					instanceIds = DescribeEc2.getInstance().fetchInstanceIds(servletContext.getRealPath(""));	
					
				}
				
				session.setAttribute("NAMESPACEINDEX", SelectedIndex);
				session.setAttribute("INSTANCEIDS", instanceIds);
				
			}
		
			if(InstanceID !=null && selectedNameSpace !=null && (!(InstanceID.equalsIgnoreCase("default")) || !(selectedNameSpace.equalsIgnoreCase("default"))))
			{
				if(selectedNameSpace.equalsIgnoreCase("AWS/RDS"))
				{
					metrics.put("CPUUtilization", "CPUUtilization");
					metrics.put("DatabaseConnections", "DatabaseConnections");
					metrics.put("FreeStorageSpace", "FreeStorageSpace");
					metrics.put("FreeableMemory", "FreeableMemory");
					metrics.put("SwapUsage", "SwapUsage");
					metrics.put("ReadIOPS", "ReadIOPS");
					metrics.put("WriteIOPS", "WriteIOPS");
					metrics.put("ReadLatency", "ReadLatency");
					metrics.put("WriteLatency", "WriteLatency");
					metrics.put("ReadThroughput", "ReadThroughput");
					metrics.put("WriteThroughput", "WriteThroughput");
				}
				
				else if(selectedNameSpace.equalsIgnoreCase("AWS/EC2"))
				{
					metrics.put("CPUUtilization", "CPUUtilization");
					metrics.put("DiskReadOps", "DiskReadOps");
					metrics.put("DiskWriteOps", "DiskWriteOps");
					metrics.put("DiskReadBytes", "DiskReadBytes");
					metrics.put("DiskWriteBytes", "DiskWriteBytes");
					metrics.put("NetworkIn", "NetworkIn");
					metrics.put("NetworkOut", "NetworkOut");
				}
				session.setAttribute("SELECTEDMETRICNAME", metricName);
				session.setAttribute("METRICS", metrics);
			}
			
			
			// Percentile
			if(metricName !=null && !(metricName.equalsIgnoreCase("default")))
			{
				if(session.getAttribute("METRICS") !=null)
				{
					statistics.put("Minimum", "Minimum");
					statistics.put("Maximum", "Maximum");
					statistics.put("Average", "Average");
					statistics.put("Sum", "Sum");
				}
				session.setAttribute("SELECTEDSTATISTIC", statisticName);
				session.setAttribute("STATISTICS", statistics);
				
			}
			
			
		
/** get Info for the selected Metric
 * first reset METRICDATA of previously selected Metric
 * 
*/


if(statisticName !=null && !(statisticName.equalsIgnoreCase("default")) && metricName !=null && !(metricName.equalsIgnoreCase("default")) &&
selectedNameSpace !=null && !(selectedNameSpace.equalsIgnoreCase("default")) && InstanceID !=null && !(InstanceID.equalsIgnoreCase("default")))
{
	
	AwsCloudWatchMetricsINFO MetricsInfo = AwsCloudWatchMetricsINFO.getInstance();
	
	try
	{
		MetricsInfo.fetchMetricsInfo(servletContext.getRealPath(""),selectedNameSpace,InstanceID,metricName,statisticName);
		metricsData = MetricsInfo.getMetricsInfoList();
		session.setAttribute("METRICDATA", metricsData);
	}
	catch(IOException ioex)
	{
		System.out.println("IO Exception Occured: " + ioex.getMessage());
		ioex.printStackTrace();
	}
	catch(IllegalArgumentException argex)
	{
		System.out.println("IllegalArgument Exception Occured: " + argex.getMessage());
		argex.printStackTrace();
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	
}

		/*String json = new Gson().toJson(instanceIds);
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(json);*/
// for Retain selected statistic of  Statistics dropdonwlist
if(statisticName !=null)
{
	if(statisticName.equalsIgnoreCase("Minimum"))
	{
		selectedStatisticIndex=1;
	}
	if(statisticName.equalsIgnoreCase("Maximum"))
	{
		selectedStatisticIndex=2;
	}
	if(statisticName.equalsIgnoreCase("Average"))
	{
		selectedStatisticIndex=3;
	}
	if(statisticName.equalsIgnoreCase("Sum"))
	{
		selectedStatisticIndex=4;
	}
	
	session.setAttribute("STATISTICSELECTEDINDEX", selectedStatisticIndex);
}
		RequestDispatcher dispatcher = request.getRequestDispatcher("cloudwatch.jsp");
		dispatcher.forward(request, response);

	}

}
