#!/usr/bin/python

import hashlib
import itertools
import sys

HASH = "2b2935865b8a6749b0fd31697b467bd7"
SALT = "8kofferradio"
ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"

counter = 0
for i in range(1, 7):
    testwords = itertools.product(ALPHABET, repeat=i)
    sys.stdout.write("\n{} characters: ".format(i))
    for word in testwords:
        w = ''.join(word)
        h = hashlib.new('md5')
        h.update(SALT.encode())
        h.update(w.encode())
        counter += 1

        if counter % 10000 == 0:
            sys.stdout.write('.')
            sys.stdout.flush()

        if h.hexdigest() == HASH:
            print("\n\nFOUND: {}".format(w))
            sys.exit()
