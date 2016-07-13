from Crypto.Cipher import AES

lookup_table = dict()
plaintext = "Verschluesselung"
ciphertext = "\xbe\x39\x3d\x39\xca\x4e\x18\xf4\x1f\xa9\xd8\x8a\x9d\x47\xa5\x74"
f = None
key1 = ""
key2 = ""

for byte1_position in range(1,16):
    for byte2_position in range(byte1_position):
        for byte1 in range(256):
            for byte2 in range(256):
                key = [chr(0)] * 16
                key.pop(-byte1_position-1)
                key.insert(-byte1_position,chr(byte1))
                key.pop(-byte2_position-1)
                key.insert(-byte2_position,chr(byte2))
                key = ''.join(key)
                aes = AES.new(key)
                lookup_table[aes.encrypt(plaintext)] = key

f = open("lookup_table", "w")
for key, value in lookup_table.iteritems():
    f.write(key + ":" + value + "\n")
f.close()

for byte1_position in range(1,16):
    for byte2_position in range(byte1_position):
        for byte1 in range(256):
            for byte2 in range(256):
                key = [chr(0)] * 16
                key.pop(-byte1_position-1)
                key.insert(-byte1_position,chr(byte1))
                key.pop(-byte2_position-1)
                key.insert(-byte2_position,chr(byte2))
                key = ''.join(key)
                aes = AES.new(key)

                if aes.decrypt(ciphertext) in lookup_table:
                    key1 = lookup_table[aes.decrypt(ciphertext)]
                    key2 = key
                    break

print("Key1: " + ":".join("{:02x}".format(ord(c)) for c in key1) + " Key2: "
        + ":".join("{:02x}".format(ord(c)) for c in key2))
