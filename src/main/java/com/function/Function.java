package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FixedDelayRetry;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HelloWorld". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HelloWorld
     * 2. curl "{your host}/api/HelloWorld?name=HTTP%20Query"
     */
    @FunctionName("HelloWorld")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }

    public static int count = 1;
    /**
     * This function listens at endpoint "/api/HelloWorldRetry". The function is re-executed in case of errors until the maximum number of retries occur.
     * Retry policies: https://docs.microsoft.com/en-us/azure/azure-functions/functions-bindings-error-pages?tabs=java
     */
    @FunctionName("HelloWorldRetry")
    @FixedDelayRetry(maxRetryCount = 3, delayInterval = "00:00:05")
    public HttpResponseMessage HttpExampleRetry(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.GET, HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) throws Exception {
        context.getLogger().info("Java HTTP trigger processed a request.");

        if(count<3) {
            count ++;
            return request.createResponseBuilder(HttpStatus.OK).body(count).build();
        }

        // Parse query parameter
        final String query = request.getQueryParameters().get("name");
        final String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body(name).build();
        }
    }

    /**
     * This function listens at endpoint "/api/HelloWorldJavaVersion".
     * It can be used to verify the Java home and java version currently in use in your Azure function
     */
    @FunctionName("HelloWorldJavaVersion")
    public static HttpResponseMessage HttpTriggerJavaVersion(
        @HttpTrigger(
            name = "req",
            methods = {HttpMethod.GET, HttpMethod.POST},
            authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context
    ) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        final String javaVersion = getJavaVersion();
        context.getLogger().info("Function - HttpTriggerJavaVersion" + javaVersion);
        String valFromlocalSettings = System.getenv("Sp_SelectCost");//can read from App Settings in Portal
        context.getLogger().info("Function - Sp_SelectCost" + valFromlocalSettings);
        return request.createResponseBuilder(HttpStatus.OK).body("HttpTriggerJavaVersion:"+javaVersion).build();
    }

    public static String getJavaVersion() {
        return String.join(" - ", System.getProperty("java.home"), System.getProperty("java.version"));
    }
}