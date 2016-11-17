package http

import util.ThreadPool
import java.lang.Thread.interrupted
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.SelectionKey.OP_ACCEPT
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel

/**
 * @author Ilya Sadykov
 */
class NioHttpServer
constructor(host: String = "0.0.0.0", port: Int = 8080,
            threadsCount: Int = 4, private val httpHandler: HttpHandler) : Runnable {
    private val selector: Selector
    private val threadPool: ThreadPool
    private val serverSocketChannel: ServerSocketChannel

    init {
        threadPool = ThreadPool(threadsCount)
        selector = Selector.open()
        serverSocketChannel = ServerSocketChannel.open()
        serverSocketChannel.socket().bind(InetSocketAddress(host, port))
        serverSocketChannel.configureBlocking(false)
        serverSocketChannel.register(selector, OP_ACCEPT, Acceptor())
    }

    override fun run() {
        while (!interrupted()) {
            try {
                if (selector.select() == 0) continue
                val it = selector.selectedKeys().iterator()
                while (it.hasNext()) {
                    val key = it.next()
                    dispatch(key)
                    it.remove()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun dispatch(key: SelectionKey) {
        when (key.attachment()) {
            is Acceptor -> {
                if (key.isAcceptable) {
                    (key.attachment() as Acceptor).run()
                }
            }
            else -> (key.attachment() as Runnable).run()
        }
    }

    private inner class Acceptor : Runnable {
        override fun run() {
            var socketChannel: SocketChannel? = null
            try {
                socketChannel = serverSocketChannel.accept()
                if (socketChannel != null) {
                    PooledHandler(threadPool, httpHandler, selector, socketChannel)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                socketChannel?.finishConnect()
            }
        }
    }
}