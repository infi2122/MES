package OPC_UA;

import org.eclipse.milo.examples.client.ClientExampleRunner;
import org.eclipse.milo.examples.client.WriteExample;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class opcConnection {

    private OpcUaClient opcConnection;
    private ClientExampleRunner clientExampleRunner;
    private String nodePath = "|var|CODESYS Control Win V3 x64.Application.GVL.";
    private int nameSpaceIndex = 4;

    public OpcUaClient getOpcConnection() {
        return opcConnection;
    }

    public void setOpcConnection(OpcUaClient opcConnection) {
        this.opcConnection = opcConnection;
    }

    public ClientExampleRunner getClientExampleRunner() {
        return clientExampleRunner;
    }

    public void setClientExampleRunner(ClientExampleRunner clientExampleRunner) {
        this.clientExampleRunner = clientExampleRunner;
    }

    public void createOPCconnection() {

        /* *** It's writeExample only for the purpose of create a ClientExample,
         * it could be read or any other that implements ClientExample
         */
        try {
            WriteExample clientExample = new WriteExample();

            setClientExampleRunner(new ClientExampleRunner(clientExample, false));

            setOpcConnection(getClientExampleRunner().createClient());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public int readMCT(int machineID) {

        OpcUaClient client = getOpcConnection();

        try {
            client.connect().get();

            // synchronous read request via VariableNode
            String identifier = nodePath + "MCT_" + machineID;

            NodeId node_id = new NodeId(nameSpaceIndex, identifier);

            UaVariableNode mm = client.getAddressSpace().getVariableNode(node_id);

            DataValue value = mm.readValue();

            String val = String.valueOf(value.getValue());
            String[] val2 = val.split("=", -1);
            String[] val3 = val2[1].split("}", -1);

            System.out.println(val3[0]);

            return Integer.parseInt(val3[0]);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public void writeMCT(int machineID, int newTool) {
        OpcUaClient client = getOpcConnection();

        try {
            client.connect().get();

            short tool = (short) newTool;

            DataValue dataValue = new DataValue(new Variant(tool));
            String identifier = nodePath + "MCT_" + machineID;

            CompletableFuture<StatusCode> f = client.writeValue(new NodeId(nameSpaceIndex, identifier), dataValue);

            if (f.get().isBad()) {
                System.out.println("Error on write!");
            }
        } catch (ExecutionException ex) {
            ex.printStackTrace();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        getClientExampleRunner().getFuture().complete(client);


    }


}