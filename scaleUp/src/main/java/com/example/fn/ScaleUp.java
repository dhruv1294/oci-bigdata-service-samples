package com.example.fn;

import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.bds.BdsClient;
import com.oracle.bmc.bds.model.AddWorkerNodesDetails;
import com.oracle.bmc.bds.requests.AddWorkerNodesRequest;
import com.oracle.bmc.bds.responses.AddWorkerNodesResponse;

import java.util.Base64;

public class ScaleUp {

    private BdsClient bdsClient = null;

    final ResourcePrincipalAuthenticationDetailsProvider provider
            = ResourcePrincipalAuthenticationDetailsProvider.builder().build();

    public ScaleUp() {
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


            System.err.println("Creating a request for Adding Nodes " );

            AddWorkerNodesDetails addWorkerNodesDetails = AddWorkerNodesDetails.builder()
                    .clusterAdminPassword(ADMIN_PWD)
                    .numberOfWorkerNodes(1)
                    .nodeType(AddWorkerNodesDetails.NodeType.Worker)
                    .shape("VM.Standard.E4.Flex")
                    .blockVolumeSizeInGBs(150L)
                    .build();

            AddWorkerNodesRequest addWorkerNodesRequest = AddWorkerNodesRequest.builder()
                    .bdsInstanceId("ocid1.bigdataservice.oc1.iad.amaaaaaapvq3y5aa6kk7ghpecaqbras6io56fxtymjpvkx6vbkuvsz7vph7q")
                    .addWorkerNodesDetails(addWorkerNodesDetails)
                    .build();

            AddWorkerNodesResponse response = bdsClient.addWorkerNodes(addWorkerNodesRequest);
            responseCode =  response.get__httpStatusCode__();
            System.err.println("Got " +  responseCode + " response code  " );

        } catch (Throwable e) {
            System.err.println("Error adding Nodes " + e.getMessage());
        }

        return responseCode;
    }
    private static String encodeCredentials(String password) {
        return Base64.getEncoder().encodeToString((password).getBytes());
    }

}