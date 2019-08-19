# Simple implementation of Blockchain in scala
This repo is of course still work in progress and definetly is not complete/ready.
Project created to learn akka, akka-http, blockchain etc.

Master branch aims to be a general-purpose blockchain template.
Koin branch aims to be a simple cryptocurrency called "Koin" implementation.

# Testing
Tests work as usual, but to run multi-jvm tests of akka-cluster, you need to specifically run 
```sbt multi-jvm:test```

# Running
To start first node one should provide 3 args: host http api port and cluster tcp port. Example:
```java -jar scala-blockchain-0.1-jar-with-dependencies.jar 127.0.0.1 9001 2552```
To start more nodes, one should provide above and "host:cluster-port" to one of the already running nodes. Example:
```java -jar scala-blockchain-0.1-jar-with-dependencies.jar 127.0.0.1 9002 2553 127.0.0.1:2552

# API (json based)
## Blockchain
### GET:
* blockchain/chain - returns the whole chain
```{"timestamp":1537724947527,"blockchain":[{"previousHash":"0000000000000000000000000000000000000000000000000000000000000000","timestamp":1537724931744,"nonce":117,"data":"Genesis","index":0}]}```
* blockchain/valid - checks if blockchain is valid
```{"timestamp":1537727402999,"valid":true}```
* blockchain/latest - returns the latest block in the chain
```{"timestamp":1538329803076,"block":{"previousHash":"0000000000000000000000000000000000000000000000000000000000000000","timestamp":1538329701922,"nonce":61,"data":"Genesis","index":0}}```

### POST:
* blockchain/mine; data:String - creates new block with data = "data", mines it and returns timestamp and its json representation
```{"timestamp":1537728832880,"block":{"previousHash":"00564f199d2000d6c7d99a84282fa79de02b21720268bc8e1d37b9e2e8374113","timestamp":1537728832881,"nonce":210,"data":"{\"key1\":\"value1\", \"key2\":\"value2\"}","index":1}}```
* blockchain/refresh - updates chain on the present node with the longest chain in the cluster, return length of that chain
```2```

# Credits
##### * Some code/ideas borrowed from this great repo: https://github.com/fluency03/blockchain-in-scala
##### * Other ideas based on the course that I'm completing right now: https://www.udemy.com/build-your-blockchain-az/?start=0com/build-your-blockchain-az/?start=0
