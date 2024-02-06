package org.example
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket


fun main(args: Array<String>) {
    val server = ServerSocket(9999)
    println("Server running on PORT 9999")
    while(true){
        val socket = server.accept()
        Thread{ server(socket) }.start()
    }
}

fun server(socket: Socket) {
    val input = BufferedReader(InputStreamReader(socket.inputStream))
    val (_, filename) = input.readLine().split(" ")

    val (_, size) = input.readLine().split(" ")
    val fileSize = size.toFloat()

    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
    var i = 0
    val file = File("db/${Thread.currentThread().name}_$filename")
    var line = input.readLine()

    // To handle Progress Bar
    var perct:Int
    var lastP = -1

    while (!line.isNullOrEmpty())
    {
        buffer[i] = line.toByte()
        i++

        perct = ((i/fileSize)*100).toInt()
        // Progress Bar
        if(perct%10 == 0 && perct > lastP) {
            lastP = perct
            println("$filename: [${"*".repeat(perct/10)}${"-".repeat(10-perct/10)}]${perct}% recieved")
        }

        if(i == DEFAULT_BUFFER_SIZE) {
            file.appendBytes(buffer)
            i = 0
        }
        line = input.readLine()
    }
    file.appendBytes(buffer.sliceArray(0..<i))
    println("$filename recieved successfully!")
    socket.close()
}