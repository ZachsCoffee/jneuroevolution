/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

module utils {
    requires java.desktop;
    requires core;

    exports files.binary;
    exports files.csv;
    exports maths;
    exports maths.matrix;
    exports cli.stdout;
    exports options;
}
