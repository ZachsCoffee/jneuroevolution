/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

module machine_learning {
    requires utils;
    requires jocl;
    requires java.logging;
    requires com.google.gson;
    requires aparapi;

    exports functions;
    exports networks.interfaces;
    exports networks.multilayer_perceptron;
    exports networks.recurrent_neural_network;
    exports abstraction;
    exports data_manipulation;
    exports networks.gpu;
    opens networks.gpu to aparapi;
}
