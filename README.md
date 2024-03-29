# Packets - A packet library

## Goals

- Provide a simple, efficient and high level interface to open a server and connect to servers
- Send custom packets over the given connections effortlessly and without any boilerplate code
- Give the user the opportunity to make the entire client-server interaction asynchronous
- Be modular, so more io library adapter can be written 
- Be fast

## Quick start - General
Every operation in this API uses the <code>Operation</code> interface.
For example send a client a packet returns such an operation.
Using this operation you may execute <code>Operation#complete</code> (synchronized) or <code>Operation#queue</code> (async).
Operations are immutable and the methods mentioned above will return itself for executing itself multiple times.
<br>Some asynchronous operations use a centralized thread pool, they may also use a local thread pool depending on the operation.
You may also configure your own thread pool with the provided factories.


If you want to use custom packets, you may register your packets in the <code>PacketRegistry</code>.
You may want to watch out of registering a packet with an existing packet id. You can find a list of default packets <a href='#Packet-IDs'>here</a>.<br>
The first interaction between the server and the client will always be handshake for checking the versions etc.
Furthermore, the client and the server are both implementing [NetworkAdapter](https://github.com/GrandLamas/packets/blob/master/src/main/java/de/lama/packets/NetworkAdapter.java).

There is also an event system. You can find more details <a href='#Events'>here</a>.
<br>
Currently there is also an implementation for sending large files.
For that, use the [FileClient](https://github.com/GrandLamas/packets/blob/master/src/main/java/de/lama/packets/client/file/FileClient.java).

ATM there is just an java.net.Socket implementation of server and client.<br>
The API design supports more implementations, so I hope for more to come!

## Quick start - Server (java.net.ServerSocket)
First of all you need a <code>SocketServerBuilder</code> to create a new <code>Server</code> Instance.
With this builder, you are able to configure (tickrate, encryption, ...) your server.<br>

Using <code>SocketServerBuilder#build</code> you build your configurated server. Congratulations!<br>
```
Server server = new SocketServerBuilder().build(PORT);
```


Every connecting client will receive a Handshake for checking the API version, not answering this handshake will result in shutting down the client.

You are also able to prevent the server from accepting new clients using <code>Server#close</code> or shut it down entirely by using <code>Server#shutdown</code>

## Quickstart - Client (java.net.Socket)
Opening a new client is fairly simple.<br>
```
Client client = new SocketClientBuilder().build(ADDRESS, PORT);
```

Now, if you also want to receive packets, you may also open your client. Otherwise your client will only be able to send packets.<br>
You may use <code>Client#open</code> for that. You are also able to close the client or shut it down entirely.

## Events
Almost every central API object is an <code>EventHandlerContainer</code>, which means you can use its EventHandler to subscribe to the events of the API object.

For example the <code>Server</code> will notify you about every incoming connection if you've subscribe using the <code>ClientConnectEvent</code>.<br>
```
server.getEventHandler().subscribe(ClientConnectEvent.class, (connectEvent) -> System.out.println("Connected client " + connectEvent.client().getAddress().toString()));
```
Some events implement <code>Cancellable</code>. Some of these events can be cancelled using <code>Cancellable#behaviour#setCancelled</code>.
In addition to that, all events are records.

### List of events
**NetworkAdapter events**:<br>

* AdapterOpenEvent - Triggered when an adapter has been opened
* AdapterCloseEvent - Triggered when an adapter has been closed
* AdapterShutdownEvent - Triggered when an adapter has been shutdown


**Server events**:<br>

* ClientConnectEvent - Triggered when a client connects to a server

**Client events**:<br>

* PacketSendEvent - Triggered when a packet has been sent
* PacketReceiveEvent - Triggered when a packet has been received

## Packet-IDs
Following IDs for packets are already used any **may not be used** by the user again:<br>
The following IDs are completely random.

* 69420: HandshakePacket
* 69421: FileHeaderPacket
* 69422: FileDataPacket
* 69423: FileTransferredPacket

## Technical Stuff
The via de.lama.packets.stream transmitted packets have fixed structure:
1. **A char** which contains the type of the following data, if its a Packet is 'p' (1 byte)
2. **A long** which contains the ID of the following packet (8 bytes)
3. **An int** which contains the size of the incoming packet (4 bytes)
4. **The packet** in the format given by the wrapper. Default is UTF-8 encoded JSON (x bytes)

Packets transmitted using the <a href='https://github.com/GrandLamas/packets/blob/master/src/main/java/de/lama/packets/wrapper/CachedGsonWrapper.java'>
CachedGsonWrapper</a> (default) will use a cache, which will significantly improve performance on heavy packet load, if multiple packets with the same hash will be sent.

## TODO
- Encryption
- More implementations
- More events
- PacketCompression?
- Better builder
