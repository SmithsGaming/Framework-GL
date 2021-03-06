/**
 * Copyright 2012 Jason Sorensen (sorensenj@smert.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.smert.frameworkgl;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.util.function.Supplier;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class Network {

    private boolean debug;
    private boolean keepAlive;
    private boolean serverRunning;
    private boolean tcpNoDelay;
    private int backlog;
    private int serverPort;
    private Bootstrap client;
    private EventLoopGroup clientWorkerGroup;
    private EventLoopGroup serverAcceptGroup;
    private EventLoopGroup serverWorkerGroup;
    private LogLevel logLevel;
    private ServerBootstrap server;

    public Network() {
        keepAlive = true;
        tcpNoDelay = true;
        backlog = 128;
        serverPort = 0;
        logLevel = LogLevel.INFO;
    }

    private void createClient(String host, int port, Supplier<ChannelHandler> channelHandlerSupplier) {

        // Create event loops
        clientWorkerGroup = new NioEventLoopGroup();

        // Create channel initializer
        ChannelInitializer<SocketChannel> channelInit = new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                if (debug) {
                    p.addLast(new LoggingHandler(logLevel));
                }
                p.addLast(channelHandlerSupplier.get());
            }

        };

        // Bootstrap the client
        client = new Bootstrap();
        if (debug) {
            client.handler(new LoggingHandler(logLevel));
        }
        client.group(clientWorkerGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, keepAlive)
                .option(ChannelOption.TCP_NODELAY, tcpNoDelay)
                .handler(channelInit);

        // Connect to the host and port
        client.connect(host, port);
    }

    private void createServer(int port, Supplier<ChannelHandler> channelHandlerSupplier) {

        // Are we already running?
        if (serverRunning) {
            return;
        }

        serverPort = port;

        // Create event loops
        serverAcceptGroup = new NioEventLoopGroup(1);
        serverWorkerGroup = new NioEventLoopGroup();

        // Create channel initializer
        ChannelInitializer<SocketChannel> channelInit = new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                if (debug) {
                    p.addLast(new LoggingHandler(logLevel));
                }
                p.addLast(channelHandlerSupplier.get());
            }

        };

        // Bootstrap the server
        server = new ServerBootstrap();
        if (debug) {
            server.handler(new LoggingHandler(logLevel));
        }
        server.group(serverAcceptGroup, serverWorkerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, backlog)
                .childHandler(channelInit)
                .childOption(ChannelOption.SO_KEEPALIVE, keepAlive)
                .childOption(ChannelOption.TCP_NODELAY, tcpNoDelay);

        // Start listening on the port
        server.bind(port);

        // The server is now running
        serverRunning = true;
    }

    public void destroy() {
        if (clientWorkerGroup != null) {
            clientWorkerGroup.shutdownGracefully();
            clientWorkerGroup = null;
        }
        client = null;
        if (serverAcceptGroup != null) {
            serverAcceptGroup.shutdownGracefully();
            serverAcceptGroup = null;
        }
        if (serverWorkerGroup != null) {
            serverWorkerGroup.shutdownGracefully();
            serverWorkerGroup = null;
        }
        server = null;
        serverRunning = false;
    }

    public int getBacklog() {
        return backlog;
    }

    public void setBacklog(int backlog) {
        this.backlog = backlog;
    }

    public int getServerPort() {
        return serverPort;
    }

    public boolean isDebug() {
        return debug;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevelDebug() {
        this.logLevel = LogLevel.DEBUG;
    }

    public void setLogLevelError() {
        this.logLevel = LogLevel.ERROR;
    }

    public void setLogLevelInfo() {
        this.logLevel = LogLevel.INFO;
    }

    public void setLogLevelTrace() {
        this.logLevel = LogLevel.TRACE;
    }

    public void setLogLevelWarn() {
        this.logLevel = LogLevel.WARN;
    }

    public ServerBootstrap getServer() {
        return server;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public void startClient(String host, int port, ChannelHandler sharedChannelHandler) {
        Supplier<ChannelHandler> channelHandlerSupplier = () -> {
            return sharedChannelHandler;
        };
        createClient(host, port, channelHandlerSupplier);
    }

    public void startClient(String host, int port, Supplier<ChannelHandler> channelHandlerSupplier) {
        createClient(host, port, channelHandlerSupplier);
    }

    public void startServer(int port, ChannelHandler sharedChannelHandler) {
        Supplier<ChannelHandler> channelHandlerSupplier = () -> {
            return sharedChannelHandler;
        };
        createServer(port, channelHandlerSupplier);
    }

    public void startServer(int port, Supplier<ChannelHandler> channelHandlerSupplier) {
        createServer(port, channelHandlerSupplier);
    }

}
