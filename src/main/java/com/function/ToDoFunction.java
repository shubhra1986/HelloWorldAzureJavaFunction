package com.function;

import com.function.model.ToDoItem;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.sql.annotation.SQLInput;

import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class ToDoFunction {
    @FunctionName("GetToDoItems")
    public HttpResponseMessage getAll(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            @SQLInput(
                name = "toDoItems",
                commandText = "SELECT * FROM dbo.ToDo",
                commandType = "Text",
                connectionStringSetting = "SqlConnectionString")
                ToDoItem[] toDoItems) {
        return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(toDoItems).build();
    }

    @FunctionName("GetToDoItem")
    public HttpResponseMessage getRowById(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            @SQLInput(
                name = "toDoItems",
                commandText = "SELECT * FROM dbo.ToDo",
                commandType = "Text",
                parameters = "@Id={Query.id}",
                connectionStringSetting = "SqlConnectionString")
                ToDoItem[] toDoItems) {
        ToDoItem toDoItem = toDoItems[0];
        return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(toDoItem).build();
    }

    @FunctionName("DeleteToDo")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            @SQLInput(
                name = "toDoItems",
                commandText = "dbo.DeleteToDo",
                commandType = "StoredProcedure",
                parameters = "@Id={Query.id}",
                connectionStringSetting = "SqlConnectionString")
                ToDoItem[] toDoItems) {
        return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "application/json").body(toDoItems).build();
    }
}
