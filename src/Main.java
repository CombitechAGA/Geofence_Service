import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by Fredrik on 2015-07-06.
 */

//l�s config
public class Main {
    public static void main(String[] args) {
        MqttClient client = null;
        MemoryPersistence persistence = new MemoryPersistence();
        try {
            client = new MqttClient("tcp://mqtt.phelicks.net:1883", "GeofenceService", persistence);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName("cab");
            options.setPassword("sjuttongubbar".toCharArray());
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        GeofenceCallback geofenceCallback = new GeofenceCallback(client);
        client.setCallback(geofenceCallback);


        try {
            client.subscribe("telemetry/snapshot");
            client.subscribe("set/config");
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
