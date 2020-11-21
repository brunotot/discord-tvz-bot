process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = 0;

const prefix = process.env.PREFIX;
const token = process.env.TOKEN;
const guildId = process.env.REAL_GUILD_ID;

const database = require('./database.js');
const constants = require('./constants.js');
const emailCommand = require(`./bot_commands/email`);
const codeCommand = require(`./bot_commands/code`);
const helpCommand = require(`./bot_commands/help`);

const Discord = require("discord.js");
const client = new Discord.Client();
client.login(token);

var guild;
client.on('ready', () => {
    console.log(constants.BOT_START);
    guild = client.guilds.cache.get(guildId);
});

client.on('guildMemberAdd', member => {
    database.delete({ idDiscord: member.id }, database.TABLE_CONFIRMATIONS);
    member.send(constants.WELCOME_MESSAGE);
});

client.on('guildMemberRemove', member => {
    database.delete({ idDiscord: member.id }, database.TABLE_CONFIRMATIONS);
});

client.on('message', message => {
    if (message.content.startsWith(prefix)) {
        let commandString = message.content.substring(prefix.length);
        let command = commandString.split(" ")[0];
        let value = message.content.substring(prefix.length + command.length + 1).trim();
        if (message.channel.type === "dm") {
            if (command === "email") {
                emailCommand.execute(message, value, guild);
            } else if (command === "code") {
                codeCommand.execute(message, value, guild);
            } else if (command === "help") {
                helpCommand.execute(message);
            }
        }
    }
});