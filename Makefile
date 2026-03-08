# Java compiler
JAVAC=javac
JAVA=java

# Source directory
SRC_DIR=tutorial4

# JAR dependencies
JARS=lib/amqp-client-5.20.0.jar:slf4j-api-1.7.36.jar:slf4j-simple-1.7.36.jar

# Classpath
CLASSPATH=$(JARS):.

# Source files
SOURCES=$(SRC_DIR)/*.java

# Output classes
CLASSES=$(SOURCES:.java=.class)

# Default target: compile
all: $(CLASSES)

# Compile
$(SRC_DIR)/%.class: $(SRC_DIR)/%.java
	$(JAVAC) -cp $(CLASSPATH) $<

# Run a node
# Usage: make run NODE=<myID> NEXT=<nextID>
run: $(CLASSES)
	$(JAVA) -cp $(CLASSPATH) $(SRC_DIR).RingNode 

# Clean compiled classes
clean:
	rm -f $(SRC_DIR)/*.class

.PHONY: all run clean
