
import http.HttpHandler.Companion.httpHandler
import http.NioHttpServer

/**
 * @author Ilya Sadykov
 */
val BODY = "Hello, World!"
val RESPONSE = "HTTP/1.1 200 OK\r\n" +
        "Content-Type: text/plain\r\n" +
        "Content-Length: 12\r\n\r\n$BODY"

fun main(args: Array<String>) {
    NioHttpServer(
            port = 5555,
            threadsCount = 2,
            httpHandler = httpHandler { req, writer -> writer.write(RESPONSE) }
    ).run()
}