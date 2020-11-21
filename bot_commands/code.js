const database = require('../database.js');
const stu = require('../student.js');
const functions = require('../functions.js');
const constants = require('../constants.js');

module.exports = {
    execute: async function(message, value, guild) {
        var user = message.author;
        var userId = user.id;
        let code = value;

        // 1. korak: provjera je li korisnik verificiran
        functions.getMemberById(guild, userId).then(function (currentMember) {
            var roleRedovniIzvanredni = currentMember.roles.cache.find(role => role.name === constants.REDOVNI_ROLE || role.name === constants.IZVANREDNI_ROLE);
            if (roleRedovniIzvanredni){
                user.send(constants.ALREADY_VERIFIED);
                return;
            }

            // 2. korak: provjera postoji li poslani konfirmacijski mejl prema korisniku
            res = database.select({ idDiscord: userId }, 1, database.TABLE_CONFIRMATIONS);
            if (res.length === 0) {
                user.send(constants.NO_MAIL_INPUT);
                return;
            } 

            // 3. korak: provjera ispravnog konfirmacijskog koda
            if (code !== res[0][stu.CONFIRMATIONS_CONFIRMATION_CODE]) {
                user.send(constants.WRONG_CONFIRM_CODE);
                return;
            } 

            // 4. korak: obrada podataka
            database.delete({ idDiscord: userId }, database.TABLE_CONFIRMATIONS);
            let idEmail = res[0][stu.CONFIRMATIONS_ID_EMAIL];
            res = database.select({ id: idEmail }, 1, database.TABLE_EMAILS);
            if (res.length === 0) {
                user.send(constants.VERIFICATION_UNSUCCESSFUL);
                return;
            }

            // 5. korak: verifikacija uspješna, dodaje se nickname i role
            var name = res[0][stu.EMAILS_NAME];
            var surname = res[0][stu.EMAILS_SURNAME];
            var string = name + " " + surname;
            try {
                let roleRedovniIzvanredni = guild.roles.cache.find(role => role.name === res[0][stu.EMAILS_EDUCATION].replace(/[0-9]/g, ''));
                currentMember.setNickname(string);
                currentMember.roles.add(roleRedovniIzvanredni).then(res => {});
            } catch (e) {
                console.log("Korisnik " + string + " nije dobio nickname ili rolu. ID: " + userId);
                user.send(constants.VERIFICATION_UNSUCCESSFUL);
                return;
            }
            user.send(constants.VERIFICATION_SUCCESSFUL);
        });
    }
};

