/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

module neuroevolution {
    requires evolution_algorithms;
    requires machine_learning;
    requires utils;
    requires core;

    exports neuroevolution;
    exports execution;
    exports execution.common;
}