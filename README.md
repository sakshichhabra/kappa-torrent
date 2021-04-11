# KappaTorrent

### Create TCP connections
1. In the main thread create connections to previous peers and create a thread for each
connection.
2. After finishing making connections, open a server socket to accept connections from
future peers. Create a thread for each receiving connection.
3. All TCP connections have been created. Join all threads before exit.
4. (Optional) Try to start threads as soon as created.

### Handshake
1. Perform handshake in peer.java before creating connection threads because we need Hashmap of peerid and socket before starting thread.


### uf.PeerState(common to all shared with all connection threads)
1. CommonCfg
2. PeersInfo
2. connected? Map | change to handshaked? If handshaked/bitfield we can send have message otherwise it will be coverd in bitfield. 
2. Array of bit-field for each TCP connection.
3. Array of sockets, each thread must be given all sockets so that they can send 'have' to all neighbors.

### State of one connection(given to connection, about specific connection)
1. uf.PeerState 
2. peerInfo-index


