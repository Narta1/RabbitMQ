package tutorial4;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class PingPong {
    
    private final static String QUEUE_NAME = "ping";
    private final static String PONG_QUEUE_NAME = "pong";

    public static void main(String[] argv) throws Exception {
        String role=argv[0];
        
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel=connection.createChannel();
        channel.queueDeclare(QUEUE_NAME,true,false,false,null);
        channel.queueDeclare(PONG_QUEUE_NAME,true,false,false,null);

            if(role.equals("ping")){
                    String message="Ping";
                    channel.basicPublish("", PONG_QUEUE_NAME, null, message.getBytes());

                    DeliverCallback deliverCallback=(consumerTag,delivery)->{
                        String msg=new String(delivery.getBody(),"UTF-8");
                        System.out.println(" ping Received '" + msg + "'");
                        String response="Ping";
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt(); 
                        }
                        channel.basicPublish("",PONG_QUEUE_NAME,null,response.getBytes());

                    };
                    channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
            }else if(role.equals("pong")){
                DeliverCallback deliverCallback=(consumerTag,delivery)->{
                    String message=new String(delivery.getBody(),"UTF-8");
                    System.out.println(" pong Received '" + message + "'");
                    String response="Pong";
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); 
                    }
                    channel.basicPublish("",QUEUE_NAME,null,response.getBytes());
                };
                channel.basicConsume(PONG_QUEUE_NAME, true, deliverCallback, consumerTag -> { });
                    
            }else{
                    System.out.println("Usage: java PingPong ping|pong");
                    System.exit(1);
                }
            
    
    
    
    }
}