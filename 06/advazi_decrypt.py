#!/usr/bin/env python
# -*- coding: utf-8 -*-
# First Argument: File-Destination
# Second Argument: Tolerance-Level

import sys

def translateMessage(key, msg):
    translated = []

    keyIndex = 0
    for c in msg:
        num = ord(c)
        num = num ^ ord(key[keyIndex])
        translated.append(chr(num))
        keyIndex += 1
        if keyIndex == len(key):
            keyIndex = 0
    return ''.join(translated)

message = []
tolerance = int(sys.argv[2]) #The Amount of Chars that have to loop in the message to accept as key

f = open(sys.argv[1], "rb")
byte = f.read(1)
while byte != "":
    message.append(byte)
    byte = f.read(1)
print(message)
print(len(message))

for padding_length in range(100):
    i = 1
    key = []
    while not key[-tolerance:] == message[len(message)-len(key)-tolerance:len(message)-len(key)]:
        key.insert(0,message[-i])
        i+=1
    key = map(lambda x: chr(padding_length ^ ord(x)), key)
    f.close()
    decrypted = translateMessage(key, message)
    words = decrypted.split(" ")
    print(decrypted)
