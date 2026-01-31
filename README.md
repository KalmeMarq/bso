# BSO

I wanted to make my own json binary thing, so I looked at NBT, BSON and CBOR to see what they were up to. It's simple like NBT but I wanted to see what other stuff I could add.

To identify a type there's a byte with its id (the 4 rightmost bits) and additional data (the 4 leftmost bits) (0x{AD}{ID}).

The additional data has information to tell if it's a subtype or/and to write the data in a more compact way.
