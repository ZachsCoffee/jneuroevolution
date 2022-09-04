package core.layer;

public interface Layer extends Imitable<Layer>, LayerSchemaResolver{
    MatrixReader[] execute(MatrixReader[] inputChannels);
}
