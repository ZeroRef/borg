package org.zeroref.borg.lab;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.zeroref.borg.runtime.EndpointWire;

import java.io.IOException;
import java.util.List;

public class Env {

    public static SingleNodeKafkaCluster CLUSTER;

    @BeforeClass
    public void boot() throws IOException {
        CLUSTER = new SingleNodeKafkaCluster();
        CLUSTER.startup();

        CLUSTER.sendMessages(new ProducerRecord<>("ready", "yes"));

        while (true){
            List<String> ready = CLUSTER.readAllMessages("ready");

            if(ready.size() != 0)
                break;
        }
    }

    @AfterClass
    public void release(){
        CLUSTER.shutdown();
    }

    protected int countMessages(String topicName){

        int budget = 5 * 1000;
        int used = 0;

        while (budget > used){
            List<String> strings = CLUSTER.readAllMessages(topicName);
            used +=400;

            if(strings.size() == 0){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            return strings.size();
        }

        return 0;
    }

    protected int expect0Messages(String topicName, int deadline){

        int used = 0;

        while (deadline > used){
            List<String> strings = CLUSTER.readAllMessages(topicName);
            used +=400;

            boolean holds = strings.size() == 0;

            System.out.println(used);

            if(holds){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }else{
                return strings.size();
            }
        }

        return 0;
    }

    protected void send(String topic, String message){
        CLUSTER.sendMessages(new ProducerRecord<>(topic, message));
    }

    protected EndpointWire wire(String name){
        return new EndpointWire(name, CLUSTER.getKafkaConnect(),CLUSTER.getZookeeperString());
    }
}
