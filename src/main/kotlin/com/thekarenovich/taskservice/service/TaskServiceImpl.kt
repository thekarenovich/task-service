package com.thekarenovich.taskservice.service

import com.thekarenovich.task.console.*
import io.grpc.stub.StreamObserver
import io.grpc.Status
import org.lognet.springboot.grpc.GRpcService
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@GRpcService
class TaskServiceImpl : TaskServiceGrpc.TaskServiceImplBase() {

    private val tasks = ConcurrentHashMap<Int, Task>()
    private val taskIdGenerator = AtomicInteger(1)

    override fun addTask(request: AddTaskRequest, responseObserver: StreamObserver<Task>) {
        val taskId = taskIdGenerator.getAndIncrement()
        val task = Task.newBuilder()
            .setId(taskId)
            .setTitle(request.title)
            .setDescription(request.description)
            .setCompleted(false)
            .build()
        tasks[taskId] = task
        responseObserver.onNext(task)
        responseObserver.onCompleted()
    }

    override fun getTask(request: GetTaskRequest, responseObserver: StreamObserver<Task>) {
        val task = tasks[request.taskId]
        if (task != null) {
            responseObserver.onNext(task)
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Task not found").asException())
        }
        responseObserver.onCompleted()
    }

    override fun markTaskAsCompleted(request: MarkTaskRequest, responseObserver: StreamObserver<Task>) {
        val task = tasks[request.taskId]
        if (task != null) {
            tasks[request.taskId] = task.toBuilder().setCompleted(true).build()
            responseObserver.onNext(task)
        } else {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Task not found").asException())
        }
        responseObserver.onCompleted()
    }
}
