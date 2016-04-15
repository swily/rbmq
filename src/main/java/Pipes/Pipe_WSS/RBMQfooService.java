package Pipes.Pipe_WSS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import io.baratine.pipe.Pipe;
import io.baratine.pipe.Pipes;
import io.baratine.pipe.ResultPipeIn;
import io.baratine.pipe.ResultPipeOut;
import io.baratine.service.OnDestroy;

import io.baratine.service.OnInit;
import io.baratine.service.Result;
import io.baratine.service.Service;
import io.baratine.service.ServiceRef;
import io.baratine.service.Startup;
import io.baratine.stream.ResultStream;
import io.baratine.web.Get;
import io.baratine.web.Query;

@Service
@Startup
public class RBMQfooService {

	private Connection cn;
	private Channel chn;
	
	@Inject @Service("pipe:///messages")
	Pipes<String> _pipes;
	
	private Pipe<String> _pipeOut;

	@OnInit
	public void startup(Result<Void> r) throws IOException, TimeoutException{
			
			RBMQfooService self = ServiceRef.current().as(RBMQfooService.class);
			
			try{
				//connect to server
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost("localhost");
				Connection connection = factory.newConnection();
				Channel channel = connection.createChannel();

				chn = channel;
				cn = connection;

				//Declare exchange
				chn.exchangeDeclare("exchange0", "direct", true);
				
				//Declare queue
				channel.queueDeclare("Queue1", false, false, false, null);
				String queueName = "Queue1";
				chn.queueBind(queueName, "exchange0", "routingkey0");

			

			} catch(Exception e) {
				e.printStackTrace();
			}

			final Consumer consumer = new DefaultConsumer(chn) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException 
				{
					String message = new String(body, "UTF-8");
					
					//Message needs to be passed to Baratine
					
					//ResultPipeOut msg = ;
					//_pipes.publish(msg);
					
					_pipes.publish((x,e) -> { 
						_pipeOut = x;
					});
					
					
					//self.send(message, Result.ignore());
					
					chn.basicAck(envelope.getDeliveryTag(), false);
				}
			};
			

			chn.basicConsume("Queue1", false, consumer);
		
		
		_pipes.publish((x,e) -> { 
			System.out.println("PIPE initialized: " + x + "    exception: " + e );
			r.ok(null);
		});
		
	}
	
	
	@Get("/test")
	public void test(Result<String> r){
		System.out.println("PIPE test called: ");
		Receive(r);
		//_pipeOut.next("Msg to browser");
		r.ok(null);
	}
	

	public void Receive(Result<String> r) {
			 Date date = new Date();
			 _pipeOut.next(date.toString());
			 r.ok(null);
			// r.ok(date.toString());
	}


}
