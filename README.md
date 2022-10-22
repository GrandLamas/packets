# Packets - A packet library

## Goals

- Provide a simple and efficient interface to open a server and connect to servers
- Send custom packets over the given connections effortlessly and without any boilerplate code
- Give the user the opportunity to make the entire client-server interaction asynchronous
- Be fast

## Quickstart - General
Every operation in this API uses the <code>Operation</code> interface.
For example <code>server.send(packet)</code> returns such an operation.
Using this operation you may execute <code>Operatoin#complete</code> (synchronized) or <code>Operation#queue</code> (async).
Operations are immutable and the methods mentioned above will return itself for example executing it multiple times. 
Almost all asynchronous operations use a centralized ThreadPool, they may also use a local thread pool depending on the operation.

If you want to use custom packets, you may register your packets in the <code>PacketRegistry</code>.
You may want to watch out of registering a packet with an existing packet id. You can find a list of default packets <a href='https://github.com/GrandLamas/packets/blob/master/src/main/java/de/lama/packets/DefaultPackets.java'>here</a>.<br>
The first interaction between the server and the client will always be handshake for checking the versions etc.

There is also an event system. You can find more details <a href='#Events'>here</a>.

## Quickstart - Server
First of all you need a <code>ServerBuilder</code> to create a new <code>PacketServer</code> Instance.
With this builder, you are able to configure (tickrate, encryption, ...) your server.<br>

Using <code>ServerBuilder#build</code> you build your configurated server. Congratulations!<br>
<code>Server server = new ServerBuilder().build();</code>

You may now open the server to new clients using <code>Server#open</code>.<br>
Every connecting client will receive a Handshake for checking the API version, not answering this handshake will result in closing the socket of this new client.

You are also able to prevent the server from accepting new clients using <code>PacketServer#close</code> or shut it down entirely by using <code>Server#shutdown</code>

## Quickstart - Client
Opening a new (localhost-addressed) client is fairly simple.<br>
<code>Client client = new ClientBuilder().build();</code><br>
Unlike the server, the client cannot be reopened after getting closed once.

## Events
Almost every central API object is an <code>EventHandlerContainer</code>, which means you can use its EventHandler to subscribe to the events of the API object.

For example the <code>PacketServer</code> will notify you about every incoming connection if you've subscribe using the <code>ServerClientConnectEvent</code>.<br>
Code:<br>
<code>server.getEventHandler().subscribe(ServerClientConnectEvent.class, (connectEvent) -> System.out.println("Connected client " + connectEvent.client().getAddress().toString()));
</code>

Some of the events implement <code>Cancellable</code>. Those events can be cancelled using <code>Cancellable#setCancelled</code>.

### List of events
  **Server**:<br>
    - ServerClientConnectEvent - Triggered when a new client connects<br>
    - ServerClientDisconnectEvent - Triggered when a client disconnects<br>
    **ServerClient**:<br>
      - ServerClientPacketReceiveEvent - Triggered when a packet has been received from the client<br>
      - ServerClientPacketSendEvent - Triggered when a packet is about to be sent to a client<br>
