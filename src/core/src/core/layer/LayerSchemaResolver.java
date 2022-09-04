package core.layer;

import core.schema.SchemaRow;

public interface LayerSchemaResolver {
    MatrixSchema[] getSchema(MatrixSchema[] inputSchema);

    SchemaRow getSchemaRow(MatrixSchema[] inputSchema);
}
