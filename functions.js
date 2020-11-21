const constants = require('./constants.js');
const adminMail = process.env.ADMIN_MAIL;
const adminPass = process.env.ADMIN_PASS;

const nodemailer = require('nodemailer');
const transporter = nodemailer.createTransport({
    service: constants.MAIL_PROVIDER,
    auth: {
        user: adminMail,
        pass: adminPass
    }
});

module.exports = {
    getConfirmationCode: function(length) {
        var result = '';
        var characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
        var charactersLength = characters.length;
        for (var i = 0; i < length; i++) {
            result += characters.charAt(Math.floor(Math.random() * charactersLength));
        }
        return result;
    },

    sendConfirmationCode: function(userMail, confirmationCode) {
        var mailOptions = {
            from: adminMail,
            to: userMail,
            subject: `TVZ discord potvrdni kod`,
            text: `Vas potvrdni kod je: ${confirmationCode}`
        };
        transporter.sendMail(mailOptions, function(err, info) {
            console.log(err ? ("Error occurred while sending email: " + err.message) : ("Email successfully sent: " + info.response));
        });
    },

    getMemberById: function(guild, id) {
        return guild.members.fetch().then(fetchedMembers => {
            return fetchedMembers.find(member => member.id === id);
        });
    },

    getMemberByNickname: function(guild, nickname) {
        return guild.members.fetch().then(fetchedMembers => {
            return fetchedMembers.find(member => member.nickname === nickname);
        });
    }
};