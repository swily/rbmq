package Pipes.Pipe_WSS;

import java.io.IOException;
import java.util.Random;

import javax.inject.Inject;

import io.baratine.service.Result;
import io.baratine.service.Service;
import io.baratine.web.Path;
import io.baratine.web.ServiceWebSocket;
import io.baratine.web.Web;
import io.baratine.web.WebSocket;
import io.baratine.web.WebSocketClose;

import io.baratine.pipe.*;

@Service("session:")
public class client implements ServiceWebSocket<String, String>{

	//@Inject
	//RBMQfooService fs;
	
	@Inject @Service("pipe:///messages")
	Pipes<String> _pipes;

	private Pipe<String> _pipeIn;
	
//	private long id = new Random().nextLong();
	
	public void open(WebSocket<String> ws) {
		
		ResultPipeIn<String> resultPipe = Pipe.in((String x) -> {
			System.out.println("Lambda called back: " + x);
			ws.next(x);
		});
		
		//_pipeIn = resultPipe.pipe();
		
		_pipes.subscribe(resultPipe);
		
		System.out.println("INSIDE CLIENT.java WS");

		/*	
		 * 	
		fs.Receive((x,e) -> {
			ws.next(x);
		});
		 * 
		 * fs.Send((x,e) -> {
		System.out.println(x);
	});	
		
		try {
			fs.Register( id, (x,e,isend) -> {
				//send back to client
				ws.next(x);

			});
		} catch (IOException e) {

			e.printStackTrace();
		}	 */
	}

	@Override
	public void close(WebSocketClose code, String msg, WebSocket<String> webSocket) throws IOException{
		_pipeIn.close();
	}
	
	
	/*
	@Override
	public void next(String value, WebSocket<String> webSocket) throws IOException {
		System.out.println("Next");

		if (value.equalsIgnoreCase("register")){
			
			
			/*
			
			try {
				fs.Register(id, (x,e,isend) ->{

					//value back to JS client
					webSocket.next(x);
				});}
			catch (IOException e) {

				e.printStackTrace();
			}	
		}
		else if(value.equalsIgnoreCase("unsubscribe")){
			fs.remove(id);
		}

	}
*/
	public static void main(String[] args){

		Web.websocket("/msg").to(client.class);
		Web.include(RBMQfooService.class);
		Web.start();
	}
	@Override
	public void next(String value, WebSocket<String> webSocket) throws IOException {
		// TODO Auto-generated method stub
		
	}


}
