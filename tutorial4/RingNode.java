package tutorial4;

import com.rabbitmq.client.*;

public class RingNode {

    public static void main(String[] args) throws Exception {
        int myID = Integer.parseInt(args[0]);
        int nextID = Integer.parseInt(args[1]);

        
        

        String myQueue ="node_" + myID;
        String nextQueue= "node_" + nextID;

        boolean[] started = {false};
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        channel.queueDeclare(myQueue,false,false,false,null);
        channel.queueDeclare(nextQueue,false,false,false,null);
        if(myID==1 && !started[0]){
            started[0]=true;
            String message ="ACK_START " + myID;
            channel.basicPublish("", nextQueue, null, message.getBytes());
            System.out.println("[Node " + myID + "] PUSH");

        }

            
        DeliverCallback deliverCallback=(consumerTag,delivery)->{
            String message=new String(delivery.getBody(),"UTF-8");
            System.out.println("[Node " + myID + "] Received: " + message);
            
            if(message.startsWith("ACK_START")){
            int recieved_id=Integer.parseInt(message.split(" ")[1]);
            if(!started[0]){
                started[0]=true;
                
                channel.basicPublish("", nextQueue, null, "ACK_START".getBytes());}
            else if (myID>nextID){

                channel.basicPublish("", nextQueue, null, "ACK_START".getBytes());
            }
            else{

            }}
            else if (message.equals("ACK_START")){
                channel.basicPublish("", nextQueue, null,"PING".getBytes());

            }
            else if (message.equals("PING")){
                channel.basicPublish("", nextQueue, null, "PONG".getBytes());
            }
            else if (message.equals("PONG")){
                channel.basicPublish("", nextQueue, null, "PING".getBytes());
            }



            }
        }
}
