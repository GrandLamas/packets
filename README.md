# Packets - A packet library

## Quickstart - General
Every operation in this API uses the <code>Operation</code> interface.
For example <code>server.send(packet)</code> returns such an operation.
Using this operation you may execute <code>Operatoin#complete</code> (synchronized) or <code>Operation#queue</code> (async).
Almost all asynchronous operations use a centralized ThreadPool, they may also use a local thread pool depenending on the operation.

There is also an event system. You can find more details <a href='#Events'>here</a>.

## Quickstart - Server
First of all you need a <code>ServerBuilder</code> to create a new <code>PacketServer</code> Instance.
With this builder, you are able to configurate (tickrate, encryption, ...) your server. A <code>ExceptionHandler</code> is the only thing you need to provide.<br>

Using <code>ServerBuilder#build</code> you build your configurated server. Congratulations!<br>
<code>PacketServer server = new ServerBuilder().exceptionHandler(Throwable::printStackTrace).build();</code>

You may now open the server to new clients using <code>PacketServer#open</code>.<br>
Every connecting client will receive a Handshake for checking the API version, not answering this handshake will result in closing the socket of this new client.

You are also able to prevent the server from accepting new clients using <code>PacketServer#clode</code> or shut it down entirely by using <code>PacketServer#shutdown</code>

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
