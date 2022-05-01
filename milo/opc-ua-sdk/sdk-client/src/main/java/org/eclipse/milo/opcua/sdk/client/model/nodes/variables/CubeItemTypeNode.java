package org.eclipse.milo.opcua.sdk.client.model.nodes.variables;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.model.types.variables.CubeItemType;
import org.eclipse.milo.opcua.sdk.client.nodes.UaNode;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExtensionObject;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.structured.AxisInformation;

public class CubeItemTypeNode extends ArrayItemTypeNode implements CubeItemType {
    public CubeItemTypeNode(OpcUaClient client, NodeId nodeId, NodeClass nodeClass,
                            QualifiedName browseName, LocalizedText displayName, LocalizedText description,
                            UInteger writeMask, UInteger userWriteMask, DataValue value, NodeId dataType,
                            Integer valueRank, UInteger[] arrayDimensions, UByte accessLevel, UByte userAccessLevel,
                            Double minimumSamplingInterval, Boolean historizing) {
        super(client, nodeId, nodeClass, browseName, displayName, description, writeMask, userWriteMask, value, dataType, valueRank, arrayDimensions, accessLevel, userAccessLevel, minimumSamplingInterval, historizing);
    }

    @Override
    public AxisInformation getXAxisDefinition() throws UaException {
        PropertyTypeNode node = getXAxisDefinitionNode();
        return cast(node.getValue().getValue().getValue(), AxisInformation.class);
    }

    @Override
    public void setXAxisDefinition(AxisInformation xAxisDefinition) throws UaException {
        PropertyTypeNode node = getXAxisDefinitionNode();
        ExtensionObject value = ExtensionObject.encode(client.getStaticSerializationContext(), xAxisDefinition);
        node.setValue(new Variant(value));
    }

    @Override
    public AxisInformation readXAxisDefinition() throws UaException {
        try {
            return readXAxisDefinitionAsync().get();
        } catch (ExecutionException | InterruptedException e) {
            throw UaException.extract(e).orElse(new UaException(StatusCodes.Bad_UnexpectedError, e));
        }
    }

    @Override
    public void writeXAxisDefinition(AxisInformation xAxisDefinition) throws UaException {
        try {
            writeXAxisDefinitionAsync(xAxisDefinition).get();
        } catch (ExecutionException | InterruptedException e) {
            throw UaException.extract(e).orElse(new UaException(StatusCodes.Bad_UnexpectedError, e));
        }
    }

    @Override
    public CompletableFuture<? extends AxisInformation> readXAxisDefinitionAsync() {
        return getXAxisDefinitionNodeAsync().thenCompose(node -> node.readAttributeAsync(AttributeId.Value)).thenApply(v -> cast(v.getValue().getValue(), AxisInformation.class));
    }

    @Override
    public CompletableFuture<StatusCode> writeXAxisDefinitionAsync(AxisInformation xAxisDefinition) {
        ExtensionObject encoded = ExtensionObject.encode(client.getStaticSerializationContext(), xAxisDefinition);
        DataValue value = DataValue.valueOnly(new Variant(encoded));
        return getXAxisDefinitionNodeAsync()
            .thenCompose(node -> node.writeAttributeAsync(AttributeId.Value, value));
    }

    @Override
    public PropertyTypeNode getXAxisDefinitionNode() throws UaException {
        try {
            return getXAxisDefinitionNodeAsync().get();
        } catch (ExecutionException | InterruptedException e) {
            throw UaException.extract(e).orElse(new UaException(StatusCodes.Bad_UnexpectedError, e));
        }
    }

    @Override
    public CompletableFuture<? extends PropertyTypeNode> getXAxisDefinitionNodeAsync() {
        CompletableFuture<UaNode> future = getMemberNodeAsync("http://opcfoundation.org/UA/", "XAxisDefinition", ExpandedNodeId.parse("nsu=http://opcfoundation.org/UA/;i=46"), false);
        return future.thenApply(node -> (PropertyTypeNode) node);
    }

    @Override
    public AxisInformation getYAxisDefinition() throws UaException {
        PropertyTypeNode node = getYAxisDefinitionNode();
        return cast(node.getValue().getValue().getValue(), AxisInformation.class);
    }

    @Override
    public void setYAxisDefinition(AxisInformation yAxisDefinition) throws UaException {
        PropertyTypeNode node = getYAxisDefinitionNode();
        ExtensionObject value = ExtensionObject.encode(client.getStaticSerializationContext(), yAxisDefinition);
        node.setValue(new Variant(value));
    }

    @Override
    public AxisInformation readYAxisDefinition() throws UaException {
        try {
            return readYAxisDefinitionAsync().get();
        } catch (ExecutionException | InterruptedException e) {
            throw UaException.extract(e).orElse(new UaException(StatusCodes.Bad_UnexpectedError, e));
        }
    }

    @Override
    public void writeYAxisDefinition(AxisInformation yAxisDefinition) throws UaException {
        try {
            writeYAxisDefinitionAsync(yAxisDefinition).get();
        } catch (ExecutionException | InterruptedException e) {
            throw UaException.extract(e).orElse(new UaException(StatusCodes.Bad_UnexpectedError, e));
        }
    }

    @Override
    public CompletableFuture<? extends AxisInformation> readYAxisDefinitionAsync() {
        return getYAxisDefinitionNodeAsync().thenCompose(node -> node.readAttributeAsync(AttributeId.Value)).thenApply(v -> cast(v.getValue().getValue(), AxisInformation.class));
    }

    @Override
    public CompletableFuture<StatusCode> writeYAxisDefinitionAsync(AxisInformation yAxisDefinition) {
        ExtensionObject encoded = ExtensionObject.encode(client.getStaticSerializationContext(), yAxisDefinition);
        DataValue value = DataValue.valueOnly(new Variant(encoded));
        return getYAxisDefinitionNodeAsync()
            .thenCompose(node -> node.writeAttributeAsync(AttributeId.Value, value));
    }

    @Override
    public PropertyTypeNode getYAxisDefinitionNode() throws UaException {
        try {
            return getYAxisDefinitionNodeAsync().get();
        } catch (ExecutionException | InterruptedException e) {
            throw UaException.extract(e).orElse(new UaException(StatusCodes.Bad_UnexpectedError, e));
        }
    }

    @Override
    public CompletableFuture<? extends PropertyTypeNode> getYAxisDefinitionNodeAsync() {
        CompletableFuture<UaNode> future = getMemberNodeAsync("http://opcfoundation.org/UA/", "YAxisDefinition", ExpandedNodeId.parse("nsu=http://opcfoundation.org/UA/;i=46"), false);
        return future.thenApply(node -> (PropertyTypeNode) node);
    }

    @Override
    public AxisInformation getZAxisDefinition() throws UaException {
        PropertyTypeNode node = getZAxisDefinitionNode();
        return cast(node.getValue().getValue().getValue(), AxisInformation.class);
    }

    @Override
    public void setZAxisDefinition(AxisInformation zAxisDefinition) throws UaException {
        PropertyTypeNode node = getZAxisDefinitionNode();
        ExtensionObject value = ExtensionObject.encode(client.getStaticSerializationContext(), zAxisDefinition);
        node.setValue(new Variant(value));
    }

    @Override
    public AxisInformation readZAxisDefinition() throws UaException {
        try {
            return readZAxisDefinitionAsync().get();
        } catch (ExecutionException | InterruptedException e) {
            throw UaException.extract(e).orElse(new UaException(StatusCodes.Bad_UnexpectedError, e));
        }
    }

    @Override
    public void writeZAxisDefinition(AxisInformation zAxisDefinition) throws UaException {
        try {
            writeZAxisDefinitionAsync(zAxisDefinition).get();
        } catch (ExecutionException | InterruptedException e) {
            throw UaException.extract(e).orElse(new UaException(StatusCodes.Bad_UnexpectedError, e));
        }
    }

    @Override
    public CompletableFuture<? extends AxisInformation> readZAxisDefinitionAsync() {
        return getZAxisDefinitionNodeAsync().thenCompose(node -> node.readAttributeAsync(AttributeId.Value)).thenApply(v -> cast(v.getValue().getValue(), AxisInformation.class));
    }

    @Override
    public CompletableFuture<StatusCode> writeZAxisDefinitionAsync(AxisInformation zAxisDefinition) {
        ExtensionObject encoded = ExtensionObject.encode(client.getStaticSerializationContext(), zAxisDefinition);
        DataValue value = DataValue.valueOnly(new Variant(encoded));
        return getZAxisDefinitionNodeAsync()
            .thenCompose(node -> node.writeAttributeAsync(AttributeId.Value, value));
    }

    @Override
    public PropertyTypeNode getZAxisDefinitionNode() throws UaException {
        try {
            return getZAxisDefinitionNodeAsync().get();
        } catch (ExecutionException | InterruptedException e) {
            throw UaException.extract(e).orElse(new UaException(StatusCodes.Bad_UnexpectedError, e));
        }
    }

    @Override
    public CompletableFuture<? extends PropertyTypeNode> getZAxisDefinitionNodeAsync() {
        CompletableFuture<UaNode> future = getMemberNodeAsync("http://opcfoundation.org/UA/", "ZAxisDefinition", ExpandedNodeId.parse("nsu=http://opcfoundation.org/UA/;i=46"), false);
        return future.thenApply(node -> (PropertyTypeNode) node);
    }
}