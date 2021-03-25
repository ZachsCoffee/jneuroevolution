package input;

import convolution.MatrixReader;

public interface ImageInput {
    MatrixReader[] getChannels();
}
