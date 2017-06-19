CPPFLAGS=-ansi -Wall -g -O0 -c -pedantic
LDFLAGS=-ansi -Wall -g -O0 -pedantic

all: GreatHouse.cpp NobleHouse.cpp Kingdom.cpp Wizard.cpp Whitewalker.cpp Wildling.cpp Enums.cpp Character.cpp War.cpp main.cpp; g++ $(LDFLAGS) main.cpp GreatHouse.cpp NobleHouse.cpp Kingdom.cpp Wizard.cpp Whitewalker.cpp Wildling.cpp Enums.cpp Character.cpp War.cpp -o WarSimulator
clean: ; rm -rf WarSimulator;
run: ; ./WarSimulator "3Kingdoms.txt"