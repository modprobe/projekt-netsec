require 'socket'

server = TCPServer.new('127.0.1.2', 1337)
loop do
    client = server.accept # Wait for Client to connect
    client.gets
    client.puts 'SERIAL_VALID=1'
    client.close
end
