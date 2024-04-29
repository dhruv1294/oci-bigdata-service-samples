package com.example.fn;

import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.bds.BdsClient;
import com.oracle.bmc.bds.model.Node;
import com.oracle.bmc.bds.model.RemoveNodeDetails;
import com.oracle.bmc.bds.requests.GetBdsInstanceRequest;
import com.oracle.bmc.bds.requests.RemoveNodeRequest;
import com.oracle.bmc.bds.responses.GetBdsInstanceResponse;
import com.oracle.bmc.bds.responses.RemoveNodeResponse;

import java.util.Base64;
import java.util.List;

public class ScaleDown {

    private BdsClient bdsClient = null;

    final ResourcePrincipalAuthenticationDetailsProvider provider
            = ResourcePrincipalAuthenticationDetailsProvider.builder().build();

    public ScaleDown() {
        try {

            System.err.println("OCI_RESOURCE_PRINCIPAL_REGION " + System.getenv("OCI_RESOURCE_PRINCIPAL_REGION"));
            System.err.println("OCI_RESOURCE_PRINCIPAL_RPST " + System.getenv("OCI_RESOURCE_PRINCIPAL_RPST"));
            System.err.println("OCI_RESOURCE_PRINCIPAL_PRIVATE_PEM " + System.getenv("OCI_RESOURCE_PRINCIPAL_PRIVATE_PEM"));

            bdsClient = new BdsClient(provider);

        } catch (Throwable ex) {
            System.err.println("Failed to instantiate bds client - " + ex.getMessage());
        }
    }

    public int handleRequest() {

        int responseCode = 0;

        if (bdsClient == null) {
            System.err.println("There was a problem creating the bds client object. Please check logs");
            return responseCode;
        }
        String ADMIN_PWD = encodeCredentials("Admin@123");

        try {


            System.err.println("Creating a request for to get BDSInstance " );

            GetBdsInstanceRequest getBdsInstanceRequest = GetBdsInstanceRequest.builder()
                    .bdsInstanceId("ocid1.bigdataservice.oc1.iad.amaaaaaapvq3y5aa6kk7ghpecaqbras6io56fxtymjpvkx6vbkuvsz7vph7q")
                    .opcRequestId("VRJVYQW4PQVK9Z5XENZX<unique_ID>").build();

            /* Send request to the Client */
            GetBdsInstanceResponse response = bdsClient.getBdsInstance(getBdsInstanceRequest);
            List<Node> nodes = response.getBdsInstance().getNodes();
            String workerInstanceId = "";
            for(Node node : nodes){
                if (node.getNodeType() == Node.NodeType.Worker) {
                    workerInstanceId = node.getInstanceId();
                    break; // Break the loop once a Worker node is found
                }
            }
            System.out.println(workerInstanceId);
            System.err.println("Creating a request for to remove WorkerNode " );
            RemoveNodeDetails removeNodeDetails = RemoveNodeDetails.builder()
                    .clusterAdminPassword(ADMIN_PWD)
                    .isForceRemoveEnabled(true)
                    .nodeId(workerInstanceId).build();

            RemoveNodeRequest removeNodeRequest = RemoveNodeRequest.builder()
                    .bdsInstanceId("ocid1.bigdataservice.oc1.iad.amaaaaaapvq3y5aa6kk7ghpecaqbras6io56fxtymjpvkx6vbkuvsz7vph7q")
                    .removeNodeDetails(removeNodeDetails)
                    .build();

            /* Send request to the Client */
            RemoveNodeResponse removeNodeResponse = bdsClient.removeNode(removeNodeRequest);
            System.out.println(removeNodeResponse);

        } catch (Throwable e) {
            System.err.println("Error adding Nodes " + e.getMessage());
        }

        return responseCode;
    }
    private static String encodeCredentials(String password) {
        return Base64.getEncoder().encodeToString((password).getBytes());
    }

}