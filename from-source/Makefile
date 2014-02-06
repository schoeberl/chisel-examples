#
# Building a Hello World Chisel with the Chisel itself from source
#
#SBT = java -Xmx1024M -Xss8M -XX:MaxPermSize=128M -jar sbt/sbt-launch.jar


CHISEL_SRC = $(HOME)/data/repository/chisel/src/main/scala
PROJ_SRC = ../hello-world/src/hello

all:


# compile all Chisel classes
init:
	scalac $(CHISEL_SRC)/*.scala

# compile and run the hardware generator
test:
	scalac $(PROJ_SRC)/Hello.scala
	scala hello.HelloMain


args:
	scala hello.HelloMain —-targetDir generated —-backend v

# sourcepath look like broken in scalac
not-working:
	scalac -sourcepath $(CHISEL_SRC) -sourcepath $(PROJ_SRC) Hello.scala












###### Minimum Makefile ends here ########

# Generare Verilog code
hdl:
	$(SBT) "run-main hello.HelloMain --targetDir generated --backend v"

# C++ genertion
cpp:
	$(SBT) "run-main hello.HelloMain --backend c --compile --targetDir generated"

intsbt:
	$(SBT)
