package http

import java.io.Writer

/**
 * @author Ilya Sadykov
 */
interface HttpHandler {
    fun handle(req: ByteArray, response: Writer)

    companion object {
        fun httpHandler(handler: (ByteArray, Writer) -> Unit): HttpHandler {
            return object : HttpHandler {
                override fun handle(req: ByteArray, response: Writer) {
                    handler(req, response)
                }
            }
        }
    }
}

