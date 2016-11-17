package http

import util.ThreadPool
import java.io.StringWriter
import java.nio.ByteBuffer.allocate
import java.nio.ByteBuffer.wrap
import java.nio.channels.SelectionKey
import java.nio.channels.SelectionKey.OP_READ
import java.nio.channels.Selector
import java.nio.channels.SocketChannel

/**
 * @author Ilya Sadykov
 */
internal class PooledHandler
constructor(private val pool: ThreadPool,
            private val handler: HttpHandler,
            selector: Selector,
            private val socketChannel: SocketChannel) : Runnable {
    private val selectionKey: SelectionKey

    init {
        this.socketChannel.configureBlocking(false)
        selectionKey = this.socketChannel.register(selector, OP_READ, this)
        selectionKey.interestOps(OP_READ)
        selector.wakeup()
    }

    override fun run() {
        val requestBuffer = allocate(REQ_BUFFER_SIZE)
        val readCount = socketChannel.read(requestBuffer)
        if (readCount > 0) {
            pool.execute {
                selectionKey.interestOps(SelectionKey.OP_WRITE)
                val sw = StringWriter()
                handler.handle(requestBuffer.array(), sw)
                socketChannel.write(wrap((sw.toString() as java.lang.String).bytes))
            }
        }
    }

    companion object {
        private val REQ_BUFFER_SIZE = 100
    }
}