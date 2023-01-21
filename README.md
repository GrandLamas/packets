# Packets - A packet library

## Goals

- Provide a simple and efficient interface to open a server and connect to servers
- Send custom packets over the given connections effortlessly and without any boilerplate code
- Give the user the opportunity to make the entire client-server interaction asynchronous
- Be fast

## Quickstart - General
Every operation in this API uses the <code>Operation</code> interface.
For example <code>server.send(packet)</code> returns such an operation.
Using this operation you may execute <code>Operation#complete</code> (synchronized) or <code>Operation#queue</code> (async).
Operations are immutable and the methods mentioned above will return itself for example executing it multiple times.
Almost all asynchronous operations use a centralized ThreadPool, they may also use a local thread pool depending on the operation.

If you want to use custom packets, you may register your packets in the <code>PacketRegistry</code>.
You may want to watch out of registering a packet with an existing packet id. You can find a list of default packets <a href='#Packet-IDs'>here</a>.<br>
The first interaction between the server and the client will always be handshake for checking the versions etc.
Furthermore, the client and the server are both implementing [NetworkAdapter](https://github.com/GrandLamas/packets/blob/master/src/main/java/de/lama/packets/NetworkAdapter.java).

There is also an event system. You can find more details <a href='#Events'>here</a>.

## Quickstart - Server
First of all you need a <code>ServerBuilder</code> to create a new <code>Server</code> Instance.
With this builder, you are able to configure (tickrate, encryption, ...) your server.<br>

Using <code>ServerBuilder#build</code> you build your configurated server. Congratulations!<br>
```
Server server = new ServerBuilder().build(PORT);
```

You may now open the server to new clients using <code>Server#open</code>.<br>
Every connecting client will receive a Handshake for checking the API version, not answering this handshake will result in closing the socket of this new client.

You are also able to prevent the server from accepting new clients using <code>PacketServer#close</code> or shut it down entirely by using <code>Server#shutdown</code>

## Quickstart - Client
Opening a new (localhost-addressed) client is fairly simple.<br>
```
Client client = new ClientBuilder().build(ADDRESS, PORT);
```

Now, if you also want to receive packets, you may also open your client. Otherwise your client will only be able to send packets.<br>
You may use <code>client#open</code> for that. You are also able to close the client or shut it down entirely.

## Events
Almost every central API object is an <code>EventHandlerContainer</code>, which means you can use its EventHandler to subscribe to the events of the API object.

For example the <code>Server</code> will notify you about every incoming connection if you've subscribe using the <code>ClientConnectEvent</code>.<br>
```
server.getEventHandler().subscribe(ClientConnectEvent.class, (connectEvent) -> System.out.println("Connected client " + connectEvent.client().getAddress().toString()));
```
Some events implement <code>Cancellable</code>. Some of these events can be cancelled using <code>Cancellable#behaviour#setCancelled</code>.

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

* 69420: Handshake-Packet

## Technical Stuff
The transmitted packets have fixed structure:
1. **A char** which contains the type of the following data, if its a Packet is 'p' (1 byte)
2. **A long** which contains the ID of the following packet (8 bytes)
3. **An int** which contains the size of the incomming packet (4 bytes)
4. **The packet** in the format given by the wrapper. Default is UTF-8 encoded JSON (x bytes)

Packets transmitted using the <a href='https://github.com/GrandLamas/packets/blob/master/src/main/java/de/lama/packets/wrapper/CachedGsonWrapper.java'>
CachedGsonWrapper</a> (default) will use a cache, which will significantly improve performance on heavy packet load, if multiple packets with the same hash will be sent.

## TODO
- Encryption
- More events