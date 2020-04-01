#!/usr/bin/env python
import sys, os

PY2 = sys.version_info[0] == 2
PY3 = sys.version_info[0] == 3

PORT = 8000

root = './view'
web_dir = os.path.join(os.path.dirname(__file__), root)
os.chdir(web_dir)

try:
    if PY2:
        print ('Running in Python 2...')
        import SimpleHTTPServer, SocketServer
        handler = SimpleHTTPServer.SimpleHTTPRequestHandler
        server = SocketServer.TCPServer(('', PORT), handler)
    else:
        print ('Running in Python 3...')
        import http.server, socketserver
        handler = http.server.SimpleHTTPRequestHandler
        #server = http.server.HTTPServer(('', PORT), handler)
        server = socketserver.TCPServer(('', PORT), handler)

    print ('Started HTTP server on port ',PORT)
    server.serve_forever()

except KeyboardInterrupt:
    print ('^C received, shutting down the web server')
    server.socket.close()
