package qa.com.qatarpost.wih.notifications;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpHeaders;
import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author stelios@redhat.com
 *
 */
public class AddEventWIH extends AbstractLogOrThrowWorkItemHandler {

	private ObjectMapper objectMapper = new ObjectMapper();
	//HttpHeaders headers = new HttpHeaders();

	@Override
	public void abortWorkItem(WorkItem arg0, WorkItemManager arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		// TODO Auto-generated method stub

		String addEventUrl = (String) workItem.getParameter("url");
		String date_time = (String) workItem.getParameter("date_time");
		Long event_code = (Long) workItem.getParameter("event_code");
		String tracking_number = (String) workItem.getParameter("tracking_number");
		String branch = (String) workItem.getParameter("branch");
		
		String department = (String) workItem.getParameter("department");
		String inbound_outbound = (String) workItem.getParameter("inbound_outbound");
		String product_service_code = (String) workItem.getParameter("product_service_code");
		String remarks = (String) workItem.getParameter("remarks");
		String source = (String) workItem.getParameter("source");
		String user_id = (String) workItem.getParameter("user_id");
		
		try {

			URL url = new URL("http://10.202.10.147:7001/qatarpost-evm/event/addEvent");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			
			String input = "{\r\n" + 
					"  \"branch\": " + branch + ",\r\n" + 
					"  \"date_time\": " + date_time + ",\r\n" + 
					"  \"event_code\": " + event_code + ",\r\n" + 
					"  \"tracking_number\": " + tracking_number + ",\r\n" + 
					"  \"department\": " + department + ",\r\n" +
					"  \"inbound_outbound\": " + inbound_outbound + ",\r\n" +
					"  \"product_service_code\": " + product_service_code + ",\r\n" +
					"  \"remarks\": " + remarks + ",\r\n" +
					"  \"source\": " + source + ",\r\n" +
					"  \"user_id\": " + user_id + ",\r\n" +
					"}";
			
			
			System.out.println(input);
			
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}
			manager.completeWorkItem(workItem.getId(), null);

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

}
