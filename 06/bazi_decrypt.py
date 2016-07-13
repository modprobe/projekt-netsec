#!/usr/bin/env python
# -*- coding: utf-8 -*-
# First Argument: File-Destination
# Second Argument: Tolerance Level

import sys


def translateMessage(key, msg):
    translated = []  # stores the encrypted/decrypted message string

    keyIndex = 0
    for c in msg:  # loop through each character in message
        num = ord(c)
        num = num ^ ord(key[keyIndex])  # add if encrypting
        translated.append(chr(num))
        keyIndex += 1  # move to the next letter in the key
        if keyIndex == len(key):
            keyIndex = 0
    return ''.join(translated)

key = []
message = []
tolerance = int(sys.argv[2])

f = open(sys.argv[1], "rb")
byte = f.read(1)
while byte != "":
    message.append(byte)
    byte = f.read(1)
print(message)
print(len(message))

i = 1
while not key[-tolerance:] == message[len(message)-len(key)-tolerance:len(message)-len(key)]:
    key.insert(0,message[-i])
    i+=1
    print(key)

f.close()
decrypted = translateMessage(key, message)
print(decrypted)
