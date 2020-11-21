const database = require('../database.js');
const stu = require('../student.js');
const functions = require('../functions.js');
const constants = require('../constants.js');

module.exports = {
    execute: async function(message, value, guild) {
        var user = message.author;
        var userId = user.id;
        let email = value;

        // 1. korak: provjera je li korisnik verificiran
        functions.getMemberById(guild, userId).then(function (foundMember) {
            if (!foundMember) {
                user.send(constants.ALREADY_VERIFIED);
                return;
            }
            var roleRedovniIzvanredni = foundMember.roles.cache.find(role => role.name === constants.REDOVNI_ROLE || role.name === constants.IZVANREDNI_ROLE);
            if (roleRedovniIzvanredni){
                user.send(constants.ALREADY_VERIFIED);
                return;
            }

            // 2. korak: provjera validnosti unešenog maila
            res = database.select({ email: email }, 1, database.TABLE_EMAILS);
            if (res.length === 0) {
                user.send(constants.MAIL_INVALID);
                return;
            } 

            // 3. korak: provjera verificiranosti unešenog maila
            var wantedEmailId = res[0][stu.EMAILS_ID];
            var nickname = `${res[0][stu.EMAILS_NAME]} ${res[0][stu.EMAILS_SURNAME]}`;
            functions.getMemberByNickname(guild, nickname).then(function (foundMember) {
                if (foundMember && foundMember.roles.cache.find(role => role.name === constants.REDOVNI_ROLE || role.name === constants.IZVANREDNI_ROLE)) {
                    user.send(constants.ALREADY_VERIFIED);
                    return;
                }
    
                // 4. korak: provjera prečestog slanja konfirmacijskog koda
                res = database.select({ idDiscord: userId }, 1, database.TABLE_CONFIRMATIONS);
                let seconds = Math.round(new Date().getTime() / 1000);
                if (res.length !== 0) {
                    let resSeconds = res[0][stu.CONFIRMATIONS_SECONDS];
                    if (seconds - resSeconds < constants.SECONDS_TIMEOUT_MAIL_SENDING) {
                        user.send(constants.MAIL_SENDING_TOO_FAST);
                        return;
                    }
                }
    
                // 5. korak: uspješna zatražnja konfirmacijskog koda, nastavi na slanje
                let confirmationCode = functions.getConfirmationCode(10);
                database.delete({ idDiscord: userId }, database.TABLE_CONFIRMATIONS);
                database.insert([wantedEmailId, userId, confirmationCode, seconds], database.TABLE_CONFIRMATIONS);
                functions.sendConfirmationCode(email, confirmationCode);
                user.send(constants.INFO_MESSAGE_AFTER_MAIL_SEND);
            });
        });
    }
};