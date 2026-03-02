package tutorial4;

import com.rabbitmq.client.*;

public class RingNode {

    public static void main(String[] args) throws Exception {
        int myID = Integer.parseInt(args[0]);
        int nextID = Integer.parseInt(args[1]);

        String myQueue = "node_" + myID;
        String nextQueue = "node_" + nextID;

        
        boolean[] started = {false};

    }
}
