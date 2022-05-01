package org.eclipse.milo.opcua.sdk.client.model.types.objects;

import java.util.concurrent.CompletableFuture;

import org.eclipse.milo.opcua.sdk.client.model.types.variables.PropertyType;
import org.eclipse.milo.opcua.sdk.core.QualifiedProperty;
import org.eclipse.milo.opcua.sdk.core.ValueRanks;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.ExpandedNodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UInteger;

public interface AuditCancelEventType extends AuditSessionEventType {
    QualifiedProperty<UInteger> REQUEST_HANDLE = new QualifiedProperty<>(
        "http://opcfoundation.org/UA/",
        "RequestHandle",
        ExpandedNodeId.parse("nsu=http://opcfoundation.org/UA/;i=7"),
        ValueRanks.Scalar,
        UInteger.class
    );

    /**
     * Get the local value of the RequestHandle Node.
     * <p>
     * The returned value is the last seen; it is not read live from the server.
     *
     * @return the local value of the RequestHandle Node.
     * @throws UaException if an error occurs creating or getting the RequestHandle Node.
     */
    UInteger getRequestHandle() throws UaException;

    /**
     * Set the local value of the RequestHandle Node.
     * <p>
     * The value is only updated locally; it is not written to the server.
     *
     * @param requestHandle the local value to set for the RequestHandle Node.
     * @throws UaException if an error occurs creating or getting the RequestHandle Node.
     */
    void setRequestHandle(UInteger requestHandle) throws UaException;

    /**
     * Read the value of the RequestHandle Node from the server and update the local value if the
     * operation succeeds.
     *
     * @return the {@link UInteger} value read from the server.
     * @throws UaException if a service- or operation-level error occurs.
     */
    UInteger readRequestHandle() throws UaException;

    /**
     * Write a new value for the RequestHandle Node to the server and update the local value if
     * the operation succeeds.
     *
     * @param requestHandle the {@link UInteger} value to write to the server.
     * @throws UaException if a service- or operation-level error occurs.
     */
    void writeRequestHandle(UInteger requestHandle) throws UaException;

    /**
     * An asynchronous implementation of {@link #readRequestHandle()}.
     *
     * @return a CompletableFuture that completes successfully with the property value or completes
     * exceptionally if an operation- or service-level error occurs.
     */
    CompletableFuture<? extends UInteger> readRequestHandleAsync();

    /**
     * An asynchronous implementation of {@link #writeRequestHandle(UInteger)}.
     *
     * @return a CompletableFuture that completes successfully with the operation result or
     * completes exceptionally if a service-level error occurs.
     */
    CompletableFuture<StatusCode> writeRequestHandleAsync(UInteger requestHandle);

    /**
     * Get the RequestHandle {@link PropertyType} Node, or {@code null} if it does not exist.
     * <p>
     * The Node is created when first accessed and cached for subsequent calls.
     *
     * @return the RequestHandle {@link PropertyType} Node, or {@code null} if it does not exist.
     * @throws UaException if an error occurs creating or getting the Node.
     */
    PropertyType getRequestHandleNode() throws UaException;

    /**
     * Asynchronous implementation of {@link #getRequestHandleNode()}.
     *
     * @return a CompletableFuture that completes successfully with the
     * ? extends PropertyType Node or completes exceptionally if an error occurs
     * creating or getting the Node.
     */
    CompletableFuture<? extends PropertyType> getRequestHandleNodeAsync();
}
