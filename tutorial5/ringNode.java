package tutorial4;

import com.rabbitmq.client.*;

public class RingNode {

    public static void main(String[] args) throws Exception {

        int myID = Integer.parseInt(args[0]);
        int nextID = Integer.parseInt(args[1]);

        String myQueue = "node_" + myID;
        String nextQueue = "node_" + nextID;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(myQueue,false,false,false,null);
        channel.queueDeclare(nextQueue,false,false,false,null);

        // Node 1 starts the election
        if(myID == 1){
            String msg = "ELECTION " + myID;
            channel.basicPublish("", nextQueue, null, msg.getBytes());
            System.out.println("[Node "+myID+"] Started election");
        }

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {

            String message = new String(delivery.getBody(),"UTF-8");
            System.out.println("[Node "+myID+"] Received: " + message);

            if(message.startsWith("ELECTION")){

                int candidate = Integer.parseInt(message.split(" ")[1]);

                if(candidate < myID){
                    // forward smaller ID
                    channel.basicPublish("", nextQueue, null, message.getBytes());
                }
                else if(candidate > myID){
                    // replace with my ID
                    String newMsg = "ELECTION " + myID;
                    channel.basicPublish("", nextQueue, null, newMsg.getBytes());
                }
                else{
                    // message returned to origin
                    System.out.println("[Node "+myID+"] I am the leader!");

                    String leaderMsg = "LEADER " + myID;
                    channel.basicPublish("", nextQueue, null, leaderMsg.getBytes());
                }
            }

            else if(message.startsWith("LEADER")){

                int leader = Integer.parseInt(message.split(" ")[1]);
                System.out.println("[Node "+myID+"] Leader is: " + leader);

                if(leader != myID){
                    channel.basicPublish("", nextQueue, null, message.getBytes());
                }
            }
        };

        channel.basicConsume(myQueue,true,deliverCallback,consumerTag->{});
    }
}
