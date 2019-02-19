# Simple Notary Example

Installation:

```
yarn install
./node_modules/.bin/webpack-dev-server
```

* Open a browser at [localhost:8080](localhost:8080)
* Make sure you have [MetaMask](https://metamask.io/) installed, TestNet
* Use faucet if you don't have enough tokens: [Rinkeby](https://faucet.rinkeby.io/) [Kovan](https://gitter.im/kovan-testnet/faucet) 
* Open [remix IDE](https://remix.ethereum.org), click new (+) 
* Add Notary.sol, copy from here
* Run / Deploy or At Address (Rinkeby: 0x2ad87157d67f7cbebfd2e2bb3d9d547f3bd8d48a)  [Status](https://rinkeby.etherscan.io/tx/0x8763d670f2845c3f6e8ba7e8b67dcb77fba60bbacf2d0aa3b855b4729f408038)
* Add this address to ```const address = ``` in App.vue
* Now its ready! Upload a document, and notorize it.