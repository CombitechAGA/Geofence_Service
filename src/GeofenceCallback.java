import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;

/**
 * Created by Fredrik on 2015-07-06.
 */
public class GeofenceCallback implements MqttCallback{
    private MqttClient client;
    private HashMap<String, GeofenceInfo> clientToInfo;


    public GeofenceCallback(MqttClient client){
        this.client = client;
        clientToInfo = new HashMap<>();
    }

    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("GeofenceService lost connection!");
    }


    //nu timestampar vi här, vi borde skicka med timestampet, men då kanske vi måste ändra i databasen, vi kan göra det imorgon
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        String message = mqttMessage.toString();
        System.out.println(message);


       if(topic.equals("telemetry/snapshot")){
           String carID = message.split(";")[0].split("carID:")[1];
           System.out.println(carID);
           if(clientToInfo.containsKey(carID)){
               double longitude = Double.parseDouble(message.split("longitude:")[1].split(";")[0]);
               System.out.println(longitude);
               double latitude = Double.parseDouble(message.split("latitude:")[1]);
               System.out.println(latitude);
               GeofenceInfo geofenceInfo = clientToInfo.get(carID);
               geofenceInfo.setCurrentLong(longitude);
               geofenceInfo.setCurrentLat(latitude);
               boolean error =geofenceInfo.checkForPointOfNoReturn();
               if (error){
                   new PublishThread(client,carID+"/message","You have left the permitted area. Please turn around.").start();
               }
           }
           else{
               System.out.println("Nu ska jag subscribe:a");
               new SubscribeThread(client,carID+"/config").start();
               new PublishThread(client,"request/config",carID).start();
           }
       }
       else if(topic.contains("set/config")) {
           if(message.contains("home")){
               String carID = message.substring(message.indexOf(":")+1,message.indexOf(";"));
               if(clientToInfo.containsKey(carID)){
                   GeofenceInfo geofenceInfo = clientToInfo.get(carID);
                   double latitude = Double.parseDouble(message.split("home:")[1].split(",")[0]);
                   double longitude = Double.parseDouble(message.split(",")[1]);
                   System.out.println("uppdaterar min homelocation");
                   System.out.println(latitude);
                   System.out.println(longitude);
                   geofenceInfo.setHomeLong(longitude);
                   geofenceInfo.setHomeLat(latitude);
               }
           }
       }

       else if (topic.contains("/config")){
           System.out.println("Nu kom det en requestad config");
           String carID = topic.split("/config")[0];
           System.out.println("carID: "+carID);
           double latitude = Double.parseDouble(message.split("home#")[1].split(",")[0]);
           System.out.println(latitude);
           double longitude = Double.parseDouble(message.split("home#")[1].split(",")[1].split("\n")[0]);
           System.out.println(longitude);
           int radius = Integer.parseInt(message.split("geofence#")[1].split("\n")[0]);
           System.out.println("radius: "+radius);
           GeofenceInfo geofenceInfo = new GeofenceInfo(radius,latitude,longitude);
           clientToInfo.put(carID,geofenceInfo);


       }
       else{
           System.out.println("Unknown topic:\""+topic+"\"");
       }



    }



    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
