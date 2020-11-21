const index = require('../index.js');
const constants = require('../constants.js');

module.exports = {
    execute: function(message) {
        message.author.send("prefix: !\nnaredba: email [test@tvz.hr] -> za dohvacanje potvrdnog koda na mejl\nnaredba: code [ABCDEFGHIJ] -> za unos koda i zavrsetak verifikacije");
    } 
};
