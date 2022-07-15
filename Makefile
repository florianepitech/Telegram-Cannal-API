##
## TELEGRAM CHANNEL, 2022
## Telegram Channel Makefile
## File description:
## Generic Makefile for Telegram Channel
##

#=================================
#	Commands
#=================================

.PHONY:				all \
					install \
					test \
					finstall \
					clean

all:				install

install:
					mvn install

finstall:
					mvn install -DskipTests

test:
					mvn test

clean:
					mvn clean
