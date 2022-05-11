package OPC_UA;

import org.eclipse.milo.examples.client.ClientExampleRunner;
import org.eclipse.milo.examples.client.WriteExample;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.nodes.UaVariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.w3c.dom.ls.LSOutput;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class opcConnection {

    private OpcUaClient opcConnection;
    private ClientExampleRunner clientExampleRunner;

    private String globalVarNodePath = "|var|CODESYS Control Win V3 x64.Application.GVL.";
    private String IoVarNodePath = "|var|CODESYS Control Win V3 x64.Application.IoConfig_Globals_Mapping.";
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


    public String read(String node, String path, String type) {

        OpcUaClient client = getOpcConnection();

        try {
            client.connect().get();
            String identifier = null;
            if (path.equals("GVL")) {
                identifier = globalVarNodePath + node;
            } else if (path.equals("IO")) {
                identifier = IoVarNodePath + node;
            }
            // synchronous read request via VariableNode
            NodeId node_id = new NodeId(nameSpaceIndex, identifier);

            UaVariableNode mm = client.getAddressSpace().getVariableNode(node_id);

            DataValue value = mm.readValue();

            if (type.equals("int")) {
                String val = String.valueOf(value.getValue());
                String[] val2 = val.split("=", -1);
                String[] val3 = val2[1].split("}", -1);

                return val3[0];
            } else if (type.equals("bool")) {
                String val = String.valueOf(value.getValue().getValue());
                return val;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param node
     * @param path
     * @param newValue If boolean newValue, 1 = TRUE, 0 = FALSE
     *                 If int newValue
     * @param type     If type != int/bool -> error
     */
    public void write(String node, String path, int newValue, String type) {
        OpcUaClient client = getOpcConnection();

        try {
            client.connect().get();
            DataValue dataValue;

            if (type.equals("bool")) {
                if (newValue == 1) {
                    boolean i = true;
                    dataValue = new DataValue(new Variant(i));
                } else {
                    boolean i = false;
                    dataValue = new DataValue(new Variant(i));
                }


            } else if (type.equals("int")) {
                dataValue = new DataValue(new Variant((short) newValue));

            } else {
                System.out.println("Error! Type not defined in write OPC");
                return;
            }

            String identifier = null;
            if (path.equals("GVL")) {
                identifier = globalVarNodePath + node;
            } else if (path.equals("IO")) {
                identifier = IoVarNodePath + node;
            }

            CompletableFuture<StatusCode> f = client.writeValue(new NodeId(nameSpaceIndex, identifier), dataValue);

            if (f.get().isBad()) {
                System.out.println("Error on writing by OPC-UA: " + node);
            }
        } catch (ExecutionException ex) {
            ex.printStackTrace();

        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        getClientExampleRunner().getFuture().complete(client);


    }

    /**
     * @param node Means that is the variable name
     * @param path GVL or IO
     * @return
     */
    public int readInt(String node, String path) {

        return Integer.parseInt(read(node, path,"int"));
    }

    /**
     * @param node     Means that is the variable name
     * @param path     GVL or IO
     * @param newValue
     */
    public void writeInt(String node, String path, int newValue) {
        write(node, path, newValue, "int");
    }

    /**
     * @param node Means that is the variable name
     * @param path GVL or IO
     * @return
     */
    public boolean readBool(String node, String path) {

        String ret = read(node,path,"bool");
        if(ret.equals("true"))
            return true;
        else
            return false;

    }

    /**
     * @param node     Means that is the variable name
     * @param path     GVL or IO
     * @param newValue 1 or 0
     */
    public void writeBool(String node, String path, int newValue) {

        write(node, path, newValue, "bool");

    }


}
