# API
JSON

## Blockchain
### GET:
* blockchain/chain - returns the whole chain
{"timestamp":1537724947527,"blockchain":[{"previousHash":"0000000000000000000000000000000000000000000000000000000000000000","timestamp":1537724931744,"nonce":117,"data":"Genesis","index":0}]}
* blockchain/valid - checks if blockchain is valid
{"timestamp":1537727402999,"valid":true}

### PUT:
* blockchain/add_and_mine; data:String - creates new block with data = "data", mines it and returns timestamp and its json representation
{"timestamp":1537728832880,"block":{"previousHash":"00564f199d2000d6c7d99a84282fa79de02b21720268bc8e1d37b9e2e8374113","timestamp":1537728832881,"nonce":210,"data":"{\"key1\":\"value1\", \"key2\":\"value2\"}","index":1}}