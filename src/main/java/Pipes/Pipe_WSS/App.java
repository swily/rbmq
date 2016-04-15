package Pipes.Pipe_WSS;

import io.baratine.service.Result;
import io.baratine.web.Web;
import io.baratine.web.Get;




public class App
{
 @Get
 public void hello(Result<String> result)
 {
  result.ok("Hello, world");
 }
 
 
 public static void main(String []args)
 {

	 Web.include(App.class);

 }
}
