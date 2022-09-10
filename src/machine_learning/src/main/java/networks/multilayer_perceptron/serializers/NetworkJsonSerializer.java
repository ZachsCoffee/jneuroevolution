package networks.multilayer_perceptron.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import networks.multilayer_perceptron.builders.NetworkModel;
import networks.multilayer_perceptron.builders.NeuralNetworkBuilder;
import networks.multilayer_perceptron.network.NeuralNetwork;

public class NetworkJsonSerializer {
    private static final Gson gson = new GsonBuilder()
            .create();

    public static String toJson(NeuralNetwork neuralNetwork) {
        return gson.toJson(NetworkModel.from(neuralNetwork));
    }

    public static NeuralNetwork fromJson(String json) {
        NetworkModel networkModel = gson.fromJson(json, NetworkModel.class);

        return NeuralNetworkBuilder.buildFrom(networkModel);
    }
}
