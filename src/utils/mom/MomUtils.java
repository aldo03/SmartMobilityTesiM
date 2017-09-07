package utils.mom;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MomUtils {
	public static void sendMsg(ConnectionFactory factory, String queue_name, String message) throws UnsupportedEncodingException, IOException, TimeoutException{
		Connection connection = factory.newConnection();
		//System.out.println("CONNECTED!");
	    Channel channel = connection.createChannel();
	    //System.out.println("CREATED!");
	    channel.queueDeclare(queue_name, false, false, false, null);
	    //System.out.println("DECLARED!");
	    channel.basicPublish("", queue_name, null, message.getBytes("UTF-8"));
	    //System.out.println("PUBLISHED!");
	    channel.close();
	    connection.close();
	}
}
