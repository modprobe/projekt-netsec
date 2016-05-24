#!/usr/bin/python

import itertools
import sys
import socket
import time

TCP_IP = '10.1.1.1'
TCP_PORT =  1337
ALPHABET = "0123456789"
BUFFER_SIZE = 1024

counter = 0
i= 8 # i = 0 if serial length is unknown
s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
s.connect((TCP_IP,TCP_PORT))

while true:
    testwords = itertools.product(ALPHABET, repeat=i)
    #sys.stdout.write("\n{} characters: ".format(i)) if length is unknown
    for word in testwords:
        w = ''.join(word)
        message = "serial="+w
        s.send(message.encode('utf-8'))
        response = s.recv(BUFFER_SIZE)
        counter += 1
        #i += 1 if length of serial is unknown

        if counter % 100 == 0:
            sys.stdout.write('.')
            sys.stdout.flush()

        if not reponse:
            time.sleep(10)
            s.close
            s = socket.socket(socket.AF_INET,socket.SOCK_STREAM)
            s.connect((TCP_IP,TCP_PORT))

        elif response == "SERIAL_VALUE=1" :
            print("\n\nFOUND: {}".format(word))
            prompt = input("Continue? \n y(yes)/n(no) (Default:yes)")
            if prompt == "n":
                sys.exit
