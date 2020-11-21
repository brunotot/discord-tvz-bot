module.exports = {
    
    // table Emails.txt
    EMAILS_ID: 0,
    EMAILS_JMBAG: 1,
    EMAILS_EMAIL: 2,
    EMAILS_NAME: 3,
    EMAILS_SURNAME: 4,
    EMAILS_EDUCATION: 5,
    EMAILS_ORIENTATION: 6,

    // table Confirmations.txt
    CONFIRMATIONS_ID_EMAIL: 0,
    CONFIRMATIONS_ID_DISCORD: 1,
    CONFIRMATIONS_CONFIRMATION_CODE: 2,
    CONFIRMATIONS_SECONDS: 3,

    // gets index by string
    get: function(variable, table) {
        const database = require('./database.js');
        if (table === database.TABLE_EMAILS) {
            if (variable === "id") return 0;
            if (variable === "jmbag") return 1;
            if (variable === "email") return 2;
            if (variable === "name") return 3;
            if (variable === "surname") return 4;
            if (variable === "education") return 5;
            if (variable === "orientation") return 6;
        } else if (table === database.TABLE_CONFIRMATIONS) {
            if (variable === "idEmail") return 0;
            if (variable === "idDiscord") return 1;
            if (variable === "confirmationCode") return 2;
            if (variable === "seconds") return 3;
        }
    }
};