#!/usr/bin/python

import itertools
import sys
import socket
import re

from deco import *

ALPHABET = "0123456789"

@concurrent
def validate_serial(sno):
    s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    s.connect(('127.0.1.2', 1337))
    s.send("SERIAL={}\r\n".format(sno).encode())
    data = s.recv(1024)
    s.shutdown(socket.SHUT_RDWR)
    s.close()

    return bool(re.match('SERIAL_VALID=1.*', data.decode()))

@synchronized
def run():
    counter = 0
    validserials = itertools.product(ALPHABET, repeat=8)
    for serial_tpl in validserials:
        serial = ''.join(serial_tpl)
        counter += 1

        if counter % 10000 == 0:
            sys.stdout.write("{} ".format(counter))
            sys.stdout.flush()

        if validate_serial(serial):
            print("valid serial: {}".format(serial))
            prompt = input("Continue? (y/n) ")
            if prompt == "n":
                sys.exit()

if __name__ == "__main__":
    run()
