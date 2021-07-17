# KEA Entity Linking Tool

This is an entity linking tool based on Wikipedia. 

For a given text, the tool identifies meaningful text framents, and emits a corresponding Wikipedia article URL. The tool aims to solve occuring ambiguities by incorporating the textual context. 

E.G. For the text:

**"Armstrong landed on the moon"**

The result might be:

```
Armstrong: http://wikipedia.org/wiki/Neil_Armstrong
Moon: http://wikipedia.org/wiki/Moon
```

The tool is currently optimized for **German** language.

# Preparations

Before installation, the Wikipedia data needs to be retrieved and preprocessed. 
Therefore the [kea-wiki-extraction](https://github.com/yovisto/kea-wiki-extraction) should be used. This is a shell script which extracts the relevant information from the recent Wikipedia dump.

Put the created ```labels.txt``` and ```links.txt``` files to the ```data``` directory of this project.


# Installation

Compile the project:

```
mvn compile
```

Run the index creation:

```
mvn exec:java -Dexec.mainClass="com.yovisto.kea.util.IndexAccessImpl"
```

This will create the Lucene index files in the ```data```directory.

After that you might install it:

```
mvn install
```

# Usage

An example is given in the file:

```
src/main/java/com/yovisto/kea/Start.java
```


