#!/usr/bin/python

import hashlib
import sys
import bcrypt
import random

class UserAdmin(object):
    def add_user(self, username, password):
        salt = bcrypt.gensalt()
        hashed = bcrypt.hashpw(password.encode(), salt)
        with open("passwords.txt", 'a') as pwfile:
            pwfile.write("{}:{}".format(username, hashed.decode()))
            pwfile.write("\n")

    def check_user(self, username, password):
        passwords = {}
        with open("passwords.txt", "r") as pwfile:
            lines = pwfile.readlines()
            for line in lines:
                user, pw = line.split(":")
                passwords[user] = pw.strip()

        return passwords[username].encode() == bcrypt.hashpw(password.encode(), passwords[username].encode())

UserAdmin().add_user('alex', 'foobar')
print(UserAdmin().check_user('alex', 'foobar'))

