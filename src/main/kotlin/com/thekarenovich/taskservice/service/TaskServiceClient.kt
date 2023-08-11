package com.thekarenovich.taskservice.service

import com.thekarenovich.task.service.*
import io.grpc.ManagedChannelBuilder
import org.springframework.stereotype.Service

@Service
class TaskServiceClient {

    private val channel = ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext()
        .build()

    private val stub = TaskServiceGrpcKt.TaskServiceCoroutineStub(channel)

    suspend fun addTask(title: String, description: String): Task {
        val request = AddTaskRequest.newBuilder()
            .setTitle(title)
            .setDescription(description)
            .build()
        return stub.addTask(request)
    }

    suspend fun getTask(taskId: Int): Task {
        val request = GetTaskRequest.newBuilder()
            .setTaskId(taskId)
            .build()
        return stub.getTask(request)
    }

    suspend fun markTaskAsCompleted(taskId: Int): Task {
        val request = MarkTaskRequest.newBuilder()
            .setTaskId(taskId)
            .build()
        return stub.markTaskAsCompleted(request)
    }
}
